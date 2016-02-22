package sayden;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Item extends Thing{
	@Override
	public String name() { return (level >= 3 ? "+++" : (level == 2 ? "++" : (level == 1 ? "+" : ""))) + name; }
	
	private char glyph;
	public char glyph() { return glyph; }
	
	private float bloodModifyer;
	public float bloodModifyer() { return bloodModifyer; }
	public void modifyBloodModifyer(float amount) { this.bloodModifyer += amount; }
	
	private Color color;
	public Color color() { return appearance != null && appearance != name  && name.indexOf("pocion") == -1 ? Constants.UNIDENTIFIED_COLOR : color; }

	private int bonusMaxHp;
	public int bonusMaxHp() { return bonusMaxHp; }
	public void modifyBonusMaxHp(int amount) { bonusMaxHp += amount; }
	
	private ArrayList<DamageType> attackValues;
	public ArrayList<DamageType> attackValues() { return attackValues; }
	public void modifyAttackValue(DamageType type, int value) { 
		for(DamageType d : attackValues){
			if(d.id == type.id)
				d.modifyAmount(value);
		}
	}
	public int totalAttackValue(){
		int amount = 0;
		for(DamageType d : attackValues){
			amount += d.amount;
		}
		return amount;
	}
	public int attackValue(DamageType type) { 
		for(DamageType d : attackValues){
			if(d.id == type.id)
				return d.amount;
		}
		return 0;
	}
	public DamageType highestDamage() { 
		int highest = 0;
		DamageType highestDamage = null;
		
		for(DamageType d : attackValues){
			if(d.amount >= highest){
				highest = d.amount;
				highestDamage = d;
			}
		}
		
		return highestDamage;
	}

	private ArrayList<DamageType> defenseValues;
	public ArrayList<DamageType> defenseValues() { return defenseValues; };
	public void modifyDefenseValue(DamageType type, int value) { 
		for(DamageType d : defenseValues){
			if(d.id == type.id)
				d.modifyAmount(value);
		}
	}
	public int totalDefenseValue(){
		int amount = 0;
		for(DamageType d : defenseValues){
			amount += d.amount;
		}
		return amount;
	}
	public int defenseValue(DamageType type) { 
		for(DamageType d : defenseValues){
			if(d.id == type.id)
				return d.amount;
		}
		return 0;
	}
	
	private Effect quaffEffect;
	public Effect quaffEffect() { return quaffEffect; }
	public void setQuaffEffect(Effect effect) { this.quaffEffect = effect; }
	
	private List<Spell> writtenSpells;
	public List<Spell> writtenSpells() { return writtenSpells; }
	
	private Speed attackSpeed;
	public Speed attackSpeed() { return attackSpeed; }
	public void modifyAttackSpeed(Speed speed) { this.attackSpeed = speed; } 
	
	private Speed movementSpeed;
	public Speed movementSpeed() { return movementSpeed; }
	public void modifyMovementSpeed(Speed speed) { this.movementSpeed = speed; } 
	
	private boolean canBreake = false;
	public boolean canBreake() { return canBreake; }
	
	private int durability = 1;
	public int durability() { return durability; }
	public void modifyDurability(int amount) { 
		if(amount > 1) { 
			this.canBreake = true;
		}
		
		this.durability += amount; 
	}
	
	private int level = 0;
	public int level() { return level; }
	public boolean canWound() { return level >= 2; }
	
	private int countHit = 0;
	public int countHit() { 
		int to_return = 0;
		this.countHit++; 
		
		if(countHit >= 20){
			level++;
			countHit = 0;
			to_return = level;
		}
		
		return to_return;
	}
	
	private int spawnWeight = 10;
	public int spawnWeight() { return spawnWeight; }
	
	private int cantReach = 0;
	public int cantReach() { return cantReach; }
	public void modifyCantReach(int amount) { this.cantReach += amount; }
	
	private int reach = 0;
	public int reach() { return reach; }
	public void modifyReach(int amount) { this.reach += amount; }
	
	public Item augmentItem(int augment){
		//TODO: Implement
		return this;
	}
	
	public boolean equippable() { return
			getBooleanData(Constants.CHECK_ARMOR) ||
			getBooleanData(Constants.CHECK_SHIELD) ||
			getBooleanData(Constants.CHECK_WEAPON) ||
			getBooleanData(Constants.CHECK_HELMENT);
	}
	
	public Spell addWrittenSpell(String name, char gender, Effect effect, int duration, float chance, String flag, Effect negativeEffect, Speed castSpeed, boolean target){
		Spell spell = new Spell(name, gender, effect, duration, chance, flag, negativeEffect, castSpeed, target);
		writtenSpells.add(spell);
		return spell;
	}
	
	public Item(char glyph, char gender, Color color, String name, String appearance, int spawnWeight){
		super();
		this.glyph = glyph;
		this.gender = gender;
		this.color = color;
		this.name = name;
		this.appearance = appearance;
		this.writtenSpells = new ArrayList<Spell>();
		this.attackValues = new ArrayList<DamageType>();
		this.attackValues.addAll(DamageType.ALL_TYPES());
		this.defenseValues = new ArrayList<DamageType>();
		this.defenseValues.addAll(DamageType.ALL_TYPES());
		this.spawnWeight = spawnWeight;
		this.bloodModifyer = 0.5f;
		
		if(this.appearance != null && this.appearance != this.name)
			this.identify(false);
	}
	
	public Item(Item clone){
		super();
		this.glyph = clone.glyph();
		this.gender = clone.gender;
		this.color = clone.color();
		this.name = clone.name;
		this.appearance = clone.appearance;
		this.bonusMaxHp = clone.bonusMaxHp();
		this.quaffEffect = clone.quaffEffect();
		this.writtenSpells = clone.writtenSpells();
		this.attackValues = clone.attackValues();
		this.defenseValues = clone.defenseValues();
		this.bloodModifyer = clone.bloodModifyer();
		this.movementSpeed = clone.movementSpeed();
		this.attackSpeed = clone.attackSpeed();
		this.maxStacks = clone.maxStacks;
		this.stacks = 1;
		this.spawnWeight = clone.spawnWeight();
		setAllData(clone.getAllData());
		
		if(this.appearance != null && this.appearance != this.name)
			this.identify(false);
	}

	public String details() {
		String details = "";
		String useVerb = gender == 'M' ? "usado" : "usada";
		
		details += nameUnUna() + ".";
		details = details.substring(0, 1).toUpperCase()
				+ details.substring(1);
		
		if(canBreake() && durability() <= 20){
			details += " Esta a punto de romperse.";
		}else if(canBreake() && durability() <= 50){
			details += " No se ve muy resistente.";
		}else if(canBreake() && durability() > 50){
			details += " Puede romperse.";
		}
		
		if(getBooleanData(Constants.CHECK_TWO_HANDED)){
			details += " Requiere dos manos.";
		}
		
		if(isIdentified()){
			details += description == null ? "" : " " + description;
		}
		
		if(getBooleanData(Constants.CHECK_ARMOR)){
			details += " Puede ser "+useVerb+" como armadura.";
		}
		if(getBooleanData(Constants.CHECK_HELMENT)){
			details += " Puede ser "+useVerb+" como casco.";
		}
		if(getBooleanData(Constants.CHECK_SHIELD)){
			details += " Puede ser "+useVerb+" como escudo.";
		}
		if(getBooleanData(Constants.CHECK_WEAPON)){
			details += " Puede ser "+useVerb+" como arma" 
					+ (getBooleanData(Constants.CHECK_DUAL_WIELD) ? " en cualquier mano" : "") + ".";
		}
		if(stackable()){
			details += " Puede acumularse "+maxStacks+" veces.";
		}
		
		if(reach > 0){
			details += " Posee alcance de hasta " + reach + " casillas.";
		}
		
		return details.replace('ñ', (char)164).replace('Ñ', (char)165);
	}
}
