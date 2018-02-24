import java.awt.*;
import java.awt.event.*;
import java.awt.Color.*;

import javax.imageio.*;

import javax.swing.*;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JOptionPane;

import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;
import org.jfugue.theory.Chord;
import org.jfugue.theory.ChordProgression;
import org.jfugue.theory.Note;
import org.jfugue.theory.Intervals;

/**
 * Creates visuals and user interface to choose notes. Handles which notes to
 * play based on different modes. Also offers explanations for modes and 
 * instructions on how to use program. 
 *
 * @author Beth Fineberg
 * @version 1.0
 */

public class Window extends JFrame implements ActionListener
{
    JPanel displayPanel, bottomPanel, masterPanel, topPanel;

    JButton submit, notePickLabel, modePickLabel, instructionsLabel;
    
    JComboBox<String> noteSelector1, noteSelector2, noteSelector3, noteSelector4, 
        modePick, explanationPick;

    //size of window
    int WIDTH, HEIGHT;
    
    //height of note image on staff relative to middle C
    int staffHeight1, staffHeight2, staffHeight3, staffHeight4;
    
    //index value of dropdown menu choices
    int noteSelected = 0;
    int modeSelected = 0;
    int explanationSelected = 0;

    //modes to pick from
    boolean chordProgressionMode = false;
    boolean accompanimentMode = false;
    boolean jazzMode = false;
    boolean atonalMode = false;
    
    //determines if notes are visible
    boolean firstNote, secondNote, thirdNote, fourthNote;
    
    Image wholeNote = null;

    //notes played, changes based on user's choices
    Notes first,second, third, fourth;
    
    //music theory names for each tone (made more sense to me than numerals)
    String tonic = "I";
    String supertonic = "ii";
    String mediant = "iii";
    String subdominant = "IV";
    String dominant = "V";
    String submediant = "vi";
    String leadingTone = "vii";
    
    //all possible notes
    String possibleNotes[] = {"A", "A#", "B", "C", "C#", "D", "D#", "E", "F",
        "F#", "G", "G#"};
    
    Display display;
    
    Timer t;
    
    /**
     * Constructor for Window
     * 
     * @param width  the width of the window
     * @param height the height of the window
     */
    public Window(int width, int height)
    {
        masterPanel = (JPanel) this.getContentPane(); 
        masterPanel.setLayout(new BorderLayout());
        
        this.WIDTH  = width;
        this.HEIGHT = height;
        

        //makes image of grand staff usable
        try
        {
            wholeNote = ImageIO.read(this.getClass().getResourceAsStream("note.png"));
        } catch (Exception e) {}

        this.setSize(new Dimension(900, 300)); 

        //options for JComboBoxes
        
        //solfege is an easier system to understand for non-musicians
        String[] solfege      = {"do", "re", "mi", "fa", "so", "la", "ti"};
        
        String[] modes        = {"Chord Progression", "Accompaniment", "Jazz", 
            "Atonal"};
        
        String[] explanations = {"Explanations for Modes:", "Chord Progression",
            "Accompaniment", "Jazz", "Atonal"};

        //lets user find explanation for modes
        explanationPick = new JComboBox<String>(explanations);  
        explanationPick.setSelectedIndex(0);                 
        explanationPick.addActionListener(this);             
        explanationPick.setActionCommand("explanation");

        //displays instructions in picked
        instructionsLabel = new JButton("Click For Instructions");
        instructionsLabel.addActionListener(this);             
        instructionsLabel.setActionCommand("instructions");
        
        //makes clear purpose of mode options
        modePickLabel = new JButton("Pick a Mode:");

        //lets user pick mode 
        modePick = new JComboBox<String>(modes);  
        modePick.setSelectedIndex(0);                 
        modePick.addActionListener(this);             
        modePick.setActionCommand("mode");

        //makes clear purpose of note options
        notePickLabel = new JButton("Pick a Melody Note:");

        //lets user pick the first note
        noteSelector1 = new JComboBox<String>(solfege);  
        noteSelector1.setSelectedIndex(0);                 
        noteSelector1.addActionListener(this);             
        noteSelector1.setActionCommand("noteSelector1");

        //lets user pick the second note
        noteSelector2 = new JComboBox<String>(solfege);  
        noteSelector2.setSelectedIndex(0);                 
        noteSelector2.addActionListener(this);             
        noteSelector2.setActionCommand("noteSelector2");

        //lets user pick the third note
        noteSelector3 = new JComboBox<String>(solfege);  
        noteSelector3.setSelectedIndex(0);                 
        noteSelector3.addActionListener(this);             
        noteSelector3.setActionCommand("noteSelector3");

        //lets user pick the forth note
        noteSelector4 = new JComboBox<String>(solfege);  
        noteSelector4.setSelectedIndex(0);                 
        noteSelector4.addActionListener(this);             
        noteSelector4.setActionCommand("noteSelector4");

        //plays sound based on mode
        submit = new JButton("Submit");
        submit.addActionListener(this);
        submit.setActionCommand("submit");
        
        //creates a display to draw and repaint notes
        display = new Display(width, height);
        this.add(display, BorderLayout.CENTER);

        bottomPanel = new JPanel();
        bottomPanel.add(notePickLabel);
        bottomPanel.add(noteSelector1);
        bottomPanel.add(noteSelector2);
        bottomPanel.add(noteSelector3);
        bottomPanel.add(noteSelector4);
        bottomPanel.add(submit);

        topPanel = new JPanel();
        topPanel.add(modePickLabel);
        topPanel.add(modePick);
        topPanel.add(instructionsLabel);
        topPanel.add(explanationPick);
        
        //sets color of top panel
        Color n = new Color(132, 211, 179);
        topPanel.setBackground(n);
        
        //sets color of bottom panel
        Color x = new Color(163, 191, 228);
        bottomPanel.setBackground(x);

        masterPanel.add(bottomPanel, BorderLayout.SOUTH);
        masterPanel.add(topPanel, BorderLayout.NORTH);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Music Maker"); 
        this.setVisible(true);
    }
    
