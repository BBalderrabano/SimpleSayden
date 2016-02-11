package sayden.screens;

import java.awt.event.KeyEvent;

import sayden.Constants;
import sayden.Creature;
import sayden.Item;
import sayden.Tile;

public class LookScreen extends TargetBasedScreen {

	public LookScreen(Creature player, String caption, int sx, int sy) {
		super(player, caption, sx, sy);
	}

	public void enterWorldCoordinate(int x, int y, int screenX, int screenY) {
		Creature creature = player.creature(x, y);
		
		if (creature != null){
			caption = creature.glyph() + " " + Constants.capitalize(creature.nameUnUna()) + " " + creature.statusEffects();
			caption += creature.weapon() != null ? creature.weapon().glyph() : "";
			caption += creature.shield() != null ? creature.shield().glyph() : "";
			caption += creature.helment() != null ? creature.helment().glyph() : "";
			caption += creature.armor() != null ? creature.armor().glyph() : "";
			caption = PlayScreen.splitPhraseByLimit(caption, Constants.WORLD_WIDTH).get(0);
			return;
		}
		
		Item item = player.item(x, y);
		
		if (item != null){
			caption = item.glyph() + " " + item.details();
			return;
		}
		
		Tile tile = player.tile(x, y);
		caption = tile.glyph() + " " + tile.details();
	}
	
	public Screen respondToUserInput(KeyEvent key) {
		if(key.getKeyCode() == KeyEvent.VK_R){
			return null;
		}else{
			return super.respondToUserInput(key);
		}
	}
}
