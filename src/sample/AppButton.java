package sample;

import javafx.scene.control.Button;

public class AppButton extends Button{


    private void mouseHoverOn(){
        setScaleX(1.05);
        setScaleY(1.05);
    }

    private void mouseHoverOff(){
        setScaleX(1.00);
        setScaleY(1.00);
    }


    public AppButton(){
        setPrefWidth(150);
        setOnMouseEntered(e->mouseHoverOn());
        setOnMouseExited(e->mouseHoverOff());
        setId("appButton"); // sets ID (so no need to inline it in fxml. t can now be called by .css file
    }
}
