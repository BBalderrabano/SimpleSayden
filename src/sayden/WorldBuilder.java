package sayden;

public class WorldBuilder {
	private int width;
	private int height;
	private Tile[][] tiles;

	public WorldBuilder(int width, int height) {
		this.width = width;
		this.height = height;
		this.tiles = new Tile[width][height];
	}
	
	public WorldBuilder(Tile[][] tiles) {
		this.width = tiles.length;
		this.height = tiles[0].length;
		this.tiles = tiles;
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
	
	private WorldBuilder addStairs() {
		int x = -1;
		int y = -1;
		
		do {
			x = (int)(Math.random() * width);
			y = (int)(Math.random() * height);
		}
		while (!tiles[x][y].isGround());
		
		tiles[x][y] = Tile.STAIRS_UP;
		return this;
	}

	private WorldBuilder addTrees(World world, int amount) {
		int placedTrees = 0;
		boolean skipTree = false;
		
		int x = -1;
		int y = -1;
		
		while(placedTrees < amount){
			x = (int)(Math.random() * world.width());
			y = (int)(Math.random() * world.height());
			
			if(!world.tile(x, y).isGround())
				continue;
			
			for(Point test : new Point(x, y).neighbors8()){
				if(world.tile(test.x, test.y) == Tile.TREE){
					skipTree = true;
					break;
				}
			}
			
			if(!skipTree){
				tiles[x][y] = Tile.TREE;
				placedTrees++;
			}else{
				skipTree = false;
			}
		}
		
		return this;
	}
	
	public World makeForest(World world) {
		addTrees(world, (int) Math.random() * (60 - 30) + 30);
		return world;
	}
	
	public WorldBuilder makeCaves() {
		return randomizeTiles()
				.smooth(8)
				.addStairs();
	}
}