package sayden.ai;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sayden.Constants;
import sayden.Creature;
import sayden.Item;
import sayden.LevelUpController;
import sayden.Line;
import sayden.Path;
import sayden.Point;
import sayden.Tile;

public class CreatureAi {
	private String weakSpot;
	protected Creature creature;
	private Map<String, String> itemNames;
	
	public CreatureAi(Creature creature){
		this.creature = creature;
		this.creature.setCreatureAi(this);
		this.itemNames = new HashMap<String, String>();
		this.weakSpot = Constants.HEAD_POS;
	}
	
	public void setName(Item item, String name){
		itemNames.put(item.name(), name);
	}
	
	public void setWeakSpot(String position){
		this.weakSpot = position;
	}
	
	public String getWeakSpot(){
		return weakSpot == null || weakSpot.isEmpty() ? "none" : weakSpot;
	}
	
	public boolean canTakeAction(){
		return creature.getActionPoints() > 0 && 
				(creature.getActionPoints() > creature.getMovementSpeed() 
				//|| creature.getActionPoints() > creature.getAttackSpeed());	//Dont handle attacks through here
				);
	}
	
	public void onEnter(int x, int y, int z, Tile tile){
		if(creature.getActionPoints() < creature.getMovementSpeed() && !creature.isPlayer()){
			return;	//There are not enough action points to perform a movement action, return
		}else if(!creature.isPlayer()){
			creature.modifyActionPoints(-creature.getMovementSpeed());	//We can move, and we are not a player, substract movement speed
		}
		if (tile.isGround()){
			creature.x = x;
			creature.y = y;
			creature.z = z;
		} else {
			creature.doAction("bump into a wall");
		}
	}
	
	public void onAttack(int x, int y, int z, Creature other){
		if(creature.getActionPoints() < creature.getAttackSpeed() && !creature.isPlayer()){
			if(other.isPlayer()){
				if(creature.getAttackSpeed() < other.getAttackSpeed()){
					System.out.println("CONTRAATAQUE");
				}
				if(creature.getAttackSpeed() < other.getMovementSpeed()){
					System.out.println("DODGE");
				}
			}
			return;	//There are not enough action points to perform an attack, queue attack
		}else if(!creature.isPlayer()){
			creature.modifyActionPoints(-creature.getAttackSpeed());
		}
		creature.meleeAttack(other);
	}
	
	public void onUpdate(){
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
	
	public void wander(){
		int mx = (int)(Math.random() * 3) - 1;
		int my = (int)(Math.random() * 3) - 1;
		
		if(mx != 0 && my != 0){	//Elimina combate en diagonal
			if(Math.random() > .5f)
				mx = 0;
			else
				my = 0;
		}
		
		Creature other = creature.creature(creature.x + mx, creature.y + my, creature.z);
		
		if (other != null && other.name().equals(creature.name()) 
				|| !creature.tile(creature.x+mx, creature.y+my, creature.z).isGround())
			return;
		else
			creature.moveBy(mx, my, 0);
	}

	public void onGainLevel() {
		new LevelUpController().autoLevelUp(creature);
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
			if (item == null || creature.weapon() == item || creature.armor() == item)
				continue;
			
			if (toThrow == null || item.thrownAttackValue() > toThrow.attackValue())
				toThrow = item;
		}
		
		return toThrow;
	}

	protected boolean canRangedWeaponAttack(Creature other) {
		return creature.weapon() != null
		    && creature.weapon().rangedAttackValue() > 0
		    && creature.canSee(other.x, other.y, other.z);
	}

	protected boolean canPickup() {
		return creature.item(creature.x, creature.y, creature.z) != null
			&& !creature.inventory().isFull();
	}
	
	public void move(int x, int y, int z){
		List<Point> points = new Path(creature, x, y).points();
		
		if(points == null || points.isEmpty())
			return;
		
		int mx = points.get(0).x - creature.x;
		int my = points.get(0).y - creature.y;
		
		if(mx != 0 && my != 0){	//Elimina combate en diagonal
			if(Math.random() > .5f)
				mx = 0;
			else
				my = 0;
		}
		
		creature.moveBy(mx, my, 0);
	}

	public void hunt(Creature target) {
		List<Point> points = new Path(creature, target.x, target.y).points();
		
		int mx = points.get(0).x - creature.x;
		int my = points.get(0).y - creature.y;
		
		if(mx != 0 && my != 0){	//Elimina combate en diagonal
			if(Math.random() > .5f)
				mx = 0;
			else
				my = 0;
		}
		
		creature.moveBy(mx, my, 0);
	}

	protected boolean canUseBetterEquipment() {
		int currentWeaponRating = creature.weapon() == null ? 0 : creature.weapon().attackValue() + creature.weapon().rangedAttackValue();
		int currentArmorRating = creature.armor() == null ? 0 : creature.armor().defenseValue();
		
		for (Item item : creature.inventory().getItems()){
			if (item == null)
				continue;
			
			boolean isArmor = item.attackValue() + item.rangedAttackValue() < item.defenseValue();
			
			if (item.attackValue() + item.rangedAttackValue() > currentWeaponRating
					|| isArmor && item.defenseValue() > currentArmorRating)
				return true;
		}
		
		return false;
	}

	protected void useBetterEquipment() {
		int currentWeaponRating = creature.weapon() == null ? 0 : creature.weapon().attackValue() + creature.weapon().rangedAttackValue();
		int currentArmorRating = creature.armor() == null ? 0 : creature.armor().defenseValue();
		
		for (Item item : creature.inventory().getItems()){
			if (item == null)
				continue;
			
			boolean isArmor = item.attackValue() + item.rangedAttackValue() < item.defenseValue();
			
			if (item.attackValue() + item.rangedAttackValue() > currentWeaponRating
					|| isArmor && item.defenseValue() > currentArmorRating) {
				creature.equip(item);
			}
		}
	}
}
