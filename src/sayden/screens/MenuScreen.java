package sayden.screens;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import asciiPanel.AsciiPanel;
import sayden.Constants;
import sayden.Creature;
import sayden.Item;

public class MenuScreen extends InventoryBasedScreen {
	
	private int scrollX = 0;
	private int scrollY = 0;
	
	private char lateralBar = 186;				//The char for the lateral bar
	private char topBar = 205;					//The char for the top bar
	
	private int inventoryPage = 1;				//The page we start on when looking at the inventory
	private int inventoryDisplay = 0;			//The amount of items we can show at a time
	private int inventoryPages = 0; 			//The amount of pages we have in the inv screen
	private boolean inventoryInit = false;		//Turns true when the variables are initialized
	
	private boolean interactWithItem = false;	//Is interacting with the item (equip, drop, throw)
	private boolean scrollingInvent = false;	//Is scrolling the inventory
	private int scrollPosition = 0;				//The scroll position
	private int itemsBeignDisplayed = 0;		//The items being displayed
	private Item interactingItem = null;		//The item we are trying to interact with
	
	private int scrollInteraction = 0;			//The interaction selected
	private int amountOfInteractions = 0;		//The max amount of interactions on an item
	private String selectedInteraction = null;	//The selected interaction
	
	private String[] gameTips = {
								"Algunas criaturas tienen puntos debiles",
								"Intenta atacar desde diferentes direcciones",
								"Estudia las habilidades de tus enemigos",
								"No dudes en reportar errores o sugerencias",
								"Usa \"F\" para disparar armas de rango",
								"Puedes esquivar ataques lentos",
								"Cuenta cuantos ataques puedes efectuar antes de ser golpeado"
								};
	private String screenTip = "";
	
	public MenuScreen(Creature player, int sx, int sy) {
		super(player);
		scrollX = sx;
		scrollY = sy;

		screenTip = gameTips[(int) (Math.random() * gameTips.length)];
	}
	
	@Override
	public void displayOutput(AsciiPanel terminal) {
		//terminal.clear();		//Uncomment if you want to erase the game background
		int x_offset_inventory = 1;
		int y_offset_inventory = 2;
		int width_inventory = 20;
		int height_inventory = 15;
		
		int x_offset_item = x_offset_inventory;
		int y_offset_item = y_offset_inventory + height_inventory + 1;
		int width_item = terminal.getWidthInCharacters() - 3;
		int height_item = terminal.getHeightInCharacters() - y_offset_item - 1;
		
		int player_stats_x = terminal.getWidthInCharacters() - width_inventory - 2;
		
		//Bounding box
		drawBox(0, 0, terminal.getWidthInCharacters(), terminal.getHeightInCharacters(), terminal);
		//Inventory list and box
		drawInventoryBox(x_offset_inventory, y_offset_inventory
				, width_inventory, height_inventory, terminal, "INVENTARIO");
		
		//Item text box
		drawItemBox(x_offset_item, y_offset_item,
				width_item, height_item, terminal);
		
		//Player stats
		drawPlayerStats(player_stats_x, y_offset_inventory,
				width_inventory, height_inventory, terminal);
		
		//Tips
		drawTips(1, terminal);
		
		terminal.repaint();
	}
	
	private void drawTips(int offset_y, AsciiPanel terminal){
		
		terminal.writeCenter(screenTip, offset_y);
	}
	
