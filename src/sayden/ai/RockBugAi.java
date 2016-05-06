package sayden.ai;

import sayden.Constants;
import sayden.Creature;
import sayden.DamageType;
import sayden.Effect;
import sayden.Item;
import sayden.Point;
import sayden.StuffFactory;
import sayden.Tile;
import sayden.Wound;

public class RockBugAi extends CreatureAi {
	Creature player;
	StuffFactory factory;
	
	private String digestingFlag = "DIGESTING";
	
	int rocksEaten = 0;
	
	float eatWallChance = 0.4f;
	float destroyWallChance = 0.2f;
	private boolean isFull() { return rocksEaten > 8; }

	boolean corpseShattered = false;
	boolean corpseSplit = false;
	
	public RockBugAi(Creature creature, Creature player, StuffFactory factory) {
		super(creature);
		
		this.player = player;
		this.factory = factory;
		
		this.setWeakSpot(Constants.LEG_POS);
		creature.setData(Constants.RACE, "comepiedras");
		
		possibleWounds().add(new Wound(2, "abrasion acida", "Tus acidas quemaduras te mataron de dolor", 'F', 50, Wound.HIGHER_CHANCE){
			public boolean canBePicked(Creature attacker, Creature target, String position, DamageType dtype){
				if(attacker.getBooleanData(digestingFlag)){
					return true;
				}
				return false;
			}
			public boolean startFlavorText(Creature creature, Creature target){
				creature.notifyArround("Una espesa baba acida chorrea aun de la boca del comepiedras");
				target.notify("El comepiedras te muerde");
				return true;
			}
		});
		
		possibleWounds().add(new Wound(1, "mordisco acido", "La mordida del comepiedras llena tu sangre de acido",'M', 40, Wound.LOW_CHANCE){
			public boolean canBePicked(Creature attacker, Creature target, String position, DamageType dtype){
				return dtype.equals(DamageType.SLICE);
			}
			public boolean startFlavorText(Creature creature, Creature target){
				if(target.isPlayer()){
					creature.doAction("te hinca los dientes, aun cubiertos en bilis acida");
				}else{
					creature.doAction("muerde " + target.nameAlALa() + " con sus dientes cubiertos en acido");
				}
				return true;
			}
		});
		
		possibleFatality().add(new Wound(4, "destrozo", null, 'M', 1, Wound.LOWEST_CHANCE){
			public boolean canBePicked(Creature attacker, Creature target, String position, DamageType dtype){
				return dtype.equals(DamageType.BLUNT) && position == Constants.HEAD_POS;
			}
			public boolean startFlavorText(Creature creature, Creature target){ 
				creature.notifyArround("El golpe destroza el caparazon del comepiedras, salpicando sangre y viceras enderredor");
				return false; 
			}
			public void start(Creature creature){
				creature.makeBleed(80f);
				corpseShattered = true;
			}
		});
		
		possibleFatality().add(new Wound(4, "cercenamiento", null, 'M', 1, Wound.LOWEST_CHANCE){
			public boolean canBePicked(Creature attacker, Creature target, String position, DamageType dtype){
				return dtype.equals(DamageType.SLICE) && position == Constants.LEG_POS;
			}
			public boolean startFlavorText(Creature creature, Creature target){
				String text = "";
				if(creature.weapon() != null){
					text = Constants.capitalize((creature.isPlayer() ? 
							"Tu " + creature.weapon().nameWNoStacks() : creature.weapon().nameElLaWNoStacks() + " " + creature.nameDelDeLa()));
				}else{
					text = Constants.capitalize((creature.isPlayer() ? "Tu golpe" : "El golpe " + creature.nameDelDeLa()));
				}
				creature.notifyArround("%s separa en dos al comepiedras, que se retuerce cercenado, hasta morir en agonia", text);
				corpseSplit = true;
				return false;
			}
			public void start(Creature creature){
				creature.makeBleed(150f);
			}
		});
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
		if(corpseShattered){
			corpse.name = "cadaver destrozado de comepiedras";
		}else if(corpseSplit){
			corpse.name = "cadaver cercenado de comepiedras";
		}
	}
	
	public void onUpdate(){
		super.onUpdate();
		
		if (creature.canSee(player.x, player.y) && creature.vigor() <= creature.maxVigor() * .5f){
			hunt(player);
		}else{
			Item rockCheck = creature.item(creature.x, creature.y);
			
			if(rockCheck != null && rockCheck.nameWStacks().equals("roca") && !creature.getBooleanData(digestingFlag)){
				creature.pickup();
				
				creature.doAction("regurgita una bilis acida sobre " + rockCheck.nameElLa());
				creature.doAction("consume la roca ganando fuerzas");
				
				creature.modifyActionPoints(-creature.getActionPoints());
				digestRock();
				
				return;
			}
			for(Point p : creature.position().neighbors4()){
				if(creature.world().tile(p.x, p.y) == Tile.WALL){
					if(Math.random() < eatWallChance && !creature.getBooleanData(digestingFlag)){
						
						creature.doAction("regurgita una bilis acida sobre la pared");
						creature.doAction("devora la pared ganando fuerzas");
						
						if(Math.random() < destroyWallChance && isFull()){
							creature.dig(p.x, p.y);
							rocksEaten++;
						}
						
						creature.modifyActionPoints(-creature.getActionPoints());
						digestRock();
						
						return;
					}else{
						creature.modifyActionPoints(-creature.getActionPoints());
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
	
	void digestRock(){
		creature.addEffect(new Effect("digiriendo", 12){
			public void start(Creature creature){
				creature.setData(digestingFlag, true);
			}
			public void end(Creature creature){
				creature.unsetData(digestingFlag);
			}
		});
	}
}
