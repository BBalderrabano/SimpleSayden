package sayden;

import java.util.ArrayList;

public class Wound extends Thing implements Cloneable {
	
	public Object clone()throws CloneNotSupportedException{	return super.clone(); }
	
	public static enum MESSAGE{
		ON_HIT,
		ON_MOVE
	}
	
	public static ArrayList<Wound> DEFAULT_WOUNDS(){
		ArrayList<Wound> defWounds = new ArrayList<Wound>();
		
		defWounds.add(new Wound(1, "moreton", "Tus heridas finalmente te colapsan, mueres en agonia", 'M', 20, 100){
			@Override
			public boolean canBePicked(Creature attacker, Creature target, String position, int dtype) {
				return dtype == DamageType.BLUNT;
			}
		});
		
		defWounds.add(new Wound(1, "corte superficial", "Tus heridas finalmente te colapsan, mueres en agonia", 'M', 20, 100){
			@Override
			public boolean canBePicked(Creature attacker, Creature target, String position, int dtype) {
				return dtype == DamageType.SLICE ||
						dtype == DamageType.PIERCING ||
						dtype == DamageType.RANGED;
			}
		});
		
		defWounds.add(new Wound(1, "herida arcana", "La magia finalmente te es demasia, mueres de un derrame cerebral", 'F', 20, 100){
			@Override
			public boolean canBePicked(Creature attacker, Creature target, String position, int dtype) {
				return dtype == DamageType.MAGIC;
			}
		});
		
		return defWounds;
	}
	
	protected int duration;
	
	protected double weight;
	
	protected int vigor;
	
	public boolean isDone() { return this.duration < 1; }
	
	private String causeOfDeath;
	public String causeOfDeath() { return causeOfDeath; }
	
	public Wound(int vigor, String name, String causeOfDeath, char gender, int duration, double weight){
		this.duration = duration;
		this.causeOfDeath = causeOfDeath;
		this.name = name;
		this.gender = gender;
		this.weight = weight;
		this.vigor = vigor;
	}
	
	public boolean canBePickedFRanged(Creature target, int dtype, int amount){
		return true;
	}
	
	public boolean canBePicked(Creature attacker, Creature target, String position, int dtype){
		return true;
	}
	
	public boolean startFlavorText(Creature creature, Creature target){
		return true;
	}
	
	public void update(Creature creature){
		this.duration--;
	}
	
	public void start(Creature creature){
		
	}
	
	public void end(Creature creature){
		
	}
	
	public void broadcastMessage(MESSAGE msg, Creature creature){
		
	}
}
