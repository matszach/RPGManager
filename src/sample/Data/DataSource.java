package sample.Data;

import java.sql.*;
import java.util.ArrayList;

final public class DataSource {

    // this is a singleton
    private static DataSource inst = new DataSource();
    private DataSource(){}




    // ** SQL STATEMENTS **


    // NPC view
    public static final String  NPC_VIEW_NAME = "NPC_list";

    // executes SQL statement
    private static boolean executeStatement(String command) {
        try (Statement statement = currentCampaignDatabase.createStatement()) {
            System.out.println(command); // FIXME: 2019-02-22
            statement.execute(command);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Select all records from table
    private static final String SELECT_ALL_FROM = "SELECT * FROM ";
    private static String SQL_selectAllRecordsFromTable(String tableName){
        return SELECT_ALL_FROM + tableName;
    }

    // Embed in single quotes
    private static String embedInSingleQuotes(String statement){
        return "\'" + statement + "\'";
    }
    private static String embedInPercentSymbols(String statement){
        return "%" + statement + "%";
    }

    // Insert a record into a table
    private static final String INSERT_INTO = "INSERT INTO ";
    private static final String VALUES = " VALUES ";
    private static String SQL_insertIntoTable(String tableName){
        return INSERT_INTO + tableName + VALUES;
    }

    // Select specific ID
    private static final String WHERE_ID_EQUALS = " WHERE " + DataObject.HEADER_ID + " = ";
    private static String SQL_whereIDEquals(int ID){
        return WHERE_ID_EQUALS + ID;
    }

    // Select name like
    private static final String WHERE_NAME_LIKE = " WHERE " + DataObject.HEADER_NAME + " LIKE ";
    private static String SQL_whereNameLike(String namePart){
        return WHERE_NAME_LIKE + "'%" + namePart + "%'";
    }

    // Select locationID equal to
    private static final String WHERE_LOCATION_ID_EQUALS = " WHERE " + NPC.HEADER_LOCATION_ID + " = ";
    private static String SQL_whereLocationIDEquals(int locationID){
        return WHERE_LOCATION_ID_EQUALS + locationID;
    }

    // Select locationIDs where location name like...
    private static final String WHERE_LOCATION_NAME_LIKE =
                        " WHERE " + NPC.HEADER_LOCATION_ID + " IN (" + " SELECT " + DataObject.HEADER_ID +
                        " FROM " + Location.TABLE_NAME + WHERE_NAME_LIKE;
    private static String NPCsWhereLocationNameLike(String namePart){
        return WHERE_LOCATION_NAME_LIKE + "'%" + namePart + "%')";
    }







    // * Prepared Statements *

    // PREP Insert NPC
    private static final String PREP_INSERT_NPC =
            INSERT_INTO + NPC.TABLE_NAME + VALUES + " (null,?,?,?)";
    private static PreparedStatement PS_insertNPC;

    // PREP Insert Location
    private static final String PREP_INSERT_LOCATION =
            INSERT_INTO + Location.TABLE_NAME + VALUES + " (null,?,?)";
    private static PreparedStatement PS_insertLocation;

    // PREP Select NPCs where name like
    private static final String PREP_SELECT_FROM_NPCs_WHERE_NAME_LIKE =
            SELECT_ALL_FROM + NPC.TABLE_NAME + WHERE_NAME_LIKE + "?";
    private static PreparedStatement PS_queryAllFromNPCsWithNameLike;

    // PREP Select NPCs where name like
    private static final String PREP_SELECT_FROM_LOCATIONS_WHERE_NAME_LIKE =
            SELECT_ALL_FROM + Location.TABLE_NAME + WHERE_NAME_LIKE + "?";
    private static PreparedStatement PS_queryAllFromLocationsWithNameLike;

    // PREP Select NPCs where location name like
    private static final String PREP_SELECT_FROM_NPCs_WHERE_LOCATION_NAME_LIKE =
            SELECT_ALL_FROM + NPC.TABLE_NAME + WHERE_LOCATION_NAME_LIKE + "?" + ")";
    private static PreparedStatement PS_queryAllFromNPCsWhereLocationNameLike;

    // PREP Update NPC where ID equals
    private static final String PREP_UPDATE_NPC_WHERE_ID_EQUALS =
            "UPDATE " + NPC.TABLE_NAME + " SET " +
            NPC.HEADER_NAME + " = ?, " +
            NPC.HEADER_LOCATION_ID + " = ?, " +
            NPC.HEADER_DESCRIPTION + " = ? " +
            WHERE_ID_EQUALS + "?";
    private static PreparedStatement PS_updateNPCWhereIDEquals;

    // PREP Update Location where ID equals
    private static final String PREP_UPDATE_LOCATION_WHERE_ID_EQUALS =
            "UPDATE " + Location.TABLE_NAME + " SET " +
            Location.HEADER_NAME + " = ?, " +
            Location.HEADER_DESCRIPTION + " = ? " +
            WHERE_ID_EQUALS + "?";
    private static PreparedStatement PS_updateLocationWhereIDEquals;






    // * Prepared Statements initialisation *
    private static void initPreparedStatements() throws SQLException{
        PS_insertNPC = currentCampaignDatabase.prepareStatement(PREP_INSERT_NPC);
        PS_insertLocation = currentCampaignDatabase.prepareStatement(PREP_INSERT_LOCATION);
        PS_queryAllFromNPCsWithNameLike = currentCampaignDatabase.prepareStatement(PREP_SELECT_FROM_NPCs_WHERE_NAME_LIKE);
        PS_queryAllFromLocationsWithNameLike = currentCampaignDatabase.prepareStatement(PREP_SELECT_FROM_LOCATIONS_WHERE_NAME_LIKE);
        PS_queryAllFromNPCsWhereLocationNameLike = currentCampaignDatabase.prepareStatement(PREP_SELECT_FROM_NPCs_WHERE_LOCATION_NAME_LIKE);
        PS_updateNPCWhereIDEquals = currentCampaignDatabase.prepareStatement(PREP_UPDATE_NPC_WHERE_ID_EQUALS);
        PS_updateLocationWhereIDEquals = currentCampaignDatabase.prepareStatement(PREP_UPDATE_LOCATION_WHERE_ID_EQUALS);
    }
    private static void closePreparedStatements() throws SQLException{
        PS_insertNPC.close();
        PS_insertLocation.close();
        PS_queryAllFromNPCsWithNameLike.close();
        PS_queryAllFromLocationsWithNameLike.close();
        PS_queryAllFromNPCsWhereLocationNameLike.close();
        PS_updateNPCWhereIDEquals.close();
        PS_updateLocationWhereIDEquals.close();
    }







    // ** CONNECTION METHODS **


    // connection to a chosen campaign database
    private static Connection currentCampaignDatabase;

    // sets up a connection to a database in the database folder, creates a new database if one of such name is not present
    public static void setupConnectionToDatabase(String databaseName){
        try {
            currentCampaignDatabase = DriverManager.getConnection("jdbc:sqlite:databases\\" + databaseName + ".db");
            initPreparedStatements();
            createAnyAbsentTablesAndViews();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    // sets up a connection to a database outside the database folder, creates a new database if one of such name is not present
    public static void setupConnectionToExternalDatabase(String databasePath){
        try {
            currentCampaignDatabase = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
            createAnyAbsentTablesAndViews();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    // create absent tables
    private static void createAnyAbsentTablesAndViews() throws SQLException{

        Statement statement = currentCampaignDatabase.createStatement();

        // NPC table
        statement.execute(
            "CREATE TABLE IF NOT EXISTS " + NPC.TABLE_NAME + " (" +
            DataObject.HEADER_ID + " INTEGER NOT NULL PRIMARY KEY, " +
            DataObject.HEADER_NAME + " TEXT NOT NULL, " +
            NPC.HEADER_LOCATION_ID + " INTEGER," +
            NPC.HEADER_DESCRIPTION + " TEXT)");

        // Location table
        statement.execute(
            "CREATE TABLE IF NOT EXISTS " + Location.TABLE_NAME + " (" +
            DataObject.HEADER_ID + " INTEGER NOT NULL PRIMARY KEY, " +
            DataObject.HEADER_NAME + " TEXT NOT NULL, " +
            Location.HEADER_DESCRIPTION + " TEXT)");

        // NPC view with NPC.locationID -> Location.name
        statement.execute(
            "CREATE VIEW IF NOT EXISTS " + NPC_VIEW_NAME + " AS SELECT " +
            NPC.TABLE_NAME+"."+DataObject.HEADER_ID+", " +
            NPC.TABLE_NAME+"."+DataObject.HEADER_NAME+", " +
            Location.TABLE_NAME+"."+DataObject.HEADER_NAME+", " +
            NPC.TABLE_NAME+"."+NPC.HEADER_DESCRIPTION+
            " FROM " + NPC.TABLE_NAME + "  INNER JOIN " + Location.TABLE_NAME + " WHERE " +
            Location.TABLE_NAME+"."+DataObject.HEADER_ID + " = " + NPC.TABLE_NAME+"."+NPC.HEADER_LOCATION_ID);

        statement.close();
    }

    // stop connection
    public static void stopConnectionToDatabase(){
        try {
            closePreparedStatements();
            currentCampaignDatabase.close();
        } catch (SQLException e){
            e.printStackTrace();
        }

    }








    // ** RECORD INSERTING **

    // inserts a NPC as a record
    public static boolean insertNPCIntoCurrentDatabase(NPC npc){
        StringBuilder sqlCommand = new StringBuilder(SQL_insertIntoTable(NPC.TABLE_NAME));
        sqlCommand.append("(");
        sqlCommand.append("null ,");// ID gets added automatically, as it's the primary key
        sqlCommand.append(embedInSingleQuotes(npc.getName()));
        sqlCommand.append(",");
        sqlCommand.append(npc.getLocationID());
        sqlCommand.append(",");
        sqlCommand.append(embedInSingleQuotes(npc.getDescription()));
        sqlCommand.append(")");
        return executeStatement(sqlCommand.toString());
    }
    public static boolean preparedInsertNPC(NPC npc){
        try {
            PS_insertNPC.setString(1, npc.getName());
            PS_insertNPC.setString(2, String.valueOf(npc.getLocationID()));
            PS_insertNPC.setString(3, npc.getDescription());
            PS_insertNPC.execute();
            return true;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }

    }
    public static boolean preparedInsertNPC(String npcName){
        try {
            PS_insertNPC.setString(1, npcName);
            PS_insertNPC.setString(2,"");
            PS_insertNPC.setString(3, "");
            PS_insertNPC.execute();
            return true;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }

    }

    // inserts a Location as a record
    public static boolean insertLocationIntoCurrentDatabase(Location location){
        StringBuilder sqlCommand = new StringBuilder(SQL_insertIntoTable(Location.TABLE_NAME));
        sqlCommand.append("(");
        sqlCommand.append("null ,");// ID gets added automatically, as it's the primary key
        sqlCommand.append(embedInSingleQuotes(location.getName()));
        sqlCommand.append(",");
        sqlCommand.append(embedInSingleQuotes(location.getDescription()));
        sqlCommand.append(")");
        return executeStatement(sqlCommand.toString());
    }
    public static boolean preparedInsertLocation(Location location){
        try {
            PS_insertLocation.setString(1, location.getName());
            PS_insertLocation.setString(2, location.getDescription());
            PS_insertLocation.execute();
            return true;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }

    }
    public static boolean preparedInsertLocation(String locName){
        try {
            PS_insertLocation.setString(1, locName);
            PS_insertLocation.setString(2, "");
            PS_insertLocation.execute();
            return true;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }

    }





    // * RECORD UPDATING *
    public static boolean preparedUpdateNPC(NPC npc){
        try {
            PS_updateNPCWhereIDEquals.setString(1, npc.getName());
            PS_updateNPCWhereIDEquals.setInt(2, npc.getLocationID());
            PS_updateNPCWhereIDEquals.setString(3, npc.getDescription());
            PS_updateNPCWhereIDEquals.setInt(4, npc.getId());
            PS_updateNPCWhereIDEquals.execute();
            return true;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }

    }
    public static boolean preparedUpdateLocation(Location location){
        try {
            PS_updateLocationWhereIDEquals.setString(1, location.getName());
            PS_updateLocationWhereIDEquals.setString(2, location.getDescription());
            PS_updateLocationWhereIDEquals.setInt(3, location.getId());
            PS_updateLocationWhereIDEquals.execute();
            return true;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }

    }








    // ** QUERY METHODS **


    // Converts result set into a list of NPCs
    private static ArrayList<NPC> resultSetToNPCList(ResultSet resultSet) throws SQLException{
        ArrayList<NPC> npcList = new ArrayList<>();
        while (resultSet.next()){
            NPC npc = new NPC(resultSet.getString(NPC.INDEX_NAME));
            npc.setId(resultSet.getInt(NPC.INDEX_ID));
            npc.setLocationID(resultSet.getInt(NPC.INDEX_LOCATION_ID));
            npc.setDescription(resultSet.getString(NPC.INDEX_DESCRIPTION));
            npcList.add(npc);
        }
        return npcList;
    }

    // Query ALL NPCs
    public static ArrayList<NPC> queryAllNPCs(){
        try(Statement statement = currentCampaignDatabase.createStatement()){
            statement.execute(SELECT_ALL_FROM + NPC.TABLE_NAME);
            ResultSet resultSet =  statement.getResultSet();
            return resultSetToNPCList(resultSet);
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    // Query NPCs with name like...
    public static ArrayList<NPC> queryNPCsWhereNameLike(String namePart){
        try(Statement statement = currentCampaignDatabase.createStatement()){
            statement.execute(SELECT_ALL_FROM + NPC.TABLE_NAME + SQL_whereNameLike(namePart));
            ResultSet resultSet =  statement.getResultSet();
            return resultSetToNPCList(resultSet);
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }
    public static ArrayList<NPC> preparedQueryNPCsWhereNameLike(String namePart){
        try{
            namePart = embedInPercentSymbols(namePart);
            PS_queryAllFromNPCsWithNameLike.setString(1, namePart);
            ResultSet resultSet = PS_queryAllFromNPCsWithNameLike.executeQuery();
            return resultSetToNPCList(resultSet);
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    // Query NPCs with locationID that equals...
    public static ArrayList<NPC> queryNPCsOfLocationID(int locationID){
        try(Statement statement = currentCampaignDatabase.createStatement()){
            statement.execute(SELECT_ALL_FROM + NPC.TABLE_NAME + SQL_whereLocationIDEquals(locationID));
            ResultSet resultSet =  statement.getResultSet();
            return resultSetToNPCList(resultSet);
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    // Query NPCs with location name like
    public static ArrayList<NPC> queryNPCsOfLocationNameLike(String namePart){
        try(Statement statement = currentCampaignDatabase.createStatement()){
            statement.execute(SELECT_ALL_FROM + NPC.TABLE_NAME + NPCsWhereLocationNameLike(namePart));
            ResultSet resultSet =  statement.getResultSet();
            return resultSetToNPCList(resultSet);
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }
    public static ArrayList<NPC> preparedQueryNPCsOfLocationNameLike(String namePart){
        try{
            namePart = embedInPercentSymbols(namePart);
            PS_queryAllFromNPCsWhereLocationNameLike.setString(1, namePart);
            ResultSet resultSet = PS_queryAllFromNPCsWhereLocationNameLike.executeQuery();
            return resultSetToNPCList(resultSet);
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    // Query SINGLE NPC of UNIQUE ID
    public static NPC queryNPCOfID(int ID){
        try(Statement statement = currentCampaignDatabase.createStatement()){
            statement.execute(SELECT_ALL_FROM + NPC.TABLE_NAME + SQL_whereIDEquals(ID));
            ResultSet resultSet =  statement.getResultSet();
            return resultSetToNPCList(resultSet).get(0); // list of size() == 1 expected
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }








    // Converts result set into a list of Locations
    private static ArrayList<Location> resultSetToLocationList(ResultSet resultSet) throws SQLException{
        ArrayList<Location> locationList = new ArrayList<>();
        while (resultSet.next()){
            Location location = new Location(resultSet.getString(Location.INDEX_NAME));
            location.setId(resultSet.getInt(Location.INDEX_ID));
            location.setDescription(resultSet.getString(Location.INDEX_DESCRIPTION));
            locationList.add(location);
        }
        return locationList;
    }

    // Query ALL Locations
    public static ArrayList<Location> queryAllLocations(){
        try(Statement statement = currentCampaignDatabase.createStatement()){
            statement.execute(SELECT_ALL_FROM + Location.TABLE_NAME);
            ResultSet resultSet =  statement.getResultSet();
            return resultSetToLocationList(resultSet);
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    // Query Locations with name like...
    public static ArrayList<Location> queryLocationsWhereNameLike(String namePart){
        try(Statement statement = currentCampaignDatabase.createStatement()){
            statement.execute(SELECT_ALL_FROM + Location.TABLE_NAME + SQL_whereNameLike(namePart));
            ResultSet resultSet =  statement.getResultSet();
            return resultSetToLocationList(resultSet);
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }
    public static ArrayList<Location> preparedQueryLocationsWhereNameLike(String namePart){
        try{
            namePart = embedInPercentSymbols(namePart);
            PS_queryAllFromLocationsWithNameLike.setString(1, namePart);
            ResultSet resultSet = PS_queryAllFromLocationsWithNameLike.executeQuery();
            return resultSetToLocationList(resultSet);
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    // Query SINGLE LOCATION of UNIQUE ID
    public static Location queryLocationOfID(int ID){
        try(Statement statement = currentCampaignDatabase.createStatement()){
            statement.execute(SELECT_ALL_FROM + Location.TABLE_NAME + SQL_whereIDEquals(ID));
            ResultSet resultSet =  statement.getResultSet();
            return resultSetToLocationList(resultSet).get(0); // list of size() == 1 expected
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    // Query all location names
    public static ArrayList<String> queryLocationNames(){
        try(Statement statement = currentCampaignDatabase.createStatement()){
            statement.execute("SELECT " + Location.HEADER_NAME + " FROM " + Location.TABLE_NAME);
            ResultSet resultSet =  statement.getResultSet();

            ArrayList<String> names = new ArrayList<>();
            while (resultSet.next()){
                names.add(resultSet.getString(Location.HEADER_NAME));
            }
            return names;

        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }

    }

    // Location name to Location ID
    public static int locationNameToID(String locationName){
        try(Statement statement = currentCampaignDatabase.createStatement()){
            statement.execute("SELECT " + Location.HEADER_ID + " FROM " + Location.TABLE_NAME +
                    " WHERE " + Location.HEADER_NAME + " = " + embedInSingleQuotes(locationName));
            ResultSet resultSet =  statement.getResultSet();
            if(!resultSet.isBeforeFirst()){
                return 0;
            }
            return resultSet.getInt(Location.HEADER_ID);
        } catch (SQLException e){
            e.printStackTrace();
            return 0;
        }
    }

    // Location name to Location ID
    public static String locationIDToName(int ID){
        try(Statement statement = currentCampaignDatabase.createStatement()){
            statement.execute("SELECT " + Location.HEADER_NAME + " FROM " + Location.TABLE_NAME + WHERE_ID_EQUALS + ID);
            ResultSet resultSet =  statement.getResultSet();
            if(!resultSet.isBeforeFirst()){
                return "unknown";
            }
            return resultSet.getString(Location.HEADER_NAME);
        } catch (SQLException e){
            e.printStackTrace();
            return "unknown";
        }
    }







}
