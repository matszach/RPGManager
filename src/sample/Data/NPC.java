package sample.Data;

public class NPC extends DataObject{

    public static final String TABLE_NAME = "NPCs";

    public static final int INDEX_LOCATION_ID = 3;
    public static final String HEADER_LOCATION_ID = "locationID";
    public static final int INDEX_DESCRIPTION = 4;
    public static final String HEADER_DESCRIPTION = "description";


    private int locationID;
    private String description;


    @Override
    public void update() {
        NPC updatedNPC = DataSource.queryNPCOfID(getId());
        setName(updatedNPC.getName());
        setLocationID(updatedNPC.getLocationID());
        setDescription(updatedNPC.getDescription());
    }

    // constructors
    public NPC(String name){
        super(name);
    }
    public NPC(String name, int locationID, String description){
        this(name);
        this.locationID = locationID;
        this.description = description;
    }


    public int getLocationID() {
        return locationID;
    }
    public void setLocationID(int locationID) {
        this.locationID = locationID;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
