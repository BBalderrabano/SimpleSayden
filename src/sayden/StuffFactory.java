package sayden;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import asciiPanel.AsciiPanel;
import sayden.ai.BatAi;
import sayden.ai.FungusAi;
import sayden.ai.GoblinAi;
import sayden.ai.MarauderAi;
import sayden.ai.PaseacuevasAi;
import sayden.ai.PaseacuevasMaleAi;
import sayden.ai.PlayerAi;
import sayden.ai.RockBugAi;
import sayden.ai.ZombieAi;

public class StuffFactory {
	private World world;
	private Map<String, Color> potionColors;
	private List<String> potionAppearances;
	
	public StuffFactory(World world){
		this.world = world;
		
		setUpPotionAppearances();
	}
	
	private void setUpPotionAppearances(){
		potionColors = new HashMap<String, Color>();
		potionColors.put("pocion roja", AsciiPanel.brightRed);
		potionColors.put("pocion amarilla", AsciiPanel.brightYellow);
		potionColors.put("pocion verde", AsciiPanel.brightGreen);
		potionColors.put("pocion cyan", AsciiPanel.brightCyan);
		potionColors.put("pocion azul", AsciiPanel.brightBlue);
		potionColors.put("pocion magenta", AsciiPanel.brightMagenta);
		potionColors.put("pocion negra", AsciiPanel.brightBlack);
		potionColors.put("pocion gris", AsciiPanel.white);
		potionColors.put("pocion brillante", AsciiPanel.brightWhite);

		potionAppearances = new ArrayList<String>(potionColors.keySet());
		Collections.shuffle(potionAppearances);
	}
	
	/* ########################################################################################
	 * ########################################################################################
	 * ########################################################################################
	 */
	
	public Creature newPlayer(List<String> messages, FieldOfView fov){
		Creature player = new Creature(world, '@', 'M', AsciiPanel.brightWhite, "jugador", 50);
		
		player.setStartingMovementSpeed(Speed.VERY_FAST);
		player.setStartingAttackSpeed(Speed.VERY_FAST);
		
		player.inventory().add(newShortSword(player.z));
		player.inventory().add(newWoodenShield(player.z));
		player.inventory().add(newMarauderVest(player.z));
		player.inventory().add(newMarauderHood(player.z));
		
		world.addAtEmptyLocation(player, 0);
		new PlayerAi(player, messages, fov);
		return player;
	}

	public Creature newFungus(int depth){
		Creature fungus = new Creature(world, 'f', 'M', AsciiPanel.green, "hongo", 10);
		world.addAtEmptyLocation(fungus, depth);
		new FungusAi(fungus, this);
		return fungus;
	}
	
	public Creature newBat(int depth){
		Creature bat = new Creature(world, 'b', 'M', AsciiPanel.brightYellow, "murcielago", 15);
		world.addAtEmptyLocation(bat, depth);
		new BatAi(bat);
		return bat;
	}
	
	public Creature newZombie(int depth, Creature player){
		Creature zombie = new Creature(world, 'z', 'M', AsciiPanel.white, "zombie", 50);
		world.addAtEmptyLocation(zombie, depth);
		new ZombieAi(zombie, player);
		return zombie;
	}
	
	public Creature newCaveBrute(int depth, Creature player){
		Creature caveBrute = new Creature(world, 'P', 'F', AsciiPanel.green, "derrumbadora", 30);
		caveBrute.setStartingAttackSpeed(Speed.SLOW);
		caveBrute.setStartingMovementSpeed(Speed.SLOW);
		caveBrute.setVisionRadius(4);
		
		caveBrute.modifyAttackValue(DamageType.BLUNT, 4);
		caveBrute.modifyDefenseValue(DamageType.BLUNT, 3);
		caveBrute.modifyDefenseValue(DamageType.SLICE, 2);
		
		caveBrute.equip(newBigMace(-1));
		
		world.addAtEmptyLocation(caveBrute, depth);
		PaseacuevasAi ai = new PaseacuevasAi(caveBrute, player);
		
		for(int i = 0; i < Math.random() * 2; i++){
			ai.addMale(newCaveSmall(depth, player, caveBrute));
		}
		
		return caveBrute;
	}
	
