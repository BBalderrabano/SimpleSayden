package sayden;

import java.util.ArrayList;

public class DamageType {
	public static ArrayList<DamageType> ALL_TYPES(){
		ArrayList<DamageType> toReturn = new ArrayList<DamageType>();
		toReturn.add(new DamageType(SLICE));
		toReturn.add(new DamageType(PIERCING));
		toReturn.add(new DamageType(BLUNT));
		toReturn.add(new DamageType(RANGED));
		toReturn.add(new DamageType(MAGIC));
		toReturn.add(new DamageType(POISON));
		return toReturn;
	}
	public static final DamageType SLICE = new DamageType(1, "cortante", "daño efectuado por corte", 0);
	public static final DamageType BLUNT = new DamageType(2, "contundente", "daño efectuado por golpes fuertes", 0);
	public static final DamageType PIERCING = new DamageType(3, "penetrante", "daño efectuado por penetrar la piel", 0);
	public static final DamageType RANGED = new DamageType(4, "rango", "daño efectuado por armas de rango", 0);
	public static final DamageType MAGIC = new DamageType(5, "magico", "daño efectuador por fuentes magicas", 0);
	public static final DamageType POISON = new DamageType(6, "venenoso", "daño efectuador por venenos", 0);
	
	protected int id;
	public String name;
	public String description;
	public int amount;
	
	public void modifyAmount(int value) { this.amount += value; }
	
	public DamageType(int id, String name, String description, int amount){
		this.id = id;
		this.name = name;
		this.description = description;
		this.amount = amount;
	}
	
	public DamageType(DamageType copy){
		this.id = copy.id;
		this.name = copy.name;
		this.description = copy.description;
		this.amount = copy.amount;
	}
}
