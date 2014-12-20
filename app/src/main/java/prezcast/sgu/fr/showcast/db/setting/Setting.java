package prezcast.sgu.fr.showcast.db.setting;

import android.os.Environment;

/**
 * Represent setting object.
 */
public class Setting {

    // Default directory for presentation
    public static final String DEFAULT_DIRECTORY_KEY = "setting.default.directory";
    public static final String DEFAULT_DIRECTORY_VALUE = Environment.getExternalStorageDirectory().getPath()+"/showcast";

    private int id;
    private EnumSettingsZone zone;
    private String name;
    private EnumSettingsType type;
    private String value;

    /**
     * Create new Setting
     * @param type Type of the data
     * @param zone ZOne in the list
     * @param name Name
     * @param value Value
     */
    public Setting(EnumSettingsType type,EnumSettingsZone zone,String name,String value){
        this.type = type;
        this.zone = zone;
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Setting[type: "+type.id+",zone: "+zone.id+",name: "+name+",value: "+value+"]";
    }

    public int getId() {
        return id;
    }

    public EnumSettingsZone getZone() {
        return zone;
    }

    public String getName() {
        return name;
    }

    public EnumSettingsType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setValue(String value) {
        this.value = value;
    }
}


