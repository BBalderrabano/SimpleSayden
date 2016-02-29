package sayden.ai;

import java.util.List;
import sayden.Constants;
import sayden.Creature;
import sayden.DamageType;
import sayden.Item;
import sayden.Line;
import sayden.Path;
import sayden.Point;
import sayden.Spell;
import sayden.Tile;
import sayden.Wound;
import sayden.screens.ReadSpellScreen;

public class CreatureAi {
	private String weakSpot;
	protected Creature creature;
	
	protected Creature target;
	public Creature target() { return target; }
	
	public CreatureAi(Creature creature){
		this.creature = creature;
		this.creature.setCreatureAi(this);
		this.weakSpot = Constants.NO_POS;
	}
	
	public void setWeakSpot(String position){
		this.weakSpot = position;
	}
	
	public String getWeakSpot(){
		return weakSpot == null || weakSpot.isEmpty() ? "none" : weakSpot;
	}
	
	public void onCastSpell(Spell spell, int x2, int y2) {
		// TODO Auto-generated method stub
		Creature other = creature.creature(x2, y2);
		
		if(!creature.isPlayer() && spell.castSpeed().velocity() >= creature.getActionPoints() && creature.queSpell() == null){
			if(other != null && other.isPlayer() && 
					Math.abs(spell.castSpeed().velocity() - other.getMovementSpeed().velocity()) > 1){
				creature.modifyActionPoints(-creature.getActionPoints(), false);
				creature.setQueSpell(other,  spell);
				creature.doAction("comienza a pronunciar " + spell.nameUnUna());
				return;
			}
		}else if(creature.queSpell() != null && !creature.isPlayer() && 
				creature.getActionPoints() < creature.queSpell().castSpeed().velocity()){
			return;
		}
		
		spell.effect().start(x2, y2);
		
		if(other == null || !canSee(other.x, other.y))
			return;
		
		ReadSpellScreen.lastCreature = other;
		spell.onCast(creature, other);
		other.addEffect(spell.effect());
		
		creature.learnName(spell);
		other.learnName(spell);
	}
	
	public void onEnter(int x, int y, Tile tile){
		if(creature.queAttack() != null || creature.queSpell() != null)
			return;
		
		if(creature.getActionPoints() < creature.getMovementSpeed().velocity() && !creature.isPlayer()){
			return;	//There are not enough action points to perform a movement action, return
		}else if(!creature.isPlayer()){
			creature.modifyActionPoints(-creature.getMovementSpeed().velocity(), false);	//We can move, and we are not a player, substract movement speed
		}
		if (tile.isGround()){
			creature.x = x;
			creature.y = y;
			creature.woundMove(x, y);
		}
	}
	
	public boolean onAttack(Creature other){
		if(creature.isAlly(other) &&
				!creature.getBooleanData(Constants.FLAG_ANGRY) &&
				!other.getBooleanData(Constants.FLAG_ANGRY) || 
				creature.queSpell() != null)
			return false;

		if(creature.getActionPoints() < creature.getAttackSpeed().velocity() && !creature.isPlayer()){
			if(other.isPlayer()){
				if(Math.abs(creature.getAttackSpeed().velocity() - other.getMovementSpeed().velocity()) > 2
						&& creature.queAttack() == null){
					creature.modifyActionPoints(-creature.getActionPoints(), false);
					creature.setQueAttack(other.position());
				}
			}
			return false;	//There are not enough action points to perform an attack, queue attack
		}else if(!creature.isPlayer()){
			creature.modifyActionPoints(-(creature.dualStrike() ?	creature.offWeapon().attackSpeed().velocity() : 
																	creature.getAttackSpeed().velocity()), false);
		}
		
		boolean success = creature.meleeAttack(other);
		
		if(success){
			other.stopCasting();
			creature.stopCasting();
		}
		
		String position = "";
		
		if(creature.x < other.x && creature.y >= other.y){
			position = Constants.BACK_POS;
		}else if(creature.y < other.y && creature.x <= other.x){
			position = Constants.HEAD_POS;
		}else if(creature.x > other.x && creature.y <= other.y){
			position = Constants.ARM_POS;
		}else if(creature.y > other.y && creature.x >= other.x){
			position = Constants.LEG_POS;
		}
		
		if(creature.weapon() != null && creature.weapon().wounds().size() > 0 && success 
				&& (creature.getBooleanData(Constants.DEALS_WOUNDS) || creature.weapon().level() >= 2)){
			Wound inflictWound = creature.weapon().pickWeightedWound(position);
			if(inflictWound != null)
				other.inflictWound(inflictWound);
		}
		
		creature.woundOnHit(success, position);
		other.woundOnGetHit(success, position);
						
		return success;
	}
	
