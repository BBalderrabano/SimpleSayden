package sayden;

public class Spell extends Thing{

	public String name() { return name; }

	private int manaCost;
	public int manaCost() { return manaCost; }

	private Effect effect;
	public Effect effect() { return effect; }
	
	public boolean requiresTarget(){ return true; }
	
	public Spell(String name, int manaCost, Effect effect){
		super();
		this.name = name;
		this.manaCost = manaCost;
		this.effect = effect;
	}
}
