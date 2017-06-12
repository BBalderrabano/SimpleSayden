package sayden;

public class Action {
	private Creature performer;
	public Creature getPerformer() { return performer; }
	
	private int x;
	public int _x() { return x; }
	
	private int y;
	public int _y() { return y; }

	private int radius;
	public int radius() { return radius; }
	
	private int timer;
	public int timer() { return timer; }
	
	public boolean isDone() { return timer < 1; }
	
	private Creature target;
	public Creature target() { return target; }
	
	private boolean isStun = false;
	
	public Action(Creature performer, int x, int y, int time){
		this.performer = performer;
		this.x = x;
		this.y = y;
		this.timer = time;
	}
	
	public Action(Creature performer, int time){
		this.performer = performer;
		this.timer = time;
		this.isStun = true;
	}
	
	public Action(Creature performer, Creature target, int time){
		this.performer = performer;
		this.target = target;
		this.timer = time;
	}
	
	public void tick(){
		timer -= 1;
		
		if(timer < 1 && !isStun){
			Creature attacked = target == null ? performer.creature(x, y) : target;
			
			if(attacked != null){
				performer.ai().onAttack(attacked);
			}else{
				performer.x = this.x;
				performer.y = this.y;
				performer.doAction("|falla05| el ataque!");
			}
		}
	}
}