	private void drawPlayerStats(int offset_x, int offset_y, int width, int height, AsciiPanel terminal){
		int text_offset_x = offset_x + 1;
		int display_stat_amount = 5;
		Item compareItem = null;
		
		Color hpColor = AsciiPanel.white;
		Color defColor = AsciiPanel.white;
		Color atkColor = AsciiPanel.white;
		Color aspdColor = AsciiPanel.white;
		Color mspdColor = AsciiPanel.white;
		
		String hpValue = player.hp() + "/" + player.totalMaxHp();
		String atkValue = player.attackValueTotal()+"";
		String defValue = player.defenseValueTotal()+"";
		String atkSpeed = player.getAttackSpeed().getName();
		String mspSpeed = player.getMovementSpeed().getName();
		
		drawBox(offset_x, offset_y, width , display_stat_amount + 1, terminal);
		
		if(interactingItem != null){
			if(interactingItem.getBooleanData(Constants.CHECK_ARMOR)){ compareItem = player.armor(); }
			if(interactingItem.getBooleanData(Constants.CHECK_WEAPON)){ compareItem = player.weapon(); }
			if(interactingItem.getBooleanData(Constants.CHECK_HELMENT)){ compareItem = player.helment(); }
			if(interactingItem.getBooleanData(Constants.CHECK_SHIELD)){ compareItem = player.shield(); }
				
			if(!player.hasEquipped(interactingItem)){
				if(compareItem == null){
					if(interactingItem.bonusMaxHp() > 0){
						hpValue = player.hp() + "/" + (player.totalMaxHp() + interactingItem.bonusMaxHp());
						hpColor = AsciiPanel.green;
					}
					if(interactingItem.attackValue() > 0){
						atkValue = (player.attackValueTotal() + interactingItem.attackValue()) + "";
						atkColor = AsciiPanel.green;
					}
					if(interactingItem.defenseValue() > 0){
						defValue = (player.defenseValueTotal() + interactingItem.defenseValue()) + "";
						defColor = AsciiPanel.green;
					}
				}else{
					if(interactingItem.bonusMaxHp() > compareItem.bonusMaxHp()){
						hpValue = player.hp()+"/"+
								(player.totalMaxHp() - compareItem.bonusMaxHp() + interactingItem.bonusMaxHp());
						hpColor = AsciiPanel.green;
					}else if(interactingItem.bonusMaxHp() < compareItem.bonusMaxHp()){
						hpValue = player.hp()+"/"+
								(player.totalMaxHp() - compareItem.bonusMaxHp() + interactingItem.bonusMaxHp());
						hpColor = AsciiPanel.red;
					}
					if(interactingItem.attackValue() > compareItem.attackValue()){
						atkValue = "" + (player.attackValueTotal() - compareItem.attackValue() + interactingItem.attackValue());
						atkColor = AsciiPanel.green;
					}else if(interactingItem.attackValue() < compareItem.attackValue()){
						atkValue = "" + (player.attackValueTotal() - compareItem.attackValue() + interactingItem.attackValue());
						atkColor = AsciiPanel.red;
					}
					if(interactingItem.defenseValue() > compareItem.defenseValue()){
						defValue = "" + (player.defenseValueTotal() - compareItem.defenseValue() + interactingItem.defenseValue());
						defColor = AsciiPanel.green;
					}else if(interactingItem.defenseValue() < compareItem.defenseValue()){
						defValue = "" + (player.defenseValueTotal() - compareItem.defenseValue() + interactingItem.defenseValue());
						defColor = AsciiPanel.red;
					}
				}
				
				//If the weapon modifies attackSpeed
				if(interactingItem.attackSpeed() != null){
					if(interactingItem.attackSpeed().velocity() > 
							player.getAttackSpeed().velocity()){
						atkSpeed = interactingItem.attackSpeed().getName();
						aspdColor = AsciiPanel.red;
					}else if(interactingItem.attackSpeed().velocity() <
							player.getAttackSpeed().velocity()){
						atkSpeed = interactingItem.attackSpeed().getName();
						aspdColor = AsciiPanel.green;
					}
				}
				//If the weapon modifies movementSpeed
				if(interactingItem.movementSpeed() != null){
					if(interactingItem.movementSpeed().velocity() > 
							player.getMovementSpeed().velocity()){
						mspSpeed = interactingItem.movementSpeed().getName();
						mspdColor = AsciiPanel.red;
					}else if(interactingItem.movementSpeed().velocity() <
							player.getMovementSpeed().velocity()){
						mspSpeed = interactingItem.movementSpeed().getName();
						mspdColor = AsciiPanel.green;
					}
				}
			}
		}
		
		printStat("SALUD", hpValue, text_offset_x, offset_y + 1, hpColor, terminal);
		printStat("ATAQUE", atkValue, text_offset_x, offset_y + 2, atkColor, terminal);
		printStat("DEFENSA", defValue, text_offset_x, offset_y + 3, defColor, terminal);
		printStat("V.ATQ", atkSpeed, text_offset_x, offset_y + 4, aspdColor, terminal);
		printStat("V.MOV", mspSpeed, text_offset_x, offset_y + 5, mspdColor, terminal);
	}
	
