package sayden.screens;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import asciiPanel.AsciiPanel;
import sayden.ApplicationMain;

public class WinScreen implements Screen {

	@Override
	public void displayOutput(AsciiPanel terminal) {
		terminal.write("You won.", 1, 1);
		terminal.writeCenter("-- press [enter] to restart --", 22);
	}

	@Override
	public Screen respondToUserInput(KeyEvent key, ApplicationMain main) {
		return key.getKeyCode() == KeyEvent.VK_ENTER ? new PlayScreen() : this;
	}

	@Override
	public Screen respondToMouseInput(MouseEvent mouse) {
		return this;
	}
}
