package winters.shoppinglistclient;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.StringRequest;

import java.io.StringReader;
import java.io.IOException;

import winters.shoppinglistclient.Persistence.ListDatabase;
import winters.shoppinglistclient.Persistence.entity.Entry;
import winters.shoppinglistclient.Persistence.entity.Property;

import java.lang.ref.WeakReference;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

import android.support.v7.widget.LinearLayoutManager;

import android.support.design.widget.Snackbar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{
    //private TextView mainBody;
    private List<Entry> table = null;
    String currentStore = "Any";

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerAdapter;
    private RecyclerView.LayoutManager recyclerLayoutManager;
    private RequestQueue queue = null;
    protected SwipeRefreshLayout srl;
    private static ListDatabase listdb = null;
    private static Activity toastActivity = null;

    class ReadDBAsync extends AsyncTask<Void, Void, List<Entry>>
    {
        private WeakReference<Activity> wr = null;
        private boolean uploadChanges = false;

        public ReadDBAsync(){}
        public ReadDBAsync(Activity activity)
        {
            wr = new WeakReference<Activity>(activity);
        }
        public ReadDBAsync(Boolean _uploadChanges)
        {
            uploadChanges = _uploadChanges;
        }

        @Override
        protected List<Entry> doInBackground(Void... voids)
        {
            List<Entry> retval = listdb.entryDao().getAll();
            return retval;
        }

        @Override
        protected void onPostExecute(List<Entry> result)
        {
            Activity a = (wr == null ? null : wr.get());
            if(a != null)
            {
                Toast.makeText(a, "Retrieved list from local of length " + result.size(), Toast.LENGTH_LONG).show();
            }
            table = result;
            showTable();

            if(uploadChanges)
            {
                deleteItems();
                getTableFromServlet();
            }
        }
    }

    private class WriteDBAsync extends AsyncTask<List<Entry>, Void, Void>
    {
        protected Void doInBackground(List<Entry>... entries)
        {
            if(entries.length > 0)
            {
                //List<Integer> idsToDelete = listdb.entryDao().getToDeleteIds();
                listdb.entryDao().deleteAll();
                listdb.entryDao().insertAll(entries[0]);
                //listdb.entryDao().setToDeleteIds(idsToDelete);
            }

            return null;
        }
    }

    private static class SetToDeleteDBAsync extends AsyncTask<Entry, Void, Void>
    {
        protected Void doInBackground(Entry... e)
        {
            if(e.length > 0)
            {
                //listdb.entryDao().updateEntry(e[0]);
                int result = listdb.entryDao().setToDelete(e[0].getId(),e[0].getToDelete());
                System.out.println("update query result = " + result);
            }
            return null;
        }
    }

    private class GetPropertyAsync extends AsyncTask<String, Void, String>
    {
        GetPropertyListener callback = null;
        public GetPropertyAsync(GetPropertyListener gpl)
        {
            callback = gpl;
        }

        protected String doInBackground(String... strings)
        {
            if(strings.length > 0)
            {
                return listdb.propertyDao().getProperty(strings[0]);
            }
            return null;
        }

        protected void onPostExecute(String value)
        {
            if(callback != null)
            {
                callback.handleValue(value);
            }
        }
    }

    private interface GetPropertyListener
    {
        void handleValue(String value);
    }

    private class SetPropertyAsync extends AsyncTask<String, Void, Void>
    {
        protected Void doInBackground(String... strings)
        {
            if(strings.length > 1)
            {
                Property p = new Property(strings[0], strings[1]);
                listdb.propertyDao().setProperty(p);
            }
            return null;
        }

    }

    public static void setToDeleteInDB(Entry e)
    {
        System.out.println("setting " + e.getId() + " (" + e.getName() + ") to " + e.getToDelete());
        new SetToDeleteDBAsync().execute(e);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        queue = Volley.newRequestQueue(this);

        listdb = Room.databaseBuilder(getApplicationContext(), ListDatabase.class, "list").build();
        toastActivity = this;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                deleteItems();
            }
        });

        srl = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        final SwipeRefreshLayout.OnRefreshListener srlListener = new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                deleteItems();
                getTableFromServlet();
            }
        };
        srl.setOnRefreshListener(srlListener);

        recyclerView = (RecyclerView) findViewById(R.id.mainlistview);

        recyclerLayoutManager = new LinearLayoutManager(this);
        ((LinearLayoutManager)recyclerLayoutManager).setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(recyclerLayoutManager);

        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL));

        new ReadDBAsync(true).execute();
        //while(table == null) ;
        //deleteItems();
        //getTableFromServlet();
    }

    void getTableFromServlet()
    {
        srl.measure(100, 100);
        srl.setRefreshing(true);

        //mainBody = (TextView)findViewById(R.id.mainlisttext);
        //mainBody.setText("foobar");

        //String url = "http://10.0.0.62:8080/shoppinglist/AppMainServlet";
        String url = Constants.urlBase + Constants.mainServlet;
        final Activity mainActivity = this;

        StringRequest sr = new StringRequest(Request.Method.GET, url,
        new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                receiveList(response);

                if(srl.isRefreshing())
                {
                    srl.setRefreshing(false);
                }
            }
        },
        new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                showCouldNotConnectMessage();
                new ReadDBAsync().execute();
                //new ReadDBAsync(mainActivity).execute();
                if(srl.isRefreshing())
                {
                    srl.setRefreshing(false);
                }
            }
        });

        queue.add(sr);

    }

    private void receiveList(String json)
    {
        JsonReader jr = new JsonReader(new StringReader(json));

        try
        {
            List<Entry> retval = new ArrayList<Entry>();
            jr.beginArray();
            while (jr.hasNext())
            {
                Entry e = new Entry();
                jr.beginObject();
                while (jr.hasNext())
                {
                    String name = jr.nextName();
                    if(name.equals("id"))
                    {
                        e.setId(jr.nextInt());
                    }
                    else if(name.equals("name"))
                    {
                        e.setName(jr.nextString());
                    }
                    else if(name.equals("date"))
                    {
                        e.setDate(jr.nextString());
                    }
                    else if(name.equals("amount"))
                    {
                        e.setAmount(jr.nextString());
                    }
                    else if(name.equals("store"))
                    {
                        e.setStore(jr.nextString());
                    }
                    else if(name.equals("user"))
                    {
                        e.setUser(jr.nextString());
                    }
                    else if(name.equals("notes"))
                    {
                        e.setNotes(jr.nextString());
                    }
                }
                jr.endObject();
                retval.add(e);
            }
            jr.endArray();
            //return retval;
            table = retval;
            new WriteDBAsync().execute(table);
            Calendar c = new GregorianCalendar();
            c.setTime(new Date());
            int hour = c.get(Calendar.HOUR);
            int minute = c.get(Calendar.MINUTE);
            String currentTime = "" + getMonthFromNumber(c.get(Calendar.MONTH)) + " " + c.get(Calendar.DAY_OF_MONTH)
                               + ", " + hour
                               + ":"  + (minute > 9 ? "" : "0") + minute + " " + (c.get(Calendar.AM_PM) == Calendar.PM ? "PM" : "AM");
            new SetPropertyAsync().execute("lastUpdated", currentTime);
            showTable();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            //return new ArrayList<Entry>();
            //main();
        }
    }

    private void deleteItems()
    {
        if(table == null)
        {
            showCouldNotConnectMessage();
            return;
        }

        String url = Constants.urlBase + Constants.deleteServlet + Constants.nullUser;
        for (Entry e : table) {
            url += "&d" + e.getId() + "=" + (e.getToDelete() ? "t" : "f");
        }
        System.out.println(url);

        StringRequest deleteReq = new StringRequest(Request.Method.POST, url,
        new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                System.out.println("got response");
                System.out.println(response);
                getTableFromServlet();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                showCouldNotConnectMessage();
                System.out.println(error.getMessage());
                //mainBody.setText("ERROR\n" + error.getMessage());
            }
        });

        queue.add(deleteReq);
    }

    private void showTable()
    {
        recyclerAdapter = new EntryListAdapter(Entry.filterList(table, currentStore));
        recyclerView.setAdapter(recyclerAdapter);

        //tv.setVisibility(TextView.VISIBLE);
        new GetPropertyAsync(new GetPropertyListener()
        {
            @Override
            public void handleValue(String value)
            {
                TextView tv = findViewById(R.id.last_updated_text_view);
                if(value == null)
                    Snackbar.make(recyclerView, "lastUpdated is null", Snackbar.LENGTH_SHORT).show();
                else
                    tv.setText("List last updated on " + value);
            }
        }).execute("lastUpdated");

    }

    private void showCouldNotConnectMessage()
    {
        Snackbar sb = Snackbar.make(recyclerView, "Could not connect to list server", Snackbar.LENGTH_SHORT);
        sb.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_filter);
        item.setTitle("Filter: Any");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        showTable();

        int id = item.getItemId();

        final MenuItem finalItem = item;

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {


            return true;
        } else if (id == R.id.action_filter) {
            System.out.println("filter");

            final List<Pair<String, Integer>> storeList = Entry.getUniqueStoreNames(table);
            final List<String> optionsList = new ArrayList<String>();
            for(Pair<String, Integer> s : storeList)
            {
                optionsList.add(s.first + "  (" + s.second + ")");
            }

            Spinner spinner = new Spinner(this, Spinner.MODE_DIALOG);
            //spinner.setGravity(Gravity.CENTER);

            ArrayAdapter<String> aa = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, optionsList);
            aa.setDropDownViewResource(R.layout.spinner_option);
            spinner.setAdapter(aa);
            spinner.setVisibility(View.INVISIBLE);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
                {
                    currentStore = storeList.get(i).first;
                    finalItem.setTitle("Filter: " + storeList.get(i).first);
                    showTable();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView)
                {

                }
            });

            final PopupWindow pw = new PopupWindow(spinner, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
            pw.showAtLocation(spinner.getRootView(), Gravity.CENTER, 0, 0);
            spinner.performClick();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String getMonthFromNumber(int i)
    {
        switch(i+1)
        {
            case 1: return "Jan";
            case 2: return "Feb";
            case 3: return "Mar";
            case 4: return "Apr";
            case 5: return "May";
            case 6: return "Jun";
            case 7: return "Jul";
            case 8: return "Aug";
            case 9: return "Sep";
            case 10: return "Oct";
            case 11: return "Nov";
            case 12: return "Dec";
        }

        return null;
    }
}
