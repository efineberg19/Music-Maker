import java.awt.*;
import javax.swing.*;

import javax.imageio.*;

/**
 * Write a description of class Note here.
 *
 * @author Beth Fineberg
 * @version 1.0
 */
public class Notes
{
    //allows for two voices to be played at once
    String v1, v2;
    
    Image wholeNote = null;
    
    boolean visible = false;
    
    /**
     * constructor
     * 
     * @param note  the note played
     * 
     * NB: I called this Notes and not Note, because JFugue has a class 
     * called Note. Do no mix up names when using!
     */
    public Notes(String voice1, String voice2)
    {
        v1 = voice1;
        v2 = voice2;
        
        try
        {
            wholeNote = ImageIO.read(this.getClass().getResourceAsStream("note.png"));
        } catch (Exception e) {}
    }
    
    /*
     * Get V1
     * 
     * @return String note name of note assigned to object
     */
    public String getV1()
    {
        return v1;
    }
    
    /*
     * Get V2
     * 
     * @return String note name of note assigned to object
     */
    public String getV2()
    {
        return v2;
    }
}
