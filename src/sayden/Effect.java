package sayden;

public class Effect {
	protected int duration;
	protected boolean quaffable = true;	//This variable is used to check if the effect activates when thrown to a target
	
	public boolean isDone() { return duration < 1; }

	public Effect(int duration){
		this.duration = duration;
	}
	
	public Effect(int duration, boolean quaffable){
		this.duration = duration;
		this.quaffable = quaffable;
	}
	
	public Effect(Effect other){
		this.duration = other.duration; 
		this.quaffable = other.quaffable;
	}
	
	public void update(Creature creature){
		duration--;
	}
	
	public void start(Creature creature){
		
	}
	
	public void start(int x, int y, int z){

	}
	
	public void end(Creature creature){
		
	}
}
