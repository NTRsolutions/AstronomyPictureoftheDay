package youngmlee.com.astronomypictureoftheday.domain.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import youngmlee.com.astronomypictureoftheday.domain.model.Picture;

@Database(entities = {Picture.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase{

    static final int VERSION = 1;

    public abstract AppDao appDao();

}
