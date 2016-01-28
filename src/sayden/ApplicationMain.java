package sayden;

import javax.swing.JFrame;
import asciiPanel.AsciiPanel;
import sayden.screens.Screen;
import sayden.screens.StartScreen;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ApplicationMain extends JFrame implements KeyListener {
	private static final long serialVersionUID = 1060623638149583738L;
	
	private AsciiPanel terminal;
	private Screen screen;
	
	private long lastPressProcessed = 0;
	
	public ApplicationMain(){
		super();
		terminal = new AsciiPanel(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
		
		add(terminal);
		pack();
		screen = new StartScreen();
		addKeyListener(this);
		setFocusTraversalKeysEnabled(false);
		repaint();
	}
	
	@Override
	public void repaint(){
		terminal.clear();
		screen.displayOutput(terminal);
		super.repaint();
	}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		//Limit press event to 25 mls
		if(System.currentTimeMillis() - lastPressProcessed > 25) {
			screen = screen.respondToUserInput(e);
			repaint();
            lastPressProcessed = System.currentTimeMillis();
        }    
	}

	@Override
	public void keyTyped(KeyEvent e) {}
	
	public static void main(String[] args) {
		ApplicationMain app = new ApplicationMain();
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		app.setVisible(true);
	}
}
