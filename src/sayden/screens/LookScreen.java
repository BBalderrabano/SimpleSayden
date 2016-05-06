package sayden.screens;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import asciiPanel.AsciiPanel;
import sayden.Constants;
import sayden.Creature;
import sayden.Item;
import sayden.Tile;

public class LookScreen extends TargetBasedScreen {
	
	private Creature creature = null;
	
	public LookScreen(Creature player, String caption, int sx, int sy) {
		super(player, caption, sx, sy);
	}

	@Override
	public void displayOutput(AsciiPanel terminal) {
		super.displayOutput(terminal);
		
		terminal.clear(' ', 0, 23, terminal.getWidthInCharacters(), 1);
		
		if(creature != null)
			terminal.write(creature.glyph(), 0, 23, creature.originalColor());
		else
			terminal.setCursorX(0);
		
		terminal.write(caption, terminal.getCursorX(), 23);
		
		if(creature != null){
			if(creature.weapon() != null)
				terminal.write(" " + creature.weapon().glyph(), terminal.getCursorX(), 23, creature.weapon().color());
			if(creature.offWeapon() != null)
				terminal.write(" " + creature.offWeapon().glyph(), terminal.getCursorX(), 23, creature.offWeapon().color());
			if(creature.shield() != null)
				terminal.write(" " + creature.shield().glyph(), terminal.getCursorX(), 23, creature.shield().color());
			if(creature.helment() != null)
				terminal.write(" " + creature.helment().glyph(), terminal.getCursorX(), 23, creature.helment().color());
			if(creature.armor() != null)
				terminal.write(" " + creature.armor().glyph(), terminal.getCursorX(), 23, creature.armor().color());

		}
	}
	
	public void enterWorldCoordinate(int x, int y, int screenX, int screenY) {
		creature = player.creature(x, y);
		
		if (creature != null){
			if(creature.getBooleanData(Constants.FLAG_INVISIBLE)){
				creature = null;
				return;
			}
			caption = Constants.capitalize(creature.nameUnUna()) + " " + creature.statusEffects();
			for(int i = 0; i < creature.vigor(); i++){
				caption += "|";
			}
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

	@Override
	public Screen respondToMouseInput(MouseEvent mouse) {
		return this;
	}
}
