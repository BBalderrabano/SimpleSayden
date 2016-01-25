package sayden;

public class FieldOfView {
	private World world;
	private int depth;
	
	private boolean[][] visible;
	public boolean isVisible(int x, int y, int z){
		return z == depth && x >= 0 && y >= 0 && x < visible.length && y < visible[0].length && visible[x][y];
	}
	
	private Tile[][][] tiles;
	public Tile tile(int x, int y, int z){
		return tiles[x][y][z];
	}
	
	public FieldOfView(World world){
		this.world = world;
		this.visible = new boolean[100][100];
		this.tiles = new Tile[100][100][world.depth()];
		
		for (int z = 0; z < world.depth(); z++){
			for (int x = 0; x < world.width(z); x++){
				for (int y = 0; y < world.height(z); y++){
					tiles[x][y][z] = Tile.UNKNOWN;
				}
			}
		}
	}
	
	public void update(int wx, int wy, int wz, int r){
		depth = wz;
		visible = new boolean[world.width(wz)][world.height(wz)];
		
		for (int x = -r; x < r; x++){
			for (int y = -r; y < r; y++){
				if (x*x + y*y > r*r)
					continue;
				
				if (wx + x < 0 || wx + x >= world.width(wz) || wy + y < 0 || wy + y >= world.height(wz))
					continue;
				
				for (Point p : new Line(wx, wy, wx + x, wy + y)){
					Tile tile = world.tile(p.x, p.y, wz);
					visible[p.x][p.y] = true;
					tiles[p.x][p.y][wz] = tile; 
					
					if (!tile.isGround())
						break;
				}
			}
		}
	}
}
