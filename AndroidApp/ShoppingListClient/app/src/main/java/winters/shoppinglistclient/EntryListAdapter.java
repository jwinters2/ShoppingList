package winters.shoppinglistclient;

/**
 * Created by jamie on 8/28/18.
 */

import winters.shoppinglistclient.Persistence.entity.Entry;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;

public class EntryListAdapter extends RecyclerView.Adapter<EntryListAdapter.EntryViewHolder>
{
    private List<Entry> list;
    private Map<View, Entry> entryMap = new HashMap<View, Entry>();

    public static class EntryViewHolder extends RecyclerView.ViewHolder
    {
        LinearLayout tv;
        public EntryViewHolder(LinearLayout _tv)
        {
            super(_tv);
            tv = _tv;
        }
    }

    public EntryListAdapter(List<Entry> _list)
    {
        list = _list;
    }

    @Override
    public EntryListAdapter.EntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        //TextView tv = (TextView) LayoutInflater.from(parent.getContext())
        LinearLayout tv = (LinearLayout) LayoutInflater.from(parent.getContext())
        .inflate(R.layout.entry_view, parent, false);
        setListeners(tv);
        EntryViewHolder vh = new EntryViewHolder(tv);
        return vh;
    }

    @Override
    public void onBindViewHolder(EntryViewHolder holder, int pos) {
        //holder.tv.setText(list.get(pos).getName());
        LinearLayout ll = (LinearLayout) holder.tv;

        ((TextView)ll.getChildAt(0)).setText(list.get(pos).getName());
        ((TextView)ll.getChildAt(1)).setText(list.get(pos).getAmount());
        ((TextView)ll.getChildAt(2)).setText(list.get(pos).getStore());

        TextView[] children =
        {
            (TextView)ll.getChildAt(0),
            (TextView)ll.getChildAt(1),
            (TextView)ll.getChildAt(2),
        };

        Entry e = list.get(pos);

        if (!e.getToDelete()) {
            ((TextView) ll.getChildAt(0)).setTextColor(0xff000000);
            ((TextView) ll.getChildAt(1)).setTextColor(0xff000000);
            ((TextView) ll.getChildAt(2)).setTextColor(0xff000000);
        } else {
            ((TextView) ll.getChildAt(0)).setTextColor(0xff999999);
            ((TextView) ll.getChildAt(1)).setTextColor(0xff999999);
            ((TextView) ll.getChildAt(2)).setTextColor(0xff999999);
        }

        //System.out.println("setting entry for pos " + pos);
        //System.out.println(getItemCount());
        entryMap.put(ll, list.get(pos));
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    private void setListeners(final LinearLayout l)
    {
        l.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Entry e = entryMap.get(l);

                if (e.getToDelete()) {
                    ((TextView) l.getChildAt(0)).setTextColor(0xff000000);
                    ((TextView) l.getChildAt(1)).setTextColor(0xff000000);
                    ((TextView) l.getChildAt(2)).setTextColor(0xff000000);

                    e.setToDelete(false);
                } else {
                    ((TextView) l.getChildAt(0)).setTextColor(0xff999999);
                    ((TextView) l.getChildAt(1)).setTextColor(0xff999999);
                    ((TextView) l.getChildAt(2)).setTextColor(0xff999999);

                    e.setToDelete(true);
                }

                MainActivity.setToDeleteInDB(e);
            }
        });

        l.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View view)
            {
                Entry e = entryMap.get(l);

                //System.out.println("long click");
                LayoutInflater inflater = LayoutInflater.from(l.getContext());
                View pv = inflater.inflate(R.layout.popup_window, null);
                //pv.setBackgroundColor(0xffffff00);
                final PopupWindow pw = new PopupWindow(pv, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                TextView textView = new TextView(l.getContext());
                textView.setText("textView");
                pw.setContentView(pv);
                pw.showAtLocation(l.getRootView(), Gravity.CENTER, 0, 0);

                LinearLayout lpv = (LinearLayout)(((RelativeLayout)pv).getChildAt(0));
                ((TextView)lpv.getChildAt(0)).setText(e.getName());
                ((TextView)lpv.getChildAt(1)).setText("Requested on " + e.getDate() + " by " + e.getUser());
                ((TextView)lpv.getChildAt(2)).setText(e.getNotes());

                pv.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        //System.out.println("dismiss");3
                        pw.dismiss();
                    }
                });

                return true;
            }
        });
    }
}
