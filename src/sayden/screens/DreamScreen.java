package sayden.screens;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import asciiPanel.AsciiPanel;
import sayden.ApplicationMain;
import sayden.Constants;
import sayden.Creature;
import sayden.DamageType;
import sayden.FieldOfView;
import sayden.MapLoader;
import sayden.Message;
import sayden.StuffFactory;
import sayden.Tile;
import sayden.World;
import sayden.WorldBuilder;
import sayden.autoupdater.SaveFile;
import sayden.autoupdater.SerializationUtil;

public class DreamScreen implements Screen {
	private World world;
	private Creature player;
	
	private int screenWidth;
	private int screenHeight;
	
	private List<String> messages;
	private FieldOfView fov;
	private Screen subscreen;
	public Screen subscreen(){ return player != null && player.subscreen != null ? player.subscreen : subscreen; }
	
	private int elapsedTurns = 0;
	
	private boolean displayTip = false;
	
	public DreamScreen(){
		MapLoader ml = new MapLoader();
		
		screenWidth = Constants.WORLD_WIDTH;
		screenHeight = Constants.WORLD_HEIGHT;
		messages = new ArrayList<String>();
		
		createWorld();
		
		StuffFactory factory = new StuffFactory(world);
		world.overrideFloor(ml.preBuild("DBatalla", factory));
		
		WorldBuilder wb = new WorldBuilder(world.tiles());
		world = wb.makeForest(world);
		
		fov = new FieldOfView(world);
		
		player = factory.newDreamer(messages, fov, 40, 12);
		createCreatures(factory);
		world.updateFloor();


		if(Constants.PRODUCTION){
			SaveFile saveFile = null;
			
			try {
				saveFile = (SaveFile) SerializationUtil.deserialize(Constants.SAVE_FILE_FULL_NAME);
	        } catch (ClassNotFoundException e) {
	        } catch (IOException e) {
			}
			
			if(!saveFile.isFirstDream()){
				player.notify("-- usa [wasd] para moverte --");
				saveFile.setFirstDream(true);
				
				try {
					SerializationUtil.serialize(saveFile, Constants.SAVE_FILE_FULL_NAME);
				} catch (IOException e) {
					System.out.println("[ERROR] Hubo un fallo al guardar datos");
				}
			}else{
				player.notify("-- presiona [ESC] para despertar --");
			}
		}
	}

	private void createWorld(){
		Tile[][] tiles = new Tile[screenWidth][screenHeight];
		world = new World(tiles);
		for(int x = 0; x < world.width(); x++){
			for(int y = 0; y < world.height(); y++){
				tiles[x][y] = Tile.FLOOR;
			}
		}
	}
	
	private void createCreatures(StuffFactory factory){
		for(int i = 0; i < (Math.random() * (20 - 10) + 10); i++){
			factory.newDreamFighter(player, true);
		}
		for(int i = 0; i < (Math.random() * (20 - 10) + 10); i++){
			factory.newDreamFighter(player, false);
		}
	}
	
	public int getScrollX() { return Math.max(0, Math.min(player.x - screenWidth / 2, world.width() - screenWidth)); }
	
	public int getScrollY() { return Math.max(0, Math.min(player.y - screenHeight / 2, world.height() - screenHeight)); }
	
	@Override
	public void displayOutput(AsciiPanel terminal) {
		int left = getScrollX();
		int top = getScrollY(); 
		
		displayTiles(terminal, left, top);
		displayMessages(terminal, messages);
		
		String stats = String.format(" %3d/%3d hp %s", player.vigor(), player.maxVigor(), player.statusEffects());
		terminal.write(stats, 1, 23);
		
		if (subscreen() != null)
			subscreen().displayOutput(terminal);
	}
	
	private List<String> checkDuplicateMessages(List<String> messages){
		return messages;
	}

