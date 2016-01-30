package sayden.ai;

import sayden.Constants;
import sayden.Creature;
import sayden.Effect;
import sayden.Point;

public class PaseacuevasMaleAi extends CreatureAi {

	private Creature female;
	private Creature player;
	
	public PaseacuevasMaleAi(Creature creature, Creature player, Creature female) {
		super(creature);
		this.player = player;
		this.female = female;
		
		creature.setData(Constants.RACE, "paseacuevas");
	}

	public void onUpdate(){
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
		
		if(!creature.getBooleanData("SeenPlayer") && canSee(player.x, player.y, player.z) 
				&& Math.random() * 100 < 30 && female.getData("checkPoint") == null){
			
			creature.doAction("alienta a la paseacuevas con un alarido!");
			
			creature.setData("SeenPlayer", true);
			female.setData("checkPoint", new Point(player.x, player.y, player.z));
			
			creature.addEffect(new Effect(12){
				public void end(Creature creature){
					creature.unsetData("SeenPlayer");
				}
			});
			return;
		}
		if (canThrowAt(player) && female.hp() < female.totalMaxHp() && Math.random() > 0.3f){
			creature.throwItem(getWeaponToThrow(), player.x, player.y, player.z);
			return;
		}
		if(creature.position().distance(female.position()) >= 8){
			hunt(female);
			return;
		}
		for(Point p : creature.position().neighbors(9)){
			if(creature.world().item(p.x, p.y, p.z) != null){
				hunt(p);
				return;
			}
		}
		wander();
	}
}
