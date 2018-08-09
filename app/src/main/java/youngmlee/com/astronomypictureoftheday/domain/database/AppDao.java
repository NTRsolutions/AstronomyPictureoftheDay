package youngmlee.com.astronomypictureoftheday.domain.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import youngmlee.com.astronomypictureoftheday.domain.model.Picture;

@Dao
public interface AppDao {

    @Query("SELECT * FROM picture")
    LiveData<List<Picture>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Picture> pictureList);

    @Query("DELETE FROM picture")
    void deleteAll();

}
