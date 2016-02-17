package sayden.ai;

import sayden.Constants;
import sayden.Creature;
import sayden.DamageType;
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
	
	public boolean onGetAttacked(int amount, String position, Creature attacker, Item item){
		if(attacker.isPlayer() && !creature.getBooleanData("SeenPlayer")){
			creature.setData("SeenPlayer", true);
			
			for(Creature c : creature.getCreaturesWhoSeeMe()){
				if(c.getData(Constants.RACE) != creature.getData(Constants.RACE))
					continue;
				
				c.setData("SeenPlayer", true);
			}
		}
				
		return super.onGetAttacked(amount, position, attacker);
	}
	
	public void onDecease(Item corpse){
		corpse.setQuaffEffect(new Effect("envenenado", 6, false){
			public void start(Creature creature){
				creature.notify("El cadaver del merodeador esta empapado de |veneno04|!");
			}
			public void update(Creature creature){
				super.update(creature);
				if(creature.getStringData(Constants.RACE) != "merodeador"){
					creature.receiveDamage(2, DamageType.POISON, "El veneno del merodeador consume tus viceras", true);
				}
			}
		});
	}
	
	public void onUpdate(){
		super.onUpdate();
		
		if(canPickup()){
			creature.pickup();
			return;
		}
		if(canUseBetterEquipment()){
			useBetterEquipment();
			return;
		}
		if(canSee(player.x, player.y)){
			if(player.armor() != null && player.armor().getBooleanData(Constants.CHECK_MARAUDER_DISGUISE) &&
					player.helment() != null && player.helment().getBooleanData(Constants.CHECK_MARAUDER_DISGUISE) &&
						!creature.getBooleanData("SeenPlayer")){
				return;
			}
			
			creature.setData("SeenPlayer", true);

			if(canThrowAt(player) && getWeaponToThrow() != null && creature.position().distance(player.position()) > 3){
				creature.throwItem(getWeaponToThrow(), player.x, player.y);
				return;
			}
			if(creature.hp() >= creature.totalMaxHp() * 0.3f){
				hunt(player);
				return;
			}else{
				flee(player);
				return;
			}
		}else{
			for(Creature c : creature.getCreaturesWhoSeeMe()){
				if(c.getData(Constants.RACE) != "paseacuevas")
					continue;
				
				if(creature.position().distance(c.position()) <= 5){
					flee(c);
					break;
				}
			}
		}
		
		wander();
	}
}
