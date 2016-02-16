package sayden.ai;

import sayden.Constants;
import sayden.Creature;
import sayden.Item;

public class RabbitAi extends CreatureAi {
	
	public void onDecease(Item corpse){
		corpse.makeStackable(3);
	}
	
	public RabbitAi(Creature creature) {
		super(creature);
		
		creature.setData(Constants.RACE, "conejo");
	}

	public void onUpdate(){
		super.onUpdate();
		wander();
	}
}
