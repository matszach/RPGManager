package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.Data.DataSource;

public class Main extends Application {


    @Override
    public void init() throws Exception {
        super.init();
        DataSource.setupConnectionToDatabase("test");
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("mainScene.fxml"));
        primaryStage.setTitle("RPG Manager");
        primaryStage.setScene(new Scene(root, 650, 450));
        primaryStage.show();



        // TEST

//        DataSource.insertNPCIntoCurrentDatabase(new NPC("Rex",1,"Likes dogs"));
//        DataSource.insertLocationIntoCurrentDatabase(new Location("Forrest", "Cool place"));

//        DataSource.queryAllLocations().stream().forEach(loc-> System.out.println(loc.getName()));
//        DataSource.queryAllNPCs().stream().forEach(npc-> System.out.println(npc.getName()));


//        DataSource.queryNPCsWhereNameLike("e\' OR 1=1 OR \'").stream().forEach(n-> System.out.println(n.getName()));
//        DataSource.queryLocationsWhereNameLike("c").stream().forEach(l-> System.out.println(l.getName()));

//        DataSource.queryNPCsOfLocationID(2).stream().forEach(n-> System.out.println(n.getName()));
//        DataSource.queryNPCsOfLocationNameLike("Q").stream().forEach(n-> System.out.println(n.getName()));

//        DataSource.preparedQueryNPCsWhereNameLike("e").stream().forEach(n-> System.out.println(n.getName()));
//        DataSource.preparedQueryNPCsOfLocationNameLike("e").stream().forEach(n-> System.out.println(n.getName()));
//        DataSource.preparedQueryLocationsWhereNameLike("r").stream().forEach(n-> System.out.println(n.getName()));


//        DataSource.preparedInsertNPC(new NPC("Mello", 1, "likes chocolate"));
//        DataSource.preparedInsertLocation(new Location("Temple", "temple to the Sun God"));

//        DataSource.preparedInsertNPC("Mike");

    }


    @Override
    public void stop() throws Exception {
        super.stop();
        DataSource.stopConnectionToDatabase();
    }

    public static void main(String[] args) {
        launch(args);

    }
}
