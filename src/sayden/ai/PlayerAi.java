package sayden.ai;

import java.util.List;

import sayden.Constants;
import sayden.Creature;
import sayden.FieldOfView;
import sayden.Item;
import sayden.Tile;
import sayden.Wound;

public class PlayerAi extends CreatureAi {

	private List<String> messages;
	private FieldOfView fov;
	
	private boolean isStealthing = false;
	private boolean lastSkipped = false;
	
	public PlayerAi(Creature creature, List<String> messages, FieldOfView fov) {
		super(creature);
		this.messages = messages;
		this.fov = fov;

		creature.makePlayer();
		creature.setData(Constants.RACE, "human");
	}
	
	public void onMoveBy(int mx, int my){
		if(mx == 0 && my == 0){
			lastSkipped = true;
		}else{
			if(lastSkipped){
				isStealthing = true;
			}else{
				isStealthing = false;
			}
			lastSkipped = false;
			
			if(isStealthing){
				creature.modifyStealth(Constants.STELTH_INCREMENTAL);
			}else{
				creature.removeStealth();
			}
		}
		
		super.onMoveBy(mx, my);
	}

	public void onEnter(int x, int y, Tile tile){
		if (tile.isGround()){
			creature.x = x;
			creature.y = y;
			
			Item item = creature.item(creature.x, creature.y);
			if (item != null){
				creature.notify("Hay " + item.nameUnUna() + " aqui.");
			}
			
			creature.woundMove(x, y);
		} else if (tile.isDiggable()) {
			creature.dig(x, y);
		} else if (tile.isDoor()) {
			creature.open(x, y);
		}
		
//		creature.addTime(creature.getMovementSpeed().velocity());
		creature.modifyActionPoints(creature.getMovementSpeed().velocity(), false);
	}
	
	public void onUpdate(){	}
	
	public boolean onAttack(Creature other){
		if(creature.getData("Race") == other.getData("Race") && !other.getBooleanData(Constants.FLAG_ANGRY)){
			other.ai().onTalk(creature);
			return false;
		}
		
		creature.modifyActionPoints(creature.dualStrike() ? creature.offWeapon().attackSpeed().velocity() : 
															creature.getAttackSpeed().velocity(), false);
		
		boolean success = creature.meleeAttack(other);
		
		if(success){
			if(other.hp() > 1 && other.queSpell() != null){
				other.stopCasting();
				other.modifyActionPoints(-other.getActionPoints(), false);
			}
			if(creature.weapon() != null && !creature.weapon().getBooleanData(Constants.CHECK_RANGED) && 
					!creature.weapon().getBooleanData(Constants.CHECK_UNAUGMENTABLE)){
				int weaponLevel = creature.weapon().countHit();
				
				if(weaponLevel > 0){
					creature.notify("Te sientes mas comodo con tu " + creature.weapon().name);
					if(weaponLevel == 1){
						creature.weapon().modifyAttackValue(creature.weapon().highestDamage(), 2);
					}
					if(weaponLevel == 3){
						if(creature.weapon().attackSpeed() != null){
							creature.weapon().modifyAttackSpeed(creature.weapon().attackSpeed().modifySpeed(-1));
						}
					}
				}
			}
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
		
		if(creature.weapon() != null && creature.weapon().inflictsWounds() && success){
			Wound inflictWound = creature.weapon().pickWeightedWound(position);
			if(inflictWound != null)
				other.inflictWound(inflictWound);
		}
		
		creature.woundOnHit(success, position);
		other.woundOnGetHit(success, position);
		
		return success;
	}
	
	public void onRangedWeaponAttack(Item weapon){
		int weaponLevel = weapon.countHit();
		
		if(weaponLevel > 0){
			creature.notify("Te sientes mas comodo con tu " + weapon.name);
			if(weaponLevel == 1){
				weapon.modifyAttackValue(weapon.highestDamage(), 2);
			}
			if(weaponLevel == 3){
				if(weapon.attackSpeed() != null){
					weapon.modifyAttackSpeed(weapon.attackSpeed().modifySpeed(-1));
				}
			}
		}
	}
	
	public void onNotify(String message){
		messages.add(message);
	}
	
	public boolean canSee(int wx, int wy) {
		return fov.isVisible(wx, wy);
	}
	
	public void onGainLevel(){
	}

	public Tile rememberedTile(int wx, int wy) {
		return fov.tile(wx, wy);
	}
}
