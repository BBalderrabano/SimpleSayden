package sayden.screens;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import asciiPanel.AsciiPanel;
import sayden.ApplicationMain;

public class StartScreen implements Screen {

	private float elapsedTime = 0;
	private float RGB_COLOR_1 = 0;
	private float RGB_COLOR_2 = 0;
	
	private boolean tick = false;
	
	@Override
	public void displayOutput(AsciiPanel terminal) { }

	@Override
	public Screen respondToUserInput(KeyEvent key, ApplicationMain main) {
		return key.getKeyCode() == KeyEvent.VK_ENTER ? new DreamScreen() : this;
	}
	
	public void tickTime(AsciiPanel terminal){
		elapsedTime += 1 * 0.01;
		
		RGB_COLOR_1 = elapsedTime;
		RGB_COLOR_1 = Math.min(RGB_COLOR_1, 0.8f);
		
		terminal.writeCenter("Sayden", 5, new Color(RGB_COLOR_1, RGB_COLOR_1, RGB_COLOR_1));
		
		if(elapsedTime > 1){
			if(RGB_COLOR_2 >= 0.8f)
				tick = true;
			else if(RGB_COLOR_2 <= 0)
				tick = false;
			
			if(tick)				
				RGB_COLOR_2 -= 1 * 0.05;
			else
				RGB_COLOR_2 += 1 * 0.05;
			
			RGB_COLOR_2 = Math.min(RGB_COLOR_2, 0.8f);
			RGB_COLOR_2 = Math.max(RGB_COLOR_2, 0f);
			
			terminal.writeCenter("-- presiona [enter] para so"+(char)164+"ar --", 22, new Color(RGB_COLOR_2, RGB_COLOR_2, RGB_COLOR_2));
		}
		
		terminal.repaint();
	}

	@Override
	public Screen respondToMouseInput(MouseEvent mouse) {
		return this;
	}
}
