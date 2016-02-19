package sayden.ai;

import java.util.ArrayList;

import sayden.Constants;
import sayden.Creature;
import sayden.DamageType;
import sayden.Effect;
import sayden.Item;

public class MarauderAi extends CreatureAi {

	private Creature player;
	private ArrayList<Creature> pack;
	
	boolean playerHidden() { return player != null &&
			player.armor() != null && player.armor().getBooleanData(Constants.CHECK_MARAUDER_DISGUISE) &&
			player.helment() != null && player.helment().getBooleanData(Constants.CHECK_MARAUDER_DISGUISE) &&
			!creature.getBooleanData(Constants.FLAG_SEEN_PLAYER); }
	
	public MarauderAi(Creature creature, Creature player) {
		super(creature);
		this.player = player;
		this.setWeakSpot(Constants.HEAD_POS);
		this.pack = new ArrayList<Creature>();
		
		creature.setData(Constants.RACE, "merodeador");
	}
	
	public boolean onGetAttacked(int amount, String position, Creature attacker){
		if(attacker.isPlayer()){
			creature.setData(Constants.FLAG_SEEN_PLAYER, true);
			alertPrescence();
		}
				
		return super.onGetAttacked(amount, position, attacker);
	}
	
	private void alertPrescence(){
		boolean alert = false;
		
		for(Creature c : creature.getCreaturesWhoSeeMe()){
			if(c.getData(Constants.RACE) != creature.getData(Constants.RACE) || c.getBooleanData(Constants.FLAG_SEEN_PLAYER))
				continue;
			
			alert = true;
			c.setData(Constants.FLAG_SEEN_PLAYER, true);
		}
		
		if(alert)
			creature.doAction("alerta de tu presencia!");
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
			if(!playerHidden()){
				pack.remove(player);
				creature.setData(Constants.FLAG_SEEN_PLAYER, true);
				
				alertPrescence();
				
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
			}
		}else{
			for(Creature c : creature.getCreaturesWhoSeeMe()){
				if((c.getData(Constants.RACE) == creature.getData(Constants.RACE)) 
						|| (c.isPlayer() && playerHidden()))
					pack.add(c);
				
				if(c.getData(Constants.RACE) == "paseacuevas"
						&& pack.size() < 4 && !pack.contains(player)){
					distanceFrom(c, 4);
					return;
				}else if(c.getData(Constants.RACE) == "paseacuevas" && (pack.size() >= 4 || pack.contains(player))){
					hunt(c);
					return;
				}
			}
		}
		
		visitCheckPoint();
		
		int dist = 0;
		Creature furthest = null;
		
		for(Creature ally : pack){
			if(creature.position().distance(ally.position()) > dist){
				dist = (int) creature.position().distance(ally.position());
				furthest = ally;
			}else{
				continue;
			}
		}
		
		if(furthest != null && dist > 2){
			hunt(furthest);
			return;
		}
		
		wander();
	}
}
