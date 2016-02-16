package sayden.ai;

import sayden.Constants;
import sayden.Creature;
import sayden.DamageType;
import sayden.Effect;

public class PaseacuevasLostAi extends CreatureAi {

	private Creature player;
	
	public PaseacuevasLostAi(Creature creature, Creature player) {
		super(creature);
		this.player = player;
		
		creature.setData(Constants.RACE, "paseacuevas");
	}

	public boolean onAttack(int x, int y, Creature other){
		boolean success = false;
		
		if(creature.y < y && creature.x == x){
			creature.modifyAttackValue(DamageType.SLICE, 3);
			success = super.onAttack(x, y, other);
			
			if(success){
				creature.doAction(other.isPlayer() ? "alcanza tu cuello!" : "alcanza el cuello " + other.nameDelDeLa() + "!");
				other.addEffect(new Effect("sangrando", 4){
					public void update(Creature creature){
						super.update(creature);
						creature.receiveDamage(1, DamageType.BLEED, "Desangraste hasta morir...");
						creature.makeBleed(30);
					}
				});
			}
			
			creature.modifyAttackValue(DamageType.SLICE, -3);
		}else{
			success = super.onAttack(x, y, other);
		}
		
		return success;
	}
	
	public void onUpdate(){
		super.onUpdate();
		
		if(canSee(player.x, player.y)){
			hunt(player);
			return;
		}
		
		wander();
	}
}
