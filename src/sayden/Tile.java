package sayden;

import java.awt.Color;
import asciiPanel.AsciiPanel;

public enum Tile {
	UNKNOWN( ' ', AsciiPanel.white, "(desconocido)", 100),
	FLOOR( (char)250, AsciiPanel.yellow, "Terreno de tierra y rocas.", 20),
	WALL( (char)177, AsciiPanel.yellow, "Muro de tierra y piedras.", 100),
	BOUNDS( 'x', AsciiPanel.brightBlack, "Mas alla de los limites del mapa.", 100),
	STAIRS_DOWN( '>', AsciiPanel.white, "Una escalera de piedra que va hacia abajo.", 100),
	STAIRS_UP( '<', AsciiPanel.white, "Una escalera de piedra que va hacia arriba.", 100),
	STONE_WALL( (char)177, Color.LIGHT_GRAY, "Un muro de piedra y grafito, gastado por el tiempo", 80),
	STONE_FLOOR( (char)176, Color.lightGray, "Piso de piedra y grafito", 30),
	GRASS( ',', AsciiPanel.green, "Cesped y tierra, sin mucha vida", -15),
	DOOR_CLOSE((char)10, AsciiPanel.yellow, "Una solida puerta cerrada", 40),
	DOOR_OPEN((char)9, AsciiPanel.yellow, "Una solida puerta abierta", 40),
	WOOD_WALL((char)177, new Color(90,55,0), "Un muro de madera podrida", 20),
	WOOD_FLOOR('=', new Color(90,55,0), "Planchas de madera podridas", 0),
	CARPET('+', AsciiPanel.brightRed, "Una alfombra de terciopelo depictando la ascencion del esperitu", 10),
	FIRE_1((char)30, AsciiPanel.red, "Una columna de fuego ardiente", 100),
	FIRE_2((char)30, Color.orange, "Una columna de fuego ardiente", 100),
	FIRE_3((char)30, AsciiPanel.brightYellow, "Una columna de fuego ardiente", 100),
	BURNT_FLOOR((char)175, AsciiPanel.brightBlack, "Cenizas y escombros de un fuego", 100);
	
	public static Tile getById(int id){
		switch(id){
			case 1: return UNKNOWN;
			case 2: return FLOOR;
			case 3: return WALL;
			case 4: return BOUNDS;
			case 5: return STAIRS_DOWN;
			case 6: return STAIRS_UP;
			case 7: return STONE_WALL;
			case 8: return STONE_FLOOR;
			case 9: return GRASS;
			case 10: return DOOR_CLOSE;		//A
			case 11: return DOOR_OPEN;		//B
			case 12: return	WOOD_WALL;		//C
			case 13: return WOOD_FLOOR;
			case 14: return CARPET;
			default: return UNKNOWN;
		}
	}
	
	private char glyph;
	public char glyph() { return glyph; }
	
	private Color color;
	public Color color() { return color; }
	
	private Color backgroundColor;
	public Color backgroundColor() { return backgroundColor; }

	private String description;
	public String details(){ return description; }
	
	private int fireResistance;
	public int fireResistance() { return fireResistance; }
	
	Tile(char glyph, Color color, String description, int resistance){
		this.glyph = glyph;
		this.color = color;
		this.description = description;
		this.fireResistance = resistance;
		this.backgroundColor = Color.BLACK;
	}

	public boolean isGround() {
		return this != WALL && this != BOUNDS && this != STONE_WALL && this != DOOR_CLOSE
				&& this != WOOD_WALL;
	}

	public boolean isDoor(){
		return this == DOOR_CLOSE || this == DOOR_OPEN;
	}
	
	public boolean isDiggable() {
		return this == Tile.WALL;
	}
	
	public static Tile getFire(){
		return Math.random() > .5 ? Tile.FIRE_1 : Math.random() > .5 ? Tile.FIRE_2 : Tile.FIRE_3;
	}
}