	private void printStat(String stat_name, String value, int offset_x, int offset_y, Color color, AsciiPanel terminal){
		terminal.write(" "+stat_name+": " + value, offset_x, offset_y, color);
		terminal.write(" "+stat_name+": ", offset_x, offset_y);
	}
	
	private void drawItemBox(int offset_x, int offset_y, int width, int height, AsciiPanel terminal){
		if(scrollingInvent){
			drawBox(offset_x, offset_y, width, height, terminal);
			terminal.write((char)202, offset_x, offset_y + height);
			terminal.write((char)202, offset_x + width, offset_y + height);
			
			//We draw the item name
			int title_lenght = Math.min(interactingItem.name().length(), width - 2);
			String shorten_title = interactingItem.name().substring(0, title_lenght);
			terminal.write(shorten_title.toUpperCase(), offset_x + 1, offset_y);
			
			//We draw the item description
			ArrayList<String> descripcion =
					PlayScreen.splitPhraseByLimit(interactingItem.details(), width);
			
			for(int i = 0; i < descripcion.size(); i++){
				terminal.write(descripcion.get(i), offset_x + 1, offset_y + 1 + i);
			}
		}
	}
	
	private void drawInventoryBox(int offset_x, int offset_y, int width, int height, AsciiPanel terminal, String title){
		int startWidth = offset_x + 1;
		int startHeight = offset_y + 1;
		int itemIndex = 0;
		
		if(!inventoryInit){
			inventoryDisplay = height - 3; 		//We show height - 3 items because of the lines of the bounding box
			inventoryPages = (player.inventory().size() + inventoryDisplay - 1) / inventoryDisplay;
			inventoryInit = true;
		}
		
		//We first draw the bounding box box
		drawBox(offset_x, offset_y, width, height, terminal);
		
		//We set the starting page of the inventory
		int inventoryStartPage = Math.max(0, (inventoryPage - 1) * inventoryDisplay);

		//We go through the inventory items, taking in account the inventoryStartPage
		for(int s = inventoryStartPage; s < player.inventory().size(); s++){
			//We have no item in this position, move on
			if(player.inventory().get(s) == null){
				continue;
			}
			
			itemIndex++;	//We find an item
			
			int realHeight = startHeight + itemIndex - 1;	//The height takes in account the amount of items found
			
			Item displayItem = player.inventory().get(s);	//We grab the item in the inventory
			
			int permitted_lenght = Math.min(displayItem.getName().length(), width - 4);		//We cant let the name go out of the box
			String proccesed_line = displayItem.getName().substring(0, permitted_lenght);	//so we substring if necessary
			
			//If the list of items is bigger than the inside box stop drawing and add indicators
			if(itemIndex > inventoryDisplay){
				terminal.write((inventoryPage) + "/" + inventoryPages, width - 3, offset_y);
				terminal.write('+', width, offset_y);
				break;
			}
			//If we are on another page ALSO display page numbers
			if(inventoryPage > 1){
				terminal.write((inventoryPage) + "/" + inventoryPages, width - 3, offset_y);
				terminal.write('+', width, offset_y);
			}
			
			//Show item name and glyph, if equipped change color
			if(displayItem == player.weapon() || displayItem == player.armor()
					|| displayItem == player.helment() || displayItem == player.shield()){
				terminal.write("  " +proccesed_line, startWidth, realHeight, AsciiPanel.blue);
				terminal.write(displayItem.glyph(), startWidth, realHeight, displayItem.color());
			}else{
				terminal.write("  " + proccesed_line, startWidth, realHeight);
				terminal.write(displayItem.glyph(), startWidth, realHeight, displayItem.color());
			}

			//If we are scrolling show the arrow. If we are interacting with the item change the arrow
			if(scrollingInvent && itemIndex == scrollPosition){
				terminal.write(interactWithItem ? "->" : "<-", width - 2, realHeight);
				interactingItem = displayItem;
				if(interactWithItem){
					drawBoxInteractions(offset_x + width, offset_y, terminal, displayItem);
				}
			}
		}
		
		itemsBeignDisplayed = itemIndex;	//After the loop we count how many items are in display
		
		//We draw the box title
		int title_lenght = Math.min(title.length(), width - 2);
		String shorten_title = title.substring(0, title_lenght);
		terminal.write(shorten_title, offset_x + 1, offset_y);
	}
	
