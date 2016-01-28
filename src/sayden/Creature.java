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
	
	public int x;
	public int y;
	public int z;
	public Point position() { return new Point(x,y,z); }
	
	private Point queAttack;
	public Point queAttack(){ return queAttack; }
	public void setQueAttack(Point p) { this.queAttack = p; }
	
	private int timeUnsync;
	public void addTime(int amount){ timeUnsync += amount; if(timeUnsync > Constants.MAX_SYNC_TIME) timeUnsync = Constants.MAX_SYNC_TIME; }
	
	private char glyph;
	public char glyph() { return glyph; }
	
	private Color color;
	public Color color() { return queAttack != null ? AsciiPanel.brightBlue : color; }
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
	public void modifyActionPoints(int actionPoints) {	this.actionPoints += actionPoints; }

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
				|| check == armor || check == helment;
	}
	
	private Item weapon;
	public Item weapon() { return weapon; }
	
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
	
	private String causeOfDeath;
	public String causeOfDeath() { return causeOfDeath; }
	
	private List<String> learNames;
	
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
		this.learNames = new ArrayList<String>();
	}
	
	public void moveBy(int mx, int my, int mz){
		if (mx==0 && my==0 && mz==0){
			if(isPlayer()){
				world.modifyActionPoints( Math.max(1, movementSpeed.velocity() - 1));
			}else{
				modifyActionPoints(-Math.max(1, movementSpeed.velocity()));
			}
			return;
		}
		
		if(x+mx > world.width(z+mz) || x+mx < 0 || y+my < 0 || y+my > world.height(z+mz))
			return;
		
		Tile tile = world.tile(x+mx, y+my, z+mz);
		
		if(world.fire(x+mx, y+my, z+mz) > 0){
			modifyHp(-(int)world.fire(x+mx, y+my, z+mz) / 10, "Incinerado");
			notify("Estas siendo consumido por las llamas!");
		}
		
		if (mz == -1){
			if (tile == Tile.STAIRS_DOWN) {
				doAction("asciendes por las escaleras al piso %d", z+mz+1);
				for(int i = 0; i < timeUnsync; i++){
					world.updateFloor(z+mz);
				}
				timeUnsync = 0;
			} else {
				doAction("trata de ascender pero te detiene el techo de la cueva!");
				return;
			}
		} else if (mz == 1){
			if (tile == Tile.STAIRS_UP) {
				doAction("desciende por las escaleras al piso %d", z+mz+1);
				for(int i = 0; i < timeUnsync; i++){
					world.updateFloor(z+mz);
				}
				timeUnsync = 0;
			} else {
				doAction("trata de descender pero te detiene el piso de la cueva!");
				return;
			}
		}
		
		Creature other = world.creature(x+mx, y+my, z+mz);
		
		if (other == null){
			ai.onEnter(x+mx, y+my, z+mz, tile);
		}else{
			ai.onAttack(x+mx, y+my, z+mz, other);
		}
		
		if(world.tile(x-mx, y-my, z-mz) == Tile.DOOR_OPEN){
			open(x-mx, y-my, z-mz);
		}
	}

	public void meleeAttack(Creature other){
		commonAttack(other, weapon(), "ataca al %s por %s de daño", other.name());
	}

	public void throwItem(Item item, int x, int y, int z) {
		Creature c = world.creature(x, y, z);
				
		Item thrown = getRidOf(item);
		
		//commonAttack(other, item, "arroja %s %s efectuando %d puntos de daño", item.nameUnUna(), other.nameAlALa());
		if(c != null){
			doAction("arroja %s hacia %s", thrown.nameUnUna(), c.nameAlALa());
		}else{
			doAction("arroja %s", thrown.nameUnUna());
		}
		
		world.add(new Projectile(world, z, new Line(this.x, this.y, x, y), 
				thrown.movementSpeed() != null ? thrown.movementSpeed().modifySpeed(-1) : Speed.SUPER_FAST, thrown, this));
	}
	
	public void rangedWeaponAttack(Creature other){
		commonAttack(other, weapon(), "dispara %s %s efectuando %d puntos de daño", weapon.nameUnUna(), other.nameAlALa());
	}
	
	private void commonAttack(Creature other, Item object, String action, Object ... params) {
		int attack = 0;	//Start adding the inherit damage of the creature
		boolean weakSpotHit = false;
		boolean shieldBlock = false;
		
		//Get the defending item and check for weak spots
		if(x < other.x && y >= other.y){
			shieldBlock = true;
			if(other.ai.getWeakSpot() == Constants.CHEST_POS){
				weakSpotHit = true;
			}
		}else if(y < other.y && x <= other.x){
			//defending_item = other.helment();
			if(other.ai.getWeakSpot() == Constants.HEAD_POS){
				weakSpotHit = true;
			}
		}else if(x > other.x && y <= other.y){
			shieldBlock = true;
			if(other.ai.getWeakSpot() == Constants.ARM_POS){
				weakSpotHit = true;
			}
		}else if(y > other.y && x >= other.x){
			if(other.ai.getWeakSpot() == Constants.LEG_POS){
				weakSpotHit = true;
			}
		}
		
		if(shieldBlock && other.shield() != null){
			for(DamageType d : DamageType.ALL_TYPES()){
				if(d.id == DamageType.RANGED.id)
					continue;
				if(attackValue(d) > other.shield().defenseValue(d)){
					doAction("destroza " + other.shield().nameElLa() + " con el golpe");
					other.inventory().remove(other.shield());
					other.unequip(other.shield(), false);
					return;
				}
			}
			other.doAction("resiste el ataque "+nameDelDeLa()+" con " + other.shield().nameElLa());
			return;
		}else{
			for(DamageType d : DamageType.ALL_TYPES()){
				if(d.id == DamageType.RANGED.id)
					continue;
				attack += Math.max(0, (attackValue(d) - other.defenseValue(d)));
			}
		}
		
		int amount = Math.max(1, attack);
		
		if(weakSpotHit){
			amount += 3;
		}
		
//		if(other.isPlayer()){
//			amount -= other.defenseValueTotal();									//Substract the total defense
//		}else{
//			//If we are hitting a creature substract the defending item and the inherit defense unless we get a weakSpot
//			amount -= (defending_item == null ? 0 : defending_item.defenseValue()) + 
//					(weakSpotHit ? 0 : other.defenseValue());
//		}
		
		Object[] params2 = new Object[params.length+1];
		for (int i = 0; i < params.length; i++){
			params2[i] = params[i];
		}
		
		params2[params2.length - 1] = amount;
		
		doAction(action, params2);
		
		other.modifyHp(-amount, "Matado por " + nameUnUna());
	
		float drained_blood = (amount * Constants.BLOOD_AMOUNT_MULTIPLIER) * (object == null ? 0.1f : object.bloodModifyer());
		
		other.modifyBlood(-drained_blood);
		world.propagate(other.x, other.y, other.z, drained_blood, Constants.BLOOD_FLUID);
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
			ai.onDecease(leaveCorpse());
			world.remove(this);
		}
	}
	
	private Item leaveCorpse(){
		Item corpse = new Item('%', 'M', originalColor, "cadaver " + nameDelDeLa(), null);
		corpse.setData(Constants.CHECK_CONSUMABLE, true);
		corpse.setData(Constants.CHECK_CORPSE, true);
		world.addAtEmptySpace(corpse, x, y, z);
		for (Item item : inventory.getItems()){
			if (item != null)
				drop(item);
		}
		return corpse;
	}
	
	public void dig(int wx, int wy, int wz) {
		world.dig(wx, wy, wz);
		doAction("derrumba la pared");
	}
	
	public void open(int wx, int wy, int wz) {
		world.open(wx, wy, wz);
	}
	
	public void update(){	
		if(isPlayer()){
			regenerateHealth();
			updateEffects();
			ai.onUpdate();
			return;
		}
			
		int maxTries = 0;
		int startPoints = actionPoints;
		int endPoints = 0;
		
		while(actionPoints > 0 && maxTries < 10){
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
	
	public boolean canEnter(int wx, int wy, int wz) {
		return world.tile(wx, wy, wz).isGround() && world.creature(wx, wy, wz) == null;
	}

	public void notify(String message, Object ... params){
		ai.onNotify(String.format(message, params));
	}
	
	public void doAction(String message, Object ... params){
		for (Creature other : getCreaturesWhoSeeMe()){
			if (other == this){
				other.notify(makeSecondPerson(message)+ ".", params);
			} else {
				other.notify(String.format("%s %s.", capitalize(nameElLa()), message, false), params);
			}
		}
	}
	
	public void doAction(Item item, String message, Object ... params){
		if (hp < 1)
			return;
		
		for (Creature other : getCreaturesWhoSeeMe()){
			if (other == this){
				other.notify("Te " + makeSecondPerson(message).toLowerCase()+ ".", params);
			} else {
				other.notify(String.format("%s se %s.", capitalize(nameElLa()), message, false), params);
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
				
				Creature other = world.creature(x+ox, y+oy, z);
				
				if (other == null)
					continue;
				
				others.add(other);
			}
		}
		return others;
	}
	
	public static String capitalize(String text){
		return Character.toUpperCase(text.charAt(0))+""+text.substring(1);
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
			words[0] = capitalize(words[0]);
		
		StringBuilder builder = new StringBuilder();
		for (String word : words){
			builder.append(" ");
			builder.append(word);
		}
		
		return builder.toString().trim();
	}
	
	public boolean canSee(int wx, int wy, int wz){
		return (detectCreatures > 0 && world.creature(wx, wy, wz) != null
				|| ai.canSee(wx, wy, wz));
	}

	public Tile realTile(int wx, int wy, int wz) {
		return world.tile(wx, wy, wz);
	}
	
	public Tile tile(int wx, int wy, int wz) {
		if (canSee(wx, wy, wz))
			return world.tile(wx, wy, wz);
		else
			return ai.rememberedTile(wx, wy, wz);
	}

	public Creature creature(int wx, int wy, int wz) {
		if (canSee(wx, wy, wz))
			return world.creature(wx, wy, wz);
		else
			return null;
	}
	
	public void pickup(){
		Item item = world.item(x, y, z);
		
		if (inventory.isFull() || item == null){
			doAction("agarra la nada");
		} else {
			doAction("levanta %s", item.nameElLa());
			world.remove(x, y, z);
			inventory.add(item);
		}
	}
	
	public void drop(Item item){
		if (world.addAtEmptySpace(item, x, y, z)){
			doAction("suelta " + item.nameUnUna());
			inventory.remove(item);
			unequip(item, true);
		} else {
			notify("No hay lugar donde soltar %s.", item.nameElLa());
		}
	}
	
	public boolean isPlayer(){
		return glyph == '@';
	}
	
	public void eat(Item item){
		doAction("consume " + item.nameElLa());
		consume(item);
	}
	
	public void quaff(Item item){
		doAction("bebe " + item.nameElLa());
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
				doAction("remueve " + item.nameElLa());
			armor = null;
		} if(item == helment){
			if (hp > 0 && showText)
				doAction("remueve " + item.nameElLa());
			helment = null;
		}  if(item == shield){
			if (hp > 0 && showText)
				doAction("guarda " + item.nameElLa());
			shield = null;
		} else if (item == weapon) {
			if (hp > 0 && showText) 
				doAction("guarda " + item.nameElLa());
			weapon = null;
		}
	}
	
	public void pickup(Item item){
		if (!inventory.contains(item)) {
			if (inventory.isFull()) {
				notify("No puedes equiparte %s, libera tu inventario.", item.nameElLa());
				return;
			} else {
				if(learNames.contains(item.name)){
					item.description = null;
				}
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
				notify("No puedes equiparte %s, libera tu inventario.", item.nameElLa());
				return;
			} else {
				world.remove(item);
				inventory.add(item);
			}
		}
		
//		if (item.attackValue() == 0 && item.rangedAttackValue() == 0 && item.defenseValue() == 0)
//			return;
		
		if (item.getBooleanData(Constants.CHECK_WEAPON)){
			unequip(weapon, true);
			doAction("empuña " + item.nameUnUna());
			weapon = item;
		} else if (item.getBooleanData(Constants.CHECK_ARMOR)){
			unequip(armor, true);
			doAction("viste " + item.nameUnUna());
			armor = item;
		} else if (item.getBooleanData(Constants.CHECK_HELMENT)){
			unequip(helment, true);
			doAction("calza " + item.nameUnUna());
			helment = item;
		} else if (item.getBooleanData(Constants.CHECK_SHIELD)){
			unequip(shield, true);
			doAction("alza " + item.nameUnUna());
			shield = item;
		}else{
			return;
		}
		
		learnName(item);
	}
	
	public Item item(int wx, int wy, int wz) {
		if (canSee(wx, wy, wz))
			return world.item(wx, wy, wz);
		else
			return null;
	}
	
	public String details() {
		return String.format("weapon:%s armor:%s helment:%s shield:%s", weapon != null ? weapon().name() : "" +"", armor != null ? armor().name()+"" : "", helment != null ? helment().name()+"" : "", shield != null ? shield().name()+"" : "");
	}
	
	public void summon(Creature other) {
		world.add(other);
	}
	
	private int detectCreatures;
	public void modifyDetectCreatures(int amount) { detectCreatures += amount; }
	
	public void castSpell(Spell spell, int x2, int y2) {
		Creature other = creature(x2, y2, z);
		
		spell.effect().start(x2, y2, z);
		
		if(other == null)
			return;
		System.out.println("Encontro criatura");
		other.addEffect(spell.effect());
		//modifyMana(-spell.manaCost());
	}

	public void learnName(Item item){
		if(item.appearance != null){
			if(item.name != item.appearance){	notify(capitalize(item.appearance) + " es realmente " + item.realNameUnUna() + "!"); }
			item.appearance = null;
			if(isPlayer())
				learNames.add(item.name);
		}
	}
}
