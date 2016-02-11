package sayden.ai;

import java.util.ArrayList;

import sayden.Constants;
import sayden.Creature;
import sayden.StuffFactory;
import sayden.screens.TalkScreen;

public class PriestAi extends HumanoidAi {
	private Creature player;
	private StuffFactory factory;
	private ArrayList<String> messages;
	private ArrayList<String> options;
	
	private final String OPT_MAGIC = "Aceptar regalo";
	private final String OPT_REJECTED = "Rechazar";
	
	public PriestAi(Creature creature, Creature player, StuffFactory factory) {
		super(creature, player);
		this.player = player;
		this.messages = new ArrayList<String>();
		this.options = new ArrayList<String>();
		this.factory = factory;
		
		creature.setData(Constants.RACE, "human");
	}

	public void onTalk(Creature other){
		super.onTalk(other);
		
		if(creature.getBooleanData(Constants.FLAG_DONE_TALKING)){
			messages.clear();
			messages.add("\"Mucha suerte en tu camino, forastero\"");
			return;
		}
		
		if(!creature.getBooleanData(Constants.FLAG_INTRODUCED)){
			messages.clear();
			messages.add("\"Forastero, encuentras en tu camino lugar para la magia?\"");
			
			options.add(OPT_REJECTED);
			options.add(OPT_MAGIC);
			
			other.subscreen = new TalkScreen(other, creature, messages, options){
				@Override
				public void onSelectOption(String option) {
					super.onSelectOption(option);

					if(option.equals(OPT_MAGIC)){
						player.pickup(factory.newLeatherSpellbook(false));
						player.notify("El sacerdote te entrega un libro antiguo");
					}
					
					creature.setData(Constants.FLAG_DONE_TALKING, true);
				}
			};
		}
		
	}
	
	public void onUpdate(){
		super.onUpdate();
		
		if(!canSee(player.x, player.y))
			creature.moveBy(0,0);
	}
}
