package sayden;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import asciiPanel.AsciiPanel;
import sayden.ai.CreatureAi;
import sayden.screens.Screen;

public class Creature extends Thing{
	private World world;
	public World world() { return world; }
	public void setWorld(World newWorld) { this.world = newWorld; }
	
	public int x;
	public int y;
	public Point position() { return new Point(x,y); }
	
	private Spell queSpell;
	private Creature queSpellCreature;
	
	public Spell queSpell() { return queSpell; }
	public Creature queSpellCreature() { return queSpellCreature; }
	public void stopCasting() { if(queSpell == null && queSpellCreature == null){ return ; } doAction("|deja06| de conjurar " + queSpell.nameElLa()); this.setQueSpell(null, null); }
	
	public void setQueSpell(Creature c, Spell s) { this.queSpellCreature = c; this.queSpell = s; }
	
	private Point queAttack;
	public Point queAttack(){ return queAttack; }
	public void setQueAttack(Point p) { this.queAttack = p; }
	
	private char glyph;
	public char glyph() { return glyph; }
	
	private Color color;
	public Color color() { return queSpell != null ? AsciiPanel.brightMagenta : queAttack != null ? AsciiPanel.brightBlue : color; }
	public void changeColor(Color color) { this.color = color; }
	
	private Color originalColor;
	public Color originalColor() { return originalColor; }

	private CreatureAi ai;
	public CreatureAi ai() { return ai; }
	public void setCreatureAi(CreatureAi ai) { this.ai = ai; }
	
	private int maxHp;
	public int maxHp() { return maxHp; }
	public int totalMaxHp() { return maxHp
			+ (armor == null ? 0 : armor.bonusMaxHp())
			+ (weapon == null ? 0 : weapon.bonusMaxHp())
			+ (helment == null ? 0 : helment.bonusMaxHp())
			+ (shield == null ? 0 : shield.bonusMaxHp()); }
	
	public void modifyMaxHp(int amount) { maxHp += amount; }
	
	private int hp;
	public int hp() { return hp; }
	
	private float blood;
	public float blood() { return blood; }
	public void modifyBlood(float value) { blood += value; }
	
	private ArrayList<DamageType> attackValues;
	public void modifyAttackValue(DamageType type, int value) { 
		for(DamageType d : attackValues){
			if(d.id == type.id)
				d.modifyAmount(value);
		}
	}
	public int attackValue(DamageType type) { 
		for(DamageType d : attackValues){
			if(d.id == type.id)
				return d.amount 
					+ (weapon != null ? weapon.attackValue(type) : 0)
					+ (shield != null ? shield.attackValue(type) : 0)
					+ (helment != null ? helment.attackValue(type) : 0)
					+ (armor != null ? armor.attackValue(type) : 0);		
		}
		return 0;
	}

	private ArrayList<DamageType> defenseValues;
	public void modifyDefenseValue(DamageType type, int value) { 
		for(DamageType d : defenseValues){
			if(d.id == type.id)
				d.modifyAmount(value);
		}
	}
	public int defenseValue(DamageType type) { 
		for(DamageType d : defenseValues){
			if(d.id == type.id)
				return d.amount
					+ (weapon != null ? weapon.defenseValue(type) : 0)
					+ (helment != null ? helment.defenseValue(type) : 0)
					+ (armor != null ? armor.defenseValue(type) : 0);
		}
		return 0;
	}

	private int actionPoints;
	public int getActionPoints() { return actionPoints;	}
	public void modifyActionPoints(int actionPoints) {	
		if(isPlayer()){
			world.modifyActionPoints(actionPoints);
			return;
		}
		this.actionPoints += actionPoints; 	
	}

