package prezcast.sgu.fr.showcast.db.setting;

/**
 * Represent the type of data
 */
public enum EnumSettingsType {
    INT(1),
    STRING(2),
    BOOLEAN(3);

    public int id;

    private EnumSettingsType(int id){
        this.id = id;
    }

    public static EnumSettingsType getFromId(int id){
        for(EnumSettingsType e: EnumSettingsType.values()){
            if(e.id == id){
                return e;
            }
        }
        return null;
    }
}
