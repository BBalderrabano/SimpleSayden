package sayden.ai;

import java.util.ArrayList;

import sayden.Constants;
import sayden.Creature;
import sayden.DamageType;
import sayden.Effect;
import sayden.Item;
import sayden.Point;
import sayden.Wound;

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
		
		creature.setData(Constants.DEALS_WOUNDS, true);
		creature.setData(Constants.RACE, "merodeador");
		
		possibleFatality().add(new Wound(4, "decapitacion", null, 'F', 1, 50){
			public boolean canBePicked(Creature attacker, Creature target, String position, DamageType dtype) {
				return dtype.id == DamageType.SLICE.id && 
						position == Constants.HEAD_POS && 
						attacker.weapon() != null && target.vigor() >= 4;
			}
			public boolean startFlavorText(Creature creature, Creature target){
				String text = "";
				if(creature.isPlayer()){
					text = "de tu " + creature.weapon().nameWNoStacks() + " separas"; 
				}else{
					text = "de su " + creature.weapon().nameWNoStacks() + " " + creature.nameElLa() + " separa"; 
				}
				target.notifyArround("Con un habil movimiento %s en seco la cabeza del merodeador", text);
				Item head = new Item('*', 'F', target.originalColor(), "cabeza de merodeador", null, 0);
				head.setData("IsMarauderHead", true);
				target.drop(head);
				target.makeBleed(100f);
				return false;
			}
		});
	}
	
	public boolean onGetAttacked(String position, Wound wound, Creature attacker, Item object){
		if(attacker.isPlayer()){
			creature.setData(Constants.FLAG_SEEN_PLAYER, true);
			alertPrescence();
		}
				
		return super.onGetAttacked(position, wound, attacker, object);
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
		
		reacToHead();
		
		if(canSee(player.x, player.y)){
			if(!playerHidden()){
				pack.remove(player);
				creature.setData(Constants.FLAG_SEEN_PLAYER, true);
				
				alertPrescence();
				
				if(canThrowAt(player) && getWeaponToThrow() != null && creature.position().distance(player.position()) > 3){
					creature.throwItem(getWeaponToThrow(), player.x, player.y);
					return;
				}
				if(creature.vigor() <= creature.maxVigor() * 0.5f){
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
					distanceFrom(c, 3);
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
	
	private void reacToHead(){
		for(Point p : creature.position().neighbors(creature.visionRadius())){
			Item temp = creature.item(p.x, p.y);
			
			if(temp != null && temp.getBooleanData("IsMarauderHead") && p.distance(creature.position()) <= 4){
				if(canSee(player.x, player.y)){
					creature.doAction("huye despavorido al ver la cabeza de uno de sus compañeros");
					distanceFrom(p, 5);
				}else if(pack.size() <= 2){
					creature.doAction("parece contemplar la cabeza de su compañero en silencio..");
				}
			}
		}
	}
}
