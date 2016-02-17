package sayden;

public class Effect {
	
	protected int duration;
	protected boolean quaffable = true;	//This variable is used to check if the effect activates when thrown to a target
	
	public boolean isDone() { return duration < 1; }

	public Effect(String statusName, int duration){
		this.duration = duration;
		this.statusName = statusName;
	}
	
	public Effect(String statusName, int duration, boolean quaffable){
		this.duration = duration;
		this.quaffable = quaffable;
		this.statusName = statusName;
	}
	
	public Effect(Effect other){
		this.duration = other.duration; 
		this.quaffable = other.quaffable;
		this.statusName = other.statusName;
	}
	
	public void update(Creature creature){
		duration--;
	}
	
	public void start(Creature creature){
		
	}
	
	public void start(int x, int y){

	}
	
	public void end(Creature creature){
		
	}
	
	private String statusName;
	
	public String statusName(){
		return statusName;
	}
}
