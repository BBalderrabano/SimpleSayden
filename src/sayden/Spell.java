package sayden;

public class Spell extends Thing{	
	public static final Effect HEADACHE(){
		return new Effect("jaqueca", 200){
			public void start(Creature creature){
				creature.setData(Constants.WOUND_HEADACHE, true);
			}
			public void update(Creature creature){
				super.update(creature);
			}
			public void end(Creature creature){
				creature.unsetData(Constants.WOUND_HEADACHE);
			}
		};
	}
	
	private Effect effect;
	public Effect effect() { return effect; }
	
	private Effect negativeEffect;
	public Effect negativeEffect() { return negativeEffect; }
	
	private Speed castSpeed;
	public Speed castSpeed() { return castSpeed; }
	
	private int cooldown;
	
	private float chance;
	
	final private String flag;
	
	private boolean requiresTarget = true;
	public boolean requiresTarget(){ return requiresTarget; }
	
	public Spell(String name, Effect effect, int cooldown, float chance, String flag, Effect negative, Speed castSpeed, boolean target){
		super();
		this.name = name;
		this.effect = effect;
		this.negativeEffect = negative;
		this.cooldown = cooldown;
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

			caster.addEffect(new Effect(null, cooldown){
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
