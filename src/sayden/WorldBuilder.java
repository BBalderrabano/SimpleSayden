package sayden;

import java.util.ArrayList;

public class WorldBuilder {
	private int width;
	private int height;
	private Tile[][] tiles;
	private int[][] regions;
	private int nextRegion;

	public WorldBuilder(int width, int height) {
		this.width = width;
		this.height = height;
		this.tiles = new Tile[width][height];
		this.regions = new int[width][height];
		this.nextRegion = 1;
	}

	public World build() {
		World world = new World(tiles);		
		return world;
	}
	
	private WorldBuilder randomizeTiles() {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				tiles[x][y] = Math.random() < 0.5 ? Tile.FLOOR : Tile.WALL;
			}
		}
		return this;
	}

	private WorldBuilder smooth(int times) {
		Tile[][] tiles2 = new Tile[width][height];
		for (int time = 0; time < times; time++) {

			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
						int floors = 0;
						int rocks = 0;
	
						for (int ox = -1; ox < 2; ox++) {
							for (int oy = -1; oy < 2; oy++) {
								if (x + ox < 0 || x + ox >= width || y + oy < 0
										|| y + oy >= height)
									continue;
	
								if (tiles[x + ox][y + oy] == Tile.FLOOR)
									floors++;
								else
									rocks++;
							}
						}
						tiles2[x][y] = floors >= rocks ? Tile.FLOOR : Tile.WALL;
				}
			}
			tiles = tiles2;
		}
		return this;
	}
	
	private WorldBuilder createRegions(){
		regions = new int[width][height];
		
		for (int x = 0; x < width; x++){
			for (int y = 0; y < height; y++){
				if (tiles[x][y] != Tile.WALL && regions[x][y] == 0){
					int size = fillRegion(nextRegion++, x, y);
					
					if (size < 25)
						removeRegion(nextRegion - 1);
				}
			}
		}
		
		return this;
	}
	
	private void removeRegion(int region){
		for (int x = 0; x < width; x++){
			for (int y = 0; y < height; y++){
				if (regions[x][y] == region){
					regions[x][y] = 0;
					tiles[x][y] = Tile.WALL;
				}
			}
		}
	}
	
	private int fillRegion(int region, int x, int y) {
		int size = 1;
		ArrayList<Point> open = new ArrayList<Point>();
		open.add(new Point(x,y));
		regions[x][y] = region;
		
		while (!open.isEmpty()){
			Point p = open.remove(0);

			for (Point neighbor : p.neighbors8()){
				if (neighbor.x < 0 || neighbor.y < 0 || neighbor.x >= width || neighbor.y >= height)
					continue;
				
				if (regions[neighbor.x][neighbor.y] > 0
						|| tiles[neighbor.x][neighbor.y] == Tile.WALL)
					continue;

				size++;
				regions[neighbor.x][neighbor.y] = region;
				open.add(neighbor);
			}
		}
		return size;
	}
	
	private WorldBuilder addStairs() {
		int x = -1;
		int y = -1;
		
		do {
			x = (int)(Math.random() * width);
			y = (int)(Math.random() * height);
		}
		while (tiles[x][y] != Tile.FLOOR);
		
		tiles[x][y] = Tile.STAIRS_UP;
		return this;
	}

	public WorldBuilder makeCaves() {
		return randomizeTiles()
				.smooth(8)
				.createRegions()
				.addStairs();
	}
}