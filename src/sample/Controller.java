package sample;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import sample.Animations.CantBeEmptyAnimation;
import sample.Data.DataObject;
import sample.Data.DataSource;
import sample.Data.Location;
import sample.Data.NPC;

import java.util.ArrayList;
import java.util.Comparator;

public class Controller {


    // LEFT
    @FXML
    private ListView<DataObject> dataObjectListView;


    // CENTER
    @FXML
    private TextField nameTextField;
    @FXML
    private ComboBox<String> locationComboBox;
    @FXML
    private TextArea descriptionTextArea;
    @FXML
    private AppButton updateOrAddRecordAppButton;


    // RIGHT
    @FXML
    private Label dataTypeLabel;
    @FXML
    private AppButton showAllAppButton;
    @FXML
    private TextField searchTextField;
    @FXML
    private AppButton searchByLocationAppButton;
    @FXML
    private AppButton searchByNameAppButton;




    private boolean browsingRecords = true; // default value
    private boolean addingRecords;
    private boolean showingNPCs = true; // default value
    private boolean showingLocations;

    private void setToBrowsing(){
        browsingRecords = true;
        addingRecords = false;
        updateOrAddRecordAppButton.setText("UPDATE RECORD");
        dataObjectListView.setVisible(true);
        dataTypeLabel.setVisible(true);
        showAllAppButton.setVisible(true);
        searchTextField.setVisible(true);
        searchByNameAppButton.setVisible(true);
        searchByLocationAppButton.setVisible(true);
    }
    private void setToAdding(){
        browsingRecords = false;
        addingRecords = true;
        //updateOrAddRecordAppButton.setText("ADD RECORD"); // in higher methods
        dataObjectListView.setVisible(false);
        dataTypeLabel.setVisible(false);
        showAllAppButton.setVisible(false);
        searchTextField.setVisible(false);
        searchByNameAppButton.setVisible(false);
        searchByLocationAppButton.setVisible(false);
        if(showingNPCs){
            addValuesToComboBox();
        }
    }
    private void setToNPCs(){
        showingNPCs = true;
        showingLocations = false;
        disableLocationTextField(false);
        searchByLocationAppButton.setVisible(true);
        dataTypeLabel.setText("NPCs");
    }
    private void setToLocations(){
        showingNPCs = false;
        showingLocations = true;
        disableLocationTextField(true);
        if(!addingRecords){
            searchByLocationAppButton.setVisible(false);
            dataTypeLabel.setText("Locations");
        }
    }

    @FXML
    public void switchToBrowsingNPCs(){
        reset();
        setToNPCs();
        setToBrowsing();
    }
    @FXML
    public void switchToBrowsingLocations(){
        reset();
        setToLocations();
        setToBrowsing();
    }
    @FXML
    public void switchToAddingNPCs(){
        reset();
        setToNPCs();
        setToAdding();
        updateOrAddRecordAppButton.setText("ADD THIS NPC");
    }
    @FXML
    public void switchToAddingLocations(){
        reset();
        setToLocations();
        setToAdding();
        updateOrAddRecordAppButton.setText("ADD THIS LOCATION");
    }




    private void reset(){
        clearTextFields();
        dataObjectListView.setDisable(true);
        dataObjectListView.getItems().clear();
    }
    private void clearTextFields(){
        nameTextField.setText(null);
        locationComboBox.setValue(null);
        descriptionTextArea.setText(null);
    }
    private void disableLocationTextField(boolean state){
        if(state){
            locationComboBox.setDisable(true);
            locationComboBox.setOpacity(0.2);
        } else {
            locationComboBox.setDisable(false);
            locationComboBox.setOpacity(1);
        }
    }




    @FXML
    public void showAll(){
        reset();
        dataObjectListView.setDisable(false);
        if(showingNPCs){
            showAllNPCs();
        } else if(showingLocations){
            showAllLocations();
        }

    }
    private void showAllNPCs(){
        dataObjectListView.getItems().setAll(DataSource.queryAllNPCs());
    }
    private void showAllLocations(){
        dataObjectListView.getItems().setAll(DataSource.queryAllLocations());
    }