	private Speed getSlowestAttackSpeed(){
		Speed slowest = attackSpeed;
		if(helment != null && helment.attackSpeed() != null &&
				helment.attackSpeed().velocity() > slowest.velocity())
			slowest =  helment.attackSpeed();
		if(armor != null && armor.attackSpeed() != null &&
				armor.attackSpeed().velocity() > slowest.velocity())
			slowest =  armor.attackSpeed();
		if(shield != null && shield.attackSpeed() != null &&
				shield.attackSpeed().velocity() > slowest.velocity())
			slowest =  shield.attackSpeed();
		if(weapon != null && weapon.attackSpeed() != null && 
				weapon.attackSpeed().velocity() > slowest.velocity())
			slowest =  weapon.attackSpeed();
		
		return slowest;
	}
	private Speed startingAttackSpeed;
	public Speed startingAttackSpeed(){ return startingAttackSpeed; }
	public void setStartingAttackSpeed(Speed attackSpeed) {
		this.startingAttackSpeed = attackSpeed;
		this.attackSpeed = attackSpeed;
	}
	private Speed attackSpeed;
	public Speed getAttackSpeed() {
		return getSlowestAttackSpeed();
	}
	public void modifyAttackSpeed(Speed attackSpeed) {
		this.attackSpeed = attackSpeed;
	}
	
	private Speed getSlowestMovementSpeed(){
		Speed slowest = movementSpeed;
		if(helment != null && helment.movementSpeed() != null &&
				helment.movementSpeed().velocity() > slowest.velocity())
			slowest =  helment.movementSpeed();
		if(armor != null && armor.movementSpeed() != null &&
				armor.movementSpeed().velocity() > slowest.velocity())
			slowest =  armor.movementSpeed();
		if(shield != null && shield.movementSpeed() != null &&
				shield.movementSpeed().velocity() > slowest.velocity())
			slowest =  shield.movementSpeed();
		if(weapon != null && weapon.movementSpeed() != null && 
				weapon.movementSpeed().velocity() > slowest.velocity())
			slowest =  weapon.movementSpeed();
		
		return slowest;
	}
	private Speed startingMovementSpeed;
	public Speed startingMovementSpeed(){ return startingMovementSpeed; }
	public void setStartingMovementSpeed(Speed movementSpeed) {
		this.startingMovementSpeed = movementSpeed;
		this.movementSpeed = movementSpeed;
	}
	
	private Speed movementSpeed;
	public Speed getMovementSpeed() {
		return getSlowestMovementSpeed();
	}
	public void modifyMovementSpeed(Speed movementSpeed) {
		this.movementSpeed = movementSpeed;
	}
	
	private int visionRadius;
	public void modifyVisionRadius(int value) { visionRadius += value; }
	public void setVisionRadius(int value) { visionRadius = value; }
	public int visionRadius() { return visionRadius; }

	private Inventory inventory;
	public Inventory inventory() { return inventory; }
	
	public boolean hasEquipped(Item check){
		return check == weapon || check == shield
				|| check == armor || check == helment
				|| check == offWeapon;
	}
	
	private Item weapon;
	public Item weapon() { return weapon; }
	
	private Item offWeapon;
	public Item offWeapon() { return offWeapon; }
	
	private Item shield;
	public Item shield() { return shield; }
	
	private Item armor;
	public Item armor() { return armor; }
	
	private Item helment;
	public Item helment() { return helment; }
	
	private int regenHpCooldown;
	private int regenHpPer1000;
	public void modifyRegenHpPer1000(int amount) { regenHpPer1000 += amount; }
	
	private List<Effect> effects;
	public List<Effect> effects(){ return effects; }
	
	private List<Spell> spells;
	public List<Spell> learnedSpells(){ return spells; }
	
	private String causeOfDeath;
	public String causeOfDeath() { return causeOfDeath; }
	
	private float stealthLevel = 0;
	public int stealthLevel() { return (int) Math.max(0, stealthLevel - Constants.STEALTH_MIN_STEPS); }
	public void modifyStealth(float amount) { this.stealthLevel += amount; this.stealthLevel = Math.max(Math.min(Constants.STEALTH_LEVEL_MAX, this.stealthLevel), 0); }
	public void removeStealth() { this.stealthLevel = 0; }
	