	public Creature newMarauder(int depth, Creature player){
		Creature marauder = new Creature(world, 'm', 'M', AsciiPanel.brightYellow, "merodeador", 10);
		marauder.setStartingAttackSpeed(Speed.NORMAL);
		marauder.setStartingMovementSpeed(Speed.NORMAL);
		marauder.setVisionRadius(6);
		
		marauder.inventory().add(newMarauderPoison(-1));
		
		if(Math.random() < .3f)
			marauder.inventory().add(newMarauderVest(-1));
		if(Math.random() < .3f)
			marauder.inventory().add(newMarauderHood(-1));
		
		marauder.inventory().add(randomWeapon(-1));
		
		world.addAtEmptyLocation(marauder, depth);
		new MarauderAi(marauder, player);
		return marauder;
	}
	
	public Creature newCaveSmall(int depth, Creature player, Creature female){
		Creature caveSmall = new Creature(world, 'p', 'M', AsciiPanel.brightGreen, "paseacuevas", 8);
		caveSmall.setStartingAttackSpeed(Speed.NORMAL);
		caveSmall.setStartingMovementSpeed(Speed.FAST);
		caveSmall.setVisionRadius(8);
		
		caveSmall.modifyAttackValue(DamageType.BLUNT, 2);
		
		world.addAtEmptySpace(caveSmall, female.x, female.y, female.z);
		new PaseacuevasMaleAi(caveSmall, player, female);
		return caveSmall;
	}
	
	public Creature newRockBug(int depth, Creature player){
		Creature rockBug = new Creature(world, 'c', 'M', AsciiPanel.yellow, "comepiedras", 6);
		
		rockBug.setStartingAttackSpeed(Speed.NORMAL);
		rockBug.setStartingMovementSpeed(Speed.FAST);
		rockBug.setVisionRadius(3);
		
		rockBug.modifyAttackValue(DamageType.PIERCING, 1);
		rockBug.modifyDefenseValue(DamageType.BLUNT, 1);
		rockBug.modifyDefenseValue(DamageType.SLICE, 2);
		
		if(Math.random() > 0.2f){
			rockBug.pickup(newRockBugHelm(-1));
		}
		
		world.addAtEmptyLocation(rockBug, depth);
		new RockBugAi(rockBug, player, this);
		return rockBug;
	}

	public Creature newGoblin(int depth, Creature player){
		Creature goblin = new Creature(world, 'g', 'M', AsciiPanel.brightGreen, "goblin", 50);
		new GoblinAi(goblin, player);
		goblin.equip(randomWeapon(-1));		
		goblin.equip(randomArmor(-1));
		world.addAtEmptyLocation(goblin, depth);
		return goblin;
	}

	/* ########################################################################################
	 * ########################################################################################
	 * ########################################################################################
	 */
	
	public Item newRock(int depth){
		Item rock = new Item(',', 'F', AsciiPanel.yellow, "roca", null);
		rock.modifyAttackValue(DamageType.BLUNT, 5);
		rock.makeStackable(5);
		world.addAtEmptyLocation(rock, depth);
		return rock;
	}
	
	public Item newKnife(int depth){
		Item knife = new Item(')', 'M', AsciiPanel.brightWhite, "cuchillo", null);
		knife.modifyAttackValue(DamageType.PIERCING, 2);
		knife.setData(Constants.CHECK_WEAPON, true);
		knife.modifyStacks(3);
		knife.modifyBloodModifyer(0.7f);
		world.addAtEmptyLocation(knife, depth);
		return knife;
	}
	
	public Item newRockBugHelm(int depth){
		Item rockBugHelm = new Item('^', 'M', AsciiPanel.yellow, "caparazon de comepiedra", null);
		rockBugHelm.modifyDefenseValue(DamageType.SLICE, 2);
		rockBugHelm.modifyDefenseValue(DamageType.BLUNT, 2);
		rockBugHelm.setData(Constants.CHECK_HELMENT, true);
		world.addAtEmptyLocation(rockBugHelm, -1);
		return rockBugHelm;
	}
	
