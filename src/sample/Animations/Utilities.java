package sample.Animations;

public class Utilities {

    public static void threadWait(int ms){
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

}
