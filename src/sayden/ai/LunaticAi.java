package sayden.ai;

import sayden.Creature;
import sayden.Effect;
import sayden.Item;
import sayden.Speed;
import sayden.Wound;

public class LunaticAi extends CreatureAi {
	private Creature player;
	
	public LunaticAi(Creature creature, Creature player) {
		super(creature);
		this.player = player;
	}
	
	public boolean onAttack(Creature other){
		if(creature.y < other.y && creature.x <= other.x){
			if(Math.random() > 0.5){
				creature.doAction("muerde un pedazo %s!", other.isPlayer() ? "de tu rostro" : "del rostro " + other.nameDelDeLa());
				creature.doAction("estalla en una maniatica carcajada");
				creature.addEffect(new Effect("", 3){
					public void update(Creature creature){
						super.update(creature);
					}
				});
			}
		}else if(creature.y > other.y && creature.x >= other.x){
			if(Math.random() > 0.5){
				creature.doAction("golpea con %s %s!", 
						creature.weapon() == null ? "fuerza" : "su "+creature.weapon().name(), 
						other.isPlayer() ? "tu entrepierna" : "la entrepierna " + other.nameDelDeLa());
				creature.doAction(Math.random() > 0.5 ? "rechina sus dientes, " : "contorsiona su rostro en una mueca salvaje");
				other.modifyStunTime(1);
				other.notify("Pierdes el aliento");
			}
		}
		return super.onAttack(other);
	}
	
	public boolean onGetAttacked(String position, Wound wound, Creature attacker, Item object){
		if(attacker.isPlayer()){
			double rand = Math.random();
			
			if(rand < 0.1){
				creature.doAction("estalla en una maniatica carcajada");
				creature.addEffect(new Effect("", 3){
					public void update(Creature creature){
						super.update(creature);
					}
				});
			}else if(!creature.getBooleanData("IsFeared")){
				rand = Math.random();
				creature.notifyArround("Los ojos "+ creature.nameDelDeLa() + " se impregnan de terror");
				creature.addEffect(new Effect("", 6){
					public void start(Creature creature){
						creature.setData("IsFeared", true);
					}
					public void end(Creature creature){
						creature.unsetData("IsFeared");
						creature.notifyArround("Con un espasmo nervioso y un fuerte alarido, " + creature.nameElLa() + " retoma valentia");
					}
				});
			}
		}
		
		return super.onGetAttacked(position, wound, attacker, object);
	}
	
	public void onUpdate(){
		if(canSee(player.x, player.y)){
			if(creature.getBooleanData("IsFeared")){
				hunt(player);
			}else{
				flee(player);
			}
		}
		
		wander();
	}
}
