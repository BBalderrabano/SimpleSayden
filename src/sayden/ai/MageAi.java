package sayden.ai;

import sayden.Constants;
import sayden.Creature;
import sayden.Effect;
import sayden.Speed;
import sayden.Spell;

public class MageAi extends CreatureAi {
	private Creature player;
	
	public MageAi(Creature creature, Creature player) {
		super(creature);
		
		creature.learnedSpells().add(new Spell("test", 
			new Effect("penetrado", 1){
				public void start(Creature creature){
					creature.modifyHp(-1, "Pija en el orto");
					creature.notify("Te metieron la pija en el orto.");
				}
			}, 
		10, 0, "test_mago", null, Speed.SLOW, true));
		
		this.player = player;
		creature.setData(Constants.RACE, "mago");
	}

	public boolean canSee(int x, int y){
		return creature.queSpell() != null ? player.canSee(creature.x, creature.y) : super.canSee(x, y);
	}
	
	public void onUpdate(){
		super.onUpdate();
		
		if(canSee(player.x, player.y)){
			creature.castSpell(creature.learnedSpells().get(0), player.x, player.y);
			return;
		}
		
		wander();
	}
}
