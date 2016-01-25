package sayden.ai;

import java.util.ArrayList;

import sayden.Constants;
import sayden.Creature;
import sayden.StuffFactory;
import sayden.screens.TalkScreen;

public class BlacksMithAi extends CreatureAi {
	private Creature player;
	private StuffFactory factory;
	private ArrayList<String> messages;
	private ArrayList<String> options;
	
	private final String OPT_ESPADA = "Espada corta (cortante)";
	private final String OPT_MAZA = "Maza (contundente)";
	private final String OPT_DAGA = "Daga (penetrante)";
	private final String OPT_REJECT = "Rechazar el camino";
	
	public BlacksMithAi(Creature creature, Creature player, StuffFactory factory) {
		super(creature);
		this.player = player;
		this.messages = new ArrayList<String>();
		this.options = new ArrayList<String>();
		this.factory = factory;
		
		creature.setData(Constants.RACE, "human");
	}

	public void onTalk(Creature other){
		if(creature.getBooleanData("Finished")){
			return;
		}
		if(!creature.getBooleanData("IsIntroduced")){
			messages.clear();
			messages.add("Hola extraño, mi nombre es Marcos, mi camino es el del herrero");
			messages.add("Existo en la medida que puedo fraguar los metales");
			messages.add("Existo solo gracias a mi arte, solo a causa de mi arte");
			messages.add("Mas no tengo utilidad para mis constructos");
			messages.add("Pocas personas necesitan armas hoy en dia, y las que lo necesitan no son de fiar");
			messages.add("Eres tu de fiar? O lo que es mas importante...");
			messages.add("Encuentras en tu camino utilidad para un arma?");
			
			options.add(OPT_DAGA);
			options.add(OPT_MAZA);
			options.add(OPT_ESPADA);
			options.add(OPT_REJECT);
			other.subscreen = new TalkScreen(other, creature, messages, options){
				@Override
				public void onSelectOption(String option) {
					super.onSelectOption(option);

					if(option.equals(OPT_ESPADA)){
						player.pickup(factory.newShortSword(-1));
					}else if(option.equals(OPT_MAZA)){
						player.pickup(factory.newMace(-1));
					}else if(option.equals(OPT_DAGA)){
						player.pickup(factory.newDagger(-1));
					}else{
						creature.setData("Rejected", true);
					}
				}
			};
			creature.setData("IsIntroduced", true);
		}else if(creature.getBooleanData("Rejected")){
			messages.clear();
			messages.add("Bien...Poco consiguen las armas y demasiado demandan de uno");
			messages.add("El arma entrega la ilusion de poder, es un temible objeto de perdicion");
			messages.add("Cuantos guerreros de epocas pasadas perdieron su cordura...");
			messages.add("...su resolucion...su camino...");
			messages.add("Yo soy Marcos y mi camino es del herrero, que tengas suerte en el tuyo, donde sea que te lleve");
			other.subscreen = new TalkScreen(other, creature, messages, null);
			creature.setData("Finished", true);
		}else{
			messages.clear();
			messages.add("...");
			messages.add("Un sabio guerrero sabe que del arma poco se puede esperar");
			messages.add("Un sabio guerrero conoce sus habilidades y convierte a su arma en una extension de su persona");
			messages.add("Un sabio guerrero sabe que el arma es su maldicion pero tambien su herramienta");
			messages.add("...te ruego, desconocido, que si has de blandir una de mis armas hagas tuyo el camino del guerrero");
			messages.add("Yo soy Marcos y ya conoces mi camino...ten cuidado mientras deambules por el tuyo, desconocido");
			other.subscreen = new TalkScreen(other, creature, messages, null);
			creature.setData("Finished", true);
		}
	}
	
	public void onUpdate(){
		if(!canSee(player.x, player.y, player.z))
			wander();
	}
}
