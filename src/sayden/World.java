package sayden;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import asciiPanel.AsciiPanel;

public class World {
	private Tile[][][] tiles;
	private Item[][][] items;
	private float[][][] blood;
	private float[][][] fire;
	
	private boolean[][][] visited;
	
	private int[] width;
	public int width(int z) { return width[z]; }
	
	private int[] height;
	public int height(int z) { return height[z]; }

	private int depth;
	public int depth() { return depth; }
	
	private List<Creature> creatures;
	private List<Projectile> projectiles;
	
	public float fire(int x, int y, int z) { return fire[x][y][z]; }
	
	public World(Tile[][][] tiles){
		this.tiles = tiles;
		this.depth = tiles[0][0].length;
		this.width = new int[depth];
		this.height = new int[depth];

		Arrays.fill(width, tiles.length);
		Arrays.fill(height, tiles[0].length);
		
		this.creatures = new ArrayList<Creature>();
		this.projectiles = new ArrayList<Projectile>();
		
		this.items = new Item[tiles.length][tiles[0].length][depth];
		this.blood = new float[tiles.length][tiles[0].length][depth];
		this.fire = new float[tiles.length][tiles[0].length][depth];
		this.visited = new boolean[tiles.length][tiles[0].length][depth];
	}
	
	public void overrideFloor(int z, World newWorld){
		int maxHeight = Math.max(height[z], newWorld.height(z));
		int maxWidth = Math.max(width[z], newWorld.width(z));
		
		width[z] = newWorld.width(z);
		height[z] = newWorld.height(z);
		
		for(int y = 0; y < maxHeight; y++){
			for(int x = 0; x < maxWidth; x++){
				tiles[x][y][z] = newWorld.tile(x, y, z);
				if(tiles[x][y][z] == Tile.STAIRS_DOWN){
					tiles[x][y][z+1] = Tile.STAIRS_UP;
				}
			}
		}
	}

	public void modifyActionPoints(int amount){
		amount = Math.abs(amount);

		for(Creature c : creatures){
			if(c.isPlayer())
				continue;
			c.modifyActionPoints(amount);
		}
		for(Projectile p : projectiles){
			p.modifyActionPoints(amount);
		}
	}
	
	public Projectile projectile(int x, int y, int z){
		for (Projectile p : projectiles){
			if (p.x == x && p.y == y && p.z == z)
				return p;
		}
		return null;
	}
	
	public Creature creature(int x, int y, int z){
		for (Creature c : creatures){
			if (c.x == x && c.y == y && c.z == z)
				return c;
		}
		return null;
	}
	
	public boolean isOutBounds(int x, int y, int z){
		return (x < 0 || x >= width[z] || y < 0 || y >= height[z] || z < 0 || z >= depth);
	}
	
	public Tile tile(int x, int y, int z){
		if (x < 0 || x >= width[z] || y < 0 || y >= height[z] || z < 0 || z >= depth)
			return Tile.BOUNDS;
		else
			return tiles[x][y][z];
	}
	
	public void propagate(int x, int y, int z, float amount, String fluidType){
		Point tile = new Point(x,y,z);
		
		if(x > width(z) || x < 0 || y > height(z) || y < 0)
			return;
		
		//Blood is additive, when a tile is about to be filled with more blood than it
		//can sustain its spread to adjacent tiles
		if(fluidType == Constants.BLOOD_FLUID){
			if(blood[x][y][z] + amount < 100f){
				blood[x][y][z] += amount;
			}else{
				for(Point t : tile.neighbors8()){
					if(isOutBounds(t.x, t.y, t.z)){
						continue;
					}
					if(blood[t.x][t.y][t.z] <= 100f){
						blood[t.x][t.y][t.z] += amount;
						if(blood[t.x][t.y][t.z] > 100f){
							propagate(x,y,z, blood[t.x][t.y][t.z] - 100f, fluidType);
							blood[x][y][z] = 100f;
						}
						return;
					}
				}
			}
		}else if(fluidType == Constants.FIRE_TERRAIN){
			amount -= tiles[x][y][z].fireResistance();
			amount = Math.max(amount, 0);			
			
			fire[x][y][z] += amount;
			
			if(fire[x][y][z] > 100f){
				tiles[x][y][z] = Tile.BURNT_FLOOR;
				for(Point t : tile.neighbors4()){
					if(isOutBounds(t.x, t.y, t.z)){
						continue;
					}
					if(fire[t.x][t.y][t.z] > 0)
						continue;
					
					fire[t.x][t.y][t.z] += (fire[x][y][z] - 100);
					//fire[x][y][z] -= (fire[x][y][z] - 100);
					return;
				}
			}
		}
	}
	
