package sayden;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;

import asciiPanel.AsciiPanel;
import sayden.autoupdater.ApplicationUpdater;
import sayden.screens.Screen;
import sayden.screens.StartScreen;

public class ApplicationMain extends JFrame implements KeyListener {
	private static final long serialVersionUID = 1060623638149583738L;
	
	ApplicationUpdater updater = new ApplicationUpdater(
			"https://raw.githubusercontent.com/BBalderrabano/SimpleSayden/master/deploy/");
	
	private AsciiPanel terminal;
	private Screen screen;
	
	private long lastPressProcessed = 0;
	
	private Timer t = new Timer();
	
	public ApplicationMain(boolean checkUpdates){
		super();
		
		if(Constants.PRODUCTION && checkUpdates){
			System.out.println("Chequeando actualizaciones");
			updater.checkForUpdates();
		}
		
		terminal = new AsciiPanel(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
		
		add(terminal);
		pack();
		
		screen = new StartScreen();
		
		addKeyListener(this);
		setFocusTraversalKeysEnabled(false);
		setSize(terminal.getWidth() + 6, terminal.getHeight() + 28);
		setResizable(false);
		
		repaint();
		
		String version = "v1."+updater.currentRevision();
		
		terminal.write(version, terminal.getWidthInCharacters() - (version.length() + 1), terminal.getHeightInCharacters() - 1);
		
		t.scheduleAtFixedRate(new TimerTask(){
			@Override
			public void run() {
				((StartScreen) screen).tickTime(terminal);
			}
		}, 0, 50);
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
		if(e.getKeyCode() == KeyEvent.VK_ENTER){
			t.cancel();
		}
		if(System.currentTimeMillis() - lastPressProcessed > 25) {
			screen = screen.respondToUserInput(e);
			repaint();
            lastPressProcessed = System.currentTimeMillis();
        }    
	}

	@Override
	public void keyTyped(KeyEvent e) {}
	
	public static void main(String[] args) {
		ApplicationMain app = new ApplicationMain(args.length < 1);
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		app.setVisible(true);
	}
}
