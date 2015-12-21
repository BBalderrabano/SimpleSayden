package sayden;

import java.util.HashMap;

public class Thing {
	protected String name;
	public String getName() { return appearance == null ? name : appearance; }
	public String nameElLa(){ return gender == 'M' ? "el " + getName() : "la " + getName(); }
	public String nameDelDeLa(){ return gender == 'M' ? "del " + getName() : "de la " + getName(); }
	public String nameAlALa(){ return gender == 'M' ? "al " + getName() : "a la " + getName(); }
	public String nameUnUna(){ return gender == 'M' ? "un " + getName() : "una " + getName(); }
	
	protected String appearance;
	public String appearanceElLa(){ return gender == 'M' ? "el " + appearance : "la " + appearance; }
	public String appearanceAlALa(){ return gender == 'M' ? "al " + appearance : "a la " + appearance; }
	public String appearanceUnUna(){ return gender == 'M' ? "un " + appearance : "una " + appearance; }
	
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
	
	public Thing(){
		data = new HashMap<String, Object>();
	}
}