	public Screen subscreen;
	
	public Creature(World world, char glyph, char gender, Color color, String name, int maxHp){
		super();
		this.movementSpeed = Speed.NORMAL;
		this.attackSpeed = Speed.NORMAL;
		this.actionPoints = 0;
		this.world = world;
		this.glyph = glyph;
		this.gender = gender;
		this.color = color;
		this.originalColor = color;
		this.maxHp = maxHp;
		this.hp = maxHp;
		this.blood = maxHp * 10;
		this.visionRadius = 6;
		this.name = name;
		this.inventory = new Inventory(20);
		this.regenHpPer1000 = 10;
		this.attackValues = new ArrayList<DamageType>();
		this.attackValues.addAll(DamageType.ALL_TYPES());
		this.defenseValues = new ArrayList<DamageType>();
		this.defenseValues.addAll(DamageType.ALL_TYPES());
		this.effects = new ArrayList<Effect>();
		this.spells = new ArrayList<Spell>();
	}
	
	public void moveBy(int mx, int my){
		ai.onMoveBy(mx, my);
		
		if (mx==0 && my==0){
			modifyActionPoints(-Math.max(1, movementSpeed.velocity()));
			return;
		}
		
		if(x+mx > world.width() || x+mx < 0 || y+my < 0 || y+my > world.height())
			return;
		
		Tile tile = world.tile(x+mx, y+my);
		
		if(world.fire(x+mx, y+my) > 0){
			modifyHp(-(int)world.fire(x+mx, y+my) / 10, "Incinerado");
			notify("Estas siendo consumido por las llamas!");
		}
		
		Creature other = world.creature(x+mx, y+my);
		boolean reachWarning = false;
		
		if(weapon() != null && weapon().cantReach() > 0){
			if(other != null){
				if(isPlayer()){
					notify("Tu " + weapon().nameWNoStacks() + " es demasiado larga para alcanzar " + other.nameAlALa());
				}else{
					doAction("|falla05| el ataque!");
				}
				other = null;
				reachWarning = true;
			}
		}
		
		if(weapon() != null && other == null && weapon().reach() > 0){
			int reach = weapon.reach();
			int i = 1;
			
			while(other == null && i <= reach){
				if(!world.tile(x+(mx * i), y+(my * i)).isGround()){
					i = reach;
					break;
				}
				other = world.creature(x+(mx * i), y+(my * i));
				
				if(weapon() != null && weapon().cantReach() >= i){
					if(other != null){
						if(!reachWarning){
							if(isPlayer()){
								notify("Tu " + weapon().nameWNoStacks() + " es demasiado larga para alcanzar " + other.nameAlALa());
							}else{
								doAction("|falla05| el ataque!");
							}
						}
						other = null;
						reachWarning = true;
					}
				}
				
				i++;
			}
		}
		
		if (other == null){
			if(reachWarning){
				mx = 0;
				my = 0;
			}else{
				dualStrike = false;
			}
			ai.onEnter(x+mx, y+my, tile);
		}else{
			ai.onAttack(x+mx, y+my, other);
		}
		
		if(world.tile(x-mx, y-my) == Tile.DOOR_OPEN){
			open(x-mx, y-my);
		}
	}

	private boolean dualStrike = false;
	public boolean dualStrike() { return dualStrike; }

	public boolean meleeAttack(Creature other){
		modifyStealth(-1);

		if(offWeapon() != null){
			if(dualStrike){
				dualStrike = false;
				return commonAttack(other, offWeapon(), true);
			}else{
				dualStrike = true;
			}
		}
		
		return commonAttack(other, weapon(), false);
	}