	public Item newVictoryItem(int depth){
		Item item = new Item('*', 'M', AsciiPanel.brightWhite, "osito de peluche", null);
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	
	public Item newBread(int depth){
		Item item = new Item('%', 'M', AsciiPanel.yellow, "pan", null);
		item.setData(Constants.CHECK_CONSUMABLE, true);
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	
	public Item newFruit(int depth){
		Item item = new Item('%', 'F', AsciiPanel.brightRed, "manzana", null);
		item.setData(Constants.CHECK_CONSUMABLE, true);
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	
	public Item newDagger(int depth){
		Item item = new Item(')', 'F', AsciiPanel.white, "daga", null);
		item.modifyAttackValue(DamageType.PIERCING, 1);
		item.modifyAttackValue(DamageType.SLICE, 1);
		item.setData(Constants.CHECK_WEAPON, true);
		item.modifyBloodModifyer(0.7f);
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	
	public Item newShortSword(int depth){
		Item item = new Item(')', 'F', AsciiPanel.brightWhite, "espada corta", null);
		item.modifyAttackValue(DamageType.SLICE, 2);
		item.setData(Constants.CHECK_WEAPON, true);
		item.modifyAttackSpeed(Speed.FAST);
		item.modifyBloodModifyer(0.6f);
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	
	public Item newRockMace(int depth){
		Item rockMace = new Item(')', 'F', AsciiPanel.yellow, "estalactita", null);
		rockMace.modifyAttackValue(DamageType.BLUNT, 3);
		rockMace.setData(Constants.CHECK_WEAPON, true);
		rockMace.modifyAttackSpeed(Speed.SLOW);
		rockMace.modifyBloodModifyer(0.2f);
		world.addAtEmptyLocation(rockMace, depth);
		return rockMace;
	}
	
	public Item newBigSword(int depth){
		Item bigSword = new Item(')', 'F', Color.gray, "espadon", null);
		bigSword.modifyAttackValue(DamageType.SLICE, 3);
		bigSword.modifyAttackValue(DamageType.BLUNT, 2);
		bigSword.modifyAttackSpeed(Speed.SLOW);
		bigSword.setData(Constants.CHECK_WEAPON, true);
		bigSword.modifyBloodModifyer(0.5f);
		world.addAtEmptyLocation(bigSword, depth);
		return bigSword;
	}
	
	public Item newMorningStar(int depth){
		Item morningStar = new Item(')', 'F', AsciiPanel.brightBlack, "estrella", null);
		morningStar.modifyAttackValue(DamageType.BLUNT, 3);
		morningStar.modifyAttackValue(DamageType.PIERCING, 1);
		morningStar.modifyAttackSpeed(Speed.FAST);
		morningStar.setData(Constants.CHECK_WEAPON, true);
		morningStar.modifyBloodModifyer(0.4f);
		world.addAtEmptyLocation(morningStar, depth);
		return morningStar;
	}
	
	public Item newMace(int depth){
		Item item = new Item(')', 'M', Color.LIGHT_GRAY, "maza", null);
		item.modifyAttackValue(DamageType.BLUNT, 2);
		item.setData(Constants.CHECK_WEAPON, true);
		item.modifyBloodModifyer(0.2f);
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	
	public Item newBigMace(int depth){
		Item bigMace = new Item('}', 'F', Color.DARK_GRAY, "maza de piedra", null);
		bigMace.modifyAttackValue(DamageType.BLUNT, 8);
		bigMace.modifyAttackSpeed(Speed.SLOW);
		bigMace.modifyMovementSpeed(Speed.FAST);
		bigMace.modifyBloodModifyer(0.8f);
		bigMace.setData(Constants.CHECK_WEAPON, true);
		world.addAtEmptyLocation(bigMace, depth);
		return bigMace;
	}

	public Item newBow(int depth){
		Item item = new Item(')', 'M', AsciiPanel.yellow, "arco", null);
		item.modifyAttackValue(DamageType.RANGED, 6);
		item.modifyBloodModifyer(1f);
		item.setData(Constants.CHECK_WEAPON, true);
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	
	public Item newEdibleWeapon(int depth){
		Item item = new Item(')', 'F', AsciiPanel.yellow, "baguette", null);
		item.modifyAttackValue(DamageType.BLUNT, 1);
		item.modifyBloodModifyer(0.1f);
		item.setData(Constants.CHECK_WEAPON, true);
		item.setData(Constants.CHECK_CONSUMABLE, true);
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	
	public Item newLightArmor(int depth){
		Item item = new Item('[', 'F', AsciiPanel.green, "tunica", null);
		item.modifyDefenseValue(DamageType.SLICE, 2);
		item.modifyDefenseValue(DamageType.BLUNT, 2);
		item.setData(Constants.CHECK_ARMOR, true);
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	
	public Item newMediumArmor(int depth){
		Item item = new Item('[', 'F', AsciiPanel.white, "cota de malla", null);
		item.modifyDefenseValue(DamageType.SLICE, 4);
		item.modifyDefenseValue(DamageType.BLUNT, 4);
		item.setData(Constants.CHECK_ARMOR, true);
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	
	public Item newHeavyArmor(int depth){
		Item item = new Item('[', 'F', AsciiPanel.brightWhite, "armadura de placa", null);
		item.modifyDefenseValue(DamageType.SLICE, 6);
		item.modifyDefenseValue(DamageType.BLUNT, 6);
		item.setData(Constants.CHECK_ARMOR, true);
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	
	public Item newMarauderHood(int depth){
		Item marauderHood = new Item('^', 'F', AsciiPanel.brightYellow, "capucha merodeador", null);
		marauderHood.modifyDefenseValue(DamageType.SLICE, 1);
		marauderHood.setData(Constants.CHECK_HELMENT, true);
		marauderHood.setData(Constants.CHECK_MARAUDER_DISGUISE, true);
		world.addAtEmptyLocation(marauderHood, depth);
		return marauderHood;
	}
	
	public Item newLeatherArmor(int depth){
		Item leatherArmor = new Item(']', 'F', Color.orange, "armadura de cuero", null);
		leatherArmor.modifyDefenseValue(DamageType.SLICE, 2);
		leatherArmor.modifyDefenseValue(DamageType.BLUNT, 1);
		leatherArmor.setData(Constants.CHECK_ARMOR, true);
		world.addAtEmptyLocation(leatherArmor, depth);
		return leatherArmor;
	}
	
	public Item newMarauderVest(int depth){
		Item marauderVest = new Item(']', 'M', AsciiPanel.brightYellow, "ropajes merodeador", null);
		marauderVest.modifyDefenseValue(DamageType.SLICE, 1);
		marauderVest.modifyDefenseValue(DamageType.BLUNT, 1);
		marauderVest.setData(Constants.CHECK_ARMOR, true);
		marauderVest.setData(Constants.CHECK_MARAUDER_DISGUISE, true);
		world.addAtEmptyLocation(marauderVest, depth);
		return marauderVest;
	}
	
	public Item newWoodenShield(int depth){
		Item woodenShield = new Item('[', 'M', AsciiPanel.yellow, "escudo de madera", null);
		woodenShield.modifyDefenseValue(DamageType.SLICE, 3);
		woodenShield.modifyDefenseValue(DamageType.BLUNT, 3);
		woodenShield.modifyDefenseValue(DamageType.PIERCING, 1);
		woodenShield.setData(Constants.CHECK_SHIELD, true);
		world.addAtEmptyLocation(woodenShield, depth);
		return woodenShield;
	}
	
	public Item randomWeapon(int depth){
		switch ((int)(Math.random() * 6)){
		case 4: case 0: return newDagger(depth);
		case 5: case 1: return newShortSword(depth);
		case 2: return newMorningStar(depth);
		case 3: return newBigSword(depth);
		default: return newMace(depth);
		}
	}

	public Item randomArmor(int depth){
		switch ((int)(Math.random() * 3)){
		case 0: return newLightArmor(depth);
		case 1: return newMediumArmor(depth);
		default: return newHeavyArmor(depth);
		}
	}
	
	/* ########################################################################################
	 * ########################################################################################
	 * ########################################################################################
	 */
	
	public Item newMarauderPoison(int depth){
		String appearance = "pocion verde";
		final Item item = new Item('!', 'F', AsciiPanel.green, "ampolla venenosa", appearance);
		item.setQuaffEffect(new Effect(6){
			public void start(Creature creature){
				if(creature.getStringData(Constants.RACE) == "merodeador"){
					creature.doAction("es inmune al veneno de su gente");
				}else{
					if(creature.isPlayer())
						creature.doAction("parece empaparte en veneno!");
					else
						creature.doAction("parece empaparse en veneno!");
				}
			}
			public void update(Creature creature){
				super.update(creature);
				if(creature.getStringData(Constants.RACE) != "merodeador"){
					creature.modifyHp(-2, "El veneno del merodeador consume tus viceras");
				}
			}
		});
		item.makeStackable(5);
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	
	public Item newPotionOfHealth(int depth){
		String appearance = potionAppearances.get(0);
		final Item item = new Item('!', 'F', potionColors.get(appearance), "pocion de vida", appearance);
		item.setQuaffEffect(new Effect(1){
			public void start(Creature creature){
				if (creature.hp() == creature.totalMaxHp())
					return;
				
				creature.modifyHp(15, "Killed by a health potion?");
				creature.doAction(item, "look healthier");
			}
		});
		item.makeStackable(5);
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	
	public Item newPotionOfMana(int depth){
		String appearance = potionAppearances.get(1);
		final Item item = new Item('!', 'F', potionColors.get(appearance), "pocion de mana", appearance);
		item.setQuaffEffect(new Effect(1){
			public void start(Creature creature){
			
				
				creature.doAction(item, "look restored");
			}
		});
		item.makeStackable(5);
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	
	public Item newPotionOfSlowHealth(int depth){
		String appearance = potionAppearances.get(2);
		final Item item = new Item('!', 'F', potionColors.get(appearance), "pocion de lenta curacion", appearance);
		item.setQuaffEffect(new Effect(100){
			public void start(Creature creature){
				creature.doAction(item, "look a little better");
			}
			
			public void update(Creature creature){
				super.update(creature);
				creature.modifyHp(1, "Killed by a slow health potion?");
			}
		});
		item.makeStackable(5);
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	
	public Item newPotionOfPoison(int depth){
		String appearance = potionAppearances.get(3);
		final Item item = new Item('!', 'F', potionColors.get(appearance), "pocion de veneno", appearance);
		item.setQuaffEffect(new Effect(20){
			public void start(Creature creature){
				creature.doAction(item, "look sick");
			}
			
			public void update(Creature creature){
				super.update(creature);
				creature.modifyHp(-1, "Died of poison.");
			}
		});
		item.makeStackable(5);
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	
	public Item newPotionOfWarrior(int depth){
		String appearance = potionAppearances.get(4);
		final Item item = new Item('!', 'F', potionColors.get(appearance), "pocion del guerrero", appearance);
		item.setQuaffEffect(new Effect(20){
			public void start(Creature creature){
				creature.modifyAttackValue(DamageType.BLUNT, 5);
				creature.modifyDefenseValue(DamageType.BLUNT, 5);
				creature.doAction(item, "look stronger");
			}
			public void end(Creature creature){
				creature.modifyAttackValue(DamageType.BLUNT, -5);
				creature.modifyDefenseValue(DamageType.BLUNT, -5);
				creature.doAction("look less strong");
			}
		});
		item.makeStackable(5);
		world.addAtEmptyLocation(item, depth);
		return item;
	}

	public Item newPotionOfArcher(int depth){
		String appearance = potionAppearances.get(5);
		final Item item = new Item('!', 'F', potionColors.get(appearance), "pocion de arqueria", appearance);
		item.setQuaffEffect(new Effect(20){
			public void start(Creature creature){
				creature.modifyVisionRadius(3);
				creature.doAction(item, "look more alert");
			}
			public void end(Creature creature){
				creature.modifyVisionRadius(-3);
				creature.doAction("look less alert");
			}
		});
		item.makeStackable(5);
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	
	public Item randomPotion(int depth){
		switch ((int)(Math.random() * 9)){
		case 0: return newPotionOfHealth(depth);
		case 1: return newPotionOfHealth(depth);
		case 2: return newPotionOfMana(depth);
		case 3: return newPotionOfMana(depth);
		case 4: return newPotionOfSlowHealth(depth);
		case 5: return newPotionOfPoison(depth);
		case 6: return newPotionOfWarrior(depth);
		case 7: return newPotionOfArcher(depth);
		default: return newPotionOfArcher(depth);
		}
	}
	
	/* ########################################################################################
	 * ########################################################################################
	 * ########################################################################################
	 */
	
	public Item newWhiteMagesSpellbook(int depth) {
		Item item = new Item('+', 'M', AsciiPanel.brightWhite, "libro blanco", null);
		item.addWrittenSpell("minor heal", 4, new Effect(1){
			public void start(Creature creature){
				if (creature.hp() == creature.totalMaxHp())
					return;
				
				creature.modifyHp(20, "Killed by a minor heal spell?");
				creature.doAction("look healthier");
			}
		});
		
		item.addWrittenSpell("major heal", 8, new Effect(1){
			public void start(Creature creature){
				if (creature.hp() == creature.totalMaxHp())
					return;
				
				creature.modifyHp(50, "Killed by a major heal spell?");
				creature.doAction("look healthier");
			}
		});
		
		item.addWrittenSpell("slow heal", 12, new Effect(50){
			public void update(Creature creature){
				super.update(creature);
				creature.modifyHp(2, "Killed by a slow heal spell?");
			}
		});

		item.addWrittenSpell("inner strength", 16, new Effect(50){
			public void start(Creature creature){
				creature.modifyAttackValue(DamageType.BLUNT, 2);
				creature.modifyDefenseValue(DamageType.BLUNT, 2);
				creature.modifyVisionRadius(1);
				creature.modifyRegenHpPer1000(10);
				creature.doAction("seem to glow with inner strength");
			}
			public void update(Creature creature){
				super.update(creature);
				if (Math.random() < 0.25)
					creature.modifyHp(1, "Killed by inner strength spell?");
			}
			public void end(Creature creature){
				creature.modifyAttackValue(DamageType.BLUNT, -2);
				creature.modifyDefenseValue(DamageType.BLUNT, -2);
				creature.modifyVisionRadius(-1);
				creature.modifyRegenHpPer1000(-10);
			}
		});
		
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	
	public Item newBlueMagesSpellbook(int depth) {
		Item item = new Item('+', 'M', AsciiPanel.brightBlue, "libro azul", null);
		
		item.addWrittenSpell("blink", 6, new Effect(1){
			public void start(Creature creature){
				creature.doAction("fade out");
				
				int mx = 0;
				int my = 0;
				
				do
				{
					mx = (int)(Math.random() * 11) - 5;
					my = (int)(Math.random() * 11) - 5;
				}
				while (!creature.canEnter(creature.x+mx, creature.y+my, creature.z)
						&& creature.canSee(creature.x+mx, creature.y+my, creature.z));
				
				creature.moveBy(mx, my, 0);
				
				creature.doAction("fade in");
			}
		});
		
		item.addWrittenSpell("summon bats", 11, new Effect(1){
			public void start(Creature creature){
				for (int ox = -1; ox < 2; ox++){
					for (int oy = -1; oy < 2; oy++){
						int nx = creature.x + ox;
						int ny = creature.y + oy;
						if (ox == 0 && oy == 0 
								|| creature.creature(nx, ny, creature.z) != null)
							continue;
						
						Creature bat = newBat(0);
						
						if (!bat.canEnter(nx, ny, creature.z)){
							world.remove(bat);
							continue;
						}
						
						bat.x = nx;
						bat.y = ny;
						bat.z = creature.z;
						
						creature.summon(bat);
					}
				}
			}
		});
		
		item.addWrittenSpell("detect creatures", 16, new Effect(75){
			public void start(Creature creature){
				creature.doAction("look far off into the distance");
				creature.modifyDetectCreatures(1);
			}
			public void end(Creature creature){
				creature.modifyDetectCreatures(-1);
			}
		});
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	

	public Item randomSpellBook(int depth){
		switch ((int)(Math.random() * 2)){
		case 0: return newWhiteMagesSpellbook(depth);
		default: return newBlueMagesSpellbook(depth);
		}
	}
	
}
