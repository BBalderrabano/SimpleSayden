package sayden.screens;

import java.awt.event.KeyEvent;

import asciiPanel.AsciiPanel;
import sayden.Creature;
import sayden.Line;
import sayden.Point;

public abstract class TargetBasedScreen implements Screen {

	protected Creature player;
	protected String caption;
	private int sx;
	private int sy;
	private int x;
	private int y;
	
	public TargetBasedScreen(Creature player, String caption, int sx, int sy){
		this.player = player;
		this.caption = caption;
		this.sx = sx;
		this.sy = sy;
	}

	@Override
	public void displayOutput(AsciiPanel terminal) {
		for (Point p : new Line(sx, sy, sx + x, sy + y)){
			if (p.x < 0 || p.x >= 80 || p.y < 0 || p.y >= 24)
				continue;
			
			terminal.write('*', p.x, p.y, AsciiPanel.brightMagenta);
		}
		
		terminal.clear(' ', 0, 23, 80, 1);
		terminal.write(caption, 0, 23);
	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
		int px = x;
		int py = y;

		switch (key.getKeyCode()){
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_A: x--; break;
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_D: x++; break;
		case KeyEvent.VK_UP:
		case KeyEvent.VK_W: y--; break;
		case KeyEvent.VK_DOWN:
		case KeyEvent.VK_S: y++; break;
		case KeyEvent.VK_SPACE:
		case KeyEvent.VK_E:
		case KeyEvent.VK_ENTER: selectWorldCoordinate(player.x + x, player.y + y, sx + x, sy + y); return null; 
		case KeyEvent.VK_ESCAPE: return null;
		}
		
		if (!isAcceptable(player.x + x, player.y + y)){
			x = px;
			y = py;
		}
		
		enterWorldCoordinate(player.x + x, player.y + y, sx + x, sy + y);
		
		return this;
	}
	
	public boolean isAcceptable(int x, int y) {
		return true;
	}

	public void enterWorldCoordinate(int x, int y, int screenX, int screenY) {
		
	}

	public void selectWorldCoordinate(int x, int y, int screenX, int screenY){
	}
}