	public void throwItem(Item item, int x, int y) {
		modifyStealth(-1);

		Creature c = world.creature(x, y);
				
		Item thrown = getRidOf(item);
		
		if(c != null){
			doAction("arroja %s hacia %s", thrown.nameUnUnaWNoStacks(), c.nameAlALa());
		}else{
			doAction("arroja %s", thrown.nameUnUnaWNoStacks());
		}
		
		modifyActionPoints(-(thrown.movementSpeed() != null ? thrown.movementSpeed().velocity() : getMovementSpeed().velocity()));
		
		ai.onThrowItem(thrown);
		
		world.add(new Projectile(world, new Line(this.x, this.y, x, y), 
				thrown.movementSpeed() != null ? thrown.movementSpeed().modifySpeed(-1) : Speed.SUPER_FAST, thrown, this));
	}
	
	public void rangedWeaponAttack(Creature other){
		modifyStealth(-1);
		
		char glyph = '|';
		
		if(x != other.x && y == other.y)
			glyph = '-';
		if(x < other.x && y < other.y || x > other.x && y > other.y)
			glyph = (char)92;
		if(x < other.x && y > other.y || x > other.x && y < other.y)
			glyph = '/';
		
		Item thrown = new Item(glyph, 'F', weapon().color(), "flecha", null, 100);
		for(DamageType t : DamageType.ALL_TYPES()){
			thrown.modifyAttackValue(t, weapon().attackValue(t));
		}
		
		thrown.modifyBloodModifyer(weapon().bloodModifyer());
		thrown.setData(Constants.CHECK_AMMUNITION, true);
		
		doAction("dispara %s hacia %s", weapon.nameElLaWNoStacks(), other.nameElLa());
		
		modifyActionPoints(-(weapon().attackSpeed() != null ? weapon().attackSpeed().velocity() : getAttackSpeed().velocity()));
		
		if(weapon() != null && weapon().canBreake()){
			weapon().modifyDurability(-1);
			if(weapon().durability() < 1){
				doAction("rompe " + weapon().nameElLaWNoStacks() + " al tensarlo demasiado");
				inventory().remove(weapon());
				unequip(weapon(), false);
			}else{
				ai.onRangedWeaponAttack(weapon());
			}
		}
		
		world.add(new Projectile(world, new Line(this.x, this.y, other.x, other.y), Speed.SUPER_FAST, thrown, this));
	}
	
