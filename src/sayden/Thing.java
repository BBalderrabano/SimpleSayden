package sayden;

import java.util.HashMap;

public class Thing {
	protected String name;
	protected String appearance = null;
	protected String description = null;
	public String name() { return (appearance == null || appearance == name ? name : appearance)
			+ (stackable() && stacks > 1 ? "(x"+stacks+")" : ""); }
	public String nameElLa(){ return gender == 'M' ? "el " + name() : "la " + name(); }
	public String nameDelDeLa(){ return gender == 'M' ? "del " + name() : "de la " + name(); }
	public String nameAlALa(){ return gender == 'M' ? "al " + name() : "a la " + name(); }
	public String nameUnUna(){ return gender == 'M' ? "un " + name() : "una " + name(); }
	public String realNameUnUna() { return gender == 'M' ? "un " + name : "una " + name; }
	
	public boolean isIdentified() { return appearance == null || appearance == name; }
	
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
