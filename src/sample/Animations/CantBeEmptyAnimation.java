package sample.Animations;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

public class CantBeEmptyAnimation extends AnimationTimer {

    private final Parent animatedParent;
    private final DropShadow DROP_SHADOW = new DropShadow(20,Color.RED);

    private final double TR_STEP = 0.7;
    private final int ANIM_TIMER = 2;
    private final double[] ANIM_RANGES = {1,2,1,0,-1,-2,-1,0,1,2,1,0,-1,-2,-1,0};


    private int timer = 0;
    private int rotation = 0;

    @Override
    public void handle(long l) {

        if(timer % ANIM_TIMER == 0){
            animatedParent.setTranslateX(TR_STEP*ANIM_RANGES[rotation]);
            rotation++;
            timer = 0;
        }

        if(rotation == ANIM_RANGES.length){
            animatedParent.setEffect(null);
            stop();
        }

        timer++;

    }

    public CantBeEmptyAnimation(Parent parent){
        this.animatedParent = parent;
        animatedParent.setEffect(DROP_SHADOW);
    }
}
