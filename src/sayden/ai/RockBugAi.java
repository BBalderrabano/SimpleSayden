package sayden.ai;

import sayden.Constants;
import sayden.Creature;
import sayden.DamageType;
import sayden.Effect;
import sayden.Item;
import sayden.Point;
import sayden.StuffFactory;
import sayden.Tile;

public class RockBugAi extends CreatureAi {
	Creature player;
	StuffFactory factory;
	
	boolean digesting = false;
	
	int rocksEaten = 0;
	
	int attackBonus = 6;
	int attackBonusDuration = 8;
	int healthBonus = 10;
	float healthOnEatChance = 0.4f;
	float destroyWallChance = 0.2f;
	private boolean isFull() { return rocksEaten > 8; }
	
	public RockBugAi(Creature creature, Creature player, StuffFactory factory) {
		super(creature);
		
		this.player = player;
		this.factory = factory;
		
		this.setWeakSpot(Constants.LEG_POS);
		creature.setData(Constants.RACE, "comepiedras");
	}

	public void onDecease(Item corpse){
		corpse.unsetData(Constants.CHECK_CORPSE);
		corpse.setQuaffEffect(new Effect("alimentado", 7, false){
			public void start(Creature creature){
				creature.notify("Las viceras del comepiedras saben horripilante!");
			}
			public void update(Creature creature){
				super.update(creature);
			}
		});
	}
	
	public void onUpdate(){
		super.onUpdate();
		
		if (creature.canSee(player.x, player.y) && creature.vigor() <= creature.maxVigor() * .5f){
			hunt(player);
		}else{
			Item rockCheck = creature.item(creature.x, creature.y);
			
			if(rockCheck != null && rockCheck.nameWStacks().equals("roca")){
				creature.pickup();
				creature.doAction("consume la roca ganando fuerzas");
				creature.modifyActionPoints(-creature.getActionPoints(), false);
				addBonusDamage();
				return;
			}
			for(Point p : creature.position().neighbors4()){
				if(creature.world().tile(p.x, p.y) == Tile.WALL){
					if(Math.random() < healthOnEatChance){
						if(creature.vigor() > 1){
							creature.doAction("devora la pared ganando fuerzas");
						}
						
						if(Math.random() < destroyWallChance && isFull()){
							creature.dig(p.x, p.y);
							rocksEaten++;
						}
						
						creature.modifyActionPoints(-creature.getActionPoints(), false);
						addBonusDamage();
						return;
					}else{
						return;
					}
				}
			}
			for(Point p : creature.position().neighbors(4)){
				if(creature.world().tile(p.x, p.y) == Tile.WALL){
					hunt(p);
					return;
				}
			}
			wander();
		}
		return;
	}
	
	void addBonusDamage(){
		if(!digesting)
			return;
		
		creature.addEffect(new Effect("fortalecido", attackBonusDuration){
			public void start(Creature creature){
				creature.modifyAttackValue(DamageType.BLUNT, 6);
				digesting = true;
			}
			public void end(Creature creature){
				creature.modifyAttackValue(DamageType.BLUNT, -6);
				digesting = false;
			}
		});
	}
}
