package sayden;

public class Spell extends Thing{

	public String name() { return name; }
	
	private Effect effect;
	public Effect effect() { return effect; }
	
	private Effect negativeEffect;
	public Effect negativeEffect() { return negativeEffect; }
	
	private Speed castSpeed;
	public Speed castSpeed() { return castSpeed; }
	
	private int duration;
	
	private float chance;
	
	final private String flag;
	
	private boolean requiresTarget = true;
	public boolean requiresTarget(){ return requiresTarget; }
	
	public Spell(String name, Effect effect, int duration, float chance, String flag, Effect negative, Speed castSpeed, boolean target){
		super();
		this.name = name;
		this.effect = effect;
		this.negativeEffect = negative;
		this.duration = duration;
		this.chance = chance;
		this.flag = flag;
		this.requiresTarget = target;
		this.castSpeed = castSpeed;
	}

	public void onCast(Creature caster, Creature other) {
		double temp = Math.random();
		
		caster.modifyActionPoints(-castSpeed().velocity());
		
		if(negativeEffect != null && caster.getBooleanData(flag) && temp < (chance * 0.01)){
			
			caster.addEffect(negativeEffect);

		}else if(!caster.getBooleanData(flag)){

			caster.addEffect(new Effect(duration){
				public void start(Creature creature){
					creature.setData(flag, true);
				}
				public void update(Creature creature){
					super.update(creature);
					
					if(!creature.getBooleanData(flag))
						creature.setData(flag, true);
				}
				public void end(Creature creature){
					creature.unsetData(flag);
				}
			});
			
		}
	}
}
