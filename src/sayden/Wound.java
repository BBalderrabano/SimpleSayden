package sayden;

import java.util.ArrayList;

public class Wound extends Thing implements Cloneable {
	
	public Object clone()throws CloneNotSupportedException{	return super.clone(); }
	
	/**50*/public static int LOWEST_CHANCE = 50;
	/**80*/public static int LOW_CHANCE = 80;
	/**100*/public static int NORMAL_CHANCE = 100;
	/**150*/public static int HIGH_CHANCE = 150;
	/**200*/public static int HIGHER_CHANCE = 200;
	/**300*/public static int HIGHEST_CHANCE = 300;
	
	/**20*/public static int LOWEST_DURATION = 20;
	/**40*/public static int LOW_DURATION = 40;
	/**60*/public static int NORMAL_DURATION = 60;
	/**80*/public static int HIGH_DURATION = 80;
	/**100*/public static int HIGHER_DURATION = 100;
	/**150*/public static int VHIGH_DURATION = 150;
	/**200*/public static int HIGHEST_DURATION = 200;
	
	public static enum MESSAGE{
		ON_HIT,
		ON_MOVE
	}
	
	public static ArrayList<Wound> DEFAULT_WOUNDS(){
		ArrayList<Wound> defWounds = new ArrayList<Wound>();
		
		defWounds.add(new Wound(1, "moreton", "Tus heridas finalmente te colapsan, mueres en agonia", 'M', LOWEST_DURATION, LOW_CHANCE){
			@Override
			public boolean canBePicked(Creature attacker, Creature target, String position, DamageType dtype) {
				return dtype.equals(DamageType.BLUNT);
			}
		});
		
		defWounds.add(new Wound(1, "corte superficial", "Tus heridas finalmente te colapsan, mueres en agonia", 'M', LOWEST_DURATION, LOW_CHANCE){
			@Override
			public boolean canBePicked(Creature attacker, Creature target, String position, DamageType dtype) {
				return dtype.equals(DamageType.SLICE) ||
						dtype.equals(DamageType.PIERCING) ||
						dtype.equals(DamageType.RANGED);
			}
			public void start(Creature creature){
				creature.makeBleed(15f);
			}
		});
		
		defWounds.add(new Wound(1, "herida arcana", "La magia finalmente te es demasia, mueres de un derrame cerebral", 'F', LOWEST_DURATION, LOW_CHANCE){
			@Override
			public boolean canBePicked(Creature attacker, Creature target, String position, DamageType dtype) {
				return dtype.equals(DamageType.MAGIC);
			}
		});
		
		return defWounds;
	}
	
	public static Wound CONCUSSION(final int stuntime){
		return CONCUSSION(stuntime, Wound.LOWEST_DURATION, Wound.NORMAL_CHANCE);
	}
	
	public static Wound CONCUSSION(final int stuntime, int duration, int chance){
		return new Wound(1, "contusion", "Mueres por trauma cerebral", 'F', duration, chance){
			public boolean canBePicked(Creature attacker, Creature target, String position, DamageType dtype) {
				return position == Constants.HEAD_POS;
			}
			public void start(Creature creature){
				if(creature.helment() != null){
					creature.doAction("resiste el |aturdimiento03| con " + (creature.isPlayer() ? "tu " + creature.helment().name() : "su " + creature.helment().name()));
					return;
				}
				
				creature.notifyArround("La contusion "+ (creature.isPlayer() ? "te |aturde03|" : "|aturde03| " + creature.nameAlALa()));
				creature.modifyStunTime(stuntime);
			}
		};
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
	
	public boolean canBePickedFRanged(Creature target, DamageType dtype, int amount){
		return true;
	}
	
	public boolean canBePicked(Creature attacker, Creature target, String position, DamageType dtype){
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
