import java.awt.*;

import javax.swing.*;
import javax.swing.JPanel;

import javax.imageio.*;

/**
 * Write a description of class Display here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Display extends JPanel
{
    Image wholeNote = null;
    Image grandStaff = null;
    Image shadowDown = null;
    Image shadowUp = null;
    
    //Value added to y-coordinate of note
    int whichNote1, whichNote2, whichNote3, whichNote4;
    
    /**
     * constructor
     * 
     * @param width  the width of the display
     * @param height the height of the display
     */
    public Display(int width, int height)
    {
        setPreferredSize(new Dimension(width, height));
    }
    
    /*
     * Paint Component
     * 
     * @param g graphics object
     */
    public void paintComponent(Graphics g) 
    {
        super.paintComponent(g);
        
        try
        {
            grandStaff = ImageIO.read(this.getClass().getResourceAsStream("GrandStaff.png"));
            wholeNote = ImageIO.read(this.getClass().getResourceAsStream("note.png"));
            shadowDown = ImageIO.read(this.getClass().getResourceAsStream("shadDown.png"));
            shadowUp = ImageIO.read(this.getClass().getResourceAsStream("shadUp.png"));
        } catch (Exception e) {}
        
        //creates square for background color
        Color c = new Color(255, 180, 182);
        g.setColor(c);
        g.fillRect(0, 0, 1000, 300);
        
        //draws staff
        g.drawImage(grandStaff, 200, 10, null);
        
        //makes shadow so it looks nicer
        g.drawImage(shadowDown, 0, -2, null);
        g.drawImage(shadowUp, 0, 189, null);
        
        //draws notes
        g.drawImage(wholeNote, 275, 62 + whichNote1, null);
 
        g.drawImage(wholeNote, 365, 62 + whichNote2, null);
        
        g.drawImage(wholeNote, 465, 62 + whichNote3, null);
        
        g.drawImage(wholeNote, 560, 62 + whichNote4, null);
    }
    
    /*
     * Draw First
     * 
     * Draws the note in the first measure
     * 
     * @param int yNote value to add to y-coordinate of note
     */
    public void drawFirst(int yNote)
    {
        whichNote1 = yNote;
    }
    
    /*
     * Draw Second
     * 
     * Draws the note in the second measure
     * 
     * @param int yNote value to add to y-coordinate of note
     */
    public void drawSecond(int yNote)
    {
        whichNote2 = yNote;
    }
    
    /*
     * Draw Third
     * 
     * Draws the note in the third measure
     * 
     * @param int yNote value to add to y-coordinate of note
     */
    public void drawThird(int yNote)
    {
        whichNote3 = yNote;
    }
    
    /*
     * Draw Fourth
     * 
     * Draws the note in the fourth measure
     * 
     * @param int yNote value to add to y-coordinate of note
     */
    public void drawFourth(int yNote)
    {
        whichNote4 = yNote;
    }
}