	public boolean onGetAttacked(int amount, String position, Creature attacker){
		String amountString = "";
		
		if(position == weakSpot){
			amountString = amount + " (|critico01|)";
		}else{
			amountString = amount + "";
		}
		
		if(attacker.isPlayer()){
			attacker.notifyArround("Atacas |(%s %s %s)01| %s |[%s %s %s]02| por %s", 
					attacker.attackValue(DamageType.SLICE),
					attacker.attackValue(DamageType.BLUNT),
					attacker.attackValue(DamageType.PIERCING),
					creature.nameAlALa(),
					creature.defenseValue(DamageType.SLICE),
					creature.defenseValue(DamageType.BLUNT),
					creature.defenseValue(DamageType.PIERCING),
					amountString);
		}else if(creature.isPlayer()){
			attacker.notifyArround("%s |(%s %s %s)01| te ataca |[%s %s %s]02| por %s", 
					Constants.capitalize(attacker.nameElLa()),
					attacker.attackValue(DamageType.SLICE),
					attacker.attackValue(DamageType.BLUNT),
					attacker.attackValue(DamageType.PIERCING),
					creature.defenseValue(DamageType.SLICE),
					creature.defenseValue(DamageType.BLUNT),
					creature.defenseValue(DamageType.PIERCING),
					amountString);
		}else{
			attacker.notifyArround("%s |(%s %s %s)01| ataca %s |[%s %s %s]02| por %s", 
					Constants.capitalize(attacker.nameElLa()),
					attacker.attackValue(DamageType.SLICE),
					attacker.attackValue(DamageType.BLUNT),
					attacker.attackValue(DamageType.PIERCING),
					creature.nameAlALa(),
					creature.defenseValue(DamageType.SLICE),
					creature.defenseValue(DamageType.BLUNT),
					creature.defenseValue(DamageType.PIERCING),
					amountString);
		}
		
		creature.modifyHp(-amount, "Matado por " + attacker.nameUnUna());
		return true;
	}
	
	public void onUpdate(){
		if(!creature.isPlayer() && creature.queSpell() != null &&
				creature.getActionPoints() >= creature.queSpell().castSpeed().velocity()){
			
			if(creature.queSpellCreature() != null && canSee(creature.queSpellCreature().x, creature.queSpellCreature().y)){
				creature.castSpell(creature.queSpell(), creature.queSpellCreature().x, creature.queSpellCreature().y);
			}else{
				creature.queSpellCreature().notify(Constants.capitalize(creature.nameElLa()) + " |falla06| el conjuro!");
			}
			creature.setQueSpell(null, null);
		}
		
		if(creature.queAttack() != null && 
				creature.getActionPoints() >= creature.getAttackSpeed().velocity()){
			creature.modifyActionPoints(-creature.getAttackSpeed().velocity(), false);
			
			Creature c = creature.world().creature(creature.queAttack().x, creature.queAttack().y);
			
			if(c == null){
				creature.x = creature.queAttack().x;
				creature.y = creature.queAttack().y;
				creature.doAction("|falla05| el ataque!");
			}else{
				creature.meleeAttack(c);
			}			
			creature.setQueAttack(null);
		}
	}
	
	public void onMoveBy(int mx, int my){ }
	
	public void onTalk(Creature other) { }
	
