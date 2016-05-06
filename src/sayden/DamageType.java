package sayden;

import java.util.ArrayList;

public class DamageType {
	public static ArrayList<DamageType> ALL_TYPES_INSTANCE(){
		ArrayList<DamageType> toReturn = new ArrayList<DamageType>();
		toReturn.add(SLICE());
		toReturn.add(BLUNT());
		toReturn.add(PIERCING());
		toReturn.add(RANGED());
		toReturn.add(MAGIC());
		toReturn.add(POISON());
		return toReturn;
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
	
	public static final DamageType SLICE() { return new DamageType(1, "cortante", "daño efectuado por corte", 0); };
	public static final DamageType BLUNT() { return new DamageType(2, "contundente", "daño efectuado por golpes fuertes", 0); };
	public static final DamageType PIERCING() { return new DamageType(3, "penetrante", "daño efectuado al penetrar la piel", 0); };
	public static final DamageType RANGED() { return new DamageType(4, "rango", "daño efectuado por objetos en vuelo", 0); };
	public static final DamageType MAGIC() { return new DamageType(5, "magico", "daño efectuado por fuentes magicas", 0); };
	public static final DamageType POISON() { return new DamageType(6, "veneno", "daño efectuado por venenos", 0); };
	
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
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof DamageType))
			return false;
		DamageType other = (DamageType) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	public boolean equals(int id) {
		if (this.id != id)
			return false;
		return true;
	}
}
