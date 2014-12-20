package prezcast.sgu.fr.showcast.db.setting;

/**
 * Represent the differents part in the settings list.
 */
public enum EnumSettingsZone {

    GENERAL(1,"setting.zone.general");

    public int id;
    public String name;

    private EnumSettingsZone(int id,String name){
        this.id = id;
        this.name = name;
    }

    public static EnumSettingsZone getFromId(int id){
        for(EnumSettingsZone e:EnumSettingsZone.values()){
            if(e.id == id){
                return e;
            }
        }
        return null;
    }
}
