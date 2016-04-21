package sayden.screens;

import java.awt.event.KeyEvent;

import asciiPanel.AsciiPanel;
import sayden.Constants;
import sayden.Creature;
import sayden.Line;
import sayden.Point;

public class FireWeaponScreen extends TargetBasedScreen {

	public static Creature lastCreature = null;
	
	public FireWeaponScreen(Creature player, int sx, int sy) {
		super(player, "Dispara " + player.weapon().nameElLaWNoStacks() + " a?", sx, sy);
	}

	public void displayOutput(AsciiPanel terminal) {
		if(lastCreature != null && lastCreature.isAlive()){
			String quickFire = "(F) - Disparar " + player.weapon().nameElLaWNoStacks() + " hacia " + lastCreature.nameElLa();
			terminal.write(quickFire, terminal.getWidthInCharacters() - quickFire.length() - 1, 0);
		}
		super.displayOutput(terminal);
	}
	
	public boolean isAcceptable(int x, int y) {
		if (!player.weapon().getBooleanData(Constants.CHECK_RANGED)){
			player.notify("No tienes un arma de rango equipada.");
			return false;
		}
		if (!player.canSee(x, y))
			return false;
		
		for (Point p : new Line(player.x, player.y, x, y)){
			if (!player.realTile(p.x, p.y).isGround())
				return false;
		}
		
		return true;
	}
	
	public Screen respondToUserInput(KeyEvent key) {
		if(key.getKeyCode() == KeyEvent.VK_F && lastCreature != null && lastCreature.isAlive() && player.canSee(lastCreature.x, lastCreature.y)){
			player.rangedWeaponAttack(lastCreature);
			return null;
		}else{
			return super.respondToUserInput(key);
		}
	}
	
	public void selectWorldCoordinate(int x, int y, int screenX, int screenY){
		Creature other = player.creature(x, y);
		
		if (other == null){
			player.notify("No hay nadie a quien dispararle.");
		}else{
			lastCreature = other;
			player.rangedWeaponAttack(other);
		}
	}
}
