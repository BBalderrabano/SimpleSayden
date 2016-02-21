package sayden.ai;

import sayden.Constants;
import sayden.Creature;
import sayden.DamageType;
import sayden.Effect;
import sayden.Item;
import sayden.Point;
import sayden.Speed;
import sayden.World;

public class BigMarauderAi extends CreatureAi {

	private Creature player;
	private World world;
	
	private String armDamage = "ArmDamage";
	private String armBroken = "ArmBroken";
	
	boolean playerHidden() { return player != null &&
			player.armor() != null && player.armor().getBooleanData(Constants.CHECK_MARAUDER_DISGUISE) &&
			player.helment() != null && player.helment().getBooleanData(Constants.CHECK_MARAUDER_DISGUISE) &&
			!creature.getBooleanData(Constants.FLAG_SEEN_PLAYER); }
	
	public BigMarauderAi(Creature creature, Creature player) {
		super(creature);
		this.player = player;
		this.setWeakSpot(Constants.HEAD_POS);
		this.world = player.world();
		
		creature.setData(Constants.RACE, "merodeador");
	}
	
	public void onDecease(Item corpse){
		corpse.setQuaffEffect(new Effect("envenenado", 6, false){
			public void start(Creature creature){
				creature.notify("El cadaver del merodeador esta empapado de |veneno04|!");
			}
			public void update(Creature creature){
				super.update(creature);
				if(creature.getStringData(Constants.RACE) != "merodeador"){
					creature.receiveDamage(2, DamageType.POISON, "El veneno del merodeador consume tus viceras", true);
				}
			}
		});
	}
	
	@Override
	public boolean onGetAttacked(int amount, String position, Creature attacker){
		if(attacker.isPlayer() && !creature.getBooleanData(Constants.FLAG_SEEN_PLAYER)){
			creature.setData(Constants.FLAG_SEEN_PLAYER, true);
		}
		
		if(position == Constants.HEAD_POS){
			
			if(attacker.isPlayer()){
				creature.doAction("esquiva tu ataque");
			}else{
				creature.doAction("esquiva el ataque " + attacker.nameDelDeLa());
			}
			
			attacker.notify("No alcanzas la cabeza "+creature.nameDelDeLa()+"!");
			return false;
		}
		
		if(position == Constants.LEG_POS){
			creature.setData("LegDamage", creature.getIntegerData("LegDamage") + amount);
			
			super.onGetAttacked(amount, position, attacker);
			
			if(!creature.getBooleanData("LegBroken") && creature.getIntegerData("LegDamage") > 20 && Math.random() < 0.3f){
				creature.doAction("siente las piernas fallandole...");
				creature.modifyMovementSpeed(Speed.SUPER_SLOW);
				creature.makeBleed(40);
				creature.setData("LegBroken", true);
			}
			
		}
		
		if(position == Constants.ARM_POS){
			creature.setData(armDamage, creature.getIntegerData(armDamage) + amount);
			
			super.onGetAttacked(amount, position, attacker);

			if(!creature.getBooleanData(armBroken) && creature.getIntegerData(armDamage) > 15 && Math.random() < 0.3f){
				creature.doAction("siente los brazos fallandole...");
				creature.makeBleed(40);
				creature.setData(armBroken, true);
				creature.setQueAttack(null);
			}
			
		}
		
		return true;
	}
	
	@Override
	public boolean onAttack(int x, int y, Creature other){
		if(creature.getBooleanData(armBroken) || creature.queAttack() == null){
			return super.onAttack(x, y, other);
		}
		return false;
	}
	
	public void onUpdate(){
		if(creature.queAttack() != null && !creature.getBooleanData(armBroken)
				&& creature.getActionPoints() >= creature.getAttackSpeed().velocity()){
			
			creature.doAction("comienza a golpear a su alrededor!");
			
			for(Point p : creature.position().neighbors8()){
				Creature c = world.creature(p.x, p.y);
				if(c == null || c == creature)
					continue;
				int damage = c.receiveDamage(creature.attackValue(DamageType.BLUNT) + 5, DamageType.BLUNT, "Molido a golpes por un merodeador gigante", true);
				c.doAction("recibe un golpe por "+ damage + " de daño");
			}
			creature.modifyActionPoints(-creature.getAttackSpeed().velocity());
			creature.setQueAttack(null);
			return;
		}else{
			super.onUpdate();
		}
		
		if(canSee(player.x, player.y)){
			if(!playerHidden()){
				creature.setData(Constants.FLAG_SEEN_PLAYER, true);
				
				if(canThrowAt(player, false) && getWeaponToThrow() != null && 
						creature.position().distance(player.position()) > 3 && Math.random() < 0.2){
					creature.throwItem(getWeaponToThrow(), player.x, player.y);
					return;
				}
				
				hunt(player);
				return;
			}
		}	
		
		visitCheckPoint();
		
		wander();
	}
}
