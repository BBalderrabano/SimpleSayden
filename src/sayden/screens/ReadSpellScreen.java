package sayden.screens;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import asciiPanel.AsciiPanel;
import sayden.ApplicationMain;
import sayden.Constants;
import sayden.Creature;
import sayden.Item;
import sayden.Spell;

public class ReadSpellScreen implements Screen {

	public static Spell lastSpell = null;
	public static Creature lastCreature = null;
	
	protected Creature player;
	private Item item;
	private int sx;
	private int sy;
	
	private int spellIndex = 0;
	
	public ReadSpellScreen(Creature player, int sx, int sy, Item item){
		this.player = player;
		this.item = item;
		this.sx = sx;
		this.sy = sy;
	}
	
	public void displayOutput(AsciiPanel terminal) {
		ArrayList<String> lines = getList();
		
		String showQuestion = lines.size() > 0 ? "Elige un conjuro a lanzar" : "No recuerdas ningun conjuro";
		
		int y = 22 - lines.size();
		int x = terminal.getWidthInCharacters() - showQuestion.length() - 1;
		
		if (lines.size() > 0){
			terminal.clear(' ', x, y, 20, lines.size());
			drawBox(x - 1, y - 1, showQuestion.length() - 1, lines.size() + 1, terminal);
		}
		
		if(lastSpell != null && hasSpell(lastSpell)){
			String quickCast = "(Q) - Conjurar " + lastSpell.nameWStacks();
			if(lastCreature != null && lastCreature.isAlive())
				quickCast += " sobre " + lastCreature.nameWStacks();
			
			terminal.write(quickCast, terminal.getWidthInCharacters() - quickCast.length() - 1, 0);
		}

		for (int s = 0; s < lines.size(); s++){
			if(s == spellIndex)
				terminal.write(" " + lines.get(s) + " <-", x, y++, Constants.OVERLAY_COLOR);
			else
				terminal.write(" " + lines.get(s), x, y++);
		}
		
		terminal.clear(' ', 0, 23, 80, 1);
		terminal.write(showQuestion, x - 1, 23);
		
		terminal.repaint();
	}
	
	private void drawBox(int offset_x, int offset_y, int width, int height, AsciiPanel terminal){
		int realWidth = Math.min(width + offset_x, terminal.getWidthInCharacters() - 1);
		int realHeight = Math.min(height + offset_y, terminal.getHeightInCharacters() - 1);
		
		//Top and bottom UI bars
		for(int w = offset_x; w < realWidth; w ++){
			terminal.write((char)205, w, offset_y);
			terminal.write((char)205, w, realHeight);
		}
		for(int h = offset_y; h < realHeight; h ++){
			terminal.write((char)186, offset_x, h);
			terminal.write((char)186, realWidth, h);
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
	
	private ArrayList<String> getList() {
		ArrayList<String> lines = new ArrayList<String>();
		
		for(int i = 0; i < player.learnedSpells().size(); i++){
			String line = player.learnedSpells().get(i).nameWStacks();
			lines.add(line);
		}
		
		if(item != null){
			for (int i = 0; i < item.writtenSpells().size(); i++){
				Spell spell = item.writtenSpells().get(i);
				String line = spell.nameWStacks();
				lines.add(line);
			}
		}

		return lines;
	}
	
	private boolean hasSpell(Spell spell){
		for(Spell s : player.learnedSpells()){
			if(s.nameWStacks() == spell.nameWStacks())
				return true;
		}
		
		for(Item i : player.inventory().getItems()){
			if(i == null || i.writtenSpells() == null){
				continue;
			}
			for(Spell s : i.writtenSpells()){
				if(s.nameWStacks().equals(spell.nameWStacks())){
					return true;
				}
			}
		}
		
		return false;
	}

	public Screen respondToUserInput(KeyEvent key, ApplicationMain main) {
		ArrayList<String> lines = getList();
		
		if(key.getKeyCode() == KeyEvent.VK_Q){
			if(lastCreature != null && lastSpell != null && lastCreature.isAlive() && hasSpell(lastSpell)){
				player.castSpell(lastSpell, lastCreature.x, lastCreature.y);
				return null;
			}else if(lastSpell != null && hasSpell(lastSpell)){
				return use(lastSpell);
			}else{
				return null;
			}
		}else if(key.getKeyCode() == KeyEvent.VK_UP || key.getKeyCode() == KeyEvent.VK_W){
			spellIndex--;
		}else if(key.getKeyCode() == KeyEvent.VK_DOWN || key.getKeyCode() == KeyEvent.VK_S){
			spellIndex++;
		}else if(key.getKeyCode() == KeyEvent.VK_ESCAPE){
			return null;
		}else if(key.getKeyCode() == KeyEvent.VK_ENTER || key.getKeyCode() == KeyEvent.VK_D
				|| key.getKeyCode() == KeyEvent.VK_LEFT){
			lastSpell = item.writtenSpells().get(spellIndex);
			return use(item.writtenSpells().get(spellIndex));
		}
		
		if(spellIndex < 0)
			spellIndex = lines.size() - 1;
		
		if(spellIndex >= lines.size())
			spellIndex = 0;
		
		return this;
	}

	protected Screen use(Spell spell){
		if (spell.requiresTarget())
			return new CastSpellScreen(player, "", sx, sy, spell);
		
		player.castSpell(spell, player.x, player.y);
		return null;
	}

	@Override
	public Screen respondToMouseInput(MouseEvent mouse) {
		return this;
	}
}
