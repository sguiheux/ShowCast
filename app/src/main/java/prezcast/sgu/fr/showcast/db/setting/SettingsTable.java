package prezcast.sgu.fr.showcast.db.setting;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import prezcast.sgu.fr.showcast.db.DbException;

/**
 * Represente settings table
 */
public class SettingsTable {

    // Table name
    public static final String TABLE_NAME = "SETTINGS";

    // Colunm information
    public static final String COLONNE_ID = "ID";
    public static final int NUM_COLONNE_ID = 0;
    public static final String COLONNE_ZONE = "ZONE";
    public static final int NUM_COLONNE_ZONE = 1;
    public static final String COLONNE_NOM = "NOM";
    public static final int NUM_COLONNE_NOM = 2;
    public static final String COLONNE_TYPE = "TYPE";
    public static final int NUM_COLONNE_TYPE = 3;
    public static final String COLONNE_VALEUR = "VALEUR";
    public static final int NUM_COLONNE_VALEUR = 4;

    // Creation table
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
            + COLONNE_ID + " INTEGER  PRIMARY KEY AUTOINCREMENT, "
            + COLONNE_ZONE + " INTEGER NOT NULL, "
            + COLONNE_NOM + " STRING NOT NULL, "
            + COLONNE_TYPE + " INTEGER NOT NULL, "
            + COLONNE_VALEUR + " STRING NOT NULL); ";

    // Insert a setting in the data base
    public static void insert(Setting setting, SQLiteDatabase bdd) throws DbException {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLONNE_ZONE, setting.getZone().id);
        contentValues.put(COLONNE_NOM, setting.getName());
        contentValues.put(COLONNE_TYPE, setting.getType().id);
        contentValues.put(COLONNE_VALEUR, setting.getValue());
        long result = bdd.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            throw new DbException("Error when inserting data: " + setting.toString());
        }
    }

    /**
     * Get all settings
     * @param db DB connexion
     * @return Return the list of the settings
     */
    public static List<Setting> getAll(SQLiteDatabase db) {
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COLONNE_ID,
                        COLONNE_ZONE,
                        COLONNE_NOM,
                        COLONNE_TYPE,
                        COLONNE_VALEUR}, null, null, null,
                null, COLONNE_ZONE + " ASC", null);
        return convertCursorToListObject(cursor);
    }

    // Get settings from curosr
    private static List<Setting> convertCursorToListObject(Cursor c) {
        List<Setting> liste = new ArrayList<>();
        if (c.getCount() == 0) {
            c.close();
            return liste;
        }

        // position sur le premier item
        c.moveToFirst();

        // Pour chaque item
        do {
            Setting p = convertCursorToObject(c);
            liste.add(p);
        } while (c.moveToNext());

        // Fermeture du curseur
        c.close();

        return liste;
    }

    // Convert element in cursor to Setting
    private static Setting convertCursorToObject(Cursor c) {
        int id = c.getInt(NUM_COLONNE_ID);
        String name = c.getString(NUM_COLONNE_NOM);
        EnumSettingsZone zone = EnumSettingsZone.getFromId(c.getInt(NUM_COLONNE_ZONE));
        EnumSettingsType type = EnumSettingsType.getFromId(c.getInt(NUM_COLONNE_TYPE));
        String value = c.getString(NUM_COLONNE_VALEUR);
        Setting s = new Setting(type,zone, name, value);
        s.setId(id);
        return s;
    }

    public static void update(SQLiteDatabase db, Setting s) throws DbException {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLONNE_VALEUR, s.getValue());
        int result = db.update(TABLE_NAME, contentValues,
                COLONNE_ID + "=?",
                new String[]{String.valueOf(s.getId())});
        if(result != 1){
            throw new DbException("Error when updating data: "+s.toString());
        }
    }
}
