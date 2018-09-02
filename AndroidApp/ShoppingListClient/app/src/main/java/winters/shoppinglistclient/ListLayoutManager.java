package winters.shoppinglistclient;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.ViewGroup;
import android.support.v7.widget.LinearLayoutManager;
import android.content.Context;

public class ListLayoutManager extends LinearLayoutManager
{
    MainActivity mainActivity;

    public ListLayoutManager(Context context)
    {
        super(context);
        mainActivity = (MainActivity)context;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams()
    {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    //@Override
    public void onResume()
    {
        //mainActivity.getTable();
    }
}
