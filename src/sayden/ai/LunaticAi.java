package sayden.ai;

import sayden.Constants;
import sayden.Creature;
import sayden.Effect;
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
				creature.doAction("muerde un pedazo de tu rostro!");
				creature.doAction("estalla en una maniatica carcajada");
				creature.addEffect(new Effect("", 3){
					public void update(Creature creature){
						super.update(creature);
						creature.modifyHp(2, "Enloquecido");
					}
				});
				other.inflictWound(new Wound("desfigurado", 20, 100, Constants.HEAD_POS){
					public void start(Creature creature){
						creature.modifyVisionRadius(-3);
					}
				});
			}
		}else if(creature.y > other.y && creature.x >= other.x){
			if(Math.random() > 0.5){
				creature.doAction("golpea con %s tu entrepierna!", creature.weapon() == null ? "fuerza" : "su "+creature.weapon().name());
				creature.doAction(Math.random() > 0.5 ? "rechina sus dientes" : null);
				other.modifyActionPoints(-Speed.NORMAL.velocity(), true);
				other.notify("Pierdes el aliento");
			}
		}
		return super.onAttack(other);
	}
	
	public boolean onGetAttacked(int amount, String position, Creature attacker){
		if(attacker.isPlayer()){
			double rand = Math.random();
			
			if(rand < 0.1){
				creature.doAction("estalla en una maniatica carcajada");
				creature.addEffect(new Effect("", 3){
					public void update(Creature creature){
						super.update(creature);
						creature.modifyHp(2, "Enloquecido");
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
		
		return super.onGetAttacked(amount, position, attacker);
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