    /*
     * Start Timer
     *
     * Creates a Timer object and starts timer
     */
    public void startTimer()
    {
        t = new Timer(1, this); //1 is timer delay
        t.setActionCommand("timerFired");
        t.start();
    }
    
    /*
     * Action Performed
     * 
     * @param e press of a button
     */
    public void actionPerformed (ActionEvent e)
    {
        //tool used that interprets code to play notes
        Player player = new Player();

        if(e.getActionCommand().equals("timerFired"))  //timer has fired
        {
            display.repaint(); //calls paintComponent to redraw everything
            
            //communicates if and how a note's height should be changed
            display.drawFirst(staffHeight1);
            display.drawSecond(staffHeight2);
            display.drawThird(staffHeight3);
            display.drawFourth(staffHeight4);
        }
        
        if(e.getActionCommand() == "mode")
        {
            modeSelected = modePick.getSelectedIndex();  

            switch(modeSelected)
            {
                case 0: //chord progression selected
                {
                    chordProgressionMode = true;
                    accompanimentMode = false;
                    jazzMode = false;
                    atonalMode = false;
                }
                break;

                case 1: //accompaniment selected
                {
                    chordProgressionMode = false;
                    accompanimentMode = true;
                    jazzMode = false;
                    atonalMode = false;
                }
                break;
                
                case 2: //jazz mode selected
                {
                    chordProgressionMode = false;
                    accompanimentMode = false;
                    jazzMode = true;
                    atonalMode = false;
                }
                break;
                
                case 3: //atonal mode selected
                {
                    chordProgressionMode = false;
                    accompanimentMode = false;
                    jazzMode = false;
                    atonalMode = true;
                }
            }
        }
        
        if(e.getActionCommand() == "noteSelector1") //picks first note played
        {
            noteSelected = noteSelector1.getSelectedIndex();  
           
            switch(noteSelected)
            {                   
                case 0:
                {
                    if (chordProgressionMode)
                    {
                        first = new Notes(tonic, null);
                    }
                    else if(accompanimentMode)
                    {
                        first = new Notes("G3+C4", "E5+C6");
                    }
                    else if(jazzMode)
                    {
                        first = new Notes("C5maj7h* + C#5dim7h* + D5min7h*", null);
                    }
                    else if(atonalMode)
                    {
                        first = new Notes(possibleNotes[(int) (Math.random() * 11)] 
                            + "3+" + possibleNotes[(int) (Math.random() * 11)] + "4", 
                            "C5+" + possibleNotes[(int) (Math.random() * 11)] + "6");
                    }
                    
                    staffHeight1 = 0;
                }
                break;

                case 1:
                {
                    if (chordProgressionMode)
                    {
                        first = new Notes(supertonic, null);
                    }
                    else if(accompanimentMode)
                    {
                        first = new Notes("A3+D4", "F5+D6");
                    }
                    else if(jazzMode)
                    {
                        first = new Notes("D5min7h* + D#5dim7h* + E5min7h*", null);
                    }
                    else if(atonalMode)
                    {
                        first = new Notes(possibleNotes[(int) (Math.random() * 11)] 
                            + "3+" + possibleNotes[(int) (Math.random() * 11)] + "4", 
                            "D5+" + possibleNotes[(int) (Math.random() * 11)] + "6");
                    }
                    
                    staffHeight1 = -6;
                }
                break;

                case 2:
                {
                    if (chordProgressionMode)
                    {
                        first = new Notes(mediant, null);
                    }
                    else if(accompanimentMode)
                    {
                        first = new Notes("B3+E4", "G5+E6");
                    }
                    else if(jazzMode)
                    {
                        first = new Notes("Eb5maj7h* + D5min6h* + Eb5dim7h*", null);
                    }
                    else if(atonalMode)
                    {
                        first = new Notes(possibleNotes[(int) (Math.random() * 11)] 
                            + "3+" + possibleNotes[(int) (Math.random() * 11)] + "4", 
                            "E5+" + possibleNotes[(int) (Math.random() * 11)] + "6");
                    }
                    
                    staffHeight1 = -12;
                }
                break;

                case 3:
                {
                    if (chordProgressionMode)
                    {
                        first = new Notes(subdominant, null);
                    }
                    else if(accompanimentMode)
                    {
                        first = new Notes("F3+C4", "A5+F6");
                    }
                    else if(jazzMode)
                    {
                        first = new Notes("F5min6h* + F5maj6h* + G5maj6h*", null);
                    }
                    else if(atonalMode)
                    {
                        first = new Notes(possibleNotes[(int) (Math.random() * 11)] 
                            + "3+" + possibleNotes[(int) (Math.random() * 11)] + "4", 
                            "F5+" + possibleNotes[(int) (Math.random() * 11)] + "6");
                    }
                    
                    staffHeight1 = -18;
                }
                break;

                case 4:
                {
                    if (chordProgressionMode)
                    {
                        first = new Notes(dominant, null);
                    }
                    else if(accompanimentMode)
                    {
                        first = new Notes("D3+G4", "B5+G6");
                    }
                    else if(jazzMode)
                    {
                        first = new Notes("G5maj7h* + E5min7h* + C5maj7h*", null);
                    }
                    else if(atonalMode)
                    {
                        first = new Notes(possibleNotes[(int) (Math.random() * 11)] 
                            + "3+" + possibleNotes[(int) (Math.random() * 11)] + "4", 
                            "G5+" + possibleNotes[(int) (Math.random() * 11)] + "6");
                    }
                    
                    staffHeight1 = -24;
                }
                break;

                case 5:
                {
                    if (chordProgressionMode)
                    {
                        first = new Notes(submediant, null);
                    }
                    else if(accompanimentMode)
                    {
                        first = new Notes("A3+C4", "E5+A6");
                    }
                    else if(jazzMode)
                    {
                        first = new Notes("A5min7h* + D5dom7h* + G5maj7h*", null);
                    }
                    else if(atonalMode)
                    {
                        first = new Notes(possibleNotes[(int) (Math.random() * 11)] 
                            + "3+" + possibleNotes[(int) (Math.random() * 11)] + "4", 
                            "A5+" + possibleNotes[(int) (Math.random() * 11)] + "6");
                    }
                    
                    staffHeight1 = -30;
                }
                break;

                case 6:
                {
                    if (chordProgressionMode)
                    {
                        first = new Notes(leadingTone, null);
                    }
                    else if(accompanimentMode)
                    {
                        first = new Notes("B3+F4", "D5+B6");
                    }
                    else if(jazzMode)
                    {
                        first = new Notes("Bb4maj7h* + B4dim7h* + F5maj7h*", null);
                    }
                    else if(atonalMode)
                    {
                        first = new Notes(possibleNotes[(int) (Math.random() * 11)] 
                            + "3+" + possibleNotes[(int) (Math.random() * 11)] + "4", 
                            "B5+" + possibleNotes[(int) (Math.random() * 11)] + "6");
                    }
                    
                    staffHeight1 = -36;
                }
                break;
            }
        }
        else if(e.getActionCommand() == "noteSelector2") //picks second note played
        {
            noteSelected = noteSelector2.getSelectedIndex();  

            switch(noteSelected)
            {                   
                case 0:
                {
                    if (chordProgressionMode)
                    {
                        second = new Notes(tonic, null);
                    }
                    else if(accompanimentMode)
                    {
                        second = new Notes("G3+C4", "E5+C6");
                    }
                    else if(jazzMode)
                    {
                        second = new Notes("C5maj7h* + C#5dim7h* + D5min7h*", null);
                    }
                    else if(atonalMode)
                    {
                        second = new Notes(possibleNotes[(int) (Math.random() * 11)] 
                            + "3+" + possibleNotes[(int) (Math.random() * 11)] + "4", 
                            "C5+" + possibleNotes[(int) (Math.random() * 11)] + "6");
                    }
                    
                    staffHeight2 = 0;
                }
                break;

                case 1:
                {
                    if (chordProgressionMode)
                    {
                        second = new Notes(supertonic, null);
                    }
                    else if(accompanimentMode)
                    {
                        second = new Notes("A3+D4", "F5+D6");
                    }
                    else if(jazzMode)
                    {
                        second = new Notes("D5min7h* + D#5dim7h* + E5min7h*", null);
                    }
                    else if(atonalMode)
                    {
                        second = new Notes(possibleNotes[(int) (Math.random() * 11)] 
                            + "3+" + possibleNotes[(int) (Math.random() * 11)] + "4", 
                            "D5+" + possibleNotes[(int) (Math.random() * 11)] + "6");
                    }
                    
                    staffHeight2 = -6;
                }
                break;

                case 2:
                {
                    if (chordProgressionMode)
                    {
                        second = new Notes(mediant, null);
                    }
                    else if(accompanimentMode)
                    {
                        second = new Notes("B3+E4", "G5+E6");
                    }
                    else if(jazzMode)
                    {
                        second = new Notes("Eb5maj7h* + D5min6h* + Eb5dim7h*", null);
                    }
                    else if(atonalMode)
                    {
                        second = new Notes(possibleNotes[(int) (Math.random() * 11)] 
                            + "3+" + possibleNotes[(int) (Math.random() * 11)] + "4", 
                            "E5+" + possibleNotes[(int) (Math.random() * 11)] + "6");
                    }
                    
                    staffHeight2 = -12;
                }
                break;

                case 3:
                {
                    if (chordProgressionMode)
                    {
                        second = new Notes(subdominant, null);
                    }
                    else if(accompanimentMode)
                    {
                        second = new Notes("F3+C4", "A5+F6");
                    }
                    else if(jazzMode)
                    {
                        second = new Notes("F5min6h* + F5maj6h* + G5maj6h*", null);
                    }
                    else if(atonalMode)
                    {
                        second = new Notes(possibleNotes[(int) (Math.random() * 11)] 
                            + "3+" + possibleNotes[(int) (Math.random() * 11)] + "4", 
                            "F5+" + possibleNotes[(int) (Math.random() * 11)] + "6");
                    }
                    
                    staffHeight2 = -18;
                }
                break;

                case 4:
                {
                    if (chordProgressionMode)
                    {
                        second = new Notes(dominant, null);
                    }
                    else if(accompanimentMode)
                    {
                        second = new Notes("D3+G4", "B5+G6");
                    }
                    else if(jazzMode)
                    {
                        second = new Notes("G5maj7h* + E5min7h* + C5maj7h*", null);
                    }
                    else if(atonalMode)
                    {
                        second = new Notes(possibleNotes[(int) (Math.random() * 11)] 
                            + "3+" + possibleNotes[(int) (Math.random() * 11)] + "4", 
                            "G5+" + possibleNotes[(int) (Math.random() * 11)] + "6");
                    }
                    
                    staffHeight2 = -24;
                }
                break;

                case 5:
                {
                    if (chordProgressionMode)
                    {
                        second = new Notes(submediant, null);
                    }
                    else if(accompanimentMode)
                    {
                        second = new Notes("A3+C4", "E5+A6");
                    }
                    else if(jazzMode)
                    {
                        second = new Notes("A5min7h* + D5dom7h* + G5maj7h*", null);
                    }
                    else if(atonalMode)
                    {
                        second = new Notes(possibleNotes[(int) (Math.random() * 11)] 
                            + "3+" + possibleNotes[(int) (Math.random() * 11)] + "4", 
                            "A5+" + possibleNotes[(int) (Math.random() * 11)] + "6");
                    }
                    
                    staffHeight2 = -30;
                }
                break;

                case 6:
                {
                    if (chordProgressionMode)
                    {
                        second = new Notes(leadingTone, null);
                    }
                    else if(accompanimentMode)
                    {
                        second = new Notes("B3+F4", "D5+B6");
                    }
                    else if(jazzMode)
                    {
                        second = new Notes("Bb4maj7h* + B4dim7h* + F5maj7h*", null);
                    }
                    else if(atonalMode)
                    {
                        second = new Notes(possibleNotes[(int) (Math.random() * 11)] 
                            + "3+" + possibleNotes[(int) (Math.random() * 11)] + "4", 
                            "B5+" + possibleNotes[(int) (Math.random() * 11)] + "6");
                    }
                    
                    staffHeight2 = -36;
                }
                break;
            }
        }
        else if(e.getActionCommand() == "noteSelector3") //picks third note played
        {
            noteSelected = noteSelector3.getSelectedIndex();  

            switch(noteSelected)
            {                   
                case 0:
                {
                    if (chordProgressionMode)
                    {
                        third = new Notes(tonic, null);
                    }
                    else if(accompanimentMode)
                    {
                        third = new Notes("G3+C4", "E5+C6");
                    }
                    else if(jazzMode)
                    {
                        third = new Notes("C5maj7h* + C#5dim7h* + D5min7h*", null);
                    }
                    else if(atonalMode)
                    {
                        third = new Notes(possibleNotes[(int) (Math.random() * 11)] 
                            + "3+" + possibleNotes[(int) (Math.random() * 11)] + "4", 
                            "C5+" + possibleNotes[(int) (Math.random() * 11)] + "6");
                    }
                    
                    staffHeight3 = 0;
                }
                break;

                case 1:
                {
                    if (chordProgressionMode)
                    {
                        third = new Notes(supertonic, null);
                    }
                    else if(accompanimentMode)
                    {
                        third = new Notes("A3+D4", "F5+D6");
                    }
                    else if(jazzMode)
                    {
                        third = new Notes("D5min7h* + D#5dim7h* + E5min7h*", null);
                    }
                    else if(atonalMode)
                    {
                        third = new Notes(possibleNotes[(int) (Math.random() * 11)] 
                            + "3+" + possibleNotes[(int) (Math.random() * 11)] + "4", 
                            "D5+" + possibleNotes[(int) (Math.random() * 11)] + "6");
                    }
                    
                    staffHeight3 = -6;
                }
                break;

                case 2:
                {
                    if (chordProgressionMode)
                    {
                        third = new Notes(mediant, null);
                    }
                    else if(accompanimentMode)
                    {
                        third = new Notes("B3+E4", "G5+E6");
                    }
                    else if(jazzMode)
                    {
                        third = new Notes("Eb5maj7h* + D5min6h* + Eb5dim7h*", null);
                    }
                    else if(atonalMode)
                    {
                        third = new Notes(possibleNotes[(int) (Math.random() * 11)] 
                            + "3+" + possibleNotes[(int) (Math.random() * 11)] + "4", 
                            "E5+" + possibleNotes[(int) (Math.random() * 11)] + "6");
                    }
                    
                    staffHeight3 = -12;
                }
                break;

                case 3:
                {
                    if (chordProgressionMode)
                    {
                        third = new Notes(subdominant, null);
                    }
                    else if(accompanimentMode)
                    {
                        third = new Notes("F3+C4", "A5+F6");
                    }
                    else if(jazzMode)
                    {
                        third = new Notes("F5min6h* + F5maj6h* + G5maj6h*", null);
                    }
                    else if(atonalMode)
                    {
                        third = new Notes(possibleNotes[(int) (Math.random() * 11)] 
                            + "3+" + possibleNotes[(int) (Math.random() * 11)] + "4", 
                            "F5+" + possibleNotes[(int) (Math.random() * 11)] + "6");
                    }
                    
                    staffHeight3 = -18;
                }
                break;

                case 4:
                {
                    if (chordProgressionMode)
                    {
                        third = new Notes(dominant, null);
                    }
                    else if(accompanimentMode)
                    {
                        third = new Notes("D3+G4", "B5+G6");
                    }
                    else if(jazzMode)
                    {
                        third = new Notes("G5maj7h* + E5min7h* + C5maj7h*", null);
                    }
                    else if(atonalMode)
                    {
                        third = new Notes(possibleNotes[(int) (Math.random() * 11)] 
                            + "3+" + possibleNotes[(int) (Math.random() * 11)] + "4", 
                            "G5+" + possibleNotes[(int) (Math.random() * 11)] + "6");
                    }
                    
                    staffHeight3 = -24;
                }
                break;

                case 5:
                {
                    if (chordProgressionMode)
                    {
                        third = new Notes(submediant, null);
                    }
                    else if(accompanimentMode)
                    {
                        third = new Notes("A3+C4", "E5+A6");
                    }
                    else if(jazzMode)
                    {
                        third = new Notes("A5min7h* + D5dom7h* + G5maj7h*", null);
                    }
                    else if(atonalMode)
                    {
                        third = new Notes(possibleNotes[(int) (Math.random() * 11)] 
                            + "3+" + possibleNotes[(int) (Math.random() * 11)] + "4", 
                            "A5+" + possibleNotes[(int) (Math.random() * 11)] + "6");
                    }
                    
                    staffHeight3 = -30;
                }
                break;

                case 6:
                {
                    if (chordProgressionMode)
                    {
                        third = new Notes(leadingTone, null);
                    }
                    else if(accompanimentMode)
                    {
                        third = new Notes("B3+F4", "D5+B6");
                    }
                    else if(jazzMode)
                    {
                        third = new Notes("Bb4maj7h* + B4dim7h* + F5maj7h*", null);
                    }
                    else if(atonalMode)
                    {
                        third = new Notes(possibleNotes[(int) (Math.random() * 11)] 
                            + "3+" + possibleNotes[(int) (Math.random() * 11)] + "4", 
                            "B5+" + possibleNotes[(int) (Math.random() * 11)] + "6");
                    }
                    
                    staffHeight3 = -36;
                }
                break;
            }
        }
        else if(e.getActionCommand() == "noteSelector4") //picks fourth note played
        {
            noteSelected = noteSelector4.getSelectedIndex();  

            switch(noteSelected)
            {
                case 0:
                {
                    if (chordProgressionMode)
                    {
                        fourth = new Notes(tonic, null);
                    }
                    else if(accompanimentMode)
                    {
                        fourth = new Notes("G3+C4", "E5+C6");
                    }
                    else if(jazzMode)
                    {
                        fourth = new Notes("C5maj7h* + C#5dim7h* + D5min7h*", null);
                    }
                    else if(atonalMode)
                    {
                        fourth = new Notes(possibleNotes[(int) (Math.random() * 11)] 
                            + "3+" + possibleNotes[(int) (Math.random() * 11)] + "4", 
                            "C5+" + possibleNotes[(int) (Math.random() * 11)] + "6");
                    }
                    
                    staffHeight4 = 0;
                }
                break;

                case 1:
                {
                    if (chordProgressionMode)
                    {
                        fourth = new Notes(supertonic, null);
                    }
                    else if(accompanimentMode)
                    {
                        fourth = new Notes("A3+D4", "F5+D6");
                    }
                    else if(jazzMode)
                    {
                        fourth = new Notes("D5min7h* + D#5dim7h* + E5min7h*", null);
                    }
                    else if(atonalMode)
                    {
                        fourth = new Notes(possibleNotes[(int) (Math.random() * 11)] 
                            + "3+" + possibleNotes[(int) (Math.random() * 11)] + "4", 
                            "D5+" + possibleNotes[(int) (Math.random() * 11)] + "6");
                    }
                    
                    staffHeight4 = -6;
                }
                break;

                case 2:
                {
                    if (chordProgressionMode)
                    {
                        fourth = new Notes(mediant, null);
                    }
                    else if(accompanimentMode)
                    {
                        fourth = new Notes("B3+E4", "G5+E6");
                    }
                    else if(jazzMode)
                    {
                        fourth = new Notes("Eb5maj7h* + D5min6h* + Eb5dim7h*", null);
                    }
                    else if(atonalMode)
                    {
                        fourth = new Notes(possibleNotes[(int) (Math.random() * 11)] 
                            + "3+" + possibleNotes[(int) (Math.random() * 11)] + "4", 
                            "E5+" + possibleNotes[(int) (Math.random() * 11)] + "6");
                    }
                    
                    staffHeight4 = -12;
                }
                break;

                case 3:
                {
                    if (chordProgressionMode)
                    {
                        fourth = new Notes(subdominant, null);
                    }
                    else if(accompanimentMode)
                    {
                        fourth = new Notes("F3+C4", "A5+F6");
                    }
                    else if(jazzMode)
                    {
                        fourth = new Notes("F5min6h* + F5maj6h* + G5maj6h*", null);
                    }
                    else if(atonalMode)
                    {
                        fourth = new Notes(possibleNotes[(int) (Math.random() * 11)] 
                            + "3+" + possibleNotes[(int) (Math.random() * 11)] + "4", 
                            "F5+" + possibleNotes[(int) (Math.random() * 11)] + "6");
                    }
                    
                    staffHeight4 = -18;
                }
                break;

                case 4:
                {
                    if (chordProgressionMode)
                    {
                        fourth = new Notes(dominant, null);
                    }
                    else if(accompanimentMode)
                    {
                        fourth = new Notes("D3+G4", "B5+G6");
                    }
                    else if(jazzMode)
                    {
                        fourth = new Notes("G5maj7h* + E5min7h* + C5maj7h*", null);
                    }
                    else if(atonalMode)
                    {
                        fourth = new Notes(possibleNotes[(int) (Math.random() * 11)] 
                            + "3+" + possibleNotes[(int) (Math.random() * 11)] + "4", 
                            "G5+" + possibleNotes[(int) (Math.random() * 11)] + "6");
                    }
                    
                    staffHeight4 = -24;
                }
                break;

                case 5:
                {
                    if (chordProgressionMode)
                    {
                        fourth = new Notes(submediant, null);
                    }
                    else if(accompanimentMode)
                    {
                        fourth = new Notes("A3+C4", "E5+A6");
                    }
                    else if(jazzMode)
                    {
                        fourth = new Notes("A5min7h* + D5dom7h* + G5maj7h*", null);
                    }
                    else if(atonalMode)
                    {
                        fourth = new Notes(possibleNotes[(int) (Math.random() * 11)] 
                            + "3+" + possibleNotes[(int) (Math.random() * 11)] + "4", 
                            "A5+" + possibleNotes[(int) (Math.random() * 11)] + "6");
                    }
                    
                    staffHeight4 = -30;
                }
                break;

                case 6:
                {
                    if (chordProgressionMode)
                    {
                        fourth = new Notes(leadingTone, null);
                    }
                    else if(accompanimentMode)
                    {
                        fourth = new Notes("B3+F4", "D5+B6");
                    }
                    else if(jazzMode)
                    {
                        fourth = new Notes("Bb4maj7h* + B4dim7h* + F5maj7h*", null);
                    }
                    else if(atonalMode)
                    {
                        fourth = new Notes(possibleNotes[(int) (Math.random() * 11)] 
                            + "3+" + possibleNotes[(int) (Math.random() * 11)] + "4", 
                            "B5+" + possibleNotes[(int) (Math.random() * 11)] + "6");
                    }
                    
                    staffHeight4 = -36;
                }
                break;
            }
        }
        else if(e.getActionCommand().equals("submit")) 
        {
            //plays sound depending on which mode is chosen
            if (chordProgressionMode) //TODO: make note images erased after this
            {
                String progression = first.getV1() + "w " + second.getV1() + "w " 
                    + third.getV1() + "w " + fourth.getV1() + "w";
                ChordProgression cp = new ChordProgression(progression);
                cp.setKey("C");
                player.play(cp);
            }
            
            if(accompanimentMode)
            {
                Pattern p1 = new Pattern(first.getV1() + "h | " + second.getV1() 
                    + "h | " + third.getV1() + "h | " + fourth.getV1() + "h").setVoice(0);
                Pattern p2 = new Pattern(first.getV2() + "h | " + second.getV2() 
                    + "h | " + third.getV2() + "h | " + fourth.getV2() + "h").setVoice(1);
                player.play(p1, p2);
            }
            
            if (jazzMode)
            {
                Pattern p = new Pattern(first.getV1() + " | " + second.getV1()
                    + " | " + third.getV1() + " | " + fourth.getV1());
                player.play(p);
            }
            
            if(atonalMode)
            {
                Pattern p1 = new Pattern(first.getV1() + "h | " + second.getV1() 
                    + "h | " + third.getV1() + "h | " + fourth.getV1() + "h").setVoice(0);
                Pattern p2 = new Pattern(first.getV2() + "h | " + second.getV2() 
                    + "h | " + third.getV2() + "h | " + fourth.getV2() + "h").setVoice(1);
                player.play(p1, p2);
            }
        }
        
        if(e.getActionCommand() == "explanation")
        {
            explanationSelected = explanationPick.getSelectedIndex();  
            //displays explanation of mode picked by user

            switch(explanationSelected)
            {
                case 1: //Chord Progression Mode
                {
                    JOptionPane.showMessageDialog(null, "Chord Progression Mode " +
                        "will play the root chord of \nthe note you picked in "
                        + " C major. \nThis means that it would play a E chord " 
                        + "\nif you pick \"mi\" in the key of C.");
                }
                break;

                case 2: //Accompaniment Mode
                {
                    JOptionPane.showMessageDialog(null, "Accompaniment Mode will "
                        + "play the note you \npicked as the melody note. The "
                        + "computer will \nplay three other notes to harmonize "
                        + "with \nyour melody.");
                }
                break;
                
                case 3: //Jazz Mode
                {
                    JOptionPane.showMessageDialog(null, "Jazz Mode will play "
                        + "the note you \npicked as the melody note. The "
                        + "computer \nwill play a jazz progression of three chords \n" 
                        + "based around the melody you picked.");
                }
                break;
                
                case 4: //Atonal Mode
                {
                    JOptionPane.showMessageDialog(null, "Atonal Mode will "
                        + "play the note you picked. \nThe computer will "
                        + "randomly pick three other \nnotes to play along with "
                        + "this. Atonality is common \nin Contemporary Classical Music. " 
                        + "It means that there is \nno melodic center. There is " 
                        + "nothing more atonal \nthan randomly generated notes.");
                }
                break;
            }
        }
        
        if(e.getActionCommand() == "instructions")
        {
            JOptionPane.showMessageDialog(null, "1. Select a mode. \n2. Select "
                + "four notes for the melody. \n3. Press submit and the music "
                + "will play. \n\nNote: You must reselect all melody notes "
                + "after changing modes!");
        }
    }
}