	private boolean commonAttack(Creature other, Item object, boolean onlyObject) {
		int attack = 0;	//Start adding the inherit damage of the creature
		boolean weakSpotHit = false;
		boolean shieldBlock = false;
		String position = null;
		
		if(x < other.x && y >= other.y){
			shieldBlock = true;
			position = Constants.CHEST_POS;
			if(other.ai.getWeakSpot() == Constants.CHEST_POS){
				weakSpotHit = true;
			}
		}else if(y < other.y && x <= other.x){
			position = Constants.HEAD_POS;
			if(other.ai.getWeakSpot() == Constants.HEAD_POS){
				weakSpotHit = true;
			}
		}else if(x > other.x && y <= other.y){
			shieldBlock = true;
			position = Constants.ARM_POS;
			if(other.ai.getWeakSpot() == Constants.ARM_POS){
				weakSpotHit = true;
			}
		}else if(y > other.y && x >= other.x){
			position = Constants.LEG_POS;
			if(other.ai.getWeakSpot() == Constants.LEG_POS){
				weakSpotHit = true;
			}
		}
		
		if(shieldBlock && other.shield() != null){
			for(DamageType d : DamageType.ALL_TYPES()){
				if(d.id == DamageType.RANGED.id)
					continue;
				
				int attackValue = onlyObject ? object.attackValue(d) : attackValue(d);
				
				if(attackValue > other.shield().defenseValue(d)){
					other.shield().modifyDurability(-1);
					
					if(other.shield().durability() < 1){
						doAction("destroza " + other.shield().nameElLa() + " con el golpe");
						other.inventory().remove(other.shield());
						other.unequip(other.shield(), false);
						return false;
					}else{
						if(other.isPlayer()){
							other.notify("Tu escudo cruje por el impacto!");
						}else if(isPlayer()){
							notify(Constants.capitalize(other.shield().nameElLa()) + " " + other.nameDelDeLa()
							+ " cruje con tu impacto!");
						}
					}
				}
			}
			other.doAction("resiste el ataque "+nameDelDeLa()+" con " + other.shield().nameElLa());
			return false;
		}else{
			for(DamageType d : DamageType.ALL_TYPES()){
				if(d.id == DamageType.RANGED.id)
					continue;
				
				int attackValue = onlyObject ? object.attackValue(d) : attackValue(d);
				
				attack += Math.max(0, (attackValue - other.defenseValue(d)));
			}
		}
		
		int amount = attack;
		
		if(weakSpotHit){
			amount += 3;
		}
		
		amount = Math.max(1, amount);
		
//		if(other.isPlayer()){
//			amount -= other.defenseValueTotal();									//Substract the total defense
//		}else{
//			//If we are hitting a creature substract the defending item and the inherit defense unless we get a weakSpot
//			amount -= (defending_item == null ? 0 : defending_item.defenseValue()) + 
//					(weakSpotHit ? 0 : other.defenseValue());
//		}
		
//		Object[] params2 = new Object[params.length+1];
//		for (int i = 0; i < params.length; i++){
//			params2[i] = params[i];
//		}
//		
//		params2[params2.length - 1] = amount;
		
		if(weapon() != null && weapon().getBooleanData(Constants.CHECK_RANGED))
			amount = 1;
		
		other.ai().onGetAttacked(amount, position, this);
	
		float drained_blood = (amount * Constants.BLOOD_AMOUNT_MULTIPLIER) * (object == null ? 0.1f : object.bloodModifyer());
		
		other.makeBleed(drained_blood);
		
		if(weapon() != null && weapon().canBreake()){
			weapon().modifyDurability(-1);
			
			if(weapon().durability() < 1){
				doAction("rompe " + weapon().nameElLaWNoStacks() + " con el impacto");
				inventory().remove(weapon());
				unequip(weapon(), false);
			}
		}
		
		return true;
	}

	public void makeBleed(float amount){
		modifyBlood(-amount);
		world.propagate(x, y, amount, Constants.BLOOD_FLUID);
	}
	
	public int receiveDamage(int amount, DamageType damage, String causeOfDeath){
		return receiveDamage(amount, damage, causeOfDeath, true);
	}
	
	public int receiveDamage(int amount, DamageType damage, String causeOfDeath, boolean canKill){
		int attack = Math.abs(amount);
		
		if(damage != null){
			for(DamageType d : DamageType.ALL_TYPES()){
				if(d.id == DamageType.RANGED.id || d.id != damage.id)
					continue;
				attack -= Math.max(0, defenseValue(d));
			}
		}
		
		if(hp() - attack < 1) { attack -= Math.abs(hp() - attack) + 1; }
		
		modifyHp(-Math.max(0, attack), causeOfDeath);
		
		return Math.max(0, attack);
	}
	
	public void modifyHp(int amount, String causeOfDeath) { 
		hp += amount;
		this.causeOfDeath = causeOfDeath;
		
		if(amount > 0){
			blood += amount * (Constants.BLOOD_AMOUNT_MULTIPLIER * 0.5f);
		}
		if (hp > maxHp) {
			hp = maxHp;
		} else if (hp < 1) {
			doAction("muere");
			if(!isPlayer()){
				ai.onDecease(leaveCorpse());
			}
			world.remove(this);
		}
	}
	
