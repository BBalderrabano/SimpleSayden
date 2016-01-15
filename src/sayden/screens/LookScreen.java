package sayden.screens;

import sayden.Creature;
import sayden.Item;
import sayden.Tile;

public class LookScreen extends TargetBasedScreen {

	public LookScreen(Creature player, String caption, int sx, int sy) {
		super(player, caption, sx, sy);
	}

	public void enterWorldCoordinate(int x, int y, int screenX, int screenY) {
		Creature creature = player.creature(x, y, player.z);
		if (creature != null){
			caption = creature.glyph() + " "  + creature.name() + creature.details();
			caption = PlayScreen.splitPhraseByLimit(caption, 80).get(0);
			return;
		}
		
		Item item = player.item(x, y, player.z);
		if (item != null){
			caption = item.glyph() + " "  + item.nameElLa()+ " " + item.details();
			return;
		}
		
		Tile tile = player.tile(x, y, player.z);
		caption = tile.glyph() + " " + tile.details();
	}
}
