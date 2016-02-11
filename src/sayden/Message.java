package sayden;

import java.awt.Color;

public class Message {
	private String message;
	public String getMessage() {
		return message;
	}
	private int x_offset;
	public int getXOffset() {
		return x_offset;
	}
	public void modifyXOffset(int amount) {
		this.x_offset += amount;;
	}
	private int y_offset;
	public int getYOffset() {
		return y_offset;
	}
	private Color color;
	public Color getColor() {
		return color;
	}
	
	public Message(String message, int x_offset, int y_offset, Color color){
		this.message = message;
		this.x_offset = x_offset;
		this.y_offset = y_offset;
		this.color = color;
	}
}
