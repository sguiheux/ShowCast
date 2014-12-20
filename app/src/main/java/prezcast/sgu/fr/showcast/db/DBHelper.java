package prezcast.sgu.fr.showcast.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import prezcast.sgu.fr.showcast.db.setting.EnumSettingsType;
import prezcast.sgu.fr.showcast.db.setting.EnumSettingsZone;
import prezcast.sgu.fr.showcast.db.setting.Setting;
import prezcast.sgu.fr.showcast.db.setting.SettingsTable;

/**
 *
 */
public class DBHelper extends SQLiteOpenHelper {

    // TAg for logs.
    private static final String TAG = "ShowCast-DBHelper";

    // Database name
    private static final String DB_NAME = "ShowCast";
    // Database version
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructor
     * @param context Application context.
     * @param factory Cursor factory
     */
    public DBHelper(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DB_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + SettingsTable.TABLE_NAME + ";");
        db.execSQL(SettingsTable.CREATE_TABLE);
        insertV1(db);
    }

    /**
     * Data for version 1.
     * @param db
     */
    private void insertV1(SQLiteDatabase db) {
        // Default directory for presentation
        Setting s = new Setting(EnumSettingsType.STRING, EnumSettingsZone.GENERAL,Setting.DEFAULT_DIRECTORY_KEY,Setting.DEFAULT_DIRECTORY_VALUE);
        try {
            SettingsTable.insert(s,db);
        } catch (DbException e) {
            Log.e(TAG,e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
