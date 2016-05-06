package sayden.screens;

import java.awt.event.MouseEvent;

import sayden.Creature;
import sayden.Item;
import sayden.Line;
import sayden.Point;

public class ThrowAtScreen extends TargetBasedScreen {
	private Item item;
	
	public ThrowAtScreen(Creature player, int sx, int sy, Item item) {
		super(player, "Arrojar " + item.nameElLa() + " a?", sx, sy);
		this.item = item;
	}

	public boolean isAcceptable(int x, int y) {
		if (!player.canSee(x, y))
			return false;
		
		for (Point p : new Line(player.x, player.y, x, y)){
			if (!player.realTile(p.x, p.y).isGround())
				return false;
		}
		
		return true;
	}

	public void selectWorldCoordinate(int x, int y, int screenX, int screenY){
		player.throwItem(item, x, y);
	}

	@Override
	public Screen respondToMouseInput(MouseEvent mouse) {
		return this;
	}
}