	private Item leaveCorpse(){
		Item corpse = new Item('%', 'M', originalColor, "cadaver de " + nameWStacks() + "", null, 100);
		
		corpse.setData(Constants.CHECK_CONSUMABLE, true);
		corpse.setData(Constants.CHECK_CORPSE, true);
		corpse.setQuaffEffect(new Effect("indigesto", 1, false){
			public void start(Creature creature){
				creature.modifyHp((int) (maxHp * 0.5f), "Severa indigestion");
			}
		});
		
		world.addAtEmptySpace(corpse, x, y);
		
		for (Item item : inventory.getItems()){
			if (item != null)
				drop(item);
		}
		return corpse;
	}
	
	public void dig(int wx, int wy) {
		world.dig(wx, wy);
		doAction("derrumba la pared");
	}
	
	public void open(int wx, int wy) {
		world.open(wx, wy);
	}
	
	public void update(){	
		if(hp < 1)
			return;
		
		if(isPlayer()){
			regenerateHealth();
			updateEffects();
			ai.onUpdate();
			return;
		}
		
		//Creatures use actionPoints until they cannot
		
		int maxTries = 0;
		int startPoints = actionPoints;
		int endPoints = 0;
		
		while(actionPoints > 0 && maxTries < 2){
			startPoints = actionPoints;
			
			regenerateHealth();
			updateEffects();
			ai.onUpdate();
			
			endPoints = actionPoints;
			
			if(startPoints == endPoints)
				maxTries++;
		}
	}
	
	private void updateEffects(){
		List<Effect> done = new ArrayList<Effect>();
		
		for (Effect effect : effects){
			effect.update(this);
			if (effect.isDone()) {
				effect.end(this);
				done.add(effect);
			}
		}
		
		effects.removeAll(done);
	}
	
	private void regenerateHealth(){
		regenHpCooldown -= regenHpPer1000;
		if (regenHpCooldown < 0){
			if (hp < maxHp){
				modifyHp(1, "Muerto por regenrarse vida?");
			}
			if(blood < 1){
				doAction("ha perdido mucha sangre");
				modifyMaxHp(-1);
				blood = Constants.MIN_FLUID_AMOUNT;
			}
			regenHpCooldown += 1000;
		}
	}
	
	public boolean canEnter(int wx, int wy) {
		return world.tile(wx, wy).isGround() && world.creature(wx, wy) == null;
	}

	public void notify(String message, Object ... params){
		ai.onNotify(String.format(message, params));
	}
	
	public void doAction(String message, Object ... params){
		for (Creature other : getCreaturesWhoSeeMe()){
			if (other == this){
				other.notify(makeSecondPerson(message)+ ".", params);
			} else {
				other.notify(String.format("%s %s.", Constants.capitalize(nameElLa()), message, false), params);
			}
		}
	}
	
	public void combatAction(String message, Object ... params){
		for (Creature other : getCreaturesWhoSeeMe()){
			other.notify(message, params);
		}
	}
	
	public void doAction(Item item, String message, Object ... params){
		if (hp < 1)
			return;
		
		for (Creature other : getCreaturesWhoSeeMe()){
			if (other == this){
				other.notify("Te " + makeSecondPerson(message).toLowerCase()+ ".", params);
			} else {
				other.notify(String.format("%s se %s.", Constants.capitalize(nameElLa()), message, false), params);
			}
			other.learnName(item);
		}
	}
	
	private List<Creature> getCreaturesWhoSeeMe(){
		List<Creature> others = new ArrayList<Creature>();
		int r = 9;
		for (int ox = -r; ox < r+1; ox++){
			for (int oy = -r; oy < r+1; oy++){
				if (ox*ox + oy*oy > r*r)
					continue;
				
				Creature other = world.creature(x+ox, y+oy);
				
				if (other == null)
					continue;
				
				others.add(other);
			}
		}
		return others;
	}
	
	public static String makeSecondPerson(String text){
		return makeSecondPerson(text, true);
	}
	
