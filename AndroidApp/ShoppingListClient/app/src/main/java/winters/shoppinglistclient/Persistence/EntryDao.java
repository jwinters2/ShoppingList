package winters.shoppinglistclient.Persistence;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import winters.shoppinglistclient.Persistence.entity.Entry;

@Dao
public interface EntryDao
{
    @Query("select * from entry")
    List<Entry> getAll();

    @Query("delete from entry")
    void deleteAll();

    @Query("update entry set toDelete = :toDelete where id = :id")
    int setToDelete(int id, boolean toDelete);

    @Query("select id from entry where toDelete = 1")
    List<Integer> getToDeleteIds();

    @Query("update entry set toDelete = 1 where id in (:ids)")
    int setToDeleteIds(List<Integer> ids);

    @Insert
    void insertAll(List<Entry> entries);
}
