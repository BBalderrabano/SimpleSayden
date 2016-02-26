package sayden.ai;

import sayden.Constants;
import sayden.Creature;
import sayden.Item;
import sayden.Wound;

public class RabbitAi extends CreatureAi {
	
	Creature player;
	
	private Wound wound = new Wound("penetrado", 10){
		public void start(Creature creature){
			creature.notify("Se te abre el ojete");
		}
		public void hit(Creature creature, boolean success){
			if(success){
				creature.notify("|Te duele la cola07|");
			}
		}
	};
	
	public void onDecease(Item corpse){
		corpse.makeStackable(3);
	}
	
	public boolean onAttack(Creature other){
		boolean ret = super.onAttack(other);
		if(other.isPlayer()){
			other.inflictWound(wound);
		}
		return ret;
	}
	
	public RabbitAi(Creature creature, Creature player) {
		super(creature);
		
		this.player = player;
		creature.setData(Constants.RACE, "conejo");
	}

	public void onUpdate(){
		super.onUpdate();
		
		if(canSee(player.x, player.y))
			hunt(player);
		
		wander();
	}
}
