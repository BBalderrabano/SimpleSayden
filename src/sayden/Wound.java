package sayden;

public class Wound extends Effect implements Cloneable{

	public Object clone()throws CloneNotSupportedException{	return super.clone(); }
	
	public static Wound BLUNT_HEAD_CONCUSSION = new Wound("contusion", 10, 100, Constants.HEAD_POS){
		public void start(Creature creature){
			creature.notifyArround("El golpe impacta en %s provocando una |contusion07|", 
					creature.isPlayer() ? "tu cabeza" : "la cabeza " + creature.nameDelDeLa());
			
			creature.modifyActionPoints(-Speed.NORMAL.velocity(), true);
		}
		public void move(Creature creature, int mx, int my){
			if(mx == 0 && my == 0)
				return;
			
			creature.notifyArround("La |contusion07| en %s produce un punzante dolor al andar!", 
					creature.isPlayer() ? "tu cabeza" : "la cabeza " + creature.nameDelDeLa() + " le");
			creature.receiveDamage(2, DamageType.PIERCING, "Severo trauma cerebral");
		}
	};
	
	private int weight = 0;
	public int weight() { return weight; }
	
	private String position;
	public String position() { return position; }
	
	public Wound(String statusName, int duration, int inflictChance, String position){
		super(statusName, duration);
		
		this.weight = inflictChance;
		this.position = position;
	}
	
	public void update(Creature creature){
		super.update(creature);
	}
	
	public void onHit(Creature creature, boolean success, String position){}
	
	public void onGetHit(Creature attacker, boolean success, String position){}
	
	public void move(Creature creature, int mx, int my){}
}
