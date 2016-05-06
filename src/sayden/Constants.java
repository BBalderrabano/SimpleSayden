package sayden;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import asciiPanel.AsciiPanel;

public class Constants {
	public static final boolean PRODUCTION = false;	//Prender cuando se este pasando a produccion
	
	public static final int WORLD_WIDTH = 80;		//El ancho de la terminal AsciiPanel
	public static final int WORLD_HEIGHT = 24;		//La altura de la  terminal AsciiPanel
	
	public static final String SAVE_FILE_NAME = "savedata.ser";	//El nombre del archivo para guardar
	public static final String SAVE_FILE_DIRECTORY  = "files/";	//El directorio del ejecutable
	public static final String SAVE_FILE_FULL_NAME = SAVE_FILE_DIRECTORY + "" + SAVE_FILE_NAME;
	
	public static final int STEALTH_MIN_STEPS = 1;	//Cuantos pasos debes dar para ser considerado stealth
	public static final int STEALTH_MIN_RADIUS = 1;	//El minimo de radio de vision al que llegan las criaturas
	public static final int STEALTH_LEVEL_MAX = 8;	//El maximo de radio de vision al que se llega con el stealth
	public static final float STELTH_INCREMENTAL = 0.5f;	//Cuanto incrementa el nivel de stealth con cada paso
	
	public static final int STARTING_INV_SPACE = 20;//Con cuanto espacio de inventario inicia el player
	
	//Las posiciones de ataque
	public static final String BACK_POS = "BACK";
	public static final String ARM_POS = "ARMS";
	public static final String LEG_POS = "LEGS";
	public static final String HEAD_POS = "HEAD";
	public static final String NO_POS = "NONE";
	
	public static final String BLOOD_FLUID = "BLOOD";	//El fluido de la sangre, en un tile puede haber un maximo de 100f de sangre
	public static final float MIN_FLUID_AMOUNT = 15f;	//Cuando un tile ya tiene sangre, no puede bajar de 15f
	
	//+++++++++++++++++++++++++++CHEQUEOS VARIOS+++++++++++++++++++++++++++++++++++++
	public static final String RACE = "Race";							//El flag de la raza
	public static final String CHECKPOINT = "checkPoint";				//El flag del checkpoint
	public static final String CHECK_HELMENT = "IsHelment";
	public static final String CHECK_WEAPON = "IsWeapon";
	public static final String CHECK_ARMOR = "IsArmor";
	public static final String CHECK_SHIELD = "IsShield";
	public static final String CHECK_CONSUMABLE = "IsEdible";
	public static final String CHECK_STACKABLE = "IsStackable";
	public static final String CHECK_CORPSE = "IsCorpse";
	public static final String CHECK_TWO_HANDED = "IsTwoHanded";
	public static final String CHECK_MARAUDER_DISGUISE = "IsMarauderDisguise";
	public static final String CHECK_DUAL_WIELD = "IsDualWieldable";
	public static final String CHECK_RANGED = "IsRanged";
	public static final String CHECK_UNAUGMENTABLE = "IsUnableToAugment";
	public static final String CHECK_PROJECTILE_DISSAPEAR = "IsProjectile";
	public static final String CHECK_PROJECTILE_AUTOTARGET = "IsAutotarget";
	
	//+++++++++++++++++++++++++++FLAGS DE CONSULTA+++++++++++++++++++++++++++++++++++
	public static final String UNIVERSAL_ALLY = "IsUniversalAlly";	
	public static final String FLAG_ANGRY = "IsAngry";
	public static final String FLAG_INTRODUCED = "IsIntroduced";
	public static final String FLAG_DONE_TALKING = "IsDoneTalking";
	public static final String FLAG_INVISIBLE = "IsInvisible";
	public static final String FLAG_SEEN_PLAYER = "SeenPlayer";
	
	//+++++++++++++++++++++++++++COLORES+++++++++++++++++++++++++++++++++++++++++++++
	public static final Color UNIDENTIFIED_COLOR = AsciiPanel.magenta;	//El color de los items sin identificar
	public static final Color OVERLAY_COLOR = AsciiPanel.cyan;			//El color usado en los menus para informar que algo esta seleccionado (selecciono un item, ej)
	public static final Color THROWN_BKG_COLOR = AsciiPanel.cyan;		//El background color al arrojar un item
	public static final Color WOUND_COLOR = new Color(255, 191, 0);		//El color de las heridas
	public static final Color SPELL_QUE_COLOR = AsciiPanel.brightMagenta;	//El color de la criatura al estar lanzando un spell
	public static final Color ATTACK_QUE_COLOR = AsciiPanel.brightBlue;	//El color de la criatura al tratar de atacar
	
	//+++++++++++++++++++++++++++SPELLS++++++++++++++++++++++++++++++++++++++++++++++
	public static final String SPELL_HEAL = "Healed";					//Controla el spell "heal"
	public static final String SPELL_PAIN = "Pain";						//Controla el spell "provocar dolor"
	public static final String SPELL_WOUND_HEADACHE = "HasHeadache";	//La herida que te provoca lanzar un hechizo
	
	
	public static final String STUN_TEXT = "esta |aturdido03|";			//Que dice al estar aturdida una criatura
	
	public static String capitalize(String text){						//Funcion que devuelve el string capitalizado
		return Character.toUpperCase(text.charAt(0))+""+text.substring(1);
	}
	
	private static List<String> learNames = new ArrayList<String>();	//Guardamos los nombres de los items que conoce el jugador
	public static void learnName(String name) { learNames.add(name); }	//Aprendemos un nombre
	public static boolean knownName(String name) { 						//Chequeamos si conocemos un nombre
		if(name == null || name.isEmpty() || learNames == null || learNames.size() < 1)
			return false;
		if(learNames.contains(name))
			return true;
		return false;
	} 
}
