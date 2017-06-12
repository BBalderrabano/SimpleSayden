package sayden.ai;

import java.util.ArrayList;

import asciiPanel.AsciiPanel;
import sayden.Constants;
import sayden.Creature;
import sayden.Effect;

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
		if(creature.isActive()){
			return;
		}
		
		if(males.isEmpty()){
//			if(!creature.getBooleanData(Constants.FLAG_SEEN_PLAYER)){
//				goBerzerk();
//				creature.setData(Constants.FLAG_SEEN_PLAYER, true);
//			}
//			hunt(player);
//			return;
		}else{
			for(int i = 0; i < males.size(); i++){
				if(!males.get(i).isAlive()){
					males.remove(males.get(i));
				}
			}
		}
		
		//If we can see the player
		if((canSee(player.x, player.y) || creature.getBooleanData(Constants.FLAG_SEEN_PLAYER))
				&& creature.position().distance(player.position()) > 2){
			hunt(player);
			
			if(!creature.getBooleanData(Constants.FLAG_SEEN_PLAYER)){
				creature.doAction("pega un alarido y carga contra ti!");
				goBerzerk();
			}
			
			creature.setData(Constants.FLAG_SEEN_PLAYER, true);
			return;
		}else if(canSee(player.x, player.y)){
			hunt(player);
			return;
		}
		
		visitCheckPoint();
		
		for(Creature c : males){
			//If we are far from the males lets just go towards them
			if(c.position().distance(creature.position()) >= 5){
				hunt(c);
				return;
			}
		}
		
		for(Creature c : creature.getCreaturesWhoSeeMe()){
			//Paseacuevas dont like to be next to each other...
			if(c.name == creature.name){
				distanceFrom(c, 10);
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
				creature.unsetData(Constants.FLAG_SEEN_PLAYER);
			}
		});
	}
}
