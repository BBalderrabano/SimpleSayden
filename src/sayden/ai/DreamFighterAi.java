package sayden.ai;

import sayden.Constants;
import sayden.Creature;
import sayden.Point;

public class DreamFighterAi extends CreatureAi {

	private Creature player;
	public Creature target() { return target == null ? player : target; }
	
	public DreamFighterAi(Creature creature, Creature player, boolean isAlly) {
		super(creature);
		this.target = isAlly ? null : player;
		this.player = player;

		creature.setData(Constants.RACE, isAlly ? "human" : "warrior");
	}

	public boolean onGetAttacked(int amount, String position, Creature attacker){
		if(attacker.isPlayer() && !creature.isAlly(attacker))
			target = attacker;
		
		return super.onGetAttacked(amount, position, attacker);
	}
	
	public void onUpdate(){
		super.onUpdate();
		if(target == null || target.hp() < 1 || !canSee(target.x, target.y)){
			for(Point p : creature.position().neighbors(creature.visionRadius())){
				Creature c = creature.creature(p.x, p.y);
				if(c != null && c.hp() > 1 && !c.isAlly(creature)) {
					target = c;
					break;
				}
			}
		}
		if(canPickup()){
			creature.pickup();
		}
		if(target != null && canSee(target.x, target.y)){
			if (canThrowAt(target, true) && !target.isAlly(creature)){
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
