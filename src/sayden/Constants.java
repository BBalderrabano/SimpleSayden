package sayden;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import asciiPanel.AsciiPanel;

public class Constants {
	public static final boolean PRODUCTION = true;
	
	public static final int WORLD_WIDTH = 80;
	public static final int WORLD_HEIGHT = 24;
	
	public static final String SAVE_FILE_NAME = "savedata.ser";
	public static final String SAVE_FILE_DIRECTORY  = "files/";
	public static final String SAVE_FILE_FULL_NAME = SAVE_FILE_DIRECTORY + "" + SAVE_FILE_NAME;
	
	public static final int STEALTH_MIN_STEPS = 1;
	public static final int STEALTH_MIN_RADIUS = 1;
	public static final int STEALTH_LEVEL_MAX = 8;
	public static final float STELTH_INCREMENTAL = 0.5f;
	
	public static final String CHEST_POS = "CHEST";
	public static final String ARM_POS = "ARMS";
	public static final String LEG_POS = "LEGS";
	public static final String HEAD_POS = "HEAD";
	public static final String NO_POS = "NONE";
	
	public static final String BLOOD_FLUID = "BLOOD";
	public static final String WATER_FLUID = "WATER";
	public static final String FIRE_TERRAIN = "FIRE";
	public static final float FIRE_DEGRADATION = 10f;
	public static final float MIN_FLUID_AMOUNT = 15f;
	public static final float BLOOD_AMOUNT_MULTIPLIER = 10f;
	
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
	
	public static final Color UNIDENTIFIED_COLOR = AsciiPanel.magenta;
	public static final Color OVERLAY_COLOR = AsciiPanel.cyan;
	
	public static final String RACE = "Race";
	
	public static final String CHECKPOINT = "checkPoint";
	
	public static final String FLAG_ANGRY = "IsAngry";
	public static final String FLAG_INTRODUCED = "IsIntroduced";
	public static final String FLAG_DONE_TALKING = "IsDoneTalking";
	public static final String FLAG_INVISIBLE = "IsInvisible";
	public static final String FLAG_SEEN_PLAYER = "SeenPlayer";
	
	public static final String SPELL_HEAL = "Healed";
	public static final String SPELL_PAIN = "Pain";
	
	public static final String WOUND_HEADACHE = "HasHeadache";
	
	public static String capitalize(String text){
		return Character.toUpperCase(text.charAt(0))+""+text.substring(1);
	}
	
	private static List<String> learNames = new ArrayList<String>();
	public static void learnName(String name) { learNames.add(name); }
	public static boolean knownName(String name) { 
		if(name == null || name.isEmpty() || learNames == null || learNames.size() < 1)
			return false;
		if(learNames.contains(name))
			return true;
		return false;
	} 
}