	private void colorize(AsciiPanel terminal, String text, int top){
		boolean finished = false;
		boolean foundStart = false;
		int start = 0; 	// '(' position in string
		int end = 0; 	// ')' position in string
		int lastEnd = -9999;
		
		String finalText = "";
		String requiredString = "";
		String formattedString = "";
		String colorCheck = "";

		ArrayList<Message> messages = new ArrayList<Message>();
		
		if(text.indexOf("|") == -1){
			terminal.writeCenter(text, top);
			return;
		}
		
		while(!finished){
			for(int i = 0; i < text.length(); i++) { 
			    if(text.charAt(i) == '|' && !foundStart){ 		// Looking for '(' position in string
			    	start = i;
			    	foundStart = true;
			    }else if(text.charAt(i) == '|' && foundStart){ 	// Looking for ')' position in  string
			    	end = i + 1;
			    	foundStart = false;
					break;
			    }
			}
			
			if(end == lastEnd || end == 0){
				finished = true;
				continue;
			}else{
				requiredString = "";
				formattedString = "";
				colorCheck = "";
			}
			
	        requiredString = text.substring(start, end);
	        formattedString = requiredString.substring(1, requiredString.length()-1); 	
	        
	        colorCheck = formattedString.substring(formattedString.length() - 2, formattedString.length());
	        Color color = Color.white;
	        
	        if(colorCheck.equals("01")){
	        	color = AsciiPanel.red;
	        }else if(colorCheck.equals("02")){
	        	color = AsciiPanel.cyan;
	        }else if(colorCheck.equals("03")){
	        	color = Color.orange;
	        }else if(colorCheck.equals("04")){
	        	color = Color.green;
	        }else if(colorCheck.equals("05")){
	        	color = AsciiPanel.brightBlue;
	        }else if(colorCheck.equals("06")){
	        	color = AsciiPanel.brightMagenta;
	        }
	        
	        finalText = text.replace(requiredString, formattedString.substring(0, formattedString.length() - 2) );
	        formattedString = formattedString.substring(0, formattedString.length() - 2);
	        
	        int x = (terminal.getWidthInCharacters() - finalText.length()) / 2;
	        
	        text = finalText;
	        
	        if(messages.size() > 0){
	        	for(Message m : messages){
		        	m.modifyXOffset(2);
	        	}
	        }
	        
        	messages.add(new Message(formattedString, x + start, top, color));
	        
	        lastEnd = end;
		}
		
        int x = (terminal.getWidthInCharacters() - finalText.length()) / 2;
    	terminal.write(finalText, x, top);
    	
    	for(int i = 0; i < messages.size(); i++){
    		Message message = messages.get(i);
	        terminal.write(message.getMessage(), message.getXOffset(), message.getYOffset(), message.getColor());
    	}
	}
	
	private void displayMessages(AsciiPanel terminal, List<String> messages) {
		messages = checkDuplicateMessages(messages);
		
		for (int i = 0; i < messages.size(); i++){
			if(messages.get(i).length() >= screenWidth){
				ArrayList<String> toAdd = splitPhraseByLimit(messages.get(i), screenWidth - 1);
				messages.remove(i);
				messages.addAll(i, toAdd);
			}
		}

		int top = 0;
		
		if(player.y >= world.height() - (messages.size() + 2)){
			top = 1;
		}else{
			top = screenHeight - messages.size() - 1;
		}
		
		for (int i = 0; i < messages.size(); i++){
			String nTildeFix = messages.get(i).replace('ñ', (char)164).replace('Ñ', (char)165);
			colorize(terminal, nTildeFix, top + i);
		}
		if (subscreen() == null)
			messages.clear();
	}
	
	public static ArrayList<String> splitPhraseByLimit(String text, int limit){
		String[] words = text.split(" ");
		ArrayList<String> array = new ArrayList<String>();
		int i = 0;
		while (words.length > i) {
		    String line = "";
		    while ( words.length > i && line.length() + words[i].length() < limit ) {
		        line += " "+words[i];
		        i++;
		    }
		    array.add(line);
		}
		return array;
	}

	private void displayTiles(AsciiPanel terminal, int left, int top) {
		fov.update(player.x, player.y, player.visionRadius());
		
		for (int x = 0; x < screenWidth; x++){
			for (int y = 0; y < screenHeight; y++){
				int wx = x + left;
				int wy = y + top;
				
				Color dreamColor = Color.BLACK;
				
				double ratio = Math.min(1, elapsedTurns * 0.1f);
				
				Color glyph_color = blend(world.color(x, y), dreamColor, ratio);
				Color background_color = blend(world.backgroundColor(x, y), dreamColor, ratio);
				
				if (player.canSee(wx, wy)){
					if(player.x == wx && player.y == wy){
						glyph_color = player.color();
					}
					terminal.write(world.glyph(wx, wy), x, y, glyph_color, background_color);
				}else{
					terminal.write(fov.tile(wx, wy).glyph(), x, y, Color.darkGray, Color.BLACK);
				}
			}
		}
	}
	
