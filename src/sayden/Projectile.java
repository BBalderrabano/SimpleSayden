package sayden;
import java.util.List;

public class Projectile {
	public int x;
	public int y;
	
	private int step = 0;
	
	protected World world;
	protected Point start;
	protected Line path;
	protected Speed speed;
	
	private Item projectile;
	public Item projectile() { return projectile; }
	
	private Creature origin;
	
	private int actionPoints;
	public int actionPoints() { return actionPoints; }
	public void modifyActionPoints(int amount) { this.actionPoints += amount; }
	
	private boolean interrupted = false;
	public boolean isInterrupted() { return interrupted; }
	
	private boolean isDone = false;
	public boolean isDone() { 
		return isDone || this.step >= path.getPoints().size()
				|| (speed == Speed.NORMAL && this.step >= 6)
				|| (speed == Speed.SLOW && this.step >= 4)
				|| ((speed == Speed.VERY_SLOW || speed == Speed.SUPER_SLOW) && this.step >= 2); 
	}
	
	public Projectile(World world, Line line, Speed velocity, Item projectile, Creature origin){
		this.world = world;
		this.path = line;
		this.speed = velocity;
		this.projectile = projectile;
		this.actionPoints += speed.velocity();
		this.origin = origin;
		this.start = origin.position();

		step++;
	}
	
	public void update(){
		while(actionPoints >= speed.velocity()){
			List<Point> points = path.getPoints();
			
			int mx = points.get(Math.min(step, points.size() - 1)).x - x;
			int my = points.get(Math.min(step, points.size() - 1)).y - y;
			Creature c = world.creature(x, y);
			
			if(c != null && c != origin && !isDone)
				end();
			
			moveBy(mx, my);
			step++;
		}
	}
	
	public void moveBy(int mx, int my){
		Tile tile = world.tile(x+mx, y+my);		
		
		modifyActionPoints(-speed.velocity());
		
		if (tile.isGround()){
			this.x = mx+x;
			this.y = my+y;
			
			Creature check = world.creature(x, y);
			
			if(check != null && check != origin && !isDone){
				end();
			}
		}else{
			end();
		}
	}
	
	public void end(){
		this.isDone = true;
		
		Creature target = world.creature(x, y);
		
		if(target != null){
			pickTarget(target);
		}else{
			if(projectile.getBooleanData(Constants.CHECK_PROJECTILE_AUTOTARGET)){
				for(Point p : new Point(x, y).neighbors8()){
					if(world.creature(p.x, p.y) == null || world.creature(p.x, p.y) == origin)
						continue;
					
					target = world.creature(p.x, p.y);
					pickTarget(target);
					break;
				}
			}else{
				world.addAtEmptySpace(projectile, x, y);
			}
		}
		interrupted = true;
		
		if(projectile.getBooleanData(Constants.CHECK_PROJECTILE_DISSAPEAR)){
			world.remove(projectile);
		}
	}
	
	private void pickTarget(Creature target){
		int totalDamage = 0;
		
		for(int d : DamageType.ALL_TYPES()){
			totalDamage += Math.max(0, (projectile.attackValue(d) - target.defenseValue(d)));
		}
		
		totalDamage = Math.max(1, totalDamage);	//Always deals at least 1 damage (ignores shields?)
		
		if(target.isPlayer()){
			target.notifyArround("%s te impacta!", Constants.capitalize(projectile.nameUnUna()));
		}else{
			target.notifyArround("%s impacta %s!", 
					Constants.capitalize(projectile.nameUnUna()),
					target.nameAlALa());
		}

		if(projectile.quaffEffect() != null && projectile.quaffEffect().quaffable){
			target.addEffect(projectile.quaffEffect());
			target.learnName(projectile);
			origin.learnName(projectile);
			projectile.quaffEffect().start(x, y);
		}else{
			world.addAtEmptySpace(projectile, x, y);
		}
		
		target.setData(Constants.CHECKPOINT, path.getPoints().get(0));
		
		if(target.isAlive() && target.queSpell() != null){
			target.stopCasting();
			target.modifyActionPoints(-target.getActionPoints());
		}
	}
}