	public void onNotify(String message){ }
	
	public void onDecease(Item corpse) { }
	
	public void onRangedWeaponAttack(Item weapon) { }
	
	public void onThrowItem(Item thrown) { }

	public boolean canSee(int wx, int wy) {
		int visionRadius = creature.visionRadius();
		Creature c = creature.world().creature(wx, wy);
		
		if(c != null && c.isPlayer())
			visionRadius = Math.max(Constants.STEALTH_MIN_RADIUS, visionRadius - c.stealthLevel());
		
		if ((creature.x-wx)*(creature.x-wx) + (creature.y-wy)*(creature.y-wy) > visionRadius*visionRadius)
			return false;
		
		for (Point p : new Line(creature.x, creature.y, wx, wy)){
			if (creature.realTile(p.x, p.y).isGround() || p.x == wx && p.y == wy)
				continue;
			
			return false;
		}
		
		return true;
	}

	public Tile rememberedTile(int wx, int wy) {
		return Tile.UNKNOWN;
	}

	protected boolean canThrowAt(Creature other) {
		return creature.canSee(other.x, other.y)
			&& getWeaponToThrow() != null;
	}

	protected Item getWeaponToThrow() {
		Item toThrow = null;
		
		for (Item item : creature.inventory().getItems()){
			if (item == null || creature.weapon() == item || creature.armor() == item ||
					creature.helment() == item || creature.shield() == item)
				continue;
			
			if (toThrow == null && (item.totalAttackValue() > 0 || item.stackable()))
				toThrow = item;
		}
		
		return toThrow;
	}
	
	protected boolean canRangedWeaponAttack(Creature other) {
		return creature.weapon() != null
		    && creature.weapon().attackValue(DamageType.RANGED) > 0
		    && creature.canSee(other.x, other.y);
	}

	protected boolean canPickup() {
		return creature.item(creature.x, creature.y) != null
			&& !creature.inventory().isFull()
			&& !creature.item(creature.x, creature.y).getBooleanData(Constants.CHECK_CORPSE);
	}
	
	protected boolean canMove(int mx, int my){
		Creature other = creature.creature(creature.x + mx, creature.y + my);
		
		if (other != null && (other.getStringData("Race") == creature.getStringData("Race")) 
				|| !creature.tile(creature.x+mx, creature.y+my).isGround()){
			return false;
		}else{
			return true;
		}
	}
	
	public void wander(){
		int mx = (int)(Math.random() * 3) - 1;
		int my = (int)(Math.random() * 3) - 1;
		
		if(mx != 0 && my != 0){	//Elimina combate en diagonal
			if(Math.random() > .5f)
				mx = 0;
			else
				my = 0;
		}
		
		if (!canMove(mx, my))
			return;
		else
			creature.moveBy(mx, my);
	}
	
	public void flee(Creature target){
		List<Point> points = new Path(creature, target.x, target.y).points();
		
		if(points == null || points.isEmpty())
			return;
		
		int mx = points.get(0).x - creature.x;
		int my = points.get(0).y - creature.y;
		int x_distance = Math.abs(creature.x - target.x);
		int y_distance = Math.abs(creature.y - target.y);
			
		Point no_diagonal = eliminateDiagonal(mx, my, x_distance, y_distance);
		
		creature.moveBy(-no_diagonal.x, -no_diagonal.y);
	}
	
	public boolean distanceFrom(Creature target, int amount){
		if(target == null || target.hp() < 1 || amount == 0)
			return false;
		
		if(creature.position().distance(target.position()) <= amount){
			flee(target);
			return true;
		}
		
		return false;
	}
	
	public void hunt(Creature target) {
		List<Point> points = new Path(creature, target.x, target.y).points();
		
		if(points == null || points.isEmpty())
			return;
		
		int mx = points.get(0).x - creature.x;
		int my = points.get(0).y - creature.y;
		int x_distance = Math.abs(creature.x - target.x);
		int y_distance = Math.abs(creature.y - target.y);
		
		Point no_diagonal = eliminateDiagonal(mx, my, x_distance, y_distance);
		
		boolean distanced = distanceFrom(target, creature.weapon() != null ? creature.weapon().cantReach() : 0);
		
		if(!distanced)
			creature.moveBy(no_diagonal.x, no_diagonal.y);
	}
	
