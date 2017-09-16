
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

public class WordPanel extends JPanel implements Runnable {

    public static volatile boolean done;
    private WordRecord[] words;
    private WordApp wa;
    private int noWords;
    private int maxY;
    private int[] values = new int[noWords];
    int i =0;
    Random r = new Random();
    
    public void paintComponent(Graphics g) {
        int width = getWidth();
        int height = getHeight();
        g.clearRect(0, 0, width, height);
        g.setColor(Color.red);
        g.fillRect(0, maxY - 10, width, height);

        g.setColor(Color.black);
        g.setFont(new Font("Helvetica", Font.PLAIN, 26));
        //draw the words
        //animation must be added 
        for (int i = 0; i < noWords; i++) {
            g.drawString(words[i].getWord(), words[i].getX(), words[i].getY()-5);
        }

    }

    WordPanel(WordRecord[] words, int maxY) {
        this.words = words; //will this work?
        noWords = words.length;
        done = false;
        this.maxY = maxY;
    }

    public void run() {

      int i = r.nextInt(1000000);
       i = i%noWords;
        
        if (wa.done == true){
            for (int j = 0; j < noWords; j++) {
                words[j].resetWord();     
            }
        }
        

        int length = words[i].getWord().length();
        while (wa.done ==false) {

            if (words[i].getY() > maxY-5) {
                
                wa.score.missedWord();
                wa.updateGUI();
                words[i].resetWord();
            }
            if (words[i].matchWord(wa.fieldText)) {
                wa.score.caughtWord(length);
                wa.updateGUI();
            }

            

            words[i].drop(5);
            repaint();
            try {
                Thread.sleep(words[i].getSpeed());
            } catch (InterruptedException ex) {
                Logger.getLogger(WordPanel.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        
    }
}
