package sayden.screens;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import asciiPanel.AsciiPanel;
import sayden.Constants;
import sayden.Creature;
import sayden.DamageType;
import sayden.FieldOfView;
import sayden.Line;
import sayden.MapLoader;
import sayden.Message;
import sayden.Point;
import sayden.StuffFactory;
import sayden.Tile;
import sayden.World;
import sayden.WorldBuilder;

public class PlayScreen implements Screen {
	private World world;
	private Creature player;
	
	private int screenWidth;
	private int screenHeight;
	
	private List<String> messages;
	private FieldOfView fov;
	private Screen subscreen;
	public Screen subscreen(){ return player != null && player.subscreen != null ? player.subscreen : subscreen; }
	
	private boolean pauseTooltip = true;
	
	private int depth = 0;
	
	public PlayScreen(){
		MapLoader ml = new MapLoader();
		
		screenWidth = Constants.WORLD_WIDTH;
		screenHeight = Constants.WORLD_HEIGHT;
		messages = new ArrayList<String>();
		
		createWorld();
		
		StuffFactory factory = new StuffFactory(world);

		world.overrideFloor(ml.preBuild("Pueblo", factory));
		
		fov = new FieldOfView(world);
		
		player = factory.newPlayer(messages, fov);

		populateTown(factory);
	}
	
	private void descendStairs(){
		if(world.tile(player.x, player.y) == Tile.STAIRS_DOWN || world.tile(player.x, player.y) == Tile.STAIRS_UP){
			depth++;
			
			createWorld();
			StuffFactory factory = new StuffFactory(world);
			fov = new FieldOfView(world);
			
			factory.movePlayer(player, messages, fov, world);
			
			world.createRegions();
			
			if(!world.tile(player.x, player.y).isGround()){
				Point targetPoint = new Point(player.x, player.y);
				
				world.setTile(player.x, player.y, Tile.FLOOR);
				
				for(Point p : player.position().neighbors((int) (Math.random() * (10 - 3) + 3))){
					if(!world.tile(p.x, p.y).isGround() || p == targetPoint)
						continue;
					
					targetPoint = p;
					break;
				}
				
				for(Point p : new Line(targetPoint.x, targetPoint.y, player.x, player.y)){
					world.setTile(p.x, p.y, Tile.FLOOR);
				}
			}
			
			createCreatures(factory);
			createItems(factory);
			
			world.add(player);
		}
	}
	
	private void populateTown(StuffFactory factory){
		for (int i = 0; i < Math.random() * 10; i++){
			factory.newRabbit(player);
		}
		player.notify("Despiertas en un pueblo abandonado.");
	}

	private int randomNumber(int min, int max){
		return (int) (Math.random() * (max - min) + min);
	}
	
	private void createCreatures(StuffFactory factory){	
		switch(depth){
			case 1:
				for (int i = 0; i < 15; i++){
					factory.newRockBug(player);
				}
				
				for (int i = 0; i < 2; i++){
					factory.newHidden(player);
				}
				
				for (int i = 0; i < 2; i++){
					factory.newDog(player, null);
				}
				
				for(int i = 0; i < 2; i++){
					factory.newCaveBrute(player);
				}
				
				for(int i = 0; i < 8; i++){
					factory.newMarauder(player);
				}
			break;
			case 2:
				for (int i = 0; i < 15; i++){
					factory.newRockBug(player);
				}
				
				for (int i = 0; i < 2; i++){
					factory.newDog(player, null);
				}
				
				for (int i = 0; i < 4; i++){
					factory.newHidden(player);
				}
				
				for(int i = 0; i < 3; i++){
					factory.newCaveBrute(player);
				}
				
				for(int i = 0; i < 3; i++){
					factory.newCaveLost(player);
				}
				
				for(int i = 0; i < randomNumber(8, 12); i++){
					factory.newMarauder(player);
				}
			break;
			case 3:
				for (int i = 0; i < 15; i++){
					factory.newRockBug(player);
				}
				
				for (int i = 0; i < 2; i++){
					factory.newDog(player, null);
				}
				
				for (int i = 0; i < 6; i++){
					factory.newHidden(player);
				}
				
				for(int i = 0; i < randomNumber(8, 14); i++){
					factory.newMarauder(player);
				}
				
				for(int i = 0; i < randomNumber(3, 6); i++){
					factory.newCaveLost(player);
				}
				
				for(int i = 0; i < randomNumber(1, 3); i++){
					factory.newHugeMarauder(player);
				}
			break;
			case 4:
				for (int i = 0; i < 15; i++){
					factory.newRockBug(player);
				}
				
				for (int i = 0; i < 6; i++){
					factory.newHidden(player);
				}
				
				for(int i = 0; i < randomNumber(12, 18); i++){
					factory.newMarauder(player);
				}
				
				for(int i = 0; i < randomNumber(5, 8); i++){
					factory.newCaveLost(player);
				}
				
				for(int i = 0; i < randomNumber(3, 6); i++){
					factory.newHugeMarauder(player);
				}
			break;
			default:
				for (int i = 0; i < 15; i++){
					factory.newRockBug(player);
				}
				
				for (int i = 0; i < 6; i++){
					factory.newHidden(player);
				}
				
				for(int i = 0; i < randomNumber(12, 18); i++){
					factory.newMarauder(player);
				}
				
				for(int i = 0; i < randomNumber(5, 8); i++){
					factory.newCaveLost(player);
				}
				
				for(int i = 0; i < randomNumber(3, 8); i++){
					factory.newHugeMarauder(player);
				}
			break;
		}
	}

