package sayden;

import java.awt.Color;
import asciiPanel.AsciiPanel;

public class Constants {
	public static final int WORLD_WIDTH = 80;
	public static final int WORLD_HEIGHT = 24;
	
	public static final String CHEST_POS = "CHEST";
	public static final String ARM_POS = "ARMS";
	public static final String LEG_POS = "LEGS";
	public static final String HEAD_POS = "HEAD";
	public static final String NO_POS = "NONE";
	
	public static final String BLOOD_FLUID = "BLOOD";
	public static final String WATER_FLUID = "WATER";
	public static final String FIRE_TERRAIN = "FIRE";
	public static final float FIRE_DEGRADATION = 10f;
	public static final float MIN_FLUID_AMOUNT = 10f;
	public static final float BLOOD_AMOUNT_MULTIPLIER = 10f;
	
	public static final String CHECK_HELMENT = "IsHelment";
	public static final String CHECK_WEAPON = "IsWeapon";
	public static final String CHECK_ARMOR = "IsArmor";
	public static final String CHECK_SHIELD = "IsShield";
	public static final String CHECK_CONSUMABLE = "IsEdible";
	public static final String CHECK_STACKABLE = "IsStackable";
	public static final String CHECK_CORPSE = "IsCorpse";
	public static final String CHECK_MARAUDER_DISGUISE = "IsMarauderDisguise";
	
	public static final Color UNIDENTIFIED_COLOR = AsciiPanel.magenta;
	
	public static final String RACE = "Race";
	
	public static final boolean DEBUG_MODE = false;
	
	public static final String FLAG_ANGRY = "IsAngry";
}
