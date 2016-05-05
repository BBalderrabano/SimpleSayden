package sayden;

import java.util.ArrayList;

public class DamageType {
	public static ArrayList<DamageType> ALL_TYPES_INSTANCE(){
		ArrayList<DamageType> toReturn = new ArrayList<DamageType>();
		toReturn.add(new DamageType(1, "cortante", "daño efectuado por corte", 0));
		toReturn.add(new DamageType(2, "contundente", "daño efectuado por golpes fuertes", 0));
		toReturn.add(new DamageType(3, "penetrante", "daño efectuado por penetrar la piel", 0));
		toReturn.add(new DamageType(4, "rango", "daño efectuado por armas de rango", 0));
		toReturn.add(new DamageType(5, "magico", "daño efectuador por fuentes magicas", 0));
		toReturn.add(new DamageType(6, "veneno", "daño efectuador por venenos", 0));
		return toReturn;
	}
	
	public static DamageType getInstance(int type){
		for(DamageType d : ALL_TYPES_INSTANCE()){
			if(d.id == type){
				return d;
			}
		}
		return null;
	}
	
	public static ArrayList<Integer> ALL_TYPES(){
		ArrayList<Integer> toReturn = new ArrayList<Integer>();
		toReturn.add(1);
		toReturn.add(2);
		toReturn.add(3);
		toReturn.add(4);
		toReturn.add(5);
		toReturn.add(6);
		return toReturn;
	}
	
	public static final int SLICE = 1;
	public static final int BLUNT = 2;
	public static final int PIERCING = 3;
	public static final int RANGED = 4;
	public static final int MAGIC = 5;
	public static final int POISON = 6;
	
	public int id;
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
