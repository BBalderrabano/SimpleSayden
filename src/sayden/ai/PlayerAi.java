package sayden.ai;

import java.util.List;

import sayden.Creature;
import sayden.FieldOfView;
import sayden.Item;
import sayden.Tile;

public class PlayerAi extends CreatureAi {

	private List<String> messages;
	private FieldOfView fov;
	
	public PlayerAi(Creature creature, List<String> messages, FieldOfView fov) {
		super(creature);
		this.messages = messages;
		this.fov = fov;
		this.setWeakSpot("NONE");
	}

	public void onEnter(int x, int y, int z, Tile tile){
		if (tile.isGround()){
			creature.x = x;
			creature.y = y;
			creature.z = z;
			
			Item item = creature.item(creature.x, creature.y, creature.z);
			if (item != null)
				creature.notify("Hay " + item.nameUnUna() + " aqui.");
		} else if (tile.isDiggable()) {
			creature.dig(x, y, z);
		}
		creature.world().modifyActionPoints(creature.getMovementSpeed());
	}
	
	public void onAttack(int x, int y, int z, Creature other){
		creature.world().modifyActionPoints(creature.getAttackSpeed());
		creature.meleeAttack(other);
	}
	
	public void onNotify(String message){
		messages.add(message);
	}
	
	public boolean canSee(int wx, int wy, int wz) {
		return fov.isVisible(wx, wy, wz);
	}
	
	public void onGainLevel(){
	}

	public Tile rememberedTile(int wx, int wy, int wz) {
		return fov.tile(wx, wy, wz);
	}
}