	public static String makeSecondPerson(String text, boolean capitalize){
		String[] words = text.split(" ");
		
		if(words[0].endsWith(",")){
			words[0] = words[0].substring(0, words[0].length()-1) + "s,";
		}else{
			words[0] = words[0] + "s";
		}
		
		if(capitalize)
			words[0] = Constants.capitalize(words[0]);
		
		StringBuilder builder = new StringBuilder();
		for (String word : words){
			builder.append(" ");
			builder.append(word);
		}
		
		return builder.toString().trim();
	}
	
	public boolean canSee(int wx, int wy){
		return (detectCreatures > 0 && world.creature(wx, wy) != null
				|| ai.canSee(wx, wy));
	}

	public Tile realTile(int wx, int wy) {
		return world.tile(wx, wy);
	}
	
	public Tile tile(int wx, int wy) {
		if (canSee(wx, wy))
			return world.tile(wx, wy);
		else
			return ai.rememberedTile(wx, wy);
	}

	public Creature creature(int wx, int wy) {
		if (canSee(wx, wy))
			return world.creature(wx, wy);
		else
			return null;
	}
	
	public void pickup(){
		Item item = world.item(x, y);
		
		if (inventory.isFull() || item == null){
			doAction("agarra la nada");
		} else {
			doAction("levanta %s", item.nameElLa());
			inventory.add(item);
			world.remove(x, y);
		}
	}
	
	public void drop(Item item){
		if (world.addAtEmptySpace(item, x, y)){
			doAction("suelta " + item.nameUnUna());
			inventory.remove(item);
			unequip(item, true);
		} else {
			notify("No hay lugar donde soltar %s.", item.nameElLa());
		}
	}
	
	private boolean isPlayer = false;
	public boolean isPlayer(){
		return isPlayer;
	}
	public void makePlayer(){
		this.isPlayer = true;
	}
	
	public void eat(Item item){
		doAction("consume " + item.nameElLaWNoStacks());
		consume(item);
	}
	
	public void quaff(Item item){
		doAction("bebe " + item.nameElLaWNoStacks());
		consume(item);
	}
	
	private void consume(Item item){
		addEffect(item.quaffEffect());
		getRidOf(item);
	}
	
	public void addEffect(Effect effect){
		if (effect == null)
			return;
		
		effect.start(this);
		effects.add(effect);
	}
	
	private Item getRidOf(Item item){
		inventory.removeStack(item);
		unequip(item, true);
		item.modifyStacks(-1);
		return new Item(item);
	}
	
	public void unequip(Item item, boolean showText){
		if (item == null)
			return;
		
		if (item == armor){
			if (hp > 0 && showText)
				doAction("remueve " + item.nameElLaWNoStacks());
			armor = null;
		} if(item == helment){
			if (hp > 0 && showText)
				doAction("remueve " + item.nameElLaWNoStacks());
			helment = null;
		}  if(item == shield){
			if (hp > 0 && showText)
				doAction("guarda " + item.nameElLaWNoStacks());
			shield = null;
		}  if (item == weapon) {
			if (hp > 0 && showText) 
				doAction("guarda " + item.nameElLaWNoStacks());
			weapon = null;
		} if (item == offWeapon) {
			if (hp > 0 && showText) 
				doAction("guarda " + item.nameElLaWNoStacks());
			offWeapon = null;
		}
	}
	
	public void pickup(Item item){
		if (!inventory.contains(item)) {
			if (inventory.isFull()) {
				notify("No puedes equiparte %s, libera tu inventario.", item.nameElLaWNoStacks());
				return;
			} else {
				world.remove(item);
				inventory.add(item);
			}
		}
	}
	
	public void give(Item item, Creature target){
		if(inventory.contains(item) && !target.inventory().isFull()){
			doAction("entrega %s %s", item.nameElLa(), target.nameAlALa());
			target.inventory().add(item);
			inventory.remove(item);
		}		
	}
	
