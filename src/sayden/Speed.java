package sayden;

public enum Speed {
		VERY_SLOW(5, "Muy lento"),
		SLOW(4, "Lento"),
		NORMAL(3, "Normal"),
		FAST(2, "Rapido"),
		VERY_FAST(1, "Muy rapido"),
		INS(0, "Instantaneo");
		
		private int velocity;
		public int velocity() { return velocity; }
		
		private String name;
		public String getName() { return name; }
	
		public Speed modifySpeed(int amount){
			int returningSpeed = this.velocity - amount;
			
			if(returningSpeed <= 1){
				return VERY_FAST;
			}else if(returningSpeed >= 5){
				return VERY_SLOW;
			}
			
			switch(returningSpeed){
				case 2: return FAST; 
				case 3: return NORMAL;
				case 4: return SLOW;
			}
			return NORMAL;
		}
		
		Speed(int velocity, String name){
			this.velocity = velocity;
			this.name = name;
		}
}
