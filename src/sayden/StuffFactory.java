package sayden;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import asciiPanel.AsciiPanel;
import sayden.ai.BigMarauderAi;
import sayden.ai.BlacksMithAi;
import sayden.ai.DreamFighterAi;
import sayden.ai.FungusAi;
import sayden.ai.HiddenAi;
import sayden.ai.MageAi;
import sayden.ai.MarauderAi;
import sayden.ai.PaseacuevasAi;
import sayden.ai.PaseacuevasLostAi;
import sayden.ai.PaseacuevasMaleAi;
import sayden.ai.PlayerAi;
import sayden.ai.PriestAi;
import sayden.ai.RabbitAi;
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
		potionColors.put("pocion anaranjada", Color.ORANGE);

		potionAppearances = new ArrayList<String>(potionColors.keySet());
		Collections.shuffle(potionAppearances);
	}
	
	/* ########################################################################################
	 * ########################################################################################
	 * ########################################################################################
	 */
	
	public void movePlayer(Creature player, List<String> messages, FieldOfView fov, World world){
		player.setWorld(world);
		new PlayerAi(player, messages, fov);
	}
	
	public Creature newPlayer(List<String> messages, FieldOfView fov){
		Creature player = new Creature(world, '@', 'M', AsciiPanel.brightWhite, "jugador", 50);
				
		player.setStartingMovementSpeed(Speed.VERY_FAST);
		player.setStartingAttackSpeed(Speed.VERY_FAST);
		
		Point[] startPos = {new Point(45, 2),
							new Point(36, 12)};
		int selectedStart = (int) (Math.random() * startPos.length);
		
		world.addAtEmptySpace(player, startPos[selectedStart].x, startPos[selectedStart].y);

		if(Math.random() < 0.2f){
			player.inventory().add(newAlcoholBottle(false));
		}
		
		new PlayerAi(player, messages, fov);
		return player;
	}
	
	public Creature newDreamer(List<String> messages, FieldOfView fov, int start_x, int start_y){
		Creature dreamer = new Creature(world, '@', 'M', AsciiPanel.brightWhite, "soñador", 50);
		
		dreamer.setStartingMovementSpeed(Speed.VERY_FAST);
		dreamer.setStartingAttackSpeed(Speed.VERY_FAST);
		
		dreamer.equip(randomWeapon(null, false, false, true, true, true));
		dreamer.equip(randomArmor(null, false, true, true, true, false));
		
		dreamer.addEffect(new Effect("soñando", 99999));
		
		new PlayerAi(dreamer, messages, fov);
		world.addAtEmptySpace(dreamer, start_x, start_y);
		return dreamer;
	}
	
	public Creature newDreamFighter(Creature player, boolean isAlly){
		Creature dreamFighter = null;
		
		if(isAlly)
			dreamFighter = new Creature(world, '@', 'M', AsciiPanel.blue, "aliado", 50);
		else
			dreamFighter = new Creature(world, '@', 'M', Color.gray, "enemigo", 50);
		
		dreamFighter.setVisionRadius(6);
		dreamFighter.equip(randomWeapon(null, false, false, true, false, true));
		dreamFighter.equip(randomArmor(null, false, false, true, true, dreamFighter.weapon() != null && dreamFighter.weapon().getBooleanData(Constants.CHECK_TWO_HANDED) ? false : true));
		
		new DreamFighterAi(dreamFighter, player, isAlly);
		world.addAtEmptyLocation(dreamFighter);
		return dreamFighter;
	}
	
	public Creature newRabbit(){
		Creature rabbit = new Creature(world, 'r', 'M', AsciiPanel.brightWhite, "conejo", 5);
		
		rabbit.setStartingMovementSpeed(Speed.VERY_SLOW);
		
		world.addAtEmptyLocation(rabbit);
		new RabbitAi(rabbit);
		return rabbit;
	}
	
	public Creature newMage(Creature player){
		Creature mage = new Creature(world, 'm', 'M', AsciiPanel.brightBlack, "mago", 10);
		
		world.addAtEmptyLocation(mage);
		new MageAi(mage, player);
		return mage;
	}
	
	public Creature newBlacksmith(Creature player, int x, int y){
		Creature blackSmith = new Creature(world, '8', 'M', Color.ORANGE, "herrero", 50);
		
		blackSmith.setStartingMovementSpeed(Speed.NORMAL);
		blackSmith.setStartingAttackSpeed(Speed.NORMAL);
		
		blackSmith.inventory().add(newShortSword(false));
		blackSmith.inventory().add(newDagger(false));
		blackSmith.inventory().add(newMace(false));
		
		world.addAtEmptySpace(blackSmith, x, y );
		new BlacksMithAi(blackSmith, player, this);
		return blackSmith;
	}
	
	public Creature newPriest(Creature player, int x, int y){
		Creature priest = new Creature(world, (char)234, 'M', AsciiPanel.brightCyan, "sacerdote", 50);
		
		priest.setStartingMovementSpeed(Speed.NORMAL);
		priest.setStartingAttackSpeed(Speed.NORMAL);
		
		world.addAtEmptySpace(priest, x, y );
		new PriestAi(priest, player, this);
		return priest;
	}

	public Creature newFungus(){
		Creature fungus = new Creature(world, 'f', 'M', AsciiPanel.green, "hongo", 10);
		world.addAtEmptyLocation(fungus );
		new FungusAi(fungus, this);
		return fungus;
	}

	public Creature newZombie(Creature player){
		Creature zombie = new Creature(world, 'z', 'M', AsciiPanel.white, "zombie", 50);
		world.addAtEmptyLocation(zombie );
		new ZombieAi(zombie, player);
		return zombie;
	}
	
	public Creature newCaveBrute(Creature player){
		Creature caveBrute = new Creature(world, 'P', 'F', AsciiPanel.brightGreen, "matriarca", 30);
		caveBrute.setStartingAttackSpeed(Speed.SLOW);
		caveBrute.setStartingMovementSpeed(Speed.SLOW);
		caveBrute.setVisionRadius(4);
		
		caveBrute.modifyAttackValue(DamageType.BLUNT, 4);
		caveBrute.modifyDefenseValue(DamageType.BLUNT, 2);
		caveBrute.modifyDefenseValue(DamageType.SLICE, 1);
		
		caveBrute.equip(newBigMace(false));
		
		world.addAtEmptyLocation(caveBrute );
		PaseacuevasAi ai = new PaseacuevasAi(caveBrute, player);
		
		for(int i = 0; i < Math.random() * 3; i++){
			ai.addMale(newCaveSmall(player, caveBrute));
		}
		
		return caveBrute;
	}
	
	public Creature newCaveLost(Creature player){
		Creature caveLost = new Creature(world, 'p', 'M', AsciiPanel.green, "paseacuevas", 8);
		
		caveLost.setStartingAttackSpeed(Speed.NORMAL);
		caveLost.setStartingMovementSpeed(Speed.FAST);
		
		caveLost.modifyAttackValue(DamageType.SLICE, 2);
		
		caveLost.setVisionRadius(8);
		
		world.addAtEmptyLocation(caveLost);
		new PaseacuevasLostAi(caveLost, player);
		return caveLost;
	}
		
	public Creature newCaveSmall(Creature player, Creature female){
		Creature caveSmall = new Creature(world, 'p', 'M', AsciiPanel.brightGreen, "paseacuevas", 8);
		caveSmall.setStartingAttackSpeed(Speed.NORMAL);
		caveSmall.setStartingMovementSpeed(Speed.FAST);
		caveSmall.setVisionRadius(8);
		
		caveSmall.modifyAttackValue(DamageType.BLUNT, 2);
		
		world.addAtEmptySpace(caveSmall, female.x, female.y);
		new PaseacuevasMaleAi(caveSmall, player, female);
		return caveSmall;
	}
	
	public Creature newMarauder(Creature player){
		Creature marauder = new Creature(world, 'm', 'M', AsciiPanel.brightYellow, "merodeador", 12);
		marauder.setStartingAttackSpeed(Speed.NORMAL);
		marauder.setStartingMovementSpeed(Speed.NORMAL);
		marauder.setVisionRadius(6);
		
		marauder.inventory().add(newMarauderPoison(false));
		
		if(Math.random() < .3f)
			marauder.equip(newMarauderVest(false));
		if(Math.random() < .3f)
			marauder.equip(newMarauderHood(false));
		if(Math.random() < .3f)
			marauder.inventory().add(randomThrowableWeapon(false, false));
			
		marauder.inventory().add(randomWeapon(null, false, true, false, false, false));
		
		world.addAtEmptyLocation(marauder );
		new MarauderAi(marauder, player);
		return marauder;
	}
	
	public Creature newHugeMarauder(Creature player){
		Creature bigMarauder = new Creature(world, 'M', 'M', AsciiPanel.brightYellow, "merodeador gigante", 50);
		
		bigMarauder.setVisionRadius(6);
		bigMarauder.setStartingAttackSpeed(Speed.VERY_SLOW);
		bigMarauder.setStartingMovementSpeed(Speed.NORMAL);
		bigMarauder.modifyAttackValue(DamageType.BLUNT, 8);

		bigMarauder.equip(newMarauderVest(false));
		bigMarauder.equip(newMarauderHood(false));
		
		for(int i = 0; i < Math.random() * 3; i++)
			bigMarauder.inventory().add(newGiantRock(false));
				
		world.addAtEmptyLocation(bigMarauder );
		new BigMarauderAi(bigMarauder, player);
		return bigMarauder;
	}
	
	public Creature newRockBug(Creature player){
		Creature rockBug = new Creature(world, 'c', 'M', AsciiPanel.yellow, "comepiedras", 6);
		
		rockBug.setStartingAttackSpeed(Speed.NORMAL);
		rockBug.setStartingMovementSpeed(Speed.FAST);
		rockBug.setVisionRadius(2);
		
		rockBug.modifyDefenseValue(DamageType.SLICE, 3);
		rockBug.modifyDefenseValue(DamageType.BLUNT, 2);
		
		if(Math.random() > 0.2f){
			rockBug.pickup(newRockBugHelm(false));
		}
		
		world.addAtEmptyLocation(rockBug );
		new RockBugAi(rockBug, player, this);
		return rockBug;
	}
	
	public Creature newHidden(Creature player){
		Creature hidden = new Creature(world, 'a', 'M', AsciiPanel.yellow, "acechador", 6);
		
		hidden.modifyDefenseValue(DamageType.BLUNT, 4);
		hidden.modifyDefenseValue(DamageType.PIERCING, 2);
		
		hidden.modifyAttackValue(DamageType.PIERCING, 6);
		hidden.modifyAttackSpeed(Speed.VERY_FAST);
		hidden.modifyMovementSpeed(Speed.SLOW);
		hidden.setVisionRadius(6);
		
		world.addAtEmptyLocation(hidden);
		new HiddenAi(hidden, player);
		return hidden;
	}

	/* ########################################################################################
	 * ########################################################################################
	 * ########################################################################################
	 */
	
	private int DROP_1_CHANCE = 200;
	private int DROP_2_CHANCE = 160;
	private int DROP_3_CHANCE = 80;
	private int DROP_4_CHANCE = 40;
	private int DROP_5_CHANCE = 10;
	
	private int DROP_RANGED_HANDICAP = 60;
	
	public Item newRock(boolean spawn){
		ArrayList<Item> rockItems = new ArrayList<Item>();
		rockItems.add(newSmallRock(false));
		rockItems.add(newGiantRock(false));
		rockItems.add(newRockMace(false));
		return randomWeapon(rockItems, spawn, false, false, false, false);
	}
	
	public Item newSmallRock(boolean spawn){
		Item rock = new Item(',', 'F', AsciiPanel.yellow, "roca", null, 100);
		rock.modifyAttackValue(DamageType.BLUNT, 3);
		rock.makeStackable(5);
		world.addAtEmptyLocation(rock, spawn);
		return rock;
	}
	
	public Item newGiantRock(boolean spawn){
		Item rock = new Item(';', 'F', AsciiPanel.yellow, "roca gigante", null, 40);
		rock.modifyAttackValue(DamageType.BLUNT, 8);
		rock.modifyAttackSpeed(Speed.FAST);
		rock.modifyMovementSpeed(Speed.FAST);
		world.addAtEmptyLocation(rock, spawn);
		return rock;
	}
	
	public Item newAlcoholBottle(boolean spawn){
		final Item alcohol = new Item('!', 'M', AsciiPanel.white, "alcohol", null, 100);
		alcohol.setQuaffEffect(new Effect("borracho", 1, true){
			public void start(Creature creature){
				creature.doAction(alcohol, "bebe la botella completa");
				if(Math.random() > .8){
					creature.modifyHp(-3, "Alcoholismo");
					creature.notify("El alcohol se te sube a la cabeza");
				}
			}
		});
		alcohol.makeStackable(5);
		world.addAtEmptyLocation(alcohol, spawn);
		return alcohol;
	}
	
	public Item newKnife(boolean spawn){
		Item knife = new Item((char)255, 'M', AsciiPanel.brightWhite, "cuchillo", "cuchillo", DROP_1_CHANCE);
		knife.setData(Constants.CHECK_PROJECTILE_AUTOTARGET, true);
		knife.modifyAttackValue(DamageType.PIERCING, 1);
		knife.modifyAttackValue(DamageType.RANGED, 1);
		knife.setData(Constants.CHECK_WEAPON, true);
		knife.modifyAttackSpeed(Speed.FAST);
		knife.modifyBloodModifyer(0.7f);
		knife.makeStackable(3);
		world.addAtEmptyLocation(knife, spawn);
		return knife;
	}
	
	public Item newDagger(boolean spawn){
		Item item = new Item((char)255, 'F', AsciiPanel.white, "daga", "daga", DROP_1_CHANCE);
		item.modifyAttackValue(DamageType.PIERCING, 2);
		item.setData(Constants.CHECK_WEAPON, true);
		item.modifyAttackSpeed(Speed.FAST);
		item.modifyBloodModifyer(0.7f);
		item.makeStackable(3);
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
	
	public Item newCurveDagger(boolean spawn){
		Item item = new Item((char)255, 'F', AsciiPanel.yellow, "daga curva", "daga curva", DROP_2_CHANCE);
		item.modifyAttackValue(DamageType.PIERCING, 4);
		item.modifyAttackSpeed(Speed.FAST);
		item.setData(Constants.CHECK_WEAPON, true);
		item.modifyBloodModifyer(0.75f);
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
	
	public Item newLance(boolean spawn){
		Item item = new Item((char)244, 'F', AsciiPanel.white, "lanza", "lanza", DROP_3_CHANCE);
		item.modifyAttackValue(DamageType.SLICE, 2);
		item.modifyAttackValue(DamageType.PIERCING, 4);
		
		item.setData(Constants.CHECK_WEAPON, true);
		item.setData(Constants.CHECK_TWO_HANDED, true);
		item.modifyReach(2);
		item.modifyAttackSpeed(Speed.NORMAL);
		
		item.modifyBloodModifyer(0.6f);
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
	
	public Item newLongLance(boolean spawn){
		Item item = new Item((char)244, 'F', AsciiPanel.brightWhite, "lanza larga", "lanza larga", DROP_4_CHANCE);
		item.modifyAttackValue(DamageType.SLICE, 2);
		item.modifyAttackValue(DamageType.PIERCING, 8);
		
		item.setData(Constants.CHECK_WEAPON, true);
		item.setData(Constants.CHECK_TWO_HANDED, true);
		item.modifyAttackSpeed(Speed.NORMAL);
		item.modifyReach(3);
		item.modifyCantReach(1);
		
		item.modifyBloodModifyer(0.6f);
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
	
	public Item newKnightLance(boolean spawn){
		Item item = new Item((char)244, 'F', AsciiPanel.brightBlue, "lanza de caballeria", "lanza de caballeria", DROP_4_CHANCE);
		item.modifyAttackValue(DamageType.SLICE, 2);
		item.modifyAttackValue(DamageType.BLUNT, 2);
		item.modifyAttackValue(DamageType.PIERCING, 6);
		
		item.setData(Constants.CHECK_WEAPON, true);
		item.modifyAttackSpeed(Speed.SLOW);
		item.modifyReach(2);

		item.modifyBloodModifyer(0.6f);
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
	
	public Item newJavelin(boolean spawn){
		Item item = new Item((char)244, 'F', AsciiPanel.white, "javalina", "javalina", DROP_2_CHANCE);
		item.setData(Constants.CHECK_PROJECTILE_AUTOTARGET, true);
		item.modifyAttackValue(DamageType.PIERCING, 4);
		item.modifyAttackValue(DamageType.RANGED, 2);
		
		item.setData(Constants.CHECK_WEAPON, true);
		item.modifyAttackSpeed(Speed.FAST);
		
		item.modifyBloodModifyer(0.6f);
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
	
	public Item newSchyte(boolean spawn){
		Item item = new Item((char)244, 'F', AsciiPanel.white, "guadaña", "guadaña", DROP_2_CHANCE);
		item.modifyAttackValue(DamageType.SLICE, 2);
		item.modifyAttackValue(DamageType.PIERCING, 6);
		
		item.setData(Constants.CHECK_WEAPON, true);
		item.setData(Constants.CHECK_TWO_HANDED, true);
		item.modifyReach(2);
		item.modifyCantReach(1);
		item.modifyAttackSpeed(Speed.NORMAL);
		
		item.modifyBloodModifyer(0.85f);
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
	
	public Item newCimitarra(boolean spawn){
		Item item = new Item((char)253, 'F', new Color(167, 166, 166), "cimitarra", "cimitarra", DROP_3_CHANCE);
		item.modifyAttackValue(DamageType.SLICE, 6);
		item.setData(Constants.CHECK_WEAPON, true);
		item.modifyAttackSpeed(Speed.FAST);
		item.modifyBloodModifyer(0.6f);
		
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
	
	public Item newSword(boolean spawn){
		Item item = new Item((char)253, 'F', AsciiPanel.brightWhite, "espada", "espada", DROP_2_CHANCE);
		item.modifyAttackValue(DamageType.SLICE, 6);
		item.setData(Constants.CHECK_WEAPON, true);
		item.modifyAttackSpeed(Speed.NORMAL);
		item.modifyBloodModifyer(0.6f);
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
	
	public Item newLongSword(boolean spawn){
		Item item = new Item((char)253, 'F', Color.DARK_GRAY, "espada larga", "espada larga", DROP_3_CHANCE);
		item.modifyAttackValue(DamageType.SLICE, 7);
		item.modifyAttackValue(DamageType.BLUNT, 1);
		item.setData(Constants.CHECK_WEAPON, true);
		item.modifyAttackSpeed(Speed.NORMAL);
		item.modifyBloodModifyer(0.6f);
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
	
	public Item newMandoble(boolean spawn){
		Item item = new Item((char)253, 'F', Color.green, "mandoble", "mandoble", DROP_3_CHANCE);
		item.modifyAttackValue(DamageType.SLICE, 8);
		item.modifyAttackValue(DamageType.BLUNT, 2);
		
		item.setData(Constants.CHECK_TWO_HANDED, true);
		item.setData(Constants.CHECK_WEAPON, true);
		item.modifyAttackSpeed(Speed.NORMAL);
		item.modifyBloodModifyer(0.6f);
		
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
	
	public Item newShortSword(boolean spawn){
		Item item = new Item((char)253, 'F', AsciiPanel.brightWhite, "espada corta", "espada corta", DROP_1_CHANCE);
		item.modifyAttackValue(DamageType.SLICE, 4);
		item.setData(Constants.CHECK_WEAPON, true);
		item.modifyAttackSpeed(Speed.NORMAL);
		item.modifyBloodModifyer(0.6f);
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
	
	public Item newBastardSword(boolean spawn){
		Item item = new Item((char)253, 'F', AsciiPanel.brightBlack, "espada bastarda", "espada bastarda", DROP_2_CHANCE);
		item.modifyAttackValue(DamageType.SLICE, 8);
		item.setData(Constants.CHECK_WEAPON, true);
		item.modifyAttackSpeed(Speed.SLOW);
		item.modifyBloodModifyer(0.6f);
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
	
	public Item newBigSword(boolean spawn){
		Item bigSword = new Item((char)253, 'M', Color.gray, "espadon", "espadon", DROP_3_CHANCE);
		bigSword.modifyAttackValue(DamageType.SLICE, 10);
		bigSword.modifyAttackSpeed(Speed.NORMAL);
		bigSword.setData(Constants.CHECK_WEAPON, true);
		bigSword.setData(Constants.CHECK_TWO_HANDED, true);
		bigSword.modifyBloodModifyer(0.6f);
		world.addAtEmptyLocation(bigSword, spawn);
		return bigSword;
	}
	
	public Item newHugeSword(boolean spawn){
		Item hugeSword = new Item((char)253, 'F', AsciiPanel.brightBlack, "espada gigante", "espada gigante", DROP_3_CHANCE);
		hugeSword.modifyAttackValue(DamageType.SLICE, 10);
		hugeSword.modifyAttackValue(DamageType.BLUNT, 2);
		hugeSword.modifyAttackSpeed(Speed.SLOW);
		hugeSword.modifyMovementSpeed(Speed.FAST);
		hugeSword.setData(Constants.CHECK_WEAPON, true);
		hugeSword.setData(Constants.CHECK_TWO_HANDED, true);
		hugeSword.modifyBloodModifyer(0.8f);
		world.addAtEmptyLocation(hugeSword, spawn);
		return hugeSword;
	}
	
	public Item newAxe(boolean spawn){
		Item item = new Item((char)243, 'M', Color.LIGHT_GRAY, "hacha", "hacha", DROP_1_CHANCE);
		item.modifyAttackValue(DamageType.SLICE, 2);
		item.modifyAttackValue(DamageType.BLUNT, 2);
		item.setData(Constants.CHECK_WEAPON, true);
		item.modifyAttackSpeed(Speed.NORMAL);
		item.modifyBloodModifyer(0.7f);
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
	
	public Item newBigAxe(boolean spawn){
		Item item = new Item((char)243, 'M', Color.LIGHT_GRAY, "hacha grande", "hacha grande", DROP_2_CHANCE);
		item.modifyAttackValue(DamageType.SLICE, 4);
		item.modifyAttackValue(DamageType.BLUNT, 4);
		item.setData(Constants.CHECK_WEAPON, true);
		item.modifyAttackSpeed(Speed.SLOW);
		item.modifyBloodModifyer(0.7f);
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
	
	public Item newWarAxe(boolean spawn){
		Item item = new Item((char)243, 'M', AsciiPanel.brightBlack, "hacha de guerra", "hacha de guerra", DROP_3_CHANCE);
		item.modifyAttackValue(DamageType.SLICE, 4);
		item.modifyAttackValue(DamageType.BLUNT, 4);
		item.setData(Constants.CHECK_WEAPON, true);
		item.modifyAttackSpeed(Speed.NORMAL);
		item.modifyBloodModifyer(0.7f);
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
	
	public Item newThrowAxe(boolean spawn){
		Item item = new Item((char)243, 'M', AsciiPanel.yellow, "hacha alada", "hacha alada", DROP_1_CHANCE);
		item.setData(Constants.CHECK_PROJECTILE_AUTOTARGET, true);
		item.modifyAttackValue(DamageType.SLICE, 2);
		item.modifyAttackValue(DamageType.BLUNT, 2);
		item.setData(Constants.CHECK_WEAPON, true);
		item.modifyAttackSpeed(Speed.NORMAL);
		item.modifyBloodModifyer(0.6f);
		item.makeStackable(3);
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
	
	public Item newMace(boolean spawn){
		Item item = new Item((char)254, 'M', Color.LIGHT_GRAY, "maza", "maza", DROP_1_CHANCE);
		item.modifyAttackValue(DamageType.BLUNT, 4);
		item.setData(Constants.CHECK_WEAPON, true);
		item.modifyAttackSpeed(Speed.FAST);
		item.modifyBloodModifyer(0.4f);
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
	
	public Item newHammer(boolean spawn){
		Item item = new Item((char)254, 'M', AsciiPanel.white, "martillo", "martillo", DROP_2_CHANCE);
		item.modifyAttackValue(DamageType.BLUNT, 8);
		item.setData(Constants.CHECK_WEAPON, true);
		item.modifyAttackSpeed(Speed.SLOW);
		item.modifyBloodModifyer(0.4f);
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
	
	public Item newWarHammer(boolean spawn){
		Item item = new Item((char)254, 'M', AsciiPanel.white, "martillo de guerra", "martillo de guerra", DROP_3_CHANCE);
		item.modifyAttackValue(DamageType.BLUNT, 10);
		item.setData(Constants.CHECK_WEAPON, true);
		item.modifyAttackSpeed(Speed.SLOW);
		item.modifyBloodModifyer(0.4f);
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
	
	public Item newGiantHammer(boolean spawn){
		Item item = new Item((char)254, 'M', AsciiPanel.brightBlack, "martillo gigante", "martillo gigante", DROP_4_CHANCE);
		item.modifyAttackValue(DamageType.BLUNT, 14);
		item.setData(Constants.CHECK_WEAPON, true);
		item.setData(Constants.CHECK_TWO_HANDED, true);
		item.modifyAttackSpeed(Speed.SLOW);
		item.modifyBloodModifyer(0.4f);
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
	
	public Item newBallAndChain(boolean spawn){
		Item item = new Item((char)254, 'M', Color.LIGHT_GRAY, "bola y cadena", "bola y cadena", DROP_2_CHANCE);
		item.modifyAttackValue(DamageType.BLUNT, 6);
		item.setData(Constants.CHECK_WEAPON, true);
		item.modifyAttackSpeed(Speed.NORMAL);
		item.modifyCantReach(1);
		item.modifyReach(2);
		item.modifyBloodModifyer(0.4f);
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
	
	public Item newRockMace(boolean spawn){
		Item rockMace = new Item((char)254, 'F', AsciiPanel.yellow, "estalactita", null, 10);
		rockMace.modifyAttackValue(DamageType.BLUNT, 6);
		rockMace.setData(Constants.CHECK_WEAPON, true);
		rockMace.setData(Constants.CHECK_UNAUGMENTABLE, true);
		rockMace.modifyAttackSpeed(Speed.FAST);
		
		rockMace.modifyBloodModifyer(0.4f);
		rockMace.modifyDurability((int) (Math.random() * (30 - 10)) + 10);
		world.addAtEmptyLocation(rockMace, spawn);
		return rockMace;
	}
	
	public Item newMorningStar(boolean spawn){
		Item morningStar = new Item((char)254, 'F', AsciiPanel.brightBlack, "estrella", "estrella", DROP_2_CHANCE);
		morningStar.modifyAttackValue(DamageType.SLICE, 1);
		morningStar.modifyAttackValue(DamageType.BLUNT, 4);
		morningStar.modifyAttackValue(DamageType.PIERCING, 1);
		morningStar.setData(Constants.CHECK_WEAPON, true);
		morningStar.modifyAttackSpeed(Speed.NORMAL);
		morningStar.modifyBloodModifyer(0.6f);
		world.addAtEmptyLocation(morningStar, spawn);
		return morningStar;
	}
	
	public Item newBigMace(boolean spawn){
		Item bigMace = new Item((char)254, 'F', Color.DARK_GRAY, "maza de piedra", "maza de piedra", DROP_2_CHANCE);
		bigMace.modifyAttackValue(DamageType.BLUNT, 8);
		bigMace.modifyAttackSpeed(Speed.SLOW);
		bigMace.modifyMovementSpeed(Speed.FAST);
		bigMace.setData(Constants.CHECK_UNAUGMENTABLE, true);
		bigMace.modifyBloodModifyer(0.8f);
		bigMace.setData(Constants.CHECK_WEAPON, true);
		world.addAtEmptyLocation(bigMace, spawn);
		return bigMace;
	}
	
	public Item newHalberd(boolean spawn){
		Item item = new Item((char)244, 'M', Color.LIGHT_GRAY, "alabarda", "alabarda", DROP_3_CHANCE);
		item.modifyAttackValue(DamageType.SLICE, 4);
		item.modifyAttackValue(DamageType.BLUNT, 2);
		
		item.setData(Constants.CHECK_WEAPON, true);
		item.setData(Constants.CHECK_TWO_HANDED, true);
		item.modifyAttackSpeed(Speed.NORMAL);
		item.modifyReach(2);

		item.modifyBloodModifyer(0.7f);
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
	
	public Item newShortBow(boolean spawn){
		Item item = new Item(')', 'M', AsciiPanel.yellow, "arco corto", "arco corto", DROP_1_CHANCE + DROP_RANGED_HANDICAP);
		item.modifyAttackValue(DamageType.RANGED, 1);
		item.modifyAttackValue(DamageType.PIERCING, 3);
		item.modifyAttackSpeed(Speed.FAST);
		item.modifyBloodModifyer(0.5f);
		item.modifyDurability((int) (Math.random() * (50 - 10)) + 10);
		
		item.setData(Constants.CHECK_WEAPON, true);
		item.setData(Constants.CHECK_RANGED, true);
		
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
	
	public Item newCompositeBow(boolean spawn){
		Item item = new Item(')', 'M', Color.gray, "arco compuesto", "arco compuesto", DROP_1_CHANCE + DROP_RANGED_HANDICAP);
		item.modifyAttackValue(DamageType.RANGED, 2);
		item.modifyAttackValue(DamageType.PIERCING, 4);
		item.modifyAttackSpeed(Speed.NORMAL);
		item.modifyBloodModifyer(0.5f);
		item.modifyDurability((int) (Math.random() * (50 - 30)) + 30);
		
		item.setData(Constants.CHECK_WEAPON, true);
		item.setData(Constants.CHECK_RANGED, true);
		
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
	
	public Item newLongBow(boolean spawn){
		Item item = new Item(')', 'M', AsciiPanel.white, "arco largo", "arco largo", DROP_2_CHANCE + DROP_RANGED_HANDICAP);
		item.modifyAttackValue(DamageType.RANGED, 2);
		item.modifyAttackValue(DamageType.PIERCING, 6);
		item.modifyAttackSpeed(Speed.SLOW);
		item.modifyBloodModifyer(0.5f);
		
		item.setData(Constants.CHECK_TWO_HANDED, true);
		item.setData(Constants.CHECK_WEAPON, true);
		item.setData(Constants.CHECK_RANGED, true);
		
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
	
	public Item newCrossbow(boolean spawn){
		Item item = new Item(')', 'F', Color.gray, "ballesta", "ballesta", DROP_3_CHANCE + DROP_RANGED_HANDICAP);
		item.modifyAttackValue(DamageType.RANGED, 4);
		item.modifyAttackValue(DamageType.BLUNT, 2);
		item.modifyAttackSpeed(Speed.FAST);
		item.modifyBloodModifyer(0.5f);
		
		item.setData(Constants.CHECK_WEAPON, true);
		item.setData(Constants.CHECK_RANGED, true);
		
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
	
	public Item newHeavyCrossbow(boolean spawn){
		Item item = new Item(')', 'F', Color.darkGray, "ballesta pesada", "ballesta pesada", DROP_4_CHANCE + DROP_RANGED_HANDICAP);
		item.modifyAttackValue(DamageType.RANGED, 6);
		item.modifyAttackValue(DamageType.BLUNT, 2);
		item.modifyAttackSpeed(Speed.FAST);
		item.modifyBloodModifyer(0.5f);
		
		item.setData(Constants.CHECK_WEAPON, true);
		item.setData(Constants.CHECK_RANGED, true);
		item.setData(Constants.CHECK_TWO_HANDED, true);
		
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
	
	public Item newHunterBow(boolean spawn){
		Item item = new Item(')', 'M', AsciiPanel.green, "arco cazador", "arco cazador", DROP_3_CHANCE + DROP_RANGED_HANDICAP);
		item.modifyAttackValue(DamageType.RANGED, 2);
		item.modifyAttackValue(DamageType.PIERCING, 8);
		item.modifyAttackSpeed(Speed.SLOW);
		item.modifyBloodModifyer(0.5f);
		
		item.setData(Constants.CHECK_TWO_HANDED, true);
		item.setData(Constants.CHECK_WEAPON, true);
		item.setData(Constants.CHECK_RANGED, true);
		
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
	
	public Item newKatar(boolean spawn){
		Item item = new Item((char)255, 'M', AsciiPanel.white, "katar", "katar", DROP_3_CHANCE);
		item.modifyAttackValue(DamageType.PIERCING, 3);
		item.modifyAttackValue(DamageType.SLICE, 3);
		
		item.setData(Constants.CHECK_WEAPON, true);
		item.setData(Constants.CHECK_DUAL_WIELD, true);
		item.modifyAttackSpeed(Speed.FAST);
		
		item.modifyBloodModifyer(.75f);
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
	
	public Item newWoodenShield(boolean spawn){
		Item woodenShield = new Item('#', 'M', AsciiPanel.yellow, "escudo de madera", "escudo de madera", 70);
		woodenShield.modifyDefenseValue(DamageType.SLICE, 4);
		woodenShield.modifyDefenseValue(DamageType.BLUNT, 4);
		woodenShield.modifyDefenseValue(DamageType.PIERCING, 3);
		woodenShield.setData(Constants.CHECK_SHIELD, true);
		woodenShield.modifyDurability((int) (Math.random() * (20 - 10)) + 10);
		world.addAtEmptyLocation(woodenShield, spawn);
		return woodenShield;
	}
	
	public Item newRoundShield(boolean spawn){
		Item roundShield = new Item('#', 'M', AsciiPanel.brightBlack, "escudo redondo", "escudo redondo", 40);
		roundShield.modifyDefenseValue(DamageType.SLICE, 5);
		roundShield.modifyDefenseValue(DamageType.BLUNT, 5);
		roundShield.modifyDefenseValue(DamageType.PIERCING, 4);
		roundShield.setData(Constants.CHECK_SHIELD, true);
		roundShield.modifyDurability((int) (Math.random() * (40 - 20)) + 20);
		world.addAtEmptyLocation(roundShield, spawn);
		return roundShield;
	}
	
	public Item newWoodPieceShield(boolean spawn){
		Item woodPieceShield = new Item('#', 'M', AsciiPanel.yellow, "trozo de madera", "trozo de madera", 80);
		woodPieceShield.modifyDefenseValue(DamageType.SLICE, 3);
		woodPieceShield.modifyDefenseValue(DamageType.BLUNT, 3);
		woodPieceShield.modifyDefenseValue(DamageType.PIERCING, 2);
		woodPieceShield.setData(Constants.CHECK_SHIELD, true);
		woodPieceShield.makeStackable(3);
		woodPieceShield.modifyDurability((int) (Math.random() * (20 - 10)) + 10);
		world.addAtEmptyLocation(woodPieceShield, spawn);
		return woodPieceShield;
	}
	
	public Item newRockBugHelm(boolean spawn){
		Item rockBugHelm = new Item((char)248, 'M', AsciiPanel.yellow, "caparazon de comepiedra", null, 100);
		rockBugHelm.modifyDefenseValue(DamageType.BLUNT, 1);
		rockBugHelm.setData(Constants.CHECK_HELMENT, true);
		world.addAtEmptyLocation(rockBugHelm, spawn);
		return rockBugHelm;
	}
	
	public Item newBread(boolean spawn){
		Item item = new Item('%', 'M', AsciiPanel.yellow, "pan", null, 50);
		item.setData(Constants.CHECK_CONSUMABLE, true);
		item.setQuaffEffect(new Effect("alimentado", 1, false){
			public void start(Creature creature){
				creature.modifyHp(5, "Comiste demasiado");
			}
		});
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
	
	public Item newFruit(boolean spawn){
		Item item = new Item('%', 'F', AsciiPanel.brightRed, "manzana", null, 60);
		item.setData(Constants.CHECK_CONSUMABLE, true);
		item.setQuaffEffect(new Effect("alimentado", 1, false){
			public void start(Creature creature){
				creature.modifyHp(8, "Comiste demasiado");
			}
		});
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
	
	public Item newEdibleWeapon(boolean spawn){
		Item item = new Item(')', 'F', AsciiPanel.yellow, "baguette", null, 40);
		item.modifyAttackValue(DamageType.BLUNT, 1);
		item.modifyBloodModifyer(0.1f);
		item.setData(Constants.CHECK_WEAPON, true);
		item.setData(Constants.CHECK_CONSUMABLE, true);
		item.setQuaffEffect(new Effect("alimentado", 1, false){
			public void start(Creature creature){
				creature.modifyHp(10, "El pan estaba tan duro que te mato");
			}
		});
		item.description = "Es dura como una roca.";
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
	
	public Item newMarauderHood(boolean spawn){
		Item marauderHood = new Item((char)248, 'F', AsciiPanel.brightYellow, "capucha de merodeador", "capucha de merodeador", DROP_1_CHANCE);
		marauderHood.modifyDefenseValue(DamageType.PIERCING, 1);
		marauderHood.setData(Constants.CHECK_HELMENT, true);
		marauderHood.setData(Constants.CHECK_MARAUDER_DISGUISE, true);
		marauderHood.setData(Constants.CHECK_UNAUGMENTABLE, true);
		marauderHood.description = "Cubre el rostro completamente.";
		world.addAtEmptyLocation(marauderHood, spawn);
		return marauderHood;
	}
	
	public Item newMarauderVest(boolean spawn){
		Item marauderVest = new Item((char)252, 'M', AsciiPanel.brightYellow, "abrigo de merodeador", "abrigo de merodeador", DROP_1_CHANCE);
		marauderVest.modifyDefenseValue(DamageType.SLICE, 2);
		marauderVest.modifyDefenseValue(DamageType.BLUNT, 1);
		marauderVest.setData(Constants.CHECK_MARAUDER_DISGUISE, true);
		marauderVest.setData(Constants.CHECK_UNAUGMENTABLE, true);
		marauderVest.setData(Constants.CHECK_ARMOR, true);
		world.addAtEmptyLocation(marauderVest, spawn);
		return marauderVest;
	}
	
	public Item newLeatherArmor(boolean spawn){
		Item leatherArmor = new Item((char)252, 'F', Color.orange, "armadura de cuero", "armadura de cuero", DROP_2_CHANCE);
		leatherArmor.modifyDefenseValue(DamageType.SLICE, 3);
		leatherArmor.modifyDefenseValue(DamageType.BLUNT, 1);
		leatherArmor.modifyDefenseValue(DamageType.PIERCING, 1);
		leatherArmor.setData(Constants.CHECK_ARMOR, true);
		leatherArmor.modifyMovementSpeed(Speed.FAST);
		world.addAtEmptyLocation(leatherArmor, spawn);
		return leatherArmor;
	}
	
	public Item newLeatherMediumArmor(boolean spawn){
		Item leatherArmor = new Item((char)252, 'M', AsciiPanel.yellow, "cuero tachonado", "cuero tachonado", DROP_2_CHANCE);
		leatherArmor.modifyDefenseValue(DamageType.SLICE, 2);
		leatherArmor.modifyDefenseValue(DamageType.BLUNT, 2);
		leatherArmor.modifyDefenseValue(DamageType.PIERCING, 1);
		leatherArmor.setData(Constants.CHECK_ARMOR, true);
		leatherArmor.modifyMovementSpeed(Speed.FAST);
		world.addAtEmptyLocation(leatherArmor, spawn);
		return leatherArmor;
	}
	
	public Item newMediumArmor(boolean spawn){
		Item leatherArmor = new Item((char)252, 'F', AsciiPanel.brightWhite, "cota de malla", "cota de malla", DROP_2_CHANCE);
		leatherArmor.modifyDefenseValue(DamageType.SLICE, 2);
		leatherArmor.modifyDefenseValue(DamageType.PIERCING, 3);
		leatherArmor.setData(Constants.CHECK_ARMOR, true);
		leatherArmor.modifyMovementSpeed(Speed.FAST);
		world.addAtEmptyLocation(leatherArmor, spawn);
		return leatherArmor;
	}

	public Item newLightArmor(boolean spawn){
		Item item = new Item((char)252, 'F', AsciiPanel.green, "tunica", "tunica", DROP_2_CHANCE);
		item.modifyDefenseValue(DamageType.SLICE, 1);
		item.modifyDefenseValue(DamageType.PIERCING, 2);
		item.setData(Constants.CHECK_ARMOR, true);
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
	
	public Item newFullArmor(boolean spawn){
		Item item = new Item((char)249, 'F', AsciiPanel.white, "armadura completa", "armadura completa", DROP_2_CHANCE);
		item.modifyDefenseValue(DamageType.SLICE, 3);
		item.modifyDefenseValue(DamageType.BLUNT, 3);
		item.modifyDefenseValue(DamageType.PIERCING, 1);
		item.modifyMovementSpeed(Speed.NORMAL);
		
		item.setData(Constants.CHECK_ARMOR, true);
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
	
	public Item newLaminatedArmor(boolean spawn){
		Item item = new Item((char)249, 'F', AsciiPanel.brightWhite, "armadura laminada", "armadura laminada", DROP_3_CHANCE);
		item.modifyDefenseValue(DamageType.SLICE, 3);
		item.modifyDefenseValue(DamageType.BLUNT, 4);
		item.modifyMovementSpeed(Speed.FAST);
		
		item.setData(Constants.CHECK_ARMOR, true);
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
	
	public Item newCoraza(boolean spawn){
		Item item = new Item((char)249, 'F', AsciiPanel.brightWhite, "coraza", "coraza", DROP_3_CHANCE);
		item.modifyDefenseValue(DamageType.SLICE, 1);
		item.modifyDefenseValue(DamageType.BLUNT, 7);
		item.modifyDefenseValue(DamageType.PIERCING, 1);
		item.modifyMovementSpeed(Speed.NORMAL);
		
		item.setData(Constants.CHECK_ARMOR, true);
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
	
	public Item newBandArmor(boolean spawn){
		Item item = new Item((char)249, 'F', AsciiPanel.green, "cota de bandas", "cota de bandas", DROP_3_CHANCE);
		item.modifyDefenseValue(DamageType.SLICE, 4);
		item.modifyDefenseValue(DamageType.BLUNT, 4);
		item.modifyDefenseValue(DamageType.PIERCING, 1);
		item.modifyMovementSpeed(Speed.NORMAL);
		
		item.setData(Constants.CHECK_ARMOR, true);
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
	
	public Item newWarArmor(boolean spawn){
		Item item = new Item((char)249, 'F', Color.gray, "armadura de guerra", "armadura de guerra", DROP_4_CHANCE);
		item.modifyDefenseValue(DamageType.SLICE, 4);
		item.modifyDefenseValue(DamageType.BLUNT, 5);
		item.modifyDefenseValue(DamageType.PIERCING, 2);
		item.modifyMovementSpeed(Speed.NORMAL);
		
		item.setData(Constants.CHECK_ARMOR, true);
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
	
	public Item newBigWarArmor(boolean spawn){
		Item item = new Item((char)249, 'F', AsciiPanel.brightBlack, "cota de guerra", "cota de guerra", DROP_5_CHANCE);
		item.modifyDefenseValue(DamageType.SLICE, 4);
		item.modifyDefenseValue(DamageType.BLUNT, 5);
		item.modifyDefenseValue(DamageType.PIERCING, 4);
		item.modifyMovementSpeed(Speed.SLOW);
		
		item.setData(Constants.CHECK_ARMOR, true);
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
	
	public Item newElefantArmor(boolean spawn){
		Item item = new Item((char)249, 'F', Color.darkGray, "armadura elefante", "armadura elefante", DROP_5_CHANCE);
		item.modifyDefenseValue(DamageType.SLICE, 6);
		item.modifyDefenseValue(DamageType.BLUNT, 6);
		item.modifyDefenseValue(DamageType.PIERCING, 3);
		item.modifyMovementSpeed(Speed.VERY_SLOW);
		
		item.setData(Constants.CHECK_ARMOR, true);
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
	
	public Item randomThrowableWeapon(boolean spawn, boolean fullyStacked){
		ArrayList<Item> items = new ArrayList<Item>();
		
		items.add(newKnife(false));
		items.add(newThrowAxe(false));
		items.add(newJavelin(false));
		
		Item to_return = randomWeapon(items, spawn, false, false, false, false);
		
		if(fullyStacked)
			to_return.modifyStacks(to_return.maxStacks);
		
		return to_return;
	}
	
	public Item randomWeapon(ArrayList<Item> pool, boolean spawn, boolean includeCommons, boolean includeUncommon, boolean includeRanged, boolean includeHeavy){
		ArrayList<Item> items = new ArrayList<Item>();
		
		if(includeCommons){
			items.add(newShortSword(false));
			items.add(newMace(false));
			items.add(newDagger(false));
			items.add(newMorningStar(false));
			items.add(newSword(false));
			items.add(newBastardSword(false));
			items.add(newAxe(false));
			items.add(newHammer(false));
			items.add(newLance(false));
		}
		if(includeUncommon){
			items.add(newBallAndChain(false));
			items.add(newCimitarra(false));
			items.add(newCurveDagger(false));
			items.add(newWarAxe(false));
			items.add(newWarHammer(false));
			items.add(newSchyte(false));
			items.add(newJavelin(false));
			items.add(newKnightLance(false));
			items.add(newLongLance(false));
		}
		if(includeHeavy){
			items.add(newLongSword(false));
			items.add(newMandoble(false));
			items.add(newBigSword(false));
			items.add(newHugeSword(false));
			items.add(newBigAxe(false));
			items.add(newGiantHammer(false));
			items.add(newHalberd(false));
		}
		if(includeRanged){
			items.add(newShortBow(false));
			items.add(newCompositeBow(false));
			items.add(newHunterBow(false));
			items.add(newLongBow(false));
			items.add(newCrossbow(false));
			items.add(newHeavyCrossbow(false));
		}
		
		if(pool != null)
			items.addAll(pool);
		
		// Compute the total weight of all items together
		double totalWeight = 0.0d;
		for (Item i : items)
		{
		    totalWeight += i.spawnWeight();
		}
		// Now choose a random item
		int randomIndex = -1;
		double random = Math.random() * totalWeight;
		
		for (int i = 0; i < items.size(); ++i)
		{
		    random -= items.get(i).spawnWeight();
		    if (random <= 0.0d)
		    {
		        randomIndex = i;
		        break;
		    }
		}
		
		Item myRandomItem = items.get(randomIndex);

		if(spawn){
			world.addAtEmptyLocation(myRandomItem);
		}
		
		return myRandomItem;
	}

	public Item randomArmor(ArrayList<Item> pool, boolean spawn, boolean includeLight, boolean includeMedium, boolean includeHeavy, boolean includeShields){
		ArrayList<Item> items = new ArrayList<Item>();
		
		if(includeLight){
			items.add(newLeatherArmor(false));
			items.add(newLeatherMediumArmor(false));
			items.add(newLightArmor(false));
		}
		if(includeMedium){
			items.add(newMediumArmor(false));
			items.add(newBandArmor(false));
		}
		if(includeHeavy){
			items.add(newFullArmor(false));
			items.add(newLaminatedArmor(false));
			items.add(newCoraza(false));
			items.add(newWarArmor(false));
			items.add(newBigWarArmor(false));
			items.add(newElefantArmor(false));
		}
		if(includeShields){
			items.add(newWoodenShield(false));
			items.add(newWoodPieceShield(false));
			items.add(newRoundShield(false));
		}
		
		if(pool != null)
			items.addAll(pool);
		
		// Compute the total weight of all items together
		double totalWeight = 0.0d;
		for (Item i : items)
		{
		    totalWeight += i.spawnWeight();
		}
		// Now choose a random item
		int randomIndex = -1;
		double random = Math.random() * totalWeight;
		
		for (int i = 0; i < items.size(); ++i)
		{
		    random -= items.get(i).spawnWeight();
		    if (random <= 0.0d)
		    {
		        randomIndex = i;
		        break;
		    }
		}
		
		Item myRandomItem = items.get(randomIndex);

		if(spawn){
			world.addAtEmptyLocation(myRandomItem);
		}
		
		return myRandomItem;
	}
	
	/* ########################################################################################
	 * ########################################################################################
	 * ########################################################################################
	 */
	
	public Item newPotionOfHealth(boolean spawn){
		String appearance = potionAppearances.get(0);
		final Item item = new Item((char)245, 'F', potionColors.get(appearance), "pocion de vida", appearance, 60);
		item.setQuaffEffect(new Effect("reanimado", 1){
			public void start(Creature creature){
				if (creature.hp() == creature.totalMaxHp())
					return;
				
				creature.modifyHp(15, "Killed by a health potion?");
				creature.doAction(item, "siente reanimado");
			}
		});
		item.makeStackable(5);
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
		
	public Item newPotionOfFire(boolean spawn){
		String appearance = potionAppearances.get(1);
		final Item item = new Item((char)245, 'F', potionColors.get(appearance), "pocion ignea", appearance, 70);
		item.setQuaffEffect(new Effect("incinerado", 1){
			public void start(int x, int y){
				world.propagate(x, y, 400, Constants.FIRE_TERRAIN);
			}
		});
		item.makeStackable(5);
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
	
	public Item newPotionOfSlowHealth(boolean spawn){
		String appearance = potionAppearances.get(2);
		final Item item = new Item((char)245, 'F', potionColors.get(appearance), "pocion de lenta curacion", appearance, 60);
		item.setQuaffEffect(new Effect("reanimado", 10){
			public void start(Creature creature){
				creature.doAction(item, "siente aliviado");
			}
			
			public void update(Creature creature){
				super.update(creature);
				creature.modifyHp(1, "Killed by a slow health potion?");
			}
		});
		item.makeStackable(5);
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
	
	public Item newPotionOfPoison(boolean spawn){
		String appearance = potionAppearances.get(3);
		final Item item = new Item((char)245, 'F', potionColors.get(appearance), "pocion de veneno", appearance, 70);
		item.setQuaffEffect(new Effect("envenenado", 10){
			public void start( Creature creature){
				creature.doAction(item, "siente |enfermo04|");
			}
			
			public void update(Creature creature){
				super.update(creature);
				creature.receiveDamage(1, DamageType.POISON, "Vomitas tus viceras", true);
			}
		});
		item.makeStackable(5);
		world.addAtEmptyLocation(item, spawn);
		return item;
	}

	
	public Item newPotionOfWarrior(boolean spawn){
		String appearance = potionAppearances.get(4);
		final Item item = new Item((char)245, 'F', potionColors.get(appearance), "pocion del guerrero", appearance, 50);
		item.setQuaffEffect(new Effect("fortalecido", 20){
			public void start(Creature creature){
				creature.modifyAttackValue(DamageType.BLUNT, 5);
				creature.modifyDefenseValue(DamageType.BLUNT, 5);
				creature.doAction(item, "siente mas fuerte");
			}
			public void end(Creature creature){
				creature.modifyAttackValue(DamageType.BLUNT, -5);
				creature.modifyDefenseValue(DamageType.BLUNT, -5);
				creature.doAction("siente amainar su fuerza");
			}
		});
		item.makeStackable(5);
		world.addAtEmptyLocation(item, spawn);
		return item;
	}

	public Item newPotionOfArcher(boolean spawn){
		String appearance = potionAppearances.get(5);
		final Item item = new Item((char)245, 'F', potionColors.get(appearance), "pocion de arqueria", appearance, 70);
		item.setQuaffEffect(new Effect("alerta", 20){
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
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
	
	public Item newMarauderPoison(boolean spawn){
		String appearance = potionAppearances.get(6);
		final Item item = new Item((char)245, 'F', potionColors.get(appearance), "pocion de merodeador", appearance, 100);
		item.setQuaffEffect(new Effect("envenenado", 8, true){
			public void start(Creature creature){
				if(creature.getStringData(Constants.RACE) == "merodeador"){
					creature.doAction("es inmune al veneno de su gente");
				}else{
					if(creature.isPlayer())
						creature.doAction(item, "empapa en |veneno04|!");
					else
						creature.doAction(item, "empapa en |veneno04|!");
				}
			}
			public void update(Creature creature){
				super.update(creature);
				if(creature.getStringData(Constants.RACE) != "merodeador"){
					creature.receiveDamage(2, DamageType.POISON, "El veneno del merodeador consume tus viceras", true);
				}
			}
		});
		item.makeStackable(5);
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
	
	public Item randomPotion(boolean spawn){
		ArrayList<Item> potions = new ArrayList<Item>();
		potions.add(newPotionOfArcher(false));
		potions.add(newPotionOfHealth(false));
		potions.add(newPotionOfPoison(false));
		potions.add(newPotionOfSlowHealth(false));
		potions.add(newPotionOfWarrior(false));
		return randomWeapon(potions, spawn, false, false, false, false);
	}
	
	/* ########################################################################################
	 * ########################################################################################
	 * ########################################################################################
	 */
	
	public Item newLeatherSpellbook(boolean spawn){
		final Item item = new Item('+', 'M', AsciiPanel.brightWhite, "libro de cuero", null, 100);
		
		item.addWrittenSpell("plegaria de vida", 'F', new Effect("reanimado", 1){
			public void start(Creature creature){
				if (creature.hp() == creature.totalMaxHp() ||
						creature.getBooleanData("Blasfemous")){
					creature.notify("Tu plegaria no es escuchada...");
					return;
				}else{
					creature.notify("Pliegas al cielo por vida");
				}
				
				creature.modifyHp(10, "Alcanzado por la palabra de vida");
				creature.doAction(item, "siente mas recuperado!");
			}
		}, 15, 88, Constants.SPELL_HEAL, new Effect("blasfemo", 20){
			public void start(Creature creature){
				creature.notify("Tus plegarias pierden notoriedad!");
				creature.setData("Blasfemous", true);
			}
			public void end(Creature creature){
				creature.unsetData("Blasfemous");
			}
		}, Speed.FAST, false);
		
		item.addWrittenSpell("conjuro doloroso", 'M', new Effect("adolorido", 1){
			public void start(Creature creature){
				creature.notify("Pronuncias la palabra del dolor");
				int amount = creature.receiveDamage(6, DamageType.MAGIC, "Muere abrumado de un dolor insoportable", false);
				
				if(amount > 0){
					creature.doAction(item, "retuerce en agonia!");
				}else{
					creature.doAction(item, "resigna al dolor");
				}
			}
		}, 4, 90, Constants.SPELL_PAIN,  new Effect("adolorido", 1){
			public void start(Creature creature){
				creature.notify("Sientes en tu piel el dolor que infliges");
				creature.receiveDamage(8, DamageType.MAGIC, "Muere abrumado de un dolor insoportable", false);
			}
		},Speed.FAST, true);
		
		world.addAtEmptyLocation(item, spawn);
		return item;
	}
	
//	public Item newWhiteMagesSpellbook() {
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
//		world.addAtEmptyLocation(item );
//		return item;
//	}
//	
//	public Item newBlueMagesSpellbook() {
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
//		world.addAtEmptyLocation(item );
//		return item;
//	}
	

	public Item randomSpellBook(boolean spawn){
		switch ((int)(Math.random() * 2)){
		case 0: return newLeatherSpellbook(spawn);
		default: return newLeatherSpellbook(spawn);
		}
	}
	
}