	public void equip(Item item){
		if (!inventory.contains(item)) {
			if (inventory.isFull()) {
				notify("No puedes equiparte %s, libera tu inventario.", item.nameElLaWNoStacks());
				return;
			} else {
				world.remove(item);
				inventory.add(item);
			}
		}
		
//		if (item.attackValue() == 0 && item.rangedAttackValue() == 0 && item.defenseValue() == 0)
//			return;
		
		if (item.getBooleanData(Constants.CHECK_DUAL_WIELD)){
			unequip(shield, true);
			
			if(offWeapon == null){
				offWeapon = item;
				doAction("empuña " + item.nameUnUnaWNoStacks() + " en tu otra mano");
			}else{
				if(weapon == null){
					weapon = item;
					doAction("empuña " + item.nameUnUnaWNoStacks() + " en tu mano buena");
				}else{
					unequip(weapon, true);
					doAction("empuña " + item.nameUnUnaWNoStacks() + " en tu mano buena");
				}
			}
		}else if (item.getBooleanData(Constants.CHECK_WEAPON)){
			unequip(weapon, true);
			if(item.getBooleanData(Constants.CHECK_TWO_HANDED)){
				unequip(shield, true);
			}
			doAction("empuña " + item.nameUnUnaWNoStacks());
			weapon = item;
		} else if (item.getBooleanData(Constants.CHECK_ARMOR)){
			unequip(armor, true);
			doAction("viste " + item.nameUnUnaWNoStacks());
			armor = item;
		} else if (item.getBooleanData(Constants.CHECK_HELMENT)){
			unequip(helment, true);
			doAction("calza " + item.nameUnUnaWNoStacks());
			helment = item;
		} else if (item.getBooleanData(Constants.CHECK_SHIELD)){
			if(weapon != null && weapon.getBooleanData(Constants.CHECK_TWO_HANDED)){
				doAction("tiene ambas manos ocupadas");
			}else{
				unequip(shield, true);
				doAction("alza " + item.nameUnUnaWNoStacks());
				shield = item;
			}
		}else{
			return;
		}
		
		learnName(item);
	}
	
	public Item item(int wx, int wy) {
		if (canSee(wx, wy))
			return world.item(wx, wy);
		else
			return null;
	}
	
	public String details() {
		return String.format("weapon:%s armor:%s helment:%s shield:%s", weapon != null ? weapon().nameWNoStacks() : "" +"", armor != null ? armor().nameWNoStacks()+"" : "", helment != null ? helment().nameWNoStacks()+"" : "", shield != null ? shield().nameWNoStacks()+"" : "");
	}
	
	public void summon(Creature other) {
		world.add(other);
	}
	
	private int detectCreatures;
	public void modifyDetectCreatures(int amount) { detectCreatures += amount; }
	
	public void castSpell(Spell spell, int x2, int y2) {
		ai.onCastSpell(spell, x2, y2);
	}

	public void learnName(Item item){
		if(item != null && item.appearance != null){
			if(!item.isIdentified()){
				item.identify(true);
				notify(Constants.capitalize(item.appearance) + " es realmente " + item.nameUnUnaWNoStacks() + "!"); 
			}
			if(isPlayer())
				Constants.learnName(item.name);
		}
	}
	
	public void learnName(Spell spell){
		if(spell != null && spell.appearance != null){
			if(!spell.isIdentified()){
				spell.identify(true);
				notify("El conjuro es realmente " + spell.nameUnUnaWNoStacks() + "!"); 
			}
			if(isPlayer())
				Constants.learnName(spell.name);
		}
	}
	
	public String statusEffects(){
		String effectString = "";
		
		for(int i = 0; i < effects.size(); i++){
			if(effects.get(i) == null || effects.get(i).statusName() == null
					|| effectString.indexOf(effects.get(i).statusName()) != -1)
				continue;
			
			effectString += " " + effects.get(i).statusName();
		}
		
		if(stealthLevel > Constants.STEALTH_MIN_STEPS)
			effectString += " escabullido";
		
		return effectString;
	}
}
