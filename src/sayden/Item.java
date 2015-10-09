package sayden;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Item extends Thing{

	private char glyph;
	public char glyph() { return glyph; }
	
	private Color color;
	public Color color() { return color; }

	public String name() { return name; }

	public String appearance() { return appearance; }
	
	private int foodValue;
	public int foodValue() { return foodValue; }
	public void modifyFoodValue(int amount) { foodValue += amount; }

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
	}
	
	public String details() {
		String details = "";
		
		if (attackValue != 0)
			details += "  attack:" + attackValue;

		if (thrownAttackValue != 1)
			details += "  thrown:" + thrownAttackValue;
		
		if (rangedAttackValue > 0)
			details += "  ranged:" + rangedAttackValue;
		
		if (defenseValue != 0)
			details += "  defense:" + defenseValue;

		if (foodValue != 0)
			details += "  food:" + foodValue;
		
		return details;
	}
}
