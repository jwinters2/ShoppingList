package winters.shoppinglistclient.Persistence;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import winters.shoppinglistclient.Persistence.entity.Property;

@Dao
public interface PropertyDao
{
    @Query("select value from property where name = :name")
    String getProperty(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long setProperty(Property p);
}