	public char glyph(int x, int y, int z){
		Creature creature = creature(x, y, z);
		Projectile projectile = projectile(x, y, z);
		
		if(projectile != null)
			return projectile.projectile().glyph();
		
		if(fire[x][y][z] > 0)
			return (char)30;
		
		if (creature != null)
			return creature.glyph();
		
		if (item(x,y,z) != null)
			return item(x,y,z).glyph();
		
		return tile(x, y, z).glyph();
	}
	
	public Color color(int x, int y, int z){
		Creature creature = creature(x, y, z);	
		Projectile projectile = projectile(x, y, z);
		
		if(projectile != null)
			return projectile.projectile().color();
		
		if (creature != null)
			return creature.color();
		
		if(fire[x][y][z] > 0)
			return Tile.getFire().color();
		
		if (item(x,y,z) != null)
			return item(x,y,z).color();
		
		return tile(x, y, z).color();
	}
	
	public Color backgroundColor(int x, int y, int z){
		float bloodAmount = blood[x][y][z];
		
		Projectile projectile = projectile(x, y, z);
		
		if(projectile != null)
			return AsciiPanel.cyan;
			
		if (x < 0 || x >= width[z] || y < 0 || y >= height[z] || z < 0 || z >= depth)
			return Tile.BOUNDS.backgroundColor();
		
		if(bloodAmount > 0f){
			return new Color(Math.min((bloodAmount / 100f), 1f),0,0);
		}
		
		return tile(x, y, z).backgroundColor();
	}

	public void dig(int x, int y, int z) {
		if (tile(x, y, z).isDiggable())
			tiles[x][y][z] = Tile.FLOOR;
	}
	
	public void open(int x, int y, int z) {
		if (tile(x, y, z).isDoor())
			tiles[x][y][z] = tile(x, y, z) == Tile.DOOR_OPEN ? Tile.DOOR_CLOSE : Tile.DOOR_OPEN;
	}
	
	public void addAtEmptyLocation(Creature creature, int z){
		int x;
		int y;
		
		do {
			x = (int)(Math.random() * width[z]);
			y = (int)(Math.random() * height[z]);
		} 
		while (!tile(x,y,z).isGround() || creature(x,y,z) != null);
		
		creature.x = x;
		creature.y = y;
		creature.z = z;
		creatures.add(creature);
	}
	
	private void updateProjectiles(){
		List<Projectile> done = new ArrayList<Projectile>();
		
		for (Projectile projectile : projectiles){
			projectile.update();
			if (projectile.isDone()) {
				projectile.end();
				done.add(projectile);
			}
		}
		
		projectiles.removeAll(done);
	}
	
