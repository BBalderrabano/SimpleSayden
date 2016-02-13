package sayden;

import java.util.HashMap;

public class Thing {
	protected String name;
	protected String appearance = null;
	protected String description = null;
	
	private boolean isIdentified = true;
	public boolean isIdentified() { return isIdentified; }
	public void identify(boolean identify) { this.isIdentified = identify;}
	
	public String nameWStacks() { return Constants.knownName(name) || isIdentified || appearance == null ? name : appearance + (stackable() && stacks > 1 ? "(x"+stacks+")" : ""); }
	public String nameWNoStacks() { return Constants.knownName(name) || isIdentified || appearance == null ? name : appearance; }
	
	public String nameUnUnaWNoStacks(){ return gender == 'M' ? "un " + nameWNoStacks() : "una " + nameWNoStacks(); }
	public String nameElLaWNoStacks(){ return gender == 'M' ? "el " + nameWNoStacks() : "la " + nameWNoStacks(); }
	
	public String nameElLa(){ return gender == 'M' ? "el " + nameWStacks() : "la " + nameWStacks(); }
	public String nameDelDeLa(){ return gender == 'M' ? "del " + nameWStacks() : "de la " + nameWStacks(); }
	public String nameAlALa(){ return gender == 'M' ? "al " + nameWStacks() : "a la " + nameWStacks(); }
	public String nameUnUna(){ return gender == 'M' ? "un " + nameWStacks() : "una " + nameWStacks(); }
	
	protected int stacks = 1;
	protected int maxStacks = 0;
	public boolean stackable() { return maxStacks > 0; }
	
	public void makeStackable(int maxStacks) { this.maxStacks = maxStacks; }
	public void modifyStacks(int amount){ this.stacks += amount; 
											if(stacks < 1) { this.stacks = 1; } 
											if(stacks > maxStacks) { this.stacks = maxStacks; } 
	}
	public void emptyStacks() { this.stacks = 1; }
	
	protected char gender;
		
	protected HashMap<String, Object> data;
	public void setData(String key, Object value){ data.put(key, value); }
	public void setAllData(HashMap<String, Object> datas){ data.putAll(datas); } 
	public void unsetData(String key) { data.remove(key); }
	public Object getData(String key){ return data.get(key); }
	public HashMap<String, Object> getAllData(){ return data; }
	
	public boolean getBooleanData(String key) { return data.get(key) == null ? false : (Boolean) data.get(key); }
	public int getIntegerData(String key) { return data.get(key) == null ? 0 : (Integer) data.get(key); }
	public String getStringData(String key) { return data.get(key) == null ? null : data.get(key).toString(); }
	public void modifyIntegerData(String key, int amount) { 
		if(data.get(key) == null){
			setData(key, amount);
		} else {
			int value = (Integer) data.get(key);
			data.replace(key, value);
		}
	}

	
	public Thing(){
		data = new HashMap<String, Object>();
	}
}
