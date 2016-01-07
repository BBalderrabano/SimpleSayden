package sayden.screens;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import asciiPanel.AsciiPanel;
import sayden.Creature;
import sayden.FieldOfView;
import sayden.Item;
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
	
	public PlayScreen(){
		screenWidth = 80;
		screenHeight = 23;
		messages = new ArrayList<String>();
		createWorld();
		fov = new FieldOfView(world);
		
		StuffFactory factory = new StuffFactory(world);
		createCreatures(factory);
		createItems(factory);
	}

	private void createCreatures(StuffFactory factory){
		player = factory.newPlayer(messages, fov);
			
		for(int i = 0; i < 1; i++){
			factory.newCaveBrute(player.z, player);
		}
		
		for (int z = 0; z < world.depth(); z++){
			for (int i = 0; i < 15; i++){
				factory.newRockBug(z, player);
			}
		}
	}

	private void createItems(StuffFactory factory) {
		for (int z = 0; z < world.depth(); z++){
			for (int i = 0; i < world.width() * world.height() / 50; i++){
				factory.newRock(z);
			}

			factory.newFruit(z);
			factory.newEdibleWeapon(z);
			factory.newBread(z);
			factory.randomArmor(z);
			factory.randomWeapon(z);
			factory.randomWeapon(z);
			
			for (int i = 0; i < z + 1; i++){
				factory.randomPotion(z);
				factory.randomSpellBook(z);
			}
		}
		factory.newVictoryItem(world.depth() - 1);
	}
	
	private void createWorld(){
		world = new WorldBuilder(90, 32, 5)
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
		
		String stats = String.format(" %3d/%3d hp", player.hp(), player.totalMaxHp());
		terminal.write(stats, 1, 23);
		
		if (subscreen != null)
			subscreen.displayOutput(terminal);
	}
	
	private List<String> checkDuplicateMessages(List<String> messages){
		//TODO: implement for real
		return messages;
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
		
		int top = screenHeight - messages.size();
		
		for (int i = 0; i < messages.size(); i++){
			String nTildeFix = messages.get(i).replace('ñ', (char)164).replace('Ñ', (char)165);
			terminal.writeCenter(nTildeFix, top + i);
		}
		if (subscreen == null)
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
		fov.update(player.x, player.y, player.z, player.visionRadius());
		
		for (int x = 0; x < screenWidth; x++){
			for (int y = 0; y < screenHeight; y++){
				int wx = x + left;
				int wy = y + top;

				if (player.canSee(wx, wy, player.z))
					terminal.write(world.glyph(wx, wy, player.z), x, y, world.color(wx, wy, player.z), world.backgroundColor(wx, wy, player.z));
				else
					terminal.write(fov.tile(wx, wy, player.z).glyph(), x, y, Color.darkGray, Color.BLACK);
			}
		}
	}
	
	@Override
	public Screen respondToUserInput(KeyEvent key) {
		if (subscreen != null) {
			subscreen = subscreen.respondToUserInput(key);
		} else {
			switch (key.getKeyCode()){
			case KeyEvent.VK_TAB:
			case KeyEvent.VK_I:
								subscreen = new MenuScreen(player, player.x - getScrollX(), 
										player.y - getScrollY()); break;
			case KeyEvent.VK_SPACE:
								player.moveBy(0, 0, 0); break;
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_A: player.moveBy(-1, 0, 0); break;
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_D: player.moveBy( 1, 0, 0); break;
			case KeyEvent.VK_UP:
			case KeyEvent.VK_W: player.moveBy( 0,-1, 0); break;
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_S: player.moveBy( 0, 1, 0); break;
			case KeyEvent.VK_R: subscreen = new LookScreen(player, "Observando", 
					player.x - getScrollX(), 
					player.y - getScrollY()); break;
			case KeyEvent.VK_F: 
				if (player.weapon() == null || player.weapon().rangedAttackValue() == 0)
					player.notify("No tienes un arma de rango equipada.");
				else
					subscreen = new FireWeaponScreen(player,
						player.x - getScrollX(), 
						player.y - getScrollY()); break;
			case KeyEvent.VK_G:
			case KeyEvent.VK_E:
					player.pickup(); break;
			}
			
			switch (key.getKeyChar()){
			case '<': 
				if (userIsTryingToExit())
					return userExits();
				else
					player.moveBy( 0, 0, -1); break;
			case '>': player.moveBy( 0, 0, 1); break;
			case '?': subscreen = new HelpScreen(); break;
			}
		}
		
		if (subscreen == null){
			world.updateFloor(player.z);
			player.addTime();
		}
		
		if (player.hp() < 1)
			return new LoseScreen(player);
		
		return this;
	}

	private boolean userIsTryingToExit(){
		return player.z == 0 && world.tile(player.x, player.y, player.z) == Tile.STAIRS_UP;
	}
	
	private Screen userExits(){
		for (Item item : player.inventory().getItems()){
			if (item != null && item.name().equals("teddy bear"))
				return new WinScreen();
		}
		player.modifyHp(0, "Died while cowardly fleeing the caves.");
		return new LoseScreen(player);
	}
}
