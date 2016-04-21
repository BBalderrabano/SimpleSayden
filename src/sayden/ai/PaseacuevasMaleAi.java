package sayden.ai;

import sayden.Constants;
import sayden.Creature;
import sayden.Effect;
import sayden.Point;

public class PaseacuevasMaleAi extends CreatureAi {

	private Creature female;
	private Creature player;
	
	private boolean alone = false;
	
	public PaseacuevasMaleAi(Creature creature, Creature player, Creature female) {
		super(creature);
		this.player = player;
		this.female = female;
		
		creature.setData(Constants.RACE, "paseacuevas");
	}

	public void onUpdate(){
		super.onUpdate();
		
		if((female == null || !female.isAlive()) && !alone){
			creature.addEffect(new Effect("solitario", 25){
				public void end(Creature creature){
					creature.doAction("sucumbe ante su soledad");
					new PaseacuevasLostAi(creature, player);
				}
			});
			
			alone = true;
		}
		
		if(canPickup()){
			creature.pickup();
			for(int i = 0; i < creature.inventory().size(); i++){
				if(creature.inventory().get(i) != null &&
						creature.inventory().get(i).getBooleanData(Constants.CHECK_CONSUMABLE)){
					creature.give(creature.inventory().get(i), female);
					return;
				}
			}
		}
		
		if(!creature.getBooleanData(Constants.FLAG_SEEN_PLAYER) && canSee(player.x, player.y) 
				&& Math.random() * 100 < 30 && female.getData(Constants.CHECKPOINT) == null){
			
			creature.doAction("alerta a la matriarca de tu presencia!");
			
			creature.setData(Constants.FLAG_SEEN_PLAYER, true);
			female.setData(Constants.CHECKPOINT, new Point(player.x, player.y));
			
			creature.addEffect(new Effect("alerta", 12){
				public void end(Creature creature){
					creature.unsetData(Constants.FLAG_SEEN_PLAYER);
				}
			});
			return;
		}
		if (canThrowAt(player) && female.vigor() > 2 && Math.random() > 0.3f){
			creature.throwItem(getWeaponToThrow(), player.x, player.y);
			return;
		}
		
		visitCheckPoint();
		
		if(creature.position().distance(female.position()) >= 8){
			hunt(female);
			return;
		}
		for(Point p : creature.position().neighbors(9)){
			if(creature.world().item(p.x, p.y) != null){
				hunt(p);
				return;
			}
		}
		wander();
	}
}
