package sample.Data;

abstract public class DataObject {

    public static final int INDEX_ID = 1;
    public static final String HEADER_ID = "ID";
    public static final int INDEX_NAME = 2;
    public static final String HEADER_NAME = "name";

    private int id;
    private String name;

    // updates a DataObject with values from database
    abstract public void update();

    // constructor
    public DataObject(String name){
        this.id = id;
        this.name = name;
    }

    // overrides toString() to enable displaying the DataObject in a ListView
    @Override
    public String toString() {
        return name;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
