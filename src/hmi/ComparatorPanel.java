package hmi;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;
import javax.swing.JPanel;

import lexeme.java.tree.Root;

public class ComparatorPanel extends JPanel {

    private static final long serialVersionUID = -6578673034938473816L;

    String msg = "some more text\nwith new line !!!!!!!!!!";
    Root rootObject;

    public ComparatorPanel() {
        super();
        setPreferredSize(new Dimension(800, 600));

        Font myFont = new Font("Consolas", Font.PLAIN, 24);
        setFont(myFont);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        multilineDrawString(msg, g);
    }

    public void multilineDrawString(String toDraw, Graphics g) {
        String[] lines = toDraw.split("\n");

        Font myFont = new Font("Consolas", Font.PLAIN, 24);
        setFont(myFont);
        // get metrics from the graphics
        FontMetrics metrics = g.getFontMetrics(getFont());
        // get the height of a line of text in this
        // font and render context
        int hgt = metrics.getHeight();
        int y = 0;

        for (String string : lines) {
            // get the advance of my text in this font
            // and render context
            int adv = metrics.stringWidth(string);
            g.drawString(string, 50, 50 + hgt + y - 5);
            g.drawRect(50, 50 + y, adv, hgt);
            y += hgt;
        }
    }

    public void listAllFonts() {
        String fonts[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for (int i = 0; i < fonts.length; i++) {
            System.out.println(fonts[i]);
        }
    }

    public static void main(String[] arg) {
        JFrame frame = new JFrame("");
        frame.add(new ComparatorPanel());
        frame.setVisible(true);
        frame.pack();
    }

}