	private static Color blend(Color color1, Color color2, double ratio) {
        float r = (float) ratio;
        float ir = (float) 1.0 - r;

        float rgb1[] = new float[3];
        float rgb2[] = new float[3];

        color1.getColorComponents(rgb1);
        color2.getColorComponents(rgb2);

        float red = rgb1[0] * r + rgb2[0] * ir;
        float green = rgb1[1] * r + rgb2[1] * ir;
        float blue = rgb1[2] * r + rgb2[2] * ir;

        if (red < 0) {
            red = 0;
        } else if (red > 1) {
            red = 1;
        }
        if (green < 0) {
            green = 0;
        } else if (green > 1) {
            green = 1;
        }
        if (blue < 0) {
            blue = 0;
        } else if (blue > 1) {
            blue = 1;
        }

        return new Color(red, green, blue);
    }
	
	@Override
	public Screen respondToUserInput(KeyEvent key, ApplicationMain main) {
		if(displayTip){
			if(key.getKeyCode() == KeyEvent.VK_ENTER){
				elapsedTurns++;
				displayTip = false;
			}
			return this;
		}
		
		if(elapsedTurns == 3 && !displayTip){
			player.notify("-- muevete a la casilla de un enemigo (gris) para atacarlo --");
		}else if(elapsedTurns == 1 && !displayTip){
			player.notify("Sueñas con una batalla");
		}
		
		if (subscreen() != null) {
			if(player.subscreen != null){
				player.subscreen = subscreen().respondToUserInput(key, main);
				return this;
			}else{
				subscreen = subscreen().respondToUserInput(key, main);
			}
		} else {
			switch (key.getKeyCode()){
				case KeyEvent.VK_P:
				case KeyEvent.VK_ESCAPE:
						return new PlayScreen();
				case KeyEvent.VK_TAB:
				case KeyEvent.VK_I:
						subscreen = new MenuScreen(player, player.x - getScrollX(), 
						player.y - getScrollY()); break;
				case KeyEvent.VK_SPACE:	player.moveBy(0, 0); elapsedTurns++; break;
				case KeyEvent.VK_LEFT:
				case KeyEvent.VK_A: 	player.moveBy(-1, 0); elapsedTurns++; break;
				case KeyEvent.VK_RIGHT:
				case KeyEvent.VK_D: 	player.moveBy( 1, 0); elapsedTurns++; break;
				case KeyEvent.VK_UP:
				case KeyEvent.VK_W: 	player.moveBy( 0,-1); elapsedTurns++; break;
				case KeyEvent.VK_DOWN:
				case KeyEvent.VK_S: 	player.moveBy( 0, 1); elapsedTurns++; break;
				case KeyEvent.VK_R: 
						subscreen = new LookScreen(player, "Observando", 
						player.x - getScrollX(), 
						player.y - getScrollY()); break;
				case KeyEvent.VK_F: 
					if (player.weapon() == null || player.weapon().attackValue(DamageType.RANGED) <= 0)
						player.notify("No tienes un arma de rango equipada.");
					else
						subscreen = new FireWeaponScreen(player,
						player.x - getScrollX(), 
						player.y - getScrollY()); break;
				case KeyEvent.VK_G:
				case KeyEvent.VK_E:
						player.pickup(); break;
				case KeyEvent.VK_Q:
						subscreen = new ReadSpellScreen(player, player.x - getScrollX(), 
						player.y - getScrollY(), null);
						break;
			}
			
			switch (key.getKeyChar()){
				case '?': subscreen = new HelpScreen(); break;
			}
		}

		if (subscreen() == null){
			world.updateFloor();
		}
		
		if (!player.isAlive())
			return new PlayScreen();
		
		return this;
	}

	@Override
	public Screen respondToMouseInput(MouseEvent mouse) {
		return this;
	}
}
