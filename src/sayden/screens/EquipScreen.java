package sayden.screens;

import sayden.Creature;
import sayden.Item;

public class EquipScreen extends InventoryBasedScreen {

	public EquipScreen(Creature player) {
		super(player);
	}

	protected String getVerb() {
		return "wear or wield";
	}

	protected boolean isAcceptable(Item item) {
		return item.totalAttackValue() > 0 || item.totalDefenseValue() > 0;
	}

	protected Screen use(Item item) {
		player.equip(item);
		return null;
	}
}
