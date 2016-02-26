package sayden;

public class Wound extends Effect{

	public Wound(String statusName, int duration){
		super(statusName, duration);
	}
	
	public Wound(Wound other){
		super(other);
	}
	
	public void hit(Creature creature, boolean success){}
	
	public void move(Creature creature, int mx, int my){}
}
