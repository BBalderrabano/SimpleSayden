package sayden.ai;

import sayden.Constants;
import sayden.Creature;
import sayden.Speed;

public class DogAi extends CreatureAi {
	Creature player;
	Creature owner;
	
	public DogAi(Creature creature, Creature player) {
		super(creature);
		
		this.player = player;
		creature.setData(Constants.RACE, "perro");
		creature.setData(Constants.UNIVERSAL_ALLY, true);
	}
	
	public DogAi(Creature creature, Creature player, Creature owner) {
		super(creature);
		
		this.player = player;
		this.owner = owner;
		creature.setData(Constants.RACE, "perro");
		creature.setData(Constants.UNIVERSAL_ALLY, true);
	}

	public void onUpdate(){
		if(creature.isActive()){
			return;
		}
		
		visitCheckPoint();
		
		if(canSee(player.x, player.y) && creature.position().distance(player.position()) <= 4 && creature.position().distance(player.position()) > 2){
			
			creature.doAction("gruñe, clavandote su mirada");
			creature.modifyActionPoints(Speed.NORMAL.velocity());
			
		}else if(canSee(player.x, player.y) && creature.position().distance(player.position()) <= 2){
			hunt(player);
		}else if(creature.vigor() >= creature.maxVigor() * 0.5){
			if(owner != null && !canSee(owner.x, owner.y)){
				hunt(owner);
				if(creature.getData(Constants.CHECKPOINT) == null){
					creature.setData(Constants.CHECKPOINT, creature.position());
				}
			}else if(owner != null && canSee(owner.x, owner.y) && creature.getData(Constants.CHECKPOINT) != null){
				creature.doAction("llama al " + owner.name() + " con un gemido");
				owner.setData(Constants.CHECKPOINT, creature.getData(Constants.CHECKPOINT));
				creature.modifyActionPoints(Speed.NORMAL.velocity());
			}else{
				creature.doAction("aulla, asustado");
				player.notify("El aullido de un perro resuena por la cueva");
				creature.modifyActionPoints(Speed.NORMAL.velocity());
			}
		}
		
		wander();
	}
}
