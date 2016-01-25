package sayden;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Item extends Thing{

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
	
	public Spell addWrittenSpell(String name, int manaCost, Effect effect){
		Spell spell = new Spell(name, manaCost, effect);
		writtenSpells.add(spell);
		return spell;
	}
	
	public boolean equippable() { return
			getBooleanData(Constants.CHECK_ARMOR) ||
			getBooleanData(Constants.CHECK_SHIELD) ||
			getBooleanData(Constants.CHECK_WEAPON) ||
			getBooleanData(Constants.CHECK_HELMENT);}
	
	public Item(char glyph, char gender, Color color, String name, String appearance){
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
		this.bloodModifyer = 0.5f;
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
		this.maxStacks = clone.maxStacks;
		this.stacks = 1;
		setAllData(clone.getAllData());
	}

	public String details() {
		String details = "";
		
		details += nameUnUna() + ".";
		details = details.substring(0, 1).toUpperCase()
				+ details.substring(1);
		
		if(isIdentified()){
			details += description == null ? "" : " " + description;
		}
		
		if(getBooleanData(Constants.CHECK_ARMOR)){
			details += " Puede ser usado de armadura.";
		}
		if(getBooleanData(Constants.CHECK_HELMENT)){
			details += " Puede ser usado como casco.";
		}
		if(getBooleanData(Constants.CHECK_SHIELD)){
			details += " Puede ser usado como escudo.";
		}
		if(getBooleanData(Constants.CHECK_WEAPON)){
			details += " Puede ser usado como arma.";
		}
		if(stackable()){
			details += " Puede acumularse "+maxStacks+" veces.";
		}
		
		return details.replace('ñ', (char)164).replace('Ñ', (char)165);
	}
}
