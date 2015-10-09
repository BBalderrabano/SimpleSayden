package sayden.screens;

import sayden.Creature;
import sayden.Item;

public class ExamineScreen extends InventoryBasedScreen {

	public ExamineScreen(Creature player) {
		super(player);
	}

	@Override
	protected String getVerb() {
		return "examine";
	}

	@Override
	protected boolean isAcceptable(Item item) {
		return true;
	}

	@Override
	protected Screen use(Item item) {
		player.notify("Es " + item.nameUnUna() + "." + item.details());
		return null;
	}
}
