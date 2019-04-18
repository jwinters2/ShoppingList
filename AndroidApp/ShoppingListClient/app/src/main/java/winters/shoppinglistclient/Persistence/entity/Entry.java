package winters.shoppinglistclient.Persistence.entity;

/**
 * Created by jamie on 8/28/18.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.util.Pair;

@Entity(tableName = "entry")
public class Entry
{
    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "amount")
    private String amount;

    @ColumnInfo(name = "store")
    private String store;

    @ColumnInfo(name = "user")
    private String user;

    @ColumnInfo(name = "notes")
    private String notes;

    @ColumnInfo(name = "toDelete")
    private Boolean toDelete;

    public Entry()
    {
        toDelete = false;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int _id)
    {
        id = _id;
    }

    public String getNotes()
    {
        return notes;
    }

    public void setNotes(String notes)
    {
        this.notes = notes;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    public String getStore()
    {
        return store;
    }

    public void setStore(String store)
    {
        this.store = store;
    }

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Boolean getToDelete()
    {
        return toDelete;
    }

    public void setToDelete(Boolean toDelete)
    {
        this.toDelete = toDelete;
    }

    @Override
    public String toString() {
        return "Entry{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", date='" + date + '\'' +
        ", amount='" + amount + '\'' +
        ", store='" + store + '\'' +
        ", user='" + user + '\'' +
        ", notes='" + notes + '\'' +
        '}';
    }

    public static List<Pair<String,Integer>> getUniqueStoreNames(List<Entry> entries)
    {
        List<Pair<String,Integer>> retval = new ArrayList<Pair<String,Integer>>();
        Map<String, Integer> counts = new HashMap<String, Integer>();
        int anyCount = 0;

        for(Entry e : entries)
        {
            if(e.getStore().equals("Any"))
            {
                anyCount++;
            }
            else if(counts.containsKey(e.getStore()))
            {
                counts.put(e.getStore(), counts.get(e.getStore()) + 1);
            }
            else
            {
                counts.put(e.getStore(), 1);
            }
        }

        retval.add(new Pair<String, Integer>("Any",entries.size()));

        for(Entry e : entries)
        {
            if(!e.getStore().equals("Any") && counts.containsKey(e.getStore()))
            {
                retval.add(new Pair<String, Integer>(e.getStore(), anyCount + counts.get(e.getStore())));
                counts.remove(e.getStore());
            }
        }

        //retval.add(new Pair<String, Integer>("None",0));
        return retval;
    }

    public static List<Entry> filterList(List<Entry> entries, String store)
    {
        if(store == null || store.isEmpty() || store.equals("Any"))
        {
            return new ArrayList<Entry>(entries);
        }

        List<Entry> retval = new ArrayList<Entry>();

        for(Entry e : entries)
        {
            if(e.getStore().equals(store) || e.getStore().equals("Any"))
            {
                retval.add(e);
            }
        }

        return retval;
    }
}
