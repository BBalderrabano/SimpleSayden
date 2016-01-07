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
	public Color color() { return color; }

	public String name() { return name; }

	public String appearance() { return appearance; }

	private int bonusMaxHp;
	public int bonusMaxHp() { return bonusMaxHp; }
	public void modifyBonusMaxHp(int amount) { bonusMaxHp += amount; }
	
	private int attackValue;
	public int attackValue() { return attackValue; }
	public void modifyAttackValue(int amount) { attackValue += amount; }

	private int defenseValue;
	public int defenseValue() { return defenseValue; }
	public void modifyDefenseValue(int amount) { defenseValue += amount; }

	private int thrownAttackValue;
	public int thrownAttackValue() { return thrownAttackValue; }
	public void modifyThrownAttackValue(int amount) { thrownAttackValue += amount; }

	private int rangedAttackValue;
	public int rangedAttackValue() { return rangedAttackValue; }
	public void modifyRangedAttackValue(int amount) { rangedAttackValue += amount; }
	
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
	
	public void addWrittenSpell(String name, int manaCost, Effect effect){
		writtenSpells.add(new Spell(name, manaCost, effect));
	}
	
	public Item(char glyph, char gender, Color color, String name, String appearance){
		super();
		this.glyph = glyph;
		this.gender = gender;
		this.color = color;
		this.name = name;
		this.appearance = appearance == null ? name : appearance;
		this.thrownAttackValue = 1;
		this.writtenSpells = new ArrayList<Spell>();
		this.bloodModifyer = 0.5f;
	}
	
	public String details() {
		String details = "";
		
		details += nameUnUna() + ".";
		details = details.substring(0, 1).toUpperCase()
				+ details.substring(1);

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
		
		if (attackValue != 0)
			details += "  ataque:" + attackValue;
		
		if (rangedAttackValue > 0)
			details += "  rango:" + rangedAttackValue;
		
		if (defenseValue != 0)
			details += "  defensa:" + defenseValue;
		
		return details;
	}
}
