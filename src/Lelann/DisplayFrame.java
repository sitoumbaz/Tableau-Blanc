package Lelann;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
 
public class DisplayFrame {

    JFrame frame;
    JTextArea textArea;
    int size;

    public DisplayFrame(int proc) {

        frame = new JFrame("Process " + proc);
	frame.setLocation(200+100*proc,200+100*proc);
	frame.setAlwaysOnTop (true);

	JPanel panel = new JPanel();
	frame.getContentPane().add(panel);

	// Create initial text area
	String empty = "     ";
	size = empty.length();

	// The size of the text area must be adapted here
	textArea = new JTextArea(empty, 10, 20);
	textArea.setEditable(false);

	panel.add(textArea);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public void display(String s) {

	int nextSize = s.length();
	textArea.replaceRange(s, 0, size);
	size = nextSize;
    }
}
