package sayden;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import asciiPanel.AsciiPanel;
import sayden.ai.BatAi;
import sayden.ai.BlacksMithAi;
import sayden.ai.FungusAi;
import sayden.ai.GoblinAi;
import sayden.ai.MarauderAi;
import sayden.ai.PaseacuevasAi;
import sayden.ai.PaseacuevasMaleAi;
import sayden.ai.PlayerAi;
import sayden.ai.PriestAi;
import sayden.ai.RockBugAi;
import sayden.ai.ZombieAi;

public class StuffFactory {
	private World world;
	private Map<String, Color> potionColors;
	private List<String> potionAppearances;
	
	protected Creature player;
	
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
		potionColors.put("pocion anaranjada", Color.ORANGE);

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
		
		player.modifyAttackValue(DamageType.RANGED, 1);
		
		Point[] startPos = {new Point(45, 2, 0),
							new Point(36, 12, 0)};
		int selectedStart = (int) (Math.random() * startPos.length);
		
		if(Math.random() < 0.1)
			player.inventory().add(newAlcoholBottle(-1));
		
		world.addAtEmptySpace(player, startPos[selectedStart].x, startPos[selectedStart].y, 0);
		
		new PlayerAi(player, messages, fov);
		this.player = player;
		player.doAction("despierta");
		return player;
	}
	
	public Creature newBlacksmith(Creature player, int x, int y, int depth){
		Creature blackSmith = new Creature(world, '8', 'M', Color.ORANGE, "herrero", 50);
		
		blackSmith.setStartingMovementSpeed(Speed.NORMAL);
		blackSmith.setStartingAttackSpeed(Speed.NORMAL);
		
		blackSmith.inventory().add(newShortSword(-1));
		blackSmith.inventory().add(newDagger(-1));
		blackSmith.inventory().add(newMace(-1));
		
		world.addAtEmptySpace(blackSmith, x, y, depth);
		new BlacksMithAi(blackSmith, player, this);
		return blackSmith;
	}
	
	public Creature newPriest(Creature player, int x, int y, int depth){
		Creature priest = new Creature(world, (char)234, 'M', AsciiPanel.brightCyan, "sacerdote", 50);
		
		priest.setStartingMovementSpeed(Speed.NORMAL);
		priest.setStartingAttackSpeed(Speed.NORMAL);
		
		world.addAtEmptySpace(priest, x, y, depth);
		new PriestAi(priest, player, this);
		return priest;
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
		caveBrute.modifyDefenseValue(DamageType.BLUNT, 2);
		caveBrute.modifyDefenseValue(DamageType.SLICE, 1);
		
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
		rockBug.setVisionRadius(2);
		
		rockBug.modifyAttackValue(DamageType.PIERCING, 1);
		rockBug.modifyDefenseValue(DamageType.SLICE, 1);
		
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
		rock.modifyAttackValue(DamageType.BLUNT, 3);
		rock.makeStackable(5);
		rock.description = "Tiene un substancial tamaño y bordes solidos, lo suficientemente liviana para llevar en cantidad";
		world.addAtEmptyLocation(rock, depth);
		return rock;
	}
	
	public Item newAlcoholBottle(int depth){
		final Item alcohol = new Item('¿', 'M', AsciiPanel.brightBlack, "alcohol", null);
		alcohol.setQuaffEffect(new Effect(1, true){
			public void start(Creature creature){
				creature.doAction(alcohol, "bebe la botella completa");
				if(Math.random() > .8){
					creature.modifyHp(-3, "Alcoholismo");
					creature.notify("El alcohol se te sube a la cabeza");
				}
			}
		});
		alcohol.makeStackable(5);
		world.addAtEmptyLocation(alcohol, depth);
		return alcohol;
	}
	
	public Item newKnife(int depth){
		Item knife = new Item(')', 'M', AsciiPanel.brightWhite, "cuchillo", "cuchillo");
		knife.modifyAttackValue(DamageType.PIERCING, 2);
		knife.setData(Constants.CHECK_WEAPON, true);
		knife.modifyStacks(3);
		knife.modifyBloodModifyer(0.7f);
		knife.description = "El filo fue tratado con especial cuidado para penetrar la carne, en lugar de solo cortarla. El mango"
				+ " de madera tambien le permite ser usado como proyectil.";
		world.addAtEmptyLocation(knife, depth);
		return knife;
	}
	
	public Item newRockBugHelm(int depth){
		Item rockBugHelm = new Item('^', 'M', AsciiPanel.yellow, "caparazon de comepiedra", null);
		rockBugHelm.modifyDefenseValue(DamageType.SLICE, 2);
		rockBugHelm.modifyDefenseValue(DamageType.BLUNT, 2);
		rockBugHelm.setData(Constants.CHECK_HELMENT, true);
		rockBugHelm.description = "Los comepiedras tienen una jugosa membrana en sus espaldas al nacer, haciendolos muy vulnerables"
				+ ". Sin embargo con el tiempo los sedimentos que caen en su caparazon y quedan impregnados, creando una fuerte proteccion.";
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
		item.setQuaffEffect(new Effect(1, false){
			public void start(Creature creature){
				creature.modifyHp(5, "Comiste demasiado");
			}
		});
		item.description = "Parece estar bastante viejo, pero donde hay hambre...";
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	
	public Item newFruit(int depth){
		Item item = new Item('%', 'F', AsciiPanel.brightRed, "manzana", null);
		item.setData(Constants.CHECK_CONSUMABLE, true);
		item.setQuaffEffect(new Effect(1, false){
			public void start(Creature creature){
				creature.modifyHp(8, "Comiste demasiado");
			}
		});
		item.description = "Se encuentra blanda y mohosa, probablemente descartada por los merodeadores.";
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	
	public Item newDagger(int depth){
		Item item = new Item(')', 'F', AsciiPanel.white, "daga", "daga");
		item.modifyAttackValue(DamageType.PIERCING, 1);
		item.modifyAttackValue(DamageType.SLICE, 1);
		item.setData(Constants.CHECK_WEAPON, true);
		item.modifyBloodModifyer(0.7f);
		item.description = "Esta daga no esta lo suficientemente afilada, pero es liviana y sencilla de usar en combate.";
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	
	public Item newShortSword(int depth){
		Item item = new Item(')', 'F', AsciiPanel.brightWhite, "espada corta", "espada corta");
		item.modifyAttackValue(DamageType.SLICE, 2);
		item.setData(Constants.CHECK_WEAPON, true);
		item.modifyBloodModifyer(0.6f);
		item.description = "De corto filo pero firme temple, esta espada parece ser la obra de Marcos el herrero.";
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	
	public Item newRockMace(int depth){
		Item rockMace = new Item(')', 'F', AsciiPanel.yellow, "estalactita", null);
		rockMace.modifyAttackValue(DamageType.BLUNT, 3);
		rockMace.setData(Constants.CHECK_WEAPON, true);
		rockMace.modifyAttackSpeed(Speed.FAST);
		rockMace.modifyBloodModifyer(0.2f);
		rockMace.description = "Caida del techo de la cueva. Por suerte no sobre tu cabeza.";
		world.addAtEmptyLocation(rockMace, depth);
		return rockMace;
	}
	
	public Item newBigSword(int depth){
		Item bigSword = new Item(')', 'M', Color.gray, "espadon", "espadon");
		bigSword.modifyAttackValue(DamageType.SLICE, 3);
		bigSword.modifyAttackValue(DamageType.BLUNT, 2);
		bigSword.modifyAttackSpeed(Speed.NORMAL);
		bigSword.setData(Constants.CHECK_WEAPON, true);
		bigSword.modifyBloodModifyer(0.5f);
		bigSword.description = "Tan grande y pesado que la fuerza bruta del impacto duele casi tanto como un miembro cercenado.";
		world.addAtEmptyLocation(bigSword, depth);
		return bigSword;
	}
	
	public Item newMorningStar(int depth){
		Item morningStar = new Item(')', 'F', AsciiPanel.brightBlack, "estrella", "estrella");
		morningStar.modifyAttackValue(DamageType.BLUNT, 3);
		morningStar.modifyAttackValue(DamageType.PIERCING, 1);
		morningStar.modifyAttackSpeed(Speed.FAST);
		morningStar.setData(Constants.CHECK_WEAPON, true);
		morningStar.modifyBloodModifyer(0.4f);
		morningStar.description = "El nombre es una alusion a las puas colocadas en la punta de esta maza.";
		world.addAtEmptyLocation(morningStar, depth);
		return morningStar;
	}
	
	public Item newMace(int depth){
		Item item = new Item(')', 'M', Color.LIGHT_GRAY, "maza", "maza");
		item.modifyAttackValue(DamageType.BLUNT, 2);
		item.setData(Constants.CHECK_WEAPON, true);
		item.modifyBloodModifyer(0.2f);
		item.description = "Otro de los trabajos de Marcos, quizas no el mas inspirado de todos. De todos modos "
				+ "no creo que al romper el craneo de un ser vivo mucho valga el estilo...";
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	
	public Item newBigMace(int depth){
		Item bigMace = new Item('}', 'F', Color.DARK_GRAY, "maza de piedra", "maza de piedra");
		bigMace.modifyAttackValue(DamageType.BLUNT, 8);
		bigMace.modifyAttackSpeed(Speed.NORMAL);
		bigMace.modifyMovementSpeed(Speed.FAST);
		bigMace.modifyBloodModifyer(0.8f);
		bigMace.setData(Constants.CHECK_WEAPON, true);
		bigMace.description = "Las derrumbadoras usan gigantescas rocas como mazos, las derrumbadoras no esperan "
				+ "conseguir poderosas espadas o artefactos, se como las derrumbadoras.";
		world.addAtEmptyLocation(bigMace, depth);
		return bigMace;
	}

	public Item newBow(int depth){
		Item item = new Item(')', 'M', AsciiPanel.yellow, "arco corto", "arco corto");
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
		item.setQuaffEffect(new Effect(1, false){
			public void start(Creature creature){
				creature.modifyHp(10, "El pan estaba tan duro que te mato");
			}
		});
		item.description = "Esta tan duro que puede ser usado como arma. Literalmente.";
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	
	public Item newLightArmor(int depth){
		Item item = new Item('[', 'F', AsciiPanel.green, "tunica", "tunica");
		item.modifyDefenseValue(DamageType.SLICE, 1);
		item.setData(Constants.CHECK_ARMOR, true);
		item.description = "Una tunica usada, quien sabe hace cuanto tiempo...quien sabe por quien...";
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	
	public Item newMediumArmor(int depth){
		Item item = new Item('[', 'F', AsciiPanel.white, "tunica endurecida", "tunica endurecida");
		item.modifyDefenseValue(DamageType.SLICE, 1);
		item.modifyDefenseValue(DamageType.BLUNT, 1);
		item.setData(Constants.CHECK_ARMOR, true);
		item.description = "Tiras de cuero recorren la tela, haciendola mas resistente.";
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	
	public Item newHeavyArmor(int depth){
		Item item = new Item('[', 'F', AsciiPanel.brightWhite, "armadura de placa", "armadura de placa");
		item.modifyDefenseValue(DamageType.SLICE, 3);
		item.modifyDefenseValue(DamageType.BLUNT, 2);
		item.setData(Constants.CHECK_ARMOR, true);
		item.description = "Placas de metal fundidas se conectan y forman esta armadura, de todos modos carece de muchas"
				+ " de las caracteristicas encontradas en una verdadera pieza.";
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	
	public Item newLeatherArmor(int depth){
		Item leatherArmor = new Item(']', 'F', Color.orange, "armadura de cuero", "armadura de cuero");
		leatherArmor.modifyDefenseValue(DamageType.SLICE, 1);
		leatherArmor.modifyDefenseValue(DamageType.PIERCING, 1);
		leatherArmor.setData(Constants.CHECK_ARMOR, true);
		leatherArmor.description = "Completamente construida de tiras de cuero interconectadas. El cuero es "
				+ "particularmente dificil de penetrar por un filo.";
		world.addAtEmptyLocation(leatherArmor, depth);
		return leatherArmor;
	}
	
	public Item newMarauderHood(int depth){
		Item marauderHood = new Item('^', 'F', AsciiPanel.brightYellow, "capucha de merodeador", "capucha de merodeador");
		marauderHood.modifyDefenseValue(DamageType.PIERCING, 1);
		marauderHood.setData(Constants.CHECK_HELMENT, true);
		marauderHood.setData(Constants.CHECK_MARAUDER_DISGUISE, true);
		marauderHood.description = "Los desfigurados merodeadores llevan siempre una capucha, prefieren no ver en el rostro"
				+ " de un hermano el recordatorio del camino elegido.";
		world.addAtEmptyLocation(marauderHood, depth);
		return marauderHood;
	}
	
	public Item newMarauderVest(int depth){
		Item marauderVest = new Item(']', 'M', AsciiPanel.brightYellow, "abrigo de merodeador", "abrigo de merodeador");
		marauderVest.modifyDefenseValue(DamageType.BLUNT, 1);
		marauderVest.setData(Constants.CHECK_ARMOR, true);
		marauderVest.setData(Constants.CHECK_MARAUDER_DISGUISE, true);
		marauderVest.description = "Cubre completamente el cuerpo y esconde las mangas.";
		world.addAtEmptyLocation(marauderVest, depth);
		return marauderVest;
	}
	
	public Item newWoodenShield(int depth){
		Item woodenShield = new Item('#', 'M', AsciiPanel.yellow, "escudo de madera", "escudo de madera");
		woodenShield.modifyDefenseValue(DamageType.SLICE, 3);
		woodenShield.modifyDefenseValue(DamageType.BLUNT, 3);
		woodenShield.modifyDefenseValue(DamageType.PIERCING, 1);
		woodenShield.setData(Constants.CHECK_SHIELD, true);
		woodenShield.description = "\"Escudo de madera\" es un eufemismo para cualquier pedazo de madera lo suficientemente solido.";
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
	
	public Item newPotionOfHealth(int depth){
		String appearance = potionAppearances.get(0);
		final Item item = new Item('!', 'F', potionColors.get(appearance), "pocion de vida", appearance);
		item.setQuaffEffect(new Effect(1){
			public void start(Creature creature){
				if (creature.hp() == creature.totalMaxHp())
					return;
				
				creature.modifyHp(15, "Killed by a health potion?");
				creature.doAction(item, "siente reanimado");
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
			
				
				creature.doAction(item, "siente recuperado");
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
				creature.doAction(item, "siente aliviado");
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
				creature.doAction(item, "siente enfermo");
			}
			
			public void update(Creature creature){
				super.update(creature);
				creature.receiveDamage(2, DamageType.POISON, "Vomitas tus viceras");
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
				creature.doAction(item, "siente mas fuerte");
			}
			public void end(Creature creature){
				creature.modifyAttackValue(DamageType.BLUNT, -5);
				creature.modifyDefenseValue(DamageType.BLUNT, -5);
				creature.doAction("siente su fuerza amainar");
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
				creature.doAction(item, "siente mas alerta");
			}
			public void end(Creature creature){
				creature.modifyVisionRadius(-3);
				creature.doAction("pierde el estado de alerta");
			}
		});
		item.makeStackable(5);
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	
	public Item newMarauderPoison(int depth){
		String appearance = potionAppearances.get(6);
		final Item item = new Item('!', 'F', potionColors.get(appearance), "pocion de merodeador", appearance);
		item.setQuaffEffect(new Effect(6, true){
			public void start(Creature creature){
				if(creature.getStringData(Constants.RACE) == "merodeador"){
					creature.doAction("es inmune al veneno de su gente");
				}else{
					if(creature.isPlayer())
						creature.doAction(item, "empapa en veneno!");
					else
						creature.doAction(item, "empapa en veneno!");
				}
			}
			public void update(Creature creature){
				super.update(creature);
				if(creature.getStringData(Constants.RACE) != "merodeador"){
					creature.receiveDamage(-2, DamageType.POISON, "El veneno del merodeador consume tus viceras");
				}
			}
		});
		item.makeStackable(5);
		item.description = "Los merodeadores untan su cuerpo de veneno para protejerse de los depredadores y criaturas"
				+ " de las profundidades lo que los hace invulnerables a su efecto. De todos modos es muy efectivo.";
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
	
	public Item newLeatherSpellbook(int depth){
		final Item item = new Item('+', 'M', AsciiPanel.brightWhite, "libro de cuero", null);
		
		item.addWrittenSpell("vida", new Effect(1){
			public void start(Creature creature){
				if (creature.hp() == creature.totalMaxHp() ||
						creature.getBooleanData("Blasfemous")){
					creature.notify("Pronuncias la palabra de vida en vano...");
					return;
				}else{
					creature.notify("Pronuncias la palabra de vida");
				}
				
				creature.modifyHp(10, "Alcanzado por la palabra de vida");
				creature.doAction(item, "siente mas recuperado!");
			}
		}, 10, 88, Constants.SPELL_HEAL, new Effect(20){
			public void start(Creature creature){
				creature.notify("Abusas de la palabra de la vida");
				creature.setData("Blasfemous", true);
			}
			public void end(Creature creature){
				creature.unsetData("Blasfemous");
			}
		}, Speed.FAST, false);
		
		item.addWrittenSpell("dolor", new Effect(1){
			public void start(Creature creature){
				creature.notify("Pronuncias la palabra del dolor");
				creature.receiveDamage(6, DamageType.MAGIC, "Se retuerce hasta la muerte");
				creature.doAction(item, "retuerce de dolor!");
			}
		}, 4, 60, Constants.SPELL_PAIN,  new Effect(1){
			public void start(Creature creature){
				creature.notify("Sientes en tu piel el dolor que infliges");
				creature.receiveDamage(6, DamageType.MAGIC, "Se retuerce hasta la muerte");
			}
		},Speed.FAST, true);
		
		world.addAtEmptyLocation(item, depth);
		return item;
	}
	
//	public Item newWhiteMagesSpellbook(int depth) {
//		Item item = new Item('+', 'M', AsciiPanel.brightWhite, "libro blanco", null);
//		item.addWrittenSpell("fireball", new Effect(1){
//			@Override
//			public void start(int x, int y, int z){
//				world.propagate(x, y, z, 400, Constants.FIRE_TERRAIN);
//			}			
//		});
//		item.addWrittenSpell("minor heal", new Effect(1){
//			public void start(Creature creature){
//				if (creature.hp() == creature.totalMaxHp())
//					return;
//				
//				creature.modifyHp(20, "Killed by a minor heal spell?");
//				creature.doAction("look healthier");
//			}
//		});
//		
//		item.addWrittenSpell("major heal", new Effect(1){
//			public void start(Creature creature){
//				if (creature.hp() == creature.totalMaxHp())
//					return;
//				
//				creature.modifyHp(50, "Killed by a major heal spell?");
//				creature.doAction("look healthier");
//			}
//		});
//		
//		item.addWrittenSpell("slow heal", new Effect(50){
//			public void update(Creature creature){
//				super.update(creature);
//				creature.modifyHp(2, "Killed by a slow heal spell?");
//			}
//		});
//
//		item.addWrittenSpell("inner strength", new Effect(50){
//			public void start(Creature creature){
//				creature.modifyAttackValue(DamageType.BLUNT, 2);
//				creature.modifyDefenseValue(DamageType.BLUNT, 2);
//				creature.modifyVisionRadius(1);
//				creature.modifyRegenHpPer1000(10);
//				creature.doAction("seem to glow with inner strength");
//			}
//			public void update(Creature creature){
//				super.update(creature);
//				if (Math.random() < 0.25)
//					creature.modifyHp(1, "Killed by inner strength spell?");
//			}
//			public void end(Creature creature){
//				creature.modifyAttackValue(DamageType.BLUNT, -2);
//				creature.modifyDefenseValue(DamageType.BLUNT, -2);
//				creature.modifyVisionRadius(-1);
//				creature.modifyRegenHpPer1000(-10);
//			}
//		});
//		
//		world.addAtEmptyLocation(item, depth);
//		return item;
//	}
//	
//	public Item newBlueMagesSpellbook(int depth) {
//		Item item = new Item('+', 'M', AsciiPanel.brightBlue, "libro azul", null);
//		
//		item.addWrittenSpell("blink", new Effect(1){
//			public void start(Creature creature){
//				creature.doAction("fade out");
//				
//				int mx = 0;
//				int my = 0;
//				
//				do
//				{
//					mx = (int)(Math.random() * 11) - 5;
//					my = (int)(Math.random() * 11) - 5;
//				}
//				while (!creature.canEnter(creature.x+mx, creature.y+my, creature.z)
//						&& creature.canSee(creature.x+mx, creature.y+my, creature.z));
//				
//				creature.moveBy(mx, my, 0);
//				
//				creature.doAction("fade in");
//			}
//		});
//		
//		item.addWrittenSpell("fuego", new Effect(1){
//			@Override
//			public void start(int x, int y, int z){
//				//world.add(new Projectile(world, z, line, Speed.SUPER_FAST, projectile, creature));
//				world.propagate(x, y, z, 1, Constants.FIRE_TERRAIN);
//			}			
//		});
//		
//		item.addWrittenSpell("detect creatures", new Effect(75){
//			public void start(Creature creature){
//				creature.doAction("look far off into the distance");
//				creature.modifyDetectCreatures(1);
//			}
//			public void end(Creature creature){
//				creature.modifyDetectCreatures(-1);
//			}
//		});
//		world.addAtEmptyLocation(item, depth);
//		return item;
//	}
	

	public Item randomSpellBook(int depth){
		switch ((int)(Math.random() * 2)){
		case 0: return newLeatherSpellbook(depth);
		default: return newLeatherSpellbook(depth);
		}
	}
	
}