    @FXML
    public void searchByName(){
        reset();
        dataObjectListView.setDisable(false);
        if(showingNPCs){
            searchNPCsByName();
        } else if(showingLocations){
            searchLocationsByName();
        }
    }
    private void searchNPCsByName(){
        dataObjectListView.getItems()
                .setAll(DataSource.preparedQueryNPCsWhereNameLike(searchTextField.getText()));
    }
    private void searchLocationsByName(){
        dataObjectListView.getItems()
                .setAll(DataSource.preparedQueryLocationsWhereNameLike(searchTextField.getText()));
    }




    @FXML
    public void searchByLocation(){
        reset();
        dataObjectListView.setDisable(false);
        if(showingNPCs){
            searchNPCsByLocation();
        }
    }
    private void searchNPCsByLocation(){
        dataObjectListView.getItems()
                .setAll(DataSource.preparedQueryNPCsOfLocationNameLike(searchTextField.getText()));
    }



    private void addValuesToComboBox(){
        ArrayList<String> names = DataSource.queryLocationNames();
        names.sort(Comparator.naturalOrder());
        names.add("unknown");
        locationComboBox.getItems().setAll(names);
    }
    @FXML
    public void recordToDisplay(){

        // returns if no item chosen (possible after the list view has been cleared)
        if(dataObjectListView.getSelectionModel().getSelectedItem()==null){
            return;
        }

        nameTextField.setText(dataObjectListView.getSelectionModel().getSelectedItem().getName());

        if(dataObjectListView.getSelectionModel().getSelectedItem() instanceof NPC){

            addValuesToComboBox();
            String defaultValue = DataSource.locationIDToName(((NPC)dataObjectListView.getSelectionModel().getSelectedItem()).getLocationID());
            locationComboBox.setValue(defaultValue);

            descriptionTextArea.setText(((NPC) dataObjectListView.getSelectionModel().getSelectedItem()).getDescription());

        } else if(dataObjectListView.getSelectionModel().getSelectedItem() instanceof Location) {
            descriptionTextArea.setText(((Location) dataObjectListView.getSelectionModel().getSelectedItem()).getDescription());
        }

    }




    @FXML
    public void updateOrAddRecord(){

        // start animation and breaks the method if no item selected
        if(browsingRecords && dataObjectListView.getSelectionModel().getSelectedItem() == null){
            new CantBeEmptyAnimation(updateOrAddRecordAppButton).start();
            return;
        }

        // start animation and breaks the method if nameTextField content is invalid
        if(nameTextField.getText() == null || nameTextField.getText().trim().isEmpty()){
            new CantBeEmptyAnimation(nameTextField).start();
            return;
        }

        // else continue
        if(browsingRecords){
            if(showingNPCs){
                updateNPCRecord();
            } else if(showingLocations){
                updateLocationRecord();
            }
        } else if(addingRecords){
            if(showingNPCs){
                addNewNpc();
            } else if(showingLocations){
                addNewLocation();
            }
        }

    }
    private void updateNPCRecord(){
        NPC updatedNPC = (NPC)dataObjectListView.getSelectionModel().getSelectedItem();
        updatedNPC.setName(nameTextField.getText());
        int locationID = DataSource.locationNameToID(locationComboBox.getValue());
        updatedNPC.setLocationID(locationID);
        updatedNPC.setDescription(descriptionTextArea.getText());
        DataSource.preparedUpdateNPC(updatedNPC);
    }
    private void updateLocationRecord(){
        Location updatedLocation = (Location)dataObjectListView.getSelectionModel().getSelectedItem();
        updatedLocation.setName(nameTextField.getText());
        updatedLocation.setDescription(descriptionTextArea.getText());
        DataSource.preparedUpdateLocation(updatedLocation);
    }
    private void addNewNpc(){
        String name  = nameTextField.getText();
        int locationID = DataSource.locationNameToID(locationComboBox.getValue());
        String description = descriptionTextArea.getText();
        NPC npc = new NPC(name, locationID, description);
        DataSource.preparedInsertNPC(npc);
    }
    private void addNewLocation(){
        String name  = nameTextField.getText();
        String description = descriptionTextArea.getText();
        Location location = new Location(name, description);
        DataSource.preparedInsertLocation(location);
    }


}
