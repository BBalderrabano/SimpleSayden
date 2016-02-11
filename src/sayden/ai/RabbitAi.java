package sayden.ai;

import sayden.Constants;
import sayden.Creature;

public class RabbitAi extends CreatureAi {
	
	
	public RabbitAi(Creature creature) {
		super(creature);
		
		creature.setData(Constants.RACE, "conejo");
	}

	public void onUpdate(){
		wander();
	}
}
