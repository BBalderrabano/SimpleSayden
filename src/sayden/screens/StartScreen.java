package sayden.screens;

import java.awt.event.KeyEvent;

import asciiPanel.AsciiPanel;

public class StartScreen implements Screen {

	@Override
	public void displayOutput(AsciiPanel terminal) {
		terminal.write("Simple Sayden", 1, 1);
		
		terminal.write((char)251, 1, 2);
		terminal.write((char)249, terminal.getCursorX(), 2);
		terminal.write((char)248, terminal.getCursorX(), 2);
		terminal.write((char)252, terminal.getCursorX(), 2);
		terminal.write((char)253, terminal.getCursorX(), 2);
		terminal.write((char)254, terminal.getCursorX(), 2);
		terminal.write((char)255, terminal.getCursorX(), 2);
		terminal.writeCenter("-- press [enter] to start --", 22);
	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
		return key.getKeyCode() == KeyEvent.VK_ENTER ? new PlayScreen() : this;
	}
}
