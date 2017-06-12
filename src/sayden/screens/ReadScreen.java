package sayden.screens;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import sayden.ApplicationMain;
import sayden.Creature;
import sayden.Item;

public class ReadScreen extends InventoryBasedScreen {

	private int sx;
	private int sy;
	
	public ReadScreen(Creature player, int sx, int sy) {
		super(player);
		this.sx = sx;
		this.sy = sy;
	}

	@Override
	protected String getVerb() {
		return "read";
	}

	@Override
	protected boolean isAcceptable(Item item) {
		return !item.writtenSpells().isEmpty();
	}

	@Override
	protected Screen use(Item item) {
		return new ReadSpellScreen(player, sx, sy, item);
	}

	@Override
	public Screen respondToMouseInput(MouseEvent mouse) {
		return this;
	}

	@Override
	public Screen respondToUserInput(KeyEvent key, ApplicationMain main) {
		return this;
	}
}
