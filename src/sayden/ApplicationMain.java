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
	
	//Instanciamos el app updater y le pasamos la url del repositorio donde esta el archivo .jar
	ApplicationUpdater updater = new ApplicationUpdater(
			"https://raw.githubusercontent.com/BBalderrabano/SimpleSayden/master/deploy/",
			Constants.SAVE_FILE_NAME);
	
	private AsciiPanel terminal;			//La terminal donde se va a mostrar todo el juego
	private Screen screen;					//La screen "padre" que engloba todo
	
	private long lastPressProcessed = 0;	//Guarda el tiempo del ultimo input recibido
	
	private Timer t = new Timer();			//Timer para el input
	
	/**
	 * La clase que se encarga de correr todo el juego, solo deberia haber una instancia de ApplicationMain
	 * al correr.
	 * 
	 * @param checkUpdates	Usado para que primero revise si hay actualizaciones y SOLO AHI 
	 * descargar el nuevo .jar
	 */
	public ApplicationMain(boolean checkUpdates){
		super();
		
		//El flag/constante PRODUCTION se usa para pruebas y para poder correr el juego en debug
		if(Constants.PRODUCTION && checkUpdates){
			System.out.println("Chequeando actualizaciones");
			updater.checkForUpdates();
		}
		
		//Instanciamos la terminal
		terminal = new AsciiPanel(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
		
		add(terminal);
		pack();
		
		//La primer screen es la start screen
		screen = new StartScreen();
		
		//Seteamos los keylistener y ponemos el tamaño de la pantalla
		addKeyListener(this);
		setFocusTraversalKeysEnabled(false);
		setSize(terminal.getWidth() + 6, terminal.getHeight() + 28);
		setResizable(false);
		
		repaint();
		
		String version = "v1."+updater.currentRevision();	//Mostramos en el screen la version del juego
		
		terminal.write(version, terminal.getWidthInCharacters() - (version.length() + 1), terminal.getHeightInCharacters() - 1);
		
		//Esto es para la "animacion" del start screen (el texto que se prende y se apaga)
		t.scheduleAtFixedRate(new TimerTask(){
			@Override
			public void run() {
				((StartScreen) screen).tickTime(terminal);
			}
		}, 0, 50);
	}
	
	@Override
	public void repaint(){
		//Pintamos de nuevo la terminal
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
		//Aqui se setea el intervalo de deteccion de tecla en milisegundos
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
