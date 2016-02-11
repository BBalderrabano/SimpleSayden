package sayden.ai;

import java.util.ArrayList;

import asciiPanel.AsciiPanel;
import sayden.Constants;
import sayden.Creature;
import sayden.Effect;
import sayden.Point;

public class PaseacuevasAi extends CreatureAi {

	private Creature player;
	
	private ArrayList<Creature> males = new ArrayList<Creature>();
	public void addMale(Creature creature) { males.add(creature); }
	
	public PaseacuevasAi(Creature creature, Creature player) {
		super(creature);
		this.player = player;
		
		creature.setData(Constants.RACE, "paseacuevas");
	}

	public void onUpdate(){
		super.onUpdate();
		
		if(males.isEmpty()){
			if(!creature.getBooleanData("SeenPlayer")){
				goBerzerk();
				creature.setData("SeenPlayer", true);
			}
			hunt(player);
			return;
		}else{
			for(int i = 0; i < males.size(); i++){
				if(males.get(i).hp() < 1){
					males.remove(males.get(i));
				}
			}
		}
		
		//If we can see the player
		if((canSee(player.x, player.y) || creature.getBooleanData("SeenPlayer"))
				&& creature.position().distance(player.position()) > 2){
			hunt(player);
			
			if(!creature.getBooleanData("SeenPlayer")){
				creature.doAction("pega un alarido y carga contra ti!");
				goBerzerk();
			}
			
			creature.setData("SeenPlayer", true);
			return;
		}else if(canSee(player.x, player.y)){
			hunt(player);
			return;
		}
		
		Point checkPoint = (Point) creature.getData("checkPoint");
		
		//We got to the checkpoint
		if(checkPoint != null && checkPoint.distance(creature.position()) <= 2){
			checkPoint = null;
			creature.unsetData("checkPoint");
			return;
		}else if(checkPoint != null){
			hunt(checkPoint);
			return;
		}
		for(Creature c : males){
			//If we are far from the males lets just go towards them
			if(c.position().distance(creature.position()) >= 5){
				hunt(c);
				return;
			}
		}
		wander();
	}
	
	private void goBerzerk(){
		creature.addEffect(new Effect("enfurecida", 10){
			public void start(Creature creature){
				creature.modifyMovementSpeed(creature.getMovementSpeed().modifySpeed(2));
				creature.changeColor(AsciiPanel.green);
			}
			public void end(Creature creature){
				creature.modifyMovementSpeed(creature.startingMovementSpeed());
				creature.changeColor(creature.originalColor());
				creature.unsetData("SeenPlayer");
			}
		});
	}
}
