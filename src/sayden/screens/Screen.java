package sayden.screens;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import asciiPanel.AsciiPanel;

public interface Screen {
	public void displayOutput(AsciiPanel terminal);
	
	public Screen respondToUserInput(KeyEvent key);
	
	public Screen respondToMouseInput(MouseEvent mouse);
}
