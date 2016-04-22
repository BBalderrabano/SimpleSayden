package sayden;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
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
	
	public boolean isAlly(Creature target) { return getData(Constants.RACE) == target.getData(Constants.RACE) || target.getBooleanData(Constants.UNIVERSAL_ALLY); }
	
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
	
	public int vigor() { 
		int vigor = 0;
		
		for(Wound w : inflictedWounds){
			vigor += w.vigor;
		}
		
		return vigor; 
	}
	
	private ArrayList<Wound> inflictedWounds;
	public ArrayList<Wound> inflictedWounds() { return inflictedWounds; }
	public void inflictWound(Wound wound){
		if(wound == null)
			return;
		
		Wound cloned = null;
		
		try {
			cloned = (Wound) wound.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return;
		}
		
		if(cloned == null){
			return;
		}

		cloned.start(this);
		inflictedWounds.add(cloned);
		
		if(!isPlayer && vigor() > maxVigor()){
			die(cloned.causeOfDeath());
		}else if(isPlayer && vigor() > maxVigor()){
			if(Math.random() > .5f){
				die(cloned.causeOfDeath());
			}else{
				notify("Sientes la sombra de la muerte abalanzandose sobre ti");
			}
		}
	}
	
	private int maxVigor;
	public int maxVigor() { return maxVigor; }
	public void setMaxVigor(int amount) { this.maxVigor += amount; }
	
	private boolean isAlive;
	public boolean isAlive() { return isAlive; }
	public void modifyAlive(boolean isAlive) { this.isAlive = isAlive; }
	
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
	public void modifyActionPoints(int amount, boolean affectPlayer) {
		if(isPlayer()){
			world.modifyActionPoints(amount);
			
			if(affectPlayer){
				this.actionPoints += amount;
			}
			return;
		}
		this.actionPoints += amount; 
		
		if(amount < 0){
			updateEffects();
			updateWounds();
		}
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
	
	public Creature(World world, char glyph, char gender, Color color, String name, int vigor){
		super();
		this.movementSpeed = Speed.NORMAL;
		this.attackSpeed = Speed.NORMAL;
		this.actionPoints = 0;
		this.world = world;
		this.glyph = glyph;
		this.gender = gender;
		this.color = color;
		this.originalColor = color;
		this.maxVigor = vigor;
		this.blood = vigor * 10;
		this.visionRadius = 6;
		this.name = name;
		this.inventory = new Inventory(20);
		this.inflictedWounds = new ArrayList<Wound>();
		this.attackValues = new ArrayList<DamageType>();
		this.attackValues.addAll(DamageType.ALL_TYPES());
		this.defenseValues = new ArrayList<DamageType>();
		this.defenseValues.addAll(DamageType.ALL_TYPES());
		this.effects = new ArrayList<Effect>();
		this.spells = new ArrayList<Spell>();
		this.isAlive = true;
	}
		
	public void moveBy(int mx, int my){
		if(getActionPoints() < 0 && isPlayer()){
			mx = 0;
			my = 0;
			modifyActionPoints(-actionPoints, true);
			notify("Te encuentras aturdido");
		}
		ai.onMoveBy(mx, my);
		
		if (mx==0 && my==0){
			modifyActionPoints(-Math.max(1, movementSpeed.velocity()), false);
			return;
		}
		
		if(x+mx > world.width() || x+mx < 0 || y+my < 0 || y+my > world.height())
			return;
		
		Tile tile = world.tile(x+mx, y+my);
		
		if(world.fire(x+mx, y+my) > 0){
			//TODO: modifyHp(-(int)world.fire(x+mx, y+my) / 10, "Incinerado");
			if(isPlayer)
				notify("Estas siendo consumido por las llamas!");
			else
				notifyArround(Constants.capitalize(nameElLa()) + " es consumido por las llamas!");
		}
		
		Creature other = world.creature(x+mx, y+my);
		boolean reachWarning = false;
		
		if(weapon() != null && weapon().cantReach() > 0){
			if(other != null && !isAlly(other)){
				if(isPlayer()){
					notify("Estas |demasiado cerca05| para atacar " + other.nameAlALa());
				}else if(other.isPlayer()){
					doAction("esta |demasiado cerca05| para atacarte!");
				}else{
					doAction("esta |demasiado cerca05| para atacar "+other.nameAlALa()+"!");
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
					if(other != null && !isAlly(other)){
						if(!reachWarning){
							if(isPlayer()){
								notify("Estas |demasiado cerca05| para atacar " + other.nameAlALa());
							}else if(other.isPlayer()){
								doAction("esta |demasiado cerca05| para atacarte!");
							}else{
								doAction("esta |demasiado cerca05| para atacar "+other.nameAlALa()+"!");
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
			ai.onAttack(other);
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
		
		modifyActionPoints(-(thrown.movementSpeed() != null ? thrown.movementSpeed().velocity() : getMovementSpeed().velocity()), false);
		
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
		thrown.setData(Constants.CHECK_PROJECTILE_AUTOTARGET, true);
		thrown.setData(Constants.CHECK_PROJECTILE_DISSAPEAR, true);
		
		doAction("dispara %s hacia %s", weapon.nameElLaWNoStacks(), other.nameElLa());
		
		modifyActionPoints(-(weapon().attackSpeed() != null ? weapon().attackSpeed().velocity() : getAttackSpeed().velocity()), false);
		
		if(weapon() != null && weapon().canBreak()){
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
		
		int highestDamage = -100;
		DamageType highestDamageType = null;
		
		if(x < other.x && y >= other.y){
			shieldBlock = true;
			position = Constants.BACK_POS;
			if(other.ai.getWeakSpot() == Constants.BACK_POS){
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
		
		boolean isShielding = other.shield() != null && shieldBlock;
		
		if(onlyObject && object != null && object.getBooleanData(Constants.CHECK_RANGED) || 
				!onlyObject && weapon() != null && weapon().getBooleanData(Constants.CHECK_RANGED)){
			attack += 1 - other.defenseValue(DamageType.BLUNT);
			
			if(isShielding){
				attack -= other.shield().defenseValue(DamageType.BLUNT);
			}
		}else{
			for(DamageType d : DamageType.ALL_TYPES()){
				if(d.id == DamageType.RANGED.id)
					continue;
				
				int attackValue = onlyObject ? object.attackValue(d) : attackValue(d);
				
				if(isShielding){
					attackValue -= other.shield().defenseValue(d);
				}
				
				if(weakSpotHit){
					attack += Math.max(0, attackValue);
					
					if(Math.max(0, attackValue) > highestDamage){
						highestDamage = Math.max(0, attackValue);
						highestDamageType = d;
					}
				}else{
					attack += Math.max(0, attackValue - other.defenseValue(d));
					
					if(Math.max(0, attackValue - other.defenseValue(d)) > highestDamage){
						highestDamage = Math.max(0, attackValue);
						highestDamageType = d;
					}
				}
			}
		}
		
		int amount = attack;
		
		amount = Math.max(0, amount);
		
		if(amount < 1){
			
			impactWeapon();
			
			if(isPlayer()){
				other.doAction("resiste tu ataque");
			}else{
				other.doAction("resiste el ataque " + nameDelDeLa());
			}
			return false;
			
		}
		
		ArrayList<Wound> checkWounds = new ArrayList<Wound>();
		
		if(highestDamageType == null)
			highestDamageType = DamageType.BLUNT;
		
		for(Wound w : ai.possibleWounds()){
			if(w.canBePicked(this, other, position, highestDamageType)){
				checkWounds.add(w);
			}
		}
		
		for(Wound w : other.ai().possibleFatality()){
			if(w.canBePicked(this, other, position, highestDamageType)){
				checkWounds.add(w);
			}
		}
		
		if(object != null){
			for(Wound w : object.possibleWounds()){
				if(w.canBePicked(this, other, position, highestDamageType)){
					checkWounds.add(w);
				}
			}
		}
		
		for(Wound w : Wound.DEFAULT_WOUNDS()){
			if(w.canBePicked(this, other, position, highestDamageType)){
				checkWounds.add(w);
			}
		}

		other.ai().onGetAttacked(position, pickWeightedWound(checkWounds), this, object);
	
		float drained_blood = (amount * Constants.BLOOD_AMOUNT_MULTIPLIER) * (object == null ? 0.1f : object.bloodModifyer());
		
		other.makeBleed(drained_blood);
		
		impactWeapon();
		
		if(other.shield() != null && isShielding && amount >= 1){
			other.shield().modifyDurability(-1);
			other.notifyArround(Constants.capitalize(other.shield().nameElLaWNoStacks()) + " cruje con el impacto!");
		}
		
		return true;
	}

	public Wound pickWeightedWound(ArrayList<Wound> wounds){
		Collections.shuffle(wounds);
		
		double totalWeight = 0.0d;

		int randomIndex = -1;
		double random = Math.random() * totalWeight;
		for (int i = 0; i < wounds.size(); ++i)
		{	
		    random -= wounds.get(i).weight;
		    if (random <= 0.0d)
		    {
		        randomIndex = i;
		        break;
		    }
		}
		
		return randomIndex < 0 ? null : wounds.get(randomIndex);
	}
	
	private void impactWeapon(){
		if(weapon() != null && weapon().canBreak()){
			weapon().modifyDurability(-1);
			
			if(weapon().durability() < 1){
				doAction("rompe " + weapon().nameElLaWNoStacks() + " con el impacto");
				inventory().remove(weapon());
				unequip(weapon(), false);
			}
		}
	}
	
	public void makeBleed(float amount){
		modifyBlood(-amount);
		world.propagate(x, y, amount, Constants.BLOOD_FLUID);
	}
	
	public void die(String causeOfDeath){
		if(causeOfDeath != null && !causeOfDeath.isEmpty()){
			this.causeOfDeath = causeOfDeath;
		}
		doAction("muere");
		
		isAlive = false;
		
		if(!isPlayer()){
			ai.onDecease(leaveCorpse());
			world.remove(this);
		}else{
			glyph = '%';
		}
	}
	
	private Item leaveCorpse(){
		Item corpse = new Item('%', 'M', originalColor, "cadaver de " + nameWStacks() + "", null, 0);
		
		corpse.setData(Constants.CHECK_CONSUMABLE, true);
		corpse.setData(Constants.CHECK_CORPSE, true);
		
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
		if(!isAlive())
			return;
		
		if(isPlayer()){
			updateWounds();
			updateEffects();
			ai.onUpdate();
			return;
		}
		
		//Creatures use actionPoints until they cannot, effects are updated AFTER their AP is substracted
		
		int maxTries = 0;
		int startPoints = actionPoints;
		int endPoints = 0;
		
		while(actionPoints > 0 && maxTries < 2){
			startPoints = actionPoints;
			
			ai.onUpdate();
			
			endPoints = actionPoints;
			
			if(startPoints == endPoints)
				maxTries++;
		}
	}
	
	private void updateEffects(){
		ArrayList<Effect> doneEff = new ArrayList<Effect>();
		
		for (Effect effect : effects){
			effect.update(this);
			if (effect.isDone()) {
				effect.end(this);
				doneEff.add(effect);
			}
		}

		effects.removeAll(doneEff);
	}
	
	private void updateWounds(){
		ArrayList<Wound> doneWounds = new ArrayList<Wound>();
		
		for (Wound w : inflictedWounds){
			w.update(this);
			if (w.isDone()) {
				w.end(this);
				doneWounds.add(w);
			}
		}

		inflictedWounds.removeAll(doneWounds);
	}
	
	public boolean canEnter(int wx, int wy) {
		return world.tile(wx, wy).isGround() && world.creature(wx, wy) == null;
	}

	public void notify(String message, Object ... params){
		if(message == null || message.isEmpty())
			return;
		ai.onNotify(String.format(message, params));
	}
	
	public void notifyArround(String message, Object ... params){
		if(message == null || message.isEmpty())
			return;
		for (Creature other : getCreaturesWhoSeeMe()){
			other.notify(message, params);
		}
	}
	
	public void doAction(String message, Object ... params){
		if(message == null || message.isEmpty())
			return;
		for (Creature other : getCreaturesWhoSeeMe()){
			if (other == this){
				other.notify(makeSecondPerson(message)+ ".", params);
			} else {
				other.notify(String.format("%s %s.", Constants.capitalize(nameElLa()), message, false), params);
			}
		}
	}
	
	public void doAction(Item item, String message, Object ... params){
		if(message == null || message.isEmpty())
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
	
	public List<Creature> getCreaturesWhoSeeMe(){
		List<Creature> others = new ArrayList<Creature>();
		int r = 9;
		for (int ox = -r; ox < r+1; ox++){
			for (int oy = -r; oy < r+1; oy++){
				if (ox*ox + oy*oy > r*r)
					continue;
				
				Creature other = world.creature(x+ox, y+oy);
				
				if (other == null || !other.canSee(x, y))
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
			if (isAlive && showText)
				doAction("remueve " + item.nameElLaWNoStacks());
			armor = null;
		} if(item == helment){
			if (isAlive && showText)
				doAction("remueve " + item.nameElLaWNoStacks());
			helment = null;
		}  if(item == shield){
			if (isAlive && showText)
				doAction("guarda " + item.nameElLaWNoStacks());
			shield = null;
		}  if (item == weapon) {
			if (isAlive && showText) 
				doAction("guarda " + item.nameElLaWNoStacks());
			weapon = null;
		} if (item == offWeapon) {
			if (isAlive && showText) 
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
