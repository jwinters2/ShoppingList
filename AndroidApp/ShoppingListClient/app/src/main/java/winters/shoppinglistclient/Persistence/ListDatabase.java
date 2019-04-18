package winters.shoppinglistclient.Persistence;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import winters.shoppinglistclient.Persistence.entity.Entry;
import winters.shoppinglistclient.Persistence.entity.Property;

@Database(entities = {Entry.class, Property.class}, version = 2)
public abstract class ListDatabase extends RoomDatabase
{
    public abstract EntryDao entryDao();
    public abstract PropertyDao propertyDao();
}