	private void drawBoxInteractions(int offset_x, int offset_y, AsciiPanel terminal, Item item){
		ArrayList<String> options = new ArrayList<String>();
		int max_text = 0;
		
		offset_x++;
		
		options.add("soltar");
		options.add("arrojar");
		
		if(item.getBooleanData(Constants.CHECK_ARMOR) 
				|| item.getBooleanData(Constants.CHECK_HELMENT)
				|| item.getBooleanData(Constants.CHECK_SHIELD)
				|| item.getBooleanData(Constants.CHECK_WEAPON)){
			if(item == player.weapon() || item == player.armor()
					|| item == player.helment() || item == player.shield()){
				options.add("desequipar");
			}else{
				options.add("equipar");
			}
		}
		if(!item.writtenSpells().isEmpty()){
			options.add("conjurar");
		}
		if(item.quaffEffect() != null || item.getBooleanData(Constants.CHECK_CONSUMABLE)){
			options.add("consumir");
		}
		
		max_text = longest_word(options).length() + 1;
		
		drawBox(offset_x, offset_y, max_text, options.size() + 1, terminal);
		
		amountOfInteractions = options.size();
		
		for(int s = 0; s < options.size(); s++){
			if(scrollInteraction == s){
				terminal.write(options.get(s), offset_x + 1, offset_y + s + 1, AsciiPanel.cyan);
				selectedInteraction = options.get(s);
			}else{
				terminal.write(options.get(s), offset_x + 1, offset_y + s + 1);
			}
		}
	}
	
	private void drawBox(int offset_x, int offset_y, int width, int height, AsciiPanel terminal){
		int realWidth = Math.min(width + offset_x, terminal.getWidthInCharacters() - 1);
		int realHeight = Math.min(height + offset_y, terminal.getHeightInCharacters() - 1);
		
		//Top and bottom UI bars
		for(int w = offset_x; w < realWidth; w ++){
			terminal.write(topBar, w, offset_y);
			terminal.write(topBar, w, realHeight);
		}
		for(int h = offset_y; h < realHeight; h ++){
			terminal.write(lateralBar, offset_x, h);
			terminal.write(lateralBar, realWidth, h);
		}
		
		//Corner connectors
		//Bottom left
		terminal.write((char)200, offset_x, realHeight);
		//Bottom right
		terminal.write((char)188, realWidth, realHeight);
		//Top left
		terminal.write((char)201, offset_x, offset_y);
		//Top right
		terminal.write((char)187, realWidth, offset_y);
	}
	
	protected String getVerb() {
		return "wear or wield";
	}

	protected boolean isAcceptable(Item item) {
		return true;
	}

	protected Screen use(Item item) {
		return null;
	}
	 
