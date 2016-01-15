package sayden;

public class Inventory {

	private Item[] items;
	public Item[] getItems() { return items; }
	public Item get(int i) { return items[i]; }
	public int size() { return items.length; }
	
	public Inventory(int max){
		items = new Item[max];
	}
	
	public void add(Item item){
		if(item.stackable() && containsimilar(item)){
			for (int i = 0; i < items.length; i++){
				if (items[i] != null && items[i].name().equals(item.name() ) && !items[i].stacked()){
					items[i].modifyStacks(item.stacks);
					break;
				}
			}
			return;
		}
		for (int i = 0; i < items.length; i++){
			if (items[i] == null){
				items[i] = item;
				break;
			}
		}
	}

	public void remove(Item item){
		for (int i = 0; i < items.length; i++){
			Item temp = items[i];
			if (temp == item){
				if(temp.stackable() && temp.stacks > 1){
					return;
				}
				items[i] = null;
				return;
			}
		}
	}

	public boolean isFull(){
		int size = 0;
		for (int i = 0; i < items.length; i++){
			if (items[i] != null)
				size++;
		}
		return size == items.length;
	}
	
	public boolean containsimilar(Item item) {
		for (Item i : items){
			if (i != null && i.name == item.name)
				return true;
		}
		return false;
	}
	
	public boolean contains(Item item) {
		for (Item i : items){
			if (i == item)
				return true;
		}
		return false;
	}
}
