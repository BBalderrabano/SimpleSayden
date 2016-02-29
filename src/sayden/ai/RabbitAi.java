package sayden.ai;

import sayden.Constants;
import sayden.Creature;
import sayden.Item;

public class RabbitAi extends CreatureAi {
	Creature player;
	
	public void onDecease(Item corpse){
		corpse.makeStackable(3);
	}
	
	public RabbitAi(Creature creature, Creature player) {
		super(creature);
		
		this.player = player;
		creature.setData(Constants.RACE, "conejo");
		creature.setData(Constants.DEALS_WOUNDS, true);
	}

	public void onUpdate(){
		super.onUpdate();
		
		if(canSee(player.x, player.y))
			hunt(player);
		
		wander();
	}
}
