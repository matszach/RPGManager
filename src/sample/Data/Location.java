package sample.Data;

public class Location extends DataObject {

    public static final String TABLE_NAME = "Locations";

    public static final int INDEX_DESCRIPTION = 3;
    public static final String HEADER_DESCRIPTION = "description";

    private String description;


    @Override
    public void update() {
        Location updatedLocation = DataSource.queryLocationOfID(getId());
        setName(updatedLocation.getName());
        setDescription(updatedLocation.getDescription());
    }


    // constructor
    public Location(String name){
        super(name);
    }
    public Location(String name, String description){
        this(name);
        this.description = description;
    }


    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    
}
