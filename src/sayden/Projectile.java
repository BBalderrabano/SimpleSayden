package sayden;

import java.util.List;

public class Projectile {
	public int x;
	public int y;
	public int z;
	
	private int step = 0;
	
	protected World world;
	protected Line path;
	protected Speed speed;
	
	private Item projectile;
	public Item projectile() { return projectile; }
	
	private Creature origin;
	
	private int actionPoints;
	public int actionPoints() { return actionPoints; }
	public void modifyActionPoints(int amount) { this.actionPoints += amount; }
	
	private boolean isDone = false;
	public boolean isDone() { return isDone || this.step >= path.getPoints().size(); }
	
	public Projectile(World world, Line line, Speed velocity, Item projectile, Creature creature){
		this.world = world;
		this.path = line;
		this.speed = velocity;
		this.projectile = projectile;
		this.actionPoints += speed.velocity();
		this.origin = creature;
		step++;
	}
	
	public void update(){
		Creature check = world.creature(x, y, z);
		
		if(check != null && check != origin){
			this.isDone = true;
			return;
		}else{
			while(actionPoints >= speed.velocity()){
				List<Point> points = path.getPoints();
				
				int mx = points.get(Math.min(step, points.size() - 1)).x - x;
				int my = points.get(Math.min(step, points.size() - 1)).y - y;
				
				moveBy(mx, my);
				step++;
			}
		}
	}
	
	public void moveBy(int mx, int my){
		Tile tile = world.tile(x+mx, y+my, z);		
		
		modifyActionPoints(-speed.velocity());
		
		if (tile.isGround()){
			this.x = mx+x;
			this.y = my+y;
		}else{
			end();
		}
	}
	
	public void end(){
		this.isDone = true;
		
		Creature target = world.creature(x, y, z);
		String name = projectile.nameUnUna();
		int totalDamage = 0;
		
		if(target != null){
			for(DamageType d : DamageType.ALL_TYPES()){
				totalDamage += Math.max(0, (projectile.attackValue(d) - target.defenseValue(d)));
			}
			
			target.doAction("recibe el impacto de %s por %s", projectile.nameUnUna(), totalDamage+"!");
	
			if(projectile.quaffEffect() != null){
				target.addEffect(projectile.quaffEffect());
			}else{
				world.addAtEmptySpace(projectile, x, y, z);
			}
			
			target.modifyHp(-totalDamage, "Impactado por " + name);
		}else{
			world.addAtEmptySpace(projectile, x, y, z);
		}
	}
}
