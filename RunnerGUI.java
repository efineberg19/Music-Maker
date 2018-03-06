import java.awt.Color.*;

/**
 * Creates window and starts the timer. I've really worked to make the whole project
 * as simple as possible.
 *
 * @author Beth Fineberg
 * @version 1.0
 */
public class RunnerGUI
{
    public static void main(String[] args)
    {
        //the JFrame size:
        int windowWidth = 500;
        int windowHeight = 200;
        
        Window win = new Window(windowWidth, windowHeight);
        
        //starts timer in order to redraw note images
        win.startTimer();
    }
}
