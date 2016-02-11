package sayden.screens;

import sayden.Creature;
import sayden.Line;
import sayden.Point;

public class FireWeaponScreen extends TargetBasedScreen {

	public FireWeaponScreen(Creature player, int sx, int sy) {
		super(player, "Dispara " + player.weapon().nameElLa() + " a?", sx, sy);
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
		Creature other = player.creature(x, y);
		
		if (other == null)
			player.notify("No hay nadie a quien dispararle.");
		else
			player.rangedWeaponAttack(other);
	}
}
