
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.Scanner;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
//model is separate from the view.

public class WordApp {
//shared variables

    static int noWords = 4;
    static int totalWords;

    static int frameX = 1000;
    static int frameY = 600;
    static int yLimit = 480;

    static WordDictionary dict = new WordDictionary(); //use default dictionary, to read from file eventually

    static WordRecord[] words;
    static volatile boolean done;  //must be volatile

    static Score score = new Score();
    static WordPanel w;
    static volatile boolean started = false;
    static String fieldText = "";
    static JLabel caught;
    static JLabel missed;
    static JLabel scr;

    public static void updateGUI() {

        caught.setText("Caught: " + score.getCaught() + "    ");
        missed.setText("Missed:" + score.getMissed() + "    ");
        scr.setText("Score:" + score.getScore() + "    ");

    }

    public static void setupGUI(int frameX, int frameY, int yLimit) {
        // Frame init and dimensions
        JFrame frame = new JFrame("WordGame");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(frameX, frameY);

        JPanel g = new JPanel();
        g.setLayout(new BoxLayout(g, BoxLayout.PAGE_AXIS));
        g.setSize(frameX, frameY);

        w = new WordPanel(words, yLimit);
        w.setSize(frameX, yLimit + 100);
        g.add(w);

        JPanel txt = new JPanel();
        txt.setLayout(new BoxLayout(txt, BoxLayout.LINE_AXIS));
        caught = new JLabel("Caught: " + score.getCaught() + "    ");
        missed = new JLabel("Missed:" + score.getMissed() + "    ");
        scr = new JLabel("Score:" + score.getScore() + "    ");

        txt.add(caught);
        txt.add(missed);
        txt.add(scr);

        //[snip]
        final JTextField textEntry = new JTextField("", 20);
        textEntry.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                String text = textEntry.getText();
                fieldText = text;
                textEntry.setText("");
                textEntry.requestFocus();
            }
        });

        txt.add(textEntry);
        txt.setMaximumSize(txt.getPreferredSize());
        g.add(txt);

        JPanel b = new JPanel();
        b.setLayout(new BoxLayout(b, BoxLayout.LINE_AXIS));
        JButton startB = new JButton("Start");

        // add the listener to the jbutton to handle the "pressed" event
        startB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                done = false;

                if (done == false) {
                                     
                    for (int i = 0; i < noWords; i++) {
                        Thread t = new Thread(w);
                        t.start();
                        textEntry.requestFocus();
                    }
          
                }
                //return focus to the text entry field
            }
        });
        JButton endB = new JButton("End");

        // add the listener to the jbutton to handle the "pressed" event
        endB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                done = true;

            }
        });
        JButton quitB = new JButton("Quit");
        quitB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //call another method in the same class which will close this Jframe
                frame.dispose();
            }
        });

        b.add(startB);
        b.add(endB);
        b.add(quitB);
        g.add(b);

        frame.setLocationRelativeTo(null);  // Center window on screen.
        frame.add(g); //add contents to window
        frame.setContentPane(g);
        //frame.pack();  // don't do this - packs it into small space
        frame.setVisible(true);

    }

    public static String[] getDictFromFile(String filename) {
        String[] dictStr = null;
        try {
            Scanner dictReader = new Scanner(new FileInputStream(filename));
            int dictLength = dictReader.nextInt();
            //System.out.println("read '" + dictLength+"'");

            dictStr = new String[dictLength];
            for (int i = 0; i < dictLength; i++) {
                dictStr[i] = new String(dictReader.next());
                //System.out.println(i+ " read '" + dictStr[i]+"'"); //for checking
            }
            dictReader.close();
        } catch (IOException e) {
            System.err.println("Problem reading file " + filename + " default dictionary will be used");
        }
        return dictStr;

    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in); //10 2 example_dict.txt
        String s = "100 5 example_dict.txt";
        String[] stuff = s.split(" ");
        //deal with command line arguments
        totalWords = Integer.parseInt(stuff[0]);  //total words to fall
        noWords = Integer.parseInt(stuff[1]); // total words falling at any point
        assert (totalWords >= noWords); // this could be done more neatly
        String[] tmpDict = getDictFromFile(stuff[2]); //file of words
        if (tmpDict != null) {
            dict = new WordDictionary(tmpDict);
        }

        WordRecord.dict = dict; //set the class dictionary for the words.

        words = new WordRecord[noWords];  //shared array of current words

        //[snip]
        setupGUI(frameX, frameY, yLimit);
        //Start WordPanel thread - for redrawing animation

        int x_inc = (int) frameX / noWords;
        //initialize shared array of current words

        for (int i = 0; i < noWords; i++) {
            words[i] = new WordRecord(dict.getNewWord(), i * x_inc, yLimit);
        }

    }

}