	public void updateFloor(int z){
		List<Creature> toUpdate = new ArrayList<Creature>(creatures);
				
		for (Creature creature : toUpdate){
			if(creature.z != z)
				continue;
			creature.update();
		}
		
		updateProjectiles();
		
		for (int x = 0; x < width[z]; x++){
			for (int y = 0; y < height[z]; y++){
				if(blood[x][y][z] > Constants.MIN_FLUID_AMOUNT)
					blood[x][y][z] -= 1f;
			}	
		}
		for (int x = 0; x < width[z]; x++){
			for (int y = 0; y < height[z]; y++){
				if(fire[x][y][z] > 0)
					fire[x][y][z] -= Constants.FIRE_DEGRADATION;
				if(fire[x][y][z] > 80){
					propagate(x, y, z, 30, Constants.FIRE_TERRAIN);
					fire[x][y][z] -= Constants.FIRE_DEGRADATION;
				}
				if(fire[x][y][z] > 100 && tiles[x][y][z].fireResistance() < 100)
					tiles[x][y][z] = Tile.BURNT_FLOOR;
			}	
		}	
	}

	public void remove(Creature other) {
		creatures.remove(other);
	}
	
	public void remove(Item item) {
		for (int z = 0; z < depth; z++){
			for (int x = 0; x < width[z]; x++){
				for (int y = 0; y < height[z]; y++){
					if (items[x][y][z] == item) {
						items[x][y][z] = null;
						return;
					}
				}	
			}	
		}
	}
	
	public Item item(int x, int y, int z){
		if(x < 0 || y < 0 || x >= width[z] || y >= height[z])
			return null;
		return items[x][y][z];
	}
	
	public void addAtEmptyLocation(Item item, int depth) {
		int x;
		int y;
		
		if(depth < 0){
			return;
		}
		
		do {
			x = (int)(Math.random() * width[depth]);
			y = (int)(Math.random() * height[depth]);
		} 
		while (!tile(x,y,depth).isGround() || item(x,y,depth) != null);
		
		items[x][y][depth] = item;
	}

	public void remove(int x, int y, int z) {
		items[x][y][z] = null;
	}

	public boolean addAtEmptySpace(Creature creature, int x, int y, int z){
		if (creature == null)
			return true;
		if(depth < 0)
			return true;
		
		List<Point> points = new ArrayList<Point>();
		List<Point> checked = new ArrayList<Point>();
		
		points.add(new Point(x, y, z));
		
		while (!points.isEmpty()){
			Point p = points.remove(0);
			checked.add(p);
			
			if (!tile(p.x, p.y, p.z).isGround())
				continue;
			
			if (creature(p.x, p.y, p.z) == null){
				creature.x = p.x;
				creature.y = p.y;
				creature.z = p.z;
				creatures.add(creature);
				return true;
			} else {
				List<Point> neighbors = p.neighbors8();
				neighbors.removeAll(checked);
				points.addAll(neighbors);
			}
		}
		return false;
	}
	
	public boolean addAtEmptySpace(Item item, int x, int y, int z){
		if (item == null)
			return true;
		if(z < 0)
			return true;
		
		List<Point> points = new ArrayList<Point>();
		List<Point> checked = new ArrayList<Point>();
		
		points.add(new Point(x, y, z));
		
		while (!points.isEmpty()){
			Point p = points.remove(0);
			checked.add(p);
			
			if (!tile(p.x, p.y, p.z).isGround())
				continue;
				
			if (items[p.x][p.y][p.z] == null){
				items[p.x][p.y][p.z] = item;
				Creature c = this.creature(p.x, p.y, p.z);
				if (c != null)
					c.notify("%s cae a tus pies.", item.nameUnUna().substring(0, 1).toUpperCase() + item.nameUnUna().substring(1));
				return true;
			} else {
				List<Point> neighbors = p.neighbors8();
				neighbors.removeAll(checked);
				points.addAll(neighbors);
			}
		}
		return false;
	}

	public void add(Creature pet) {
		creatures.add(pet);
	}
	
	public void add(List<Creature> pets) {
		creatures.addAll(pets);
	}
	
	public void add(Projectile missile) {
		projectiles.add(missile);
	}
	
	public float getCost(Creature mover, int sx, int sy, int tx, int ty) {
		//We can change the cost of moving to different tiles
		return 1;
	}
	
	public void pathFinderVisited(int x, int y, int z) {
		visited[x][y][z] = true;
	}
}