	public String longest_word(ArrayList<String> wordsList){
	    String longest_word="";
	    int maxLength=0;
        for(int i=0; i < wordsList.size(); i++){
            if(wordsList.get(i).length() > maxLength){
              maxLength = wordsList.get(i).length();
              longest_word = wordsList.get(i);
            }
        }
	    return longest_word;
	}
	
	public Screen respondToUserInput(KeyEvent key) {
		if(key.getKeyCode() == KeyEvent.VK_ENTER || key.getKeyCode() == KeyEvent.VK_E
				|| key.getKeyCode() == KeyEvent.VK_SPACE
				|| (key.getKeyCode() == KeyEvent.VK_RIGHT || key.getKeyCode() == KeyEvent.VK_D) && interactWithItem){
			if(!interactWithItem){
				return this;
			}else{
				if(selectedInteraction == null)
					return this;
				if(selectedInteraction == "desequipar"){ player.unequip(interactingItem);	}
				if(selectedInteraction == "equipar"){	 player.equip(interactingItem);	}
				if(selectedInteraction == "soltar"){	 player.drop(interactingItem);	}
				if(selectedInteraction == "consumir"){	 player.eat(interactingItem);	}
				if(selectedInteraction == "arrojar"){	 return new ThrowAtScreen(player, scrollX, scrollY, interactingItem);	}
				if(selectedInteraction == "conjurar"){   return new ReadSpellScreen(player, scrollX, scrollY, interactingItem);	}
			}
			return null;
		}else if (key.getKeyCode() == KeyEvent.VK_ESCAPE || 
				key.getKeyCode() == KeyEvent.VK_TAB) {
			return null;
		} else if (key.getKeyCode() == KeyEvent.VK_UP || key.getKeyCode() == KeyEvent.VK_W){
			//If not scrolling start scrolling the items and set cursor position
			if(!scrollingInvent){
				scrollingInvent = true;
				scrollPosition = Math.min(inventoryDisplay, itemsBeignDisplayed);
				return this;
			}
			//If we are interacting with the item scroll the interactions
			if(interactWithItem){
				scrollInteraction = Math.max(0, scrollInteraction - 1);
			}else{	//Else scroll the items
				scrollPosition = Math.max(1, scrollPosition - 1);
			}
			return this;
		} else if (key.getKeyCode() == KeyEvent.VK_DOWN || key.getKeyCode() == KeyEvent.VK_S){
			//If not scrolling start scrolling the items and set cursor position
			if(!scrollingInvent){
				scrollingInvent = true;
				scrollPosition = 1;
				return this;
			}
			//If we are interacting with the item scroll interactions
			if(interactWithItem){
				scrollInteraction = Math.min(amountOfInteractions - 1, scrollInteraction + 1);
			}else{	//Else scroll the items
				scrollPosition = Math.min(Math.min(inventoryDisplay, itemsBeignDisplayed), scrollPosition + 1);
			}
			return this;
		} else if (key.getKeyCode() == KeyEvent.VK_LEFT || key.getKeyCode() == KeyEvent.VK_A){
			if(scrollingInvent && !interactWithItem){
				scrollingInvent = false;	//If we are leaving the item scrolling
				selectedInteraction = null;
				interactingItem = null;
			}else if(interactWithItem){
				interactWithItem = false;	//If we are leaving the item interactions
				interactingItem = null;
				selectedInteraction = null;
			}
			return this;
		} else if (key.getKeyCode() == KeyEvent.VK_RIGHT  || key.getKeyCode() == KeyEvent.VK_D){
			if(scrollingInvent){
				interactWithItem = true;	//If we are scrolling the items
				scrollInteraction = 0;
				interactingItem = null;		//Reset the interacting item
			}
			return this;
		} else if (key.getKeyCode() == KeyEvent.VK_PLUS) {
			if(inventoryPage >= inventoryPages){
				inventoryPage--;
			}else if(inventoryPage < inventoryPages){
				inventoryPage++;
			}
			return this;
		} else {
			return this;
		}
	}
}
