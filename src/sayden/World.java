package sayden;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import asciiPanel.AsciiPanel;

public class World {
	private Tile[][] tiles;
	public Tile[][] tiles() { return tiles; }
	
	private Item[][] items;
	private float[][] blood;
	private float[][] fire;
	
	private boolean[][] visited;
	
	private int width;
	public int width() { return width; }
	
	private int height;
	public int height() { return height; }
	
	private List<Creature> creatures;
	private List<Projectile> projectiles;
	
	public float fire(int x, int y) { 
		if (x < 0 || x >= width || y < 0 || y >= height)
			return 0;
		
		return fire[x][y];
	}
	
	public World(Tile[][] tiles){
		this.tiles = tiles;

		this.width = tiles.length;
		this.height = tiles[0].length;
		
		this.creatures = new ArrayList<Creature>();
		this.projectiles = new ArrayList<Projectile>();
		
		this.items = new Item[tiles.length][tiles[0].length];
		this.blood = new float[tiles.length][tiles[0].length];
		this.fire = new float[tiles.length][tiles[0].length];
		this.visited = new boolean[tiles.length][tiles[0].length];
	}
	
	public void overrideFloor(World newWorld){
		int maxHeight = Math.max(height, newWorld.height());
		int maxWidth = Math.max(width, newWorld.width());
		
		width = newWorld.width();
		height = newWorld.height();
		
		for(int y = 0; y < maxHeight; y++){
			for(int x = 0; x < maxWidth; x++){
				tiles[x][y] = newWorld.tile(x, y);
				if(tiles[x][y] == Tile.STAIRS_DOWN){
					tiles[x][y] = Tile.STAIRS_UP;
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
	
	public Projectile projectile(int x, int y){
		for (Projectile p : projectiles){
			if (p.x == x && p.y == y)
				return p;
		}
		return null;
	}
	
	public Creature creature(int x, int y){
		for (Creature c : creatures){
			if (c.x == x && c.y == y)
				return c;
		}
		return null;
	}
	
	public boolean isOutBounds(int x, int y){
		return (x < 0 || x >= width || y < 0 || y >= height);
	}
	
	public Tile tile(int x, int y){
		if (x < 0 || x >= width || y < 0 || y >= height)
			return Tile.BOUNDS;
		else
			return tiles[x][y];
	}
	
	public void setTile(int x, int y, Tile newTile){
		if (x < 0 || x >= width || y < 0 || y >= height)
			return;
		else
			tiles[x][y] = newTile;
	}
	
	public void propagate(int x, int y, float amount, String fluidType){
		Point tile = new Point(x,y);
		
		if(x > width || x < 0 || y > height || y < 0)
			return;
		
		//Blood is additive, when a tile is about to be filled with more blood than it
		//can sustain its spread to adjacent tiles
		if(fluidType == Constants.BLOOD_FLUID){
			if(blood[x][y] + amount < 100f){
				blood[x][y] += amount;
			}else{
				for(Point t : tile.neighbors8()){
					if(isOutBounds(t.x, t.y)){
						continue;
					}
					if(blood[t.x][t.y] <= 100f){
						blood[t.x][t.y] += amount;
						if(blood[t.x][t.y] > 100f){
							propagate(x,y, blood[t.x][t.y] - 100f, fluidType);
							blood[x][y] = 100f;
						}
						return;
					}
				}
			}
		}else if(fluidType == Constants.FIRE_TERRAIN){
			amount -= tiles[x][y].fireResistance();
			amount = Math.max(amount, 0);			
			
			fire[x][y] += amount;
			
			if(fire[x][y] > 100f){
				tiles[x][y] = Tile.BURNT_FLOOR;
				for(Point t : tile.neighbors4()){
					if(isOutBounds(t.x, t.y)){
						continue;
					}
					if(fire[t.x][t.y] > 0)
						continue;
					
					fire[t.x][t.y] += (fire[x][y] - 100);
					//fire[x][y][z] -= (fire[x][y][z] - 100);
					return;
				}
			}
		}
	}
	
	public char glyph(int x, int y){
		Creature creature = creature(x, y);
		Projectile projectile = projectile(x, y);
		
		if(projectile != null)
			return projectile.projectile().glyph();
		
		if(fire[x][y] > 0)
			return (char)30;
		
		if (creature != null && !creature.getBooleanData(Constants.FLAG_INVISIBLE))
			return creature.glyph();
		
		if (item(x,y) != null)
			return item(x,y).glyph();
		
		return tile(x, y).glyph();
	}
	
	public Color color(int x, int y){
		Creature creature = creature(x, y);	
		Projectile projectile = projectile(x, y);
		
		if(projectile != null)
			return projectile.projectile().color();
		
		if (creature != null && !creature.getBooleanData(Constants.FLAG_INVISIBLE))
			return creature.color();
		
		if(fire[x][y] > 0)
			return Tile.getFire().color();
		
		if (item(x,y) != null)
			return item(x,y).color();
		
		return tile(x, y).color();
	}
	
	public Color backgroundColor(int x, int y){
		float bloodAmount = blood[x][y];
		
		Projectile projectile = projectile(x, y);
		
		if(projectile != null)
			return AsciiPanel.cyan;
			
		if (x < 0 || x >= width || y < 0 || y >= height)
			return Tile.BOUNDS.backgroundColor();
		
		if(bloodAmount > 0f){
			return new Color(Math.min((bloodAmount / 100f), 1f),0,0);
		}
		
		return tile(x, y).backgroundColor();
	}

	public void dig(int x, int y) {
		if (tile(x, y).isDiggable())
			tiles[x][y] = Tile.FLOOR;
	}
	
	public void open(int x, int y) {
		if (tile(x, y).isDoor())
			tiles[x][y] = tile(x, y) == Tile.DOOR_OPEN ? Tile.DOOR_CLOSE : Tile.DOOR_OPEN;
	}
	
	public void addAtEmptyLocation(Creature creature){
		int x;
		int y;
		
		do {
			x = (int)(Math.random() * width);
			y = (int)(Math.random() * height);
		} 
		while (!tile(x,y).isGround() || creature(x,y) != null);
		
		creature.x = x;
		creature.y = y;
		
		creatures.add(creature);
	}
	
	private void updateProjectiles(){
		List<Projectile> done = new ArrayList<Projectile>();
		
		for (Projectile projectile : projectiles){
			projectile.update();
			if (projectile.isDone()) {
				if(!projectile.isInterrupted()){ projectile.end(); }
				done.add(projectile);
			}
		}
		
		projectiles.removeAll(done);
	}
	
	public void updateFloor(){
		List<Creature> toUpdate = new ArrayList<Creature>(creatures);
				
		for (Creature creature : toUpdate){
			creature.update();
		}
		
		updateProjectiles();
		
		for (int x = 0; x < width; x++){
			for (int y = 0; y < height; y++){
				if(blood[x][y] > Constants.MIN_FLUID_AMOUNT)
					blood[x][y] -= 1f;
			}	
		}
		for (int x = 0; x < width; x++){
			for (int y = 0; y < height; y++){
				if(fire[x][y] > 0)
					fire[x][y] -= Constants.FIRE_DEGRADATION;
				if(fire[x][y] > 80){
					propagate(x, y, 30, Constants.FIRE_TERRAIN);
					fire[x][y] -= Constants.FIRE_DEGRADATION;
				}
				if(fire[x][y] > 100 && tiles[x][y].fireResistance() < 100)
					tiles[x][y] = Tile.BURNT_FLOOR;
			}	
		}	
	}

	public void remove(Creature other) {
		creatures.remove(other);
	}
	
	public void remove(Item item) {
		for (int x = 0; x < width; x++){
			for (int y = 0; y < height; y++){
				if (items[x][y] == item) {
					items[x][y] = null;
					return;
				}
			}	
		}	
	}
	
	public Item item(int x, int y){
		if(x < 0 || y < 0 || x >= width || y >= height)
			return null;
		return items[x][y];
	}
	
	public void addAtEmptyLocation(Item item) {
		addAtEmptyLocation(item, true);
	}
	
	public void addAtEmptyLocation(Item item, boolean spawn) {
		int x;
		int y;
		
		if(!spawn){
			return;
		}
		
		do {
			x = (int)(Math.random() * width);
			y = (int)(Math.random() * height);
		} 
		while (!tile(x,y).isGround() || item(x,y) != null);
		
		items[x][y] = item;
	}

	public void remove(int x, int y) {
		items[x][y] = null;
	}
	public boolean addAtEmptySpace(Creature creature, int x, int y){
		return  addAtEmptySpace(creature, x, y, true);
	}
	
	public boolean addAtEmptySpace(Creature creature, int x, int y, boolean spawn){
		if (creature == null)
			return true;
		
		if(!spawn)
			return true;
		
		List<Point> points = new ArrayList<Point>();
		List<Point> checked = new ArrayList<Point>();
		
		points.add(new Point(x, y));
		
		while (!points.isEmpty()){
			Point p = points.remove(0);
			checked.add(p);
			
			if (!tile(p.x, p.y).isGround())
				continue;
			
			if (creature(p.x, p.y) == null){
				creature.x = p.x;
				creature.y = p.y;
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
	
	public boolean addAtEmptySpace(Item item, int x, int y){
		return addAtEmptySpace(item, x, y, true);
	}
	
	public boolean addAtEmptySpace(Item item, int x, int y, boolean spawn){
		if (item == null)
			return true;
		if(!spawn)
			return true;
		
		List<Point> points = new ArrayList<Point>();
		List<Point> checked = new ArrayList<Point>();
		
		points.add(new Point(x, y));
		
		while (!points.isEmpty()){
			Point p = points.remove(0);
			checked.add(p);
			
			if (!tile(p.x, p.y).isGround())
				continue;
				
			if (items[p.x][p.y] == null){
				items[p.x][p.y] = item;
				Creature c = this.creature(p.x, p.y);
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
	
	public void pathFinderVisited(int x, int y) {
		visited[x][y] = true;
	}
}
