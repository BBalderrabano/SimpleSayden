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
	
	int attackBonus = 6;
	int attackBonusDuration = 4;
	int healthBonus = 10;
	float healthOnEatChance = 0.2f;
	float destroyWallChance = 0.2f;
	
	public RockBugAi(Creature creature, Creature player, StuffFactory factory) {
		super(creature);
		
		this.player = player;
		this.factory = factory;
		
		this.setWeakSpot(Constants.LEG_POS);
		creature.setData(Constants.RACE, "comepiedras");
	}

	public void onDecease(Item corpse){
		corpse.unsetData(Constants.CHECK_CORPSE);
		corpse.setQuaffEffect(new Effect(7, false){
			public void start(Creature creature){
				creature.notify("Las viceras del comepiedras saben horripilante!");
				creature.modifyHp(-3, "Indigestion de carne de comepiedra");
			}
			public void update(Creature creature){
				super.update(creature);
				creature.modifyHp(1, "Indigestion de carne de comepiedra");
			}
		});
	}
	
	public void onUpdate(){
		if (creature.canSee(player.x, player.y, player.z) && creature.hp() >= creature.totalMaxHp() * .3f){
			hunt(player);
		}else{
			Item rockCheck = creature.item(creature.x, creature.y, creature.z);
			
			if(rockCheck != null && rockCheck.name().equals("roca")){
				creature.pickup();
				creature.doAction("consume la roca recuperando fuerzas");
				creature.modifyHp(healthBonus, "Indigestion rocosa");
				creature.modifyActionPoints(-creature.getMovementSpeed().velocity());
				addBonusDamage();
				return;
			}
			for(Point p : creature.position().neighbors4()){
				if(creature.world().tile(p.x, p.y, p.z) == Tile.WALL){
					if(Math.random() < healthOnEatChance){
						if(creature.hp() < creature.totalMaxHp()){
							creature.doAction("devora la pared recuperando fuerzas");
						}
						creature.modifyHp(healthBonus, "Indigestion rocosa");
						
						if(Math.random() < destroyWallChance){
							creature.dig(p.x, p.y, p.z);
						}
						
						creature.modifyActionPoints(-creature.getMovementSpeed().velocity());
						addBonusDamage();
						return;
					}else{
						return;
					}
				}
			}
			for(Point p : creature.position().neighbors(4)){
				if(creature.world().tile(p.x, p.y, p.z) == Tile.WALL){
					hunt(p);
					return;
				}
			}
			wander();
		}
		return;
	}
	
	void addBonusDamage(){
		creature.addEffect(new Effect(attackBonusDuration){
			public void start(Creature creature){
				creature.modifyAttackValue(DamageType.BLUNT, 6);
			}
			public void end(Creature creature){
				creature.modifyAttackValue(DamageType.BLUNT, -6);
			}
		});
	}
}
