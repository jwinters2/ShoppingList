package winters.shoppinglistclient.Persistence;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import winters.shoppinglistclient.Persistence.entity.Entry;

@Database(entities = {Entry.class}, version = 1)
public abstract class ListDatabase extends RoomDatabase
{
    public abstract EntryDao entryDao();
}