	public void hunt(Point target) {
		List<Point> points = new Path(creature, target.x, target.y).points();
		
		if(points == null || creature == null || points.isEmpty())
			return;
		
		int mx = points.get(0).x - creature.x;
		int my = points.get(0).y - creature.y;
		
		int x_distance = Math.abs(creature.x - target.x);
		int y_distance = Math.abs(creature.y - target.y);
		
		Point no_diagonal = eliminateDiagonal(mx, my, x_distance, y_distance);
		
		creature.moveBy(no_diagonal.x, no_diagonal.y);
	}
	
	public Point eliminateDiagonal(int mx, int my, int x_distance, int y_distance){
		if(mx != 0 && my != 0){
			if(canMove(0, my) && y_distance > x_distance)
				mx = 0;
			else if(canMove(mx, 0) && x_distance > y_distance)
				my = 0;
			else if(Math.random() > .5f)
				mx = 0;
			else
				my = 0;
		}
		
		return new Point(mx, my);
	}
	
	public void visitCheckPoint(){
		Point check = (Point) creature.getData(Constants.CHECKPOINT);
		
		if(check != null){
			hunt(check);
			
			if(creature.position().distance(check) <= 2)
				creature.unsetData(Constants.CHECKPOINT);
		}
	}

	protected boolean canUseBetterEquipment() {
		int currentWeaponRating = creature.weapon() == null ? 0 : creature.weapon().totalAttackValue();
		int currentArmorRating = creature.armor() == null ? 0 : creature.armor().totalDefenseValue();
		int currentHelmentRating = creature.helment() == null ? 0 : creature.helment().totalDefenseValue();
		int currentShieldRating = creature.shield() == null ? 0 : creature.shield().totalDefenseValue();
		
		for (Item item : creature.inventory().getItems()){
			if (item == null)
				continue;
			
			boolean isWeapon = item.getBooleanData(Constants.CHECK_WEAPON);
			boolean isArmor = item.getBooleanData(Constants.CHECK_ARMOR);
			boolean isHelment = item.getBooleanData(Constants.CHECK_HELMENT);
			boolean isShield = item.getBooleanData(Constants.CHECK_SHIELD);
			
			if (item.totalAttackValue() > currentWeaponRating && isWeapon
					|| isArmor && item.totalDefenseValue() > currentArmorRating
					|| isHelment && item.totalDefenseValue() > currentHelmentRating
					|| isShield && item.totalDefenseValue() > currentShieldRating)
				return true;
		}
		
		return false;
	}

	protected void useBetterEquipment() {
		int currentWeaponRating = creature.weapon() == null ? 0 : creature.weapon().totalAttackValue();
		int currentArmorRating = creature.armor() == null ? 0 : creature.armor().totalDefenseValue();
		int currentHelmentRating = creature.helment() == null ? 0 : creature.helment().totalDefenseValue();
		int currentShieldRating = creature.shield() == null ? 0 : creature.shield().totalDefenseValue();
		
		for (Item item : creature.inventory().getItems()){
			if (item == null)
				continue;
			
			boolean isWeapon = item.getBooleanData(Constants.CHECK_WEAPON);
			boolean isArmor = item.getBooleanData(Constants.CHECK_ARMOR);
			boolean isHelment = item.getBooleanData(Constants.CHECK_HELMENT);
			boolean isShield = item.getBooleanData(Constants.CHECK_SHIELD);
			
			if (item.totalAttackValue() > currentWeaponRating && isWeapon
					|| isArmor && item.totalDefenseValue() > currentArmorRating
					|| isHelment && item.totalDefenseValue() > currentHelmentRating
					|| isShield && item.totalDefenseValue() > currentShieldRating) {
				creature.equip(item);
			}
		}
	}
}
