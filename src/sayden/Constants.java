package sayden;

public class Constants {
	public static final String CHEST_POS = "CHEST";
	public static final String ARM_POS = "ARMS";
	public static final String LEG_POS = "LEGS";
	public static final String HEAD_POS = "HEAD";
	public static final String NO_POS = "NONE";
	
	public static final String BLOOD_FLUID = "BLOOD";
	public static final String WATER_FLUID = "WATER";
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
	
	public static final String RACE = "Race";
	
	public static final boolean DEBUG_MODE = false;
	
	public static String numberToText(int iNumero){
		// Método que dado un número me lo devuelve en texto		
		 switch(iNumero){
			case 1:
				return "uno";
			case 2:
				return "dos";
			case 3:
				return "tres";
			case 4:
				return "cuatro";
			case 5:
				return "cinco";
			case 6:
				return "seis";
			case 7:
				return "siete";
			case 8:
				return "ocho";
			case 9:
				return "nueve";
			case 10:
				return "diez";
			case 0:
				return "cero";
			default:
				return "";
		 }
	}
}
