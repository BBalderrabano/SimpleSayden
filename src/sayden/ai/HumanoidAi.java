package sayden.ai;

import sayden.Constants;
import sayden.Creature;

public class HumanoidAi extends CreatureAi {
	private Creature player;
	
	public HumanoidAi(Creature creature, Creature player) {
		super(creature);
		this.player = player;
	}

	public void onUpdate(){
		if(creature.isActive()){
			return;
		}
		if(creature.getBooleanData(Constants.FLAG_ANGRY)){
			if (canUseBetterEquipment())
				useBetterEquipment();
			else if (creature.canSee(player.x, player.y))
				hunt(player);
			else if (canPickup())
				creature.pickup();
		}
	}
}
