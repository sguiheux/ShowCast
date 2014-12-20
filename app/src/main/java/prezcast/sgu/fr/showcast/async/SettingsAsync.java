package prezcast.sgu.fr.showcast.async;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import prezcast.sgu.fr.showcast.activity.SettingsActivity;
import prezcast.sgu.fr.showcast.db.DBHelper;
import prezcast.sgu.fr.showcast.db.setting.EnumSettingsZone;
import prezcast.sgu.fr.showcast.db.setting.Setting;
import prezcast.sgu.fr.showcast.db.setting.SettingsTable;

/**
 * Async task for actions on settings
 */
public class SettingsAsync extends AsyncTask<Setting, Void, Map> {

    // Weak reference to activity
    private WeakReference<Activity> ctx;

    // action to realise
    private SettingsAsyncAction action;



    private static final String TAG = "ShowCast-SettingsAsync";

    /**
     * Constructor
     *
     * @param ctx    Screen context
     * @param action Action to realize
     */
    public SettingsAsync(Activity ctx, SettingsAsyncAction action) {
        this.ctx = new WeakReference<>(ctx);
        this.action = action;
    }


    @Override
    protected Map doInBackground(Setting... params) {
        switch (action) {
            case SELECT_ALL:
                return selectAllSettings();
            case UPDATE:
                return updateSettings(params);
        }
        return null;
    }

    private Map updateSettings(Setting[] settings) {
        SQLiteOpenHelper sqLiteOpenHelper = new DBHelper(ctx.get(), null);
        SQLiteDatabase maBdd = null;
        try {
            maBdd = sqLiteOpenHelper.getWritableDatabase();
            maBdd.beginTransaction();
            for (Setting s : settings) {
                SettingsTable.update(maBdd, s);
            }
            maBdd.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Error when updating settings", e);
            return null;
        } finally {
            if (maBdd != null) {
                maBdd.endTransaction();
                maBdd.close();
            }
        }
        return selectAllSettings();
    }

    // Get all settings
    private Map selectAllSettings() {
        SQLiteOpenHelper sqLiteOpenHelper = new DBHelper(ctx.get(), null);
        SQLiteDatabase maBdd = null;
        try {
            maBdd = sqLiteOpenHelper.getReadableDatabase();
            List<Setting> listSettings = SettingsTable.getAll(maBdd);

            Map<EnumSettingsZone, List<Setting>> mapSettingsByZone = new HashMap<>();
            for (Setting s : listSettings) {
                if (!mapSettingsByZone.containsKey(s.getZone())) {
                    List<Setting> listSettingsInMap = new ArrayList<>();
                    mapSettingsByZone.put(s.getZone(), listSettingsInMap);
                }
                mapSettingsByZone.get(s.getZone()).add(s);
            }
            return mapSettingsByZone;
        } catch (Exception e) {
            Log.e(TAG, "Error when select settings", e);
            return null;
        } finally {
            if (maBdd != null) {
                maBdd.close();
            }
        }
    }

    @Override
    protected void onPostExecute(Map map) {
        Log.d("eee",map.toString());
        if(ctx.get() instanceof SettingsActivity){
            ((SettingsActivity) ctx.get()).updateSettings(map);
            ((SettingsActivity) ctx.get()).showResult(map != null);
        }
    }

    public enum SettingsAsyncAction {
        SELECT_ALL,
        UPDATE
    }
}
