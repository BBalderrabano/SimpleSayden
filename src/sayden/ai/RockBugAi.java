package sayden.ai;

import sayden.Constants;
import sayden.Creature;
import sayden.Item;
import sayden.Point;
import sayden.Tile;

public class RockBugAi extends CreatureAi {
	Creature player;
	
	public RockBugAi(Creature creature, Creature player) {
		super(creature);
		this.setWeakSpot(Constants.LEG_POS);
		this.player = player;
	}

	public void onUpdate(){
		if (creature.canSee(player.x, player.y, player.z) && creature.hp() > creature.maxHp() * .3f){
			hunt(player);
		}else{
			Item rockCheck = creature.item(creature.x, creature.y, creature.z);
			
			if(rockCheck != null && rockCheck.name().equals("roca")){
				creature.pickup();
				creature.doAction("come la roca");
				creature.modifyHp(10, "Indigestion rocosa");
				creature.modifyActionPoints(-creature.getMovementSpeed());
				return;
			}
			for(Point p : creature.position().neighbors4()){
				if(creature.world().tile(p.x, p.y, p.z) == Tile.WALL){
					if(Math.random() < 0.2f){
						if(creature.hp() < creature.maxHp()){
							creature.doAction("mordisquea la pared de piedra");
						}
						creature.modifyHp(10, "Indigestion rocosa");
						if(Math.random() < 0.5f){
							creature.dig(p.x, p.y, p.z);
						}
						creature.modifyActionPoints(-creature.getMovementSpeed());
						return;
					}else{
						return;
					}
				}
			}
			for(Point p : creature.position().neighbors(4)){
				if(creature.world().tile(p.x, p.y, p.z) == Tile.WALL){
					move(p.x, p.y, p.z);
					return;
				}
			}
			wander();
		}
	}
}
