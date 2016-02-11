package sayden.ai;

import java.util.ArrayList;

import sayden.Constants;
import sayden.Creature;
import sayden.StuffFactory;
import sayden.screens.TalkScreen;

public class BlacksMithAi extends HumanoidAi {
	private Creature player;
	private StuffFactory factory;
	private ArrayList<String> messages;
	private ArrayList<String> options;
	
	private final String OPT_ARMA = "Pedir un arma";
	private final String OPT_EXCUSA = "Excusarse";
	
	private final String OPT_ESPADA = "Espada (cortante)";
	private final String OPT_MAZA = "Maza (contundente)";
	private final String OPT_DAGA = "Daga (cortante)";
	
	public BlacksMithAi(Creature creature, Creature player, StuffFactory factory) {
		super(creature, player);
		this.player = player;
		this.messages = new ArrayList<String>();
		this.options = new ArrayList<String>();
		this.factory = factory;
		
		creature.setData(Constants.RACE, "human");
	}

	public void onTalk(Creature other){
		super.onTalk(other);
		
		if(creature.getBooleanData("Finished")){
			messages.clear();
			if(creature.getBooleanData("GiveWeapon"))
				messages.add("\"Ya tienes lo que necesitabas, ahora dejame en paz\"");
			else
				messages.add("\"Ya ocupaste demasiado de mi tiempo, dejame en paz\"");
			
			other.subscreen = new TalkScreen(other, creature, messages, null);
			return;
		}
		if(!creature.getBooleanData(Constants.FLAG_INTRODUCED)){
			messages.clear();
			messages.add("\"Hey, extraño que haces en mi herreria?\"");
			
			options.clear();
			options.add(OPT_EXCUSA);
			options.add(OPT_ARMA);
			
			other.subscreen = new TalkScreen(other, creature, messages, options){
				@Override
				public void onSelectOption(String option) {
					super.onSelectOption(option);

					if(option.equals(OPT_ARMA)){
						creature.setData("AskWeapon", true);
					}else if(option.equals(OPT_EXCUSA)){
						creature.setData("Rejected", true);
					}
				}
			};
			creature.setData(Constants.FLAG_INTRODUCED, true);
		}else if(creature.getBooleanData("Rejected")){
			messages.clear();
			messages.add("\"No hay problema pero no me molestes, estoy muy ocupado\"");
			other.subscreen = new TalkScreen(other, creature, messages, null);
			creature.setData("Finished", true);
		}else if(creature.getBooleanData("AskWeapon")){
			messages.clear();
			messages.add("El herrero te fulmina con la mirada");
			messages.add("\"Tengo algo que quizas te sirva...\"");
			messages.add("El herrero exhala un suspiro");
			messages.add("\"...guerrero\"");
			
			options.clear();
			options.add(OPT_EXCUSA);
			options.add(OPT_DAGA);
			options.add(OPT_MAZA);
			options.add(OPT_ESPADA);
			
			other.subscreen = new TalkScreen(other, creature, messages, options){
				@Override
				public void onSelectOption(String option) {
					super.onSelectOption(option);

					if(option.equals(OPT_ESPADA)){
						player.pickup(factory.newShortSword(false));
						player.notify("El herrero te entrega una espada corta");
						creature.setData("GiveWeapon", true);
					}else if(option.equals(OPT_MAZA)){
						player.pickup(factory.newMace(false));
						player.notify("El herrero te entrega una maza");
						creature.setData("GiveWeapon", true);
					}else if(option.equals(OPT_DAGA)){
						player.pickup(factory.newDagger(false));
						player.notify("El herrero te entrega una daga");
						creature.setData("GiveWeapon", true);
					}
				}
			};
			
			creature.setData("Finished", true);
		}
	}
	
	public void onUpdate(){
		super.onUpdate();
		
		if(!canSee(player.x, player.y))
			wander();
	}
}
