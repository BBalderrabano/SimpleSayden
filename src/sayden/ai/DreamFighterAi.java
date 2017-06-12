package sayden.ai;

import sayden.Constants;
import sayden.Creature;
import sayden.Item;
import sayden.Point;
import sayden.Wound;

public class DreamFighterAi extends CreatureAi {

	private Creature player;
	public Creature target() { return target == null ? player : target; }
	
	public DreamFighterAi(Creature creature, Creature player, boolean isAlly) {
		super(creature);
		this.target = isAlly ? null : player;
		this.player = player;

		creature.setData(Constants.RACE, isAlly ? "human" : "warrior");
	}

	public boolean onGetAttacked(String position, Wound wound, Creature attacker, Item object){
		if(attacker.isPlayer() && !creature.isAlly(attacker))
			target = attacker;
		
		return super.onGetAttacked(position, wound, attacker, object);
	}
	
	public void onUpdate(){
		if(creature.isActive()){
			return;
		}
		
		if(target == null || !target.isAlive() || !canSee(target.x, target.y)){
			for(Point p : creature.position().neighbors(creature.visionRadius())){
				Creature c = creature.creature(p.x, p.y);
				if(c != null && c.isAlive() && !c.isAlly(creature)) {
					target = c;
					break;
				}
			}
		}
		if(canPickup()){
			creature.pickup();
		}
		if(target != null && canSee(target.x, target.y)){
			if (canThrowAt(target) && !target.isAlly(creature)){
				creature.throwItem(getWeaponToThrow(), target.x, target.y);
				return;
			}
			hunt(target);
		}
		if(creature.isAlly(player) && canSee(player.x, player.y) && player.position().distance(creature.position()) >= 3){
			hunt(player);
		}
		wander();
	}
}
