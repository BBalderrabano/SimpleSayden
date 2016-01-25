package sayden.ai;

import sayden.Constants;
import sayden.Creature;
import sayden.Effect;
import sayden.Item;

public class MarauderAi extends CreatureAi {

	private Creature player;
	
	public MarauderAi(Creature creature, Creature player) {
		super(creature);
		this.player = player;
		this.setWeakSpot(Constants.HEAD_POS);
		
		creature.setData(Constants.RACE, "merodeador");
	}
	
	public void onDecease(Item corpse){
		corpse.setQuaffEffect(new Effect(6, false){
			public void start(Creature creature){
				creature.notify("El cadaver del merodeador esta empapado de veneno!");
			}
			public void update(Creature creature){
				super.update(creature);
				if(creature.getStringData(Constants.RACE) != "merodeador"){
					creature.modifyHp(-2, "El veneno del merodeador consume tus viceras!");
				}
			}
		});
	}
	
	public void onUpdate(){
		if(canPickup()){
			creature.pickup();
		}
		if(canUseBetterEquipment()){
			useBetterEquipment();
		}
		if(canSee(player.x, player.y, player.z)){
			if(player.armor() != null && player.armor().getBooleanData(Constants.CHECK_MARAUDER_DISGUISE) &&
					player.helment() != null && player.helment().getBooleanData(Constants.CHECK_MARAUDER_DISGUISE) &&
						!creature.getBooleanData("SeenPlayer")){
				return;
			}
			if(canThrowAt(player) && getWeaponToThrow() != null){
				creature.throwItem(getWeaponToThrow(), player.x, player.y, player.z);
				return;
			}
			if(creature.hp() >= creature.totalMaxHp() * 0.3f){
				hunt(player);
			}else{
				creature.setData("SeenPlayer", true);
				flee(player);
			}
			creature.setData("SeenPlayer", true);
		}
		return;
	}
}
