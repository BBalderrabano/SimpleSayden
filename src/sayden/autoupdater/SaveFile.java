package sayden.autoupdater;

import java.io.Serializable;

public class SaveFile implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3618908745075930225L;
	
	public SaveFile(){}
	
	private boolean firstDream = false;
	
	public boolean isFirstDream() {
		return firstDream;
	}

	public void setFirstDream(boolean firstDream) {
		this.firstDream = firstDream;
	}
	
	private int version;

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
}
