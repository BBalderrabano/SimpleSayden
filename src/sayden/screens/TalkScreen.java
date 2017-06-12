package sayden.screens;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import asciiPanel.AsciiPanel;
import sayden.ApplicationMain;
import sayden.Constants;
import sayden.Creature;
import sayden.Item;

public class TalkScreen extends InventoryBasedScreen {
	private int optionIndex = -1;
	private Creature npc;
	
	private String question;
	
	private char lateralBar = 186;				//The char for the lateral bar
	private char topBar = 205;					//The char for the top bar
	
	protected ArrayList<String> messages;
	protected ArrayList<String> options;
	
	private boolean startAggression = false;
	
	public TalkScreen(Creature player, Creature npc, ArrayList<String> messages, ArrayList<String> options) {
		super(player);
		
		this.messages = new ArrayList<String>();
		this.options = new ArrayList<String>();
		this.messages = messages;
		this.options = options;
		this.question = messages.get(messages.size() - 1);
		
		this.npc = npc;
	}
	
	private int getLargestOption(){
		int max = 0;
		for(String t : options){
			t += "<<";
			if(t.length() > max)
				max = t.length();
		}
		return max;
	}
	
	public void displayOutput(AsciiPanel terminal) {
		int top = Constants.WORLD_HEIGHT - 3;
		
		String conversingText = "Conversando con " + npc.nameElLa() + " ";
		String enterText = "ENTER";
		String combatingText = "Presiona enter para iniciar combate";
		
		drawBox(0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, terminal);
		
		terminal.write((char)203, conversingText.length() + 2, 0);
		terminal.write(lateralBar, conversingText.length() + 2, 1);
		terminal.write((char)188, conversingText.length() + 2, 2);
		for(int i = -1; i < conversingText.length(); i++){
			terminal.write(topBar, i + 2, 2);
		}
		terminal.write((char)204, 0, 2);
		
		terminal.write(conversingText, 2, 1);
		
		if(!startAggression){
			if(messages.isEmpty()){
				if(!options.isEmpty()){
					int x_offset = Constants.WORLD_WIDTH - getLargestOption() - 1;
	
					String qNTildeFix = question.replace('ñ', (char)164).replace('Ñ', (char)165);
					
					terminal.writeCenter(qNTildeFix, top);
					
					for(int i = 0; i < options.size(); i++){
						if(optionIndex == i){
							terminal.write(options.get(i), x_offset, options.size() - i, Constants.OVERLAY_COLOR);
							terminal.write("<<", terminal.getCursorX(), terminal.getCursorY());
						}else{
							terminal.write(options.get(i), x_offset, options.size() - i);
						}
					}
					
					terminal.write((char)203, x_offset - 1, 0);
					for(int i = 0; i < options.size(); i++){
						terminal.write(lateralBar, x_offset - 1, options.size() - i);}
					for(int i = -1; i < Constants.WORLD_WIDTH - x_offset; i++){
						terminal.write(topBar, x_offset + i, options.size() + 1);}
					terminal.write((char)200, x_offset - 1, options.size() + 1);
					terminal.write((char)185, Constants.WORLD_WIDTH - 1, options.size() + 1);
					
					int x_width = Constants.WORLD_WIDTH - x_offset;
					x_width = Math.round(x_width * .5f);
					x_width += (enterText.length() * .5f + 1);
					
					terminal.write(enterText, (int) (Constants.WORLD_WIDTH - x_width)
							, options.size() + 1, Constants.OVERLAY_COLOR);
				}
				return;
			}
			
			ArrayList<String> trueMessages = splitPhraseByLimit(messages.get(0), Constants.WORLD_WIDTH - 1);
			
			for(int i = 0; i < trueMessages.size(); i++){
				String nTildeFix = trueMessages.get(i).replace('ñ', (char)164).replace('Ñ', (char)165);
				terminal.writeCenter(nTildeFix, top + i - (trueMessages.size() - 1));
			}
			messages.remove(0);
		}else{
			int x_width = Constants.WORLD_WIDTH;
			x_width = Math.round(x_width * .5f);
			x_width -= (combatingText.length() * .5f + 1);
			
			drawBox(x_width, 9, combatingText.length() + 1, 2, terminal);
			
			terminal.writeCenter(combatingText, 10, AsciiPanel.brightRed);
			terminal.writeCenter("--"+enterText+"--", 12, AsciiPanel.brightRed);
		}
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

	public void onSelectOption(String option){}

	@Override
	protected String getVerb() {
		return null;
	}

	@Override
	protected boolean isAcceptable(Item item) {
		return false;
	}

	@Override
	protected Screen use(Item item) {
		return null;
	}
	
	private void drawBox(int offset_x, int offset_y, int width, int height, AsciiPanel terminal){
		int realWidth = Math.min(width + offset_x, Constants.WORLD_WIDTH - 1);
		int realHeight = Math.min(height + offset_y, Constants.WORLD_HEIGHT - 1);
		
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
	
	public Screen respondToUserInput(KeyEvent key, ApplicationMain main) {
		if(key.getKeyCode() == KeyEvent.VK_TAB){
			startAggression = !startAggression;
			return this;
		}
		
		if (key.getKeyCode() == KeyEvent.VK_ENTER && startAggression){
		 	npc.setData(Constants.FLAG_ANGRY, true);
		 	return null;
		}
		
		if(options != null){
			if(!options.isEmpty() && messages.isEmpty()){
				 if (key.getKeyCode() == KeyEvent.VK_UP || key.getKeyCode() == KeyEvent.VK_W){
						optionIndex++;
				 }
				 if (key.getKeyCode() == KeyEvent.VK_DOWN || key.getKeyCode() == KeyEvent.VK_S){
						optionIndex--;
				 }
				 if (key.getKeyCode() == KeyEvent.VK_ENTER && optionIndex > -1 && options.get(optionIndex) != null){
					 	onSelectOption(options.get(optionIndex));
					 	return null;
				 }
			}
			
			if(optionIndex >= options.size())
				optionIndex = 0;
			else if(optionIndex < 0)
				optionIndex = options.size() - 1;
			
			return messages.isEmpty() && options.isEmpty() ? null : this;
		}
		
		return messages.isEmpty() ? null : this;
	}

	@Override
	public Screen respondToMouseInput(MouseEvent mouse) {
		return this;
	}
}
