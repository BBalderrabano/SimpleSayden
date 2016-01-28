package sayden.ai;

import java.util.List;
import sayden.Constants;
import sayden.Creature;
import sayden.DamageType;
import sayden.Item;
import sayden.Line;
import sayden.Path;
import sayden.Point;
import sayden.Tile;

public class CreatureAi {
	private String weakSpot;
	protected Creature creature;
	
	private Point checkPoint;
	public Point checkPoint() { return checkPoint; }
	public void setCheckPoint(Point p) { this.checkPoint = p; }
	
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
	
	public boolean canTakeAction(){
		return (creature.getActionPoints() > 0 && 
				(creature.getActionPoints() >= creature.getMovementSpeed().velocity() 
				|| creature.getActionPoints() >= creature.getAttackSpeed().velocity()));
	}
	
	public void onDecease(Item corpse) {	
	}
	
	public void onEnter(int x, int y, int z, Tile tile){
		if(creature.queAttack() != null)
			return;
		
		if(creature.getActionPoints() < creature.getMovementSpeed().velocity() && !creature.isPlayer()){
			return;	//There are not enough action points to perform a movement action, return
		}else if(!creature.isPlayer()){
			creature.modifyActionPoints(-creature.getMovementSpeed().velocity());	//We can move, and we are not a player, substract movement speed
		}
		if (tile.isGround()){
			creature.x = x;
			creature.y = y;
			creature.z = z;
		}
	}
	
	public void onAttack(int x, int y, int z, Creature other){
		if(creature.getData("Race") == other.getData("Race") &&
				!creature.getBooleanData(Constants.FLAG_ANGRY) &&
				!other.getBooleanData(Constants.FLAG_ANGRY))
			return;

		if(creature.getActionPoints() < creature.getAttackSpeed().velocity() && !creature.isPlayer()){
			if(other.isPlayer()){
				if(Math.abs(creature.getAttackSpeed().velocity() - other.getMovementSpeed().velocity()) > 2
						&& creature.queAttack() == null){
					creature.modifyActionPoints(-creature.getActionPoints());
					creature.setQueAttack(other.position());
				}
			}
			return;	//There are not enough action points to perform an attack, queue attack
		}else if(!creature.isPlayer()){
			creature.modifyActionPoints(-creature.getAttackSpeed().velocity());
		}
		creature.meleeAttack(other);
	}
	
	public void onTalk(Creature other) { }
	
	public void onUpdate(){
		if(creature.queAttack() != null && 
				creature.getActionPoints() >= creature.getAttackSpeed().velocity()){
			creature.modifyActionPoints(-creature.getAttackSpeed().velocity());
			
			Creature c = creature.world().creature(creature.queAttack().x, creature.queAttack().y, creature.queAttack().z);
			
			if(c == null){
				creature.x = creature.queAttack().x;
				creature.y = creature.queAttack().y;
				creature.z = creature.queAttack().z;
				creature.doAction("falla el ataque!");
			}else{
				creature.meleeAttack(c);
			}			
			creature.setQueAttack(null);
		}
	}
	
	public void onNotify(String message){
	}

	public boolean canSee(int wx, int wy, int wz) {
		if (creature.z != wz)
			return false;
		
		if ((creature.x-wx)*(creature.x-wx) + (creature.y-wy)*(creature.y-wy) > creature.visionRadius()*creature.visionRadius())
			return false;
		
		for (Point p : new Line(creature.x, creature.y, wx, wy)){
			if (creature.realTile(p.x, p.y, wz).isGround() || p.x == wx && p.y == wy)
				continue;
			
			return false;
		}
		
		return true;
	}

	public Tile rememberedTile(int wx, int wy, int wz) {
		return Tile.UNKNOWN;
	}

	protected boolean canThrowAt(Creature other) {
		return creature.canSee(other.x, other.y, other.z)
			&& getWeaponToThrow() != null;
	}

	protected Item getWeaponToThrow() {
		Item toThrow = null;
		
		for (Item item : creature.inventory().getItems()){
			if (item == null || creature.weapon() == item || creature.armor() == item ||
					creature.helment() == item || creature.shield() == item)
				continue;
			
			if (toThrow == null || item.totalAttackValue() > 0)
				toThrow = item;
		}
		
		return toThrow;
	}
	
	protected boolean canRangedWeaponAttack(Creature other) {
		return creature.weapon() != null
		    && creature.weapon().attackValue(DamageType.RANGED) > 0
		    && creature.canSee(other.x, other.y, other.z);
	}

	protected boolean canPickup() {
		return creature.item(creature.x, creature.y, creature.z) != null
			&& !creature.inventory().isFull()
			&& !creature.item(creature.x, creature.y, creature.z).getBooleanData(Constants.CHECK_CORPSE);
	}
	
	protected boolean canMove(int mx, int my){
		Creature other = creature.creature(creature.x + mx, creature.y + my, creature.z);
		
		if (other != null && (other.getStringData("Race") == creature.getStringData("Race")) 
				|| !creature.tile(creature.x+mx, creature.y+my, creature.z).isGround()){
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
			creature.moveBy(mx, my, 0);
	}
	
	public void flee(Creature target){
		List<Point> points = new Path(creature, target.x, target.y).points();
		
		int mx = points.get(0).x - creature.x;
		int my = points.get(0).y - creature.y;
		int x_distance = Math.abs(creature.x - target.x);
		int y_distance = Math.abs(creature.y - target.y);
			
		Point no_diagonal = eliminateDiagonal(mx, my, x_distance, y_distance);
		
		creature.moveBy(-no_diagonal.x, -no_diagonal.y, 0);
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
		
		creature.moveBy(no_diagonal.x, no_diagonal.y, 0);
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
		
		creature.moveBy(no_diagonal.x, no_diagonal.y, 0);
	}
	
	private Point eliminateDiagonal(int mx, int my, int x_distance, int y_distance){
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
		
		return new Point(mx, my, 0);
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
