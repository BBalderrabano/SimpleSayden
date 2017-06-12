package sayden.ai;

import sayden.Creature;

public class GoblinAi extends CreatureAi {
	private Creature player;
	
	public GoblinAi(Creature creature, Creature player) {
		super(creature);
		this.player = player;
	}

	public void onUpdate(){
		if(creature.isActive()){
			return;
		}
		if (canUseBetterEquipment())
			useBetterEquipment();
		else if (canRangedWeaponAttack(player))
			creature.rangedWeaponAttack(player);
		else if (canThrowAt(player))
			creature.throwItem(getWeaponToThrow(), player.x, player.y);
		else if (creature.canSee(player.x, player.y))
			hunt(player);
		else if (canPickup())
			creature.pickup();
		else
			wander();
	}
}
