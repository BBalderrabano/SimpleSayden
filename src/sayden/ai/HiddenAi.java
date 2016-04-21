package sayden.ai;

import java.util.List;

import sayden.Constants;
import sayden.Creature;
import sayden.Effect;
import sayden.Item;
import sayden.Path;
import sayden.Point;
import sayden.StuffFactory;
import sayden.Tile;

public class HiddenAi extends CreatureAi {
	int transformedTiles = 0;
	
	Creature player;
	StuffFactory factory;
	Tile floorTile = Tile.FLOOR_MOVED;
	
	public void onDecease(Item corpse){
		corpse.unsetData(Constants.CHECK_CORPSE);
		corpse.setQuaffEffect(new Effect("alimentado", 7, false){
			public void start(Creature creature){
				creature.notify("La carne del acechador es deliciosa!");
			}
			public void update(Creature creature){
				super.update(creature);
			}
		});
	}
	
	public HiddenAi(Creature creature, Creature player) {
		super(creature);
		
		this.player = player;
		
		this.setWeakSpot(Constants.LEG_POS);
		creature.setData(Constants.RACE, "comepiedras");
		creature.setData(Constants.FLAG_INVISIBLE, true);
		creature.world().setTile(creature.x, creature.y, floorTile);
	}

	public void onUpdate(){
		super.onUpdate();
		
		if(creature.position().distance(player.position()) <= 2){
			creature.unsetData(Constants.FLAG_INVISIBLE);
		}else{
			creature.setData(Constants.FLAG_INVISIBLE, true);
		}
		
		if (creature.canSee(player.x, player.y)){
			hunt(player);
		}else{
			if(transformedTiles < 8)
				for(Point p : creature.position().neighbors4()){
					if(creature.tile(p.x, p.y) == Tile.FLOOR){
						creature.world().setTile(p.x, p.y, floorTile);
						transformedTiles++;
						break;
					}
				}
		}
		
		wander();
		
		return;
	}
	
	@Override
	public void hunt(Creature target) {
		List<Point> points = new Path(creature, target.x, target.y).points();
		
		if(points == null || points.isEmpty())
			return;
		
		int mx = points.get(0).x - creature.x;
		int my = points.get(0).y - creature.y;
		int x_distance = Math.abs(creature.x - target.x);
		int y_distance = Math.abs(creature.y - target.y);
		
		Point no_diagonal = eliminateDiagonal(mx, my, x_distance, y_distance);
		
		if(!canMove(no_diagonal.x, no_diagonal.y)){
			creature.moveBy(0, 0);
		}else{
			creature.moveBy(no_diagonal.x, no_diagonal.y);
		}
	}
	
	@Override
	protected boolean canMove(int mx, int my){
		if(creature.creature(creature.x + mx, creature.y + my) == null &&
				creature.tile(creature.x + mx, creature.y + my) != floorTile)
			return false;
		else
			return super.canMove(mx, my);
	}
	
}