	private void createItems(StuffFactory factory) {
		factory.randomPotion(true);
		factory.randomPotion(true);
		factory.randomPotion(true);
		
		for (int i = 0; i < world.width() * world.height() / 50; i++){
			factory.newRock(true);
		}

		factory.newFruit(true);
		factory.newEdibleWeapon(true);
		factory.newBread(true);
		
		factory.randomArmor(null, true, true, true, false, false);
		factory.randomWeapon(null, true, true, false, false, false);
		factory.randomWeapon(null, true, true, false, true, false);
		
		factory.randomPotion(true);
		factory.randomSpellBook(true);
	}
	
	private void createWorld(){
		world = new WorldBuilder(90, 32)
					.makeCaves()
					.build();
	}
	
	public int getScrollX() { return Math.max(0, Math.min(player.x - screenWidth / 2, world.width() - screenWidth)); }
	
	public int getScrollY() { return Math.max(0, Math.min(player.y - screenHeight / 2, world.height() - screenHeight)); }
	
	@Override
	public void displayOutput(AsciiPanel terminal) {
		int left = getScrollX();
		int top = getScrollY(); 
		
		displayTiles(terminal, left, top);
		displayMessages(terminal, messages);
		
		terminal.write(" [", 0, 23);
		terminal.setCursorX(1);
		terminal.write('[', AsciiPanel.brightBlack);
		
		for(int i = 0; i < player.vigor(); i++){
			float p = Math.min(1f, ((player.vigor() * 100.0f) / player.maxVigor()) / 100f);
			
			Color color1 = AsciiPanel.brightYellow;
			Color color2 = AsciiPanel.red;
			
			int red = (int) (color2.getRed() * p + color1.getRed() * (1 - p));
            int green = (int) (color2.getGreen() * p + color1.getGreen() * (1 - p));
            int blue = (int) (color2.getBlue() * p + color1.getBlue() * (1 - p));

            terminal.write('|', new Color(red, green, blue));
		}
		
		terminal.setCursorX(terminal.getCursorX() + Math.max(0, player.maxVigor() - player.vigor()));
		terminal.write(']', AsciiPanel.brightBlack);
		
		terminal.write(" " + player.statusEffects());

		if(pauseTooltip){
			String tooltip = "-- esc o p ayuda --";
			terminal.write(tooltip, terminal.getWidthInCharacters() - tooltip.length() - 2, 1);
			pauseTooltip = false;
		}
		
		if (subscreen() != null)
			subscreen().displayOutput(terminal);
	}
	
	private List<String> checkDuplicateMessages(List<String> messages){
		//TODO: Implement
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
	        }else if(colorCheck.equals("07")){
	        	color = Constants.WOUND_COLOR;
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
			colorize(terminal, messages.get(i).replaceAll("!!", (char)19+""), top + i);
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

				if (player.canSee(wx, wy))
					terminal.write(world.glyph(wx, wy), x, y, world.color(wx, wy), world.backgroundColor(wx, wy));
				else
					terminal.write(fov.tile(wx, wy).glyph(), x, y, Color.darkGray, Color.BLACK);
			}
		}
	}
	
	@Override
	public Screen respondToUserInput(KeyEvent key) {
		if (!player.isAlive())
			return new LoseScreen(player);
		
		if (subscreen() != null) {
			if(player.subscreen != null){
				player.subscreen = subscreen().respondToUserInput(key);
				return this;
			}else{
				subscreen = subscreen().respondToUserInput(key);
			}
		} else {
			switch (key.getKeyCode()){
				case KeyEvent.VK_P:
				case KeyEvent.VK_ESCAPE:
						subscreen = new HelpScreen(); break;
				case KeyEvent.VK_TAB:
				case KeyEvent.VK_I:
						subscreen = new MenuScreen(player, player.x - getScrollX(), 
						player.y - getScrollY()); break;
				case KeyEvent.VK_SPACE:	player.moveBy(0, 0); break;
				case KeyEvent.VK_LEFT:
				case KeyEvent.VK_A: 	player.moveBy(-1, 0); break;
				case KeyEvent.VK_RIGHT:
				case KeyEvent.VK_D: 	player.moveBy( 1, 0); break;
				case KeyEvent.VK_UP:
				case KeyEvent.VK_W: 	player.moveBy( 0,-1); break;
				case KeyEvent.VK_DOWN:
				case KeyEvent.VK_S: 	player.moveBy( 0, 1); break;
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
				case KeyEvent.VK_ENTER:
						enterKeyHandler();
						break;
			}
			
			switch (key.getKeyChar()){
				case '<': 
						descendStairs(); break;
				case '>': 	
						descendStairs(); break;
				case '?': subscreen = new HelpScreen(); break;
			}
		}

		if (subscreen() == null){
			world.updateFloor();
		}
		
		return this;
	}
	
	private void enterKeyHandler(){
		descendStairs();
		
		for(Point p : player.position().neighbors8()){
			player.open(p.x, p.y);
		}
		
		if(player.item(player.x, player.y) != null)
			player.pickup();
	}
}
