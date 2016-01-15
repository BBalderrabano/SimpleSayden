package sayden;

public enum Speed {
		SUPER_SLOW(7, "Lentisimo"),
		VERY_SLOW(6, "Muy lento"),
		SLOW(5, "Lento"),
		NORMAL(4, "Normal"),
		FAST(3, "Rapido"),
		VERY_FAST(2, "Muy rapido"),
		SUPER_FAST(1, "Rapidisimo"),
		INS(0, "Instantaneo");
		
		private int velocity;
		public int velocity() { return velocity; }
		
		private String name;
		public String getName() { return name; }
	
		public Speed modifySpeed(int amount){
			int returningSpeed = this.velocity - amount;
			
			if(returningSpeed <= 1){
				return SUPER_FAST;
			}else if(returningSpeed >= 5){
				return SUPER_SLOW;
			}
			
			switch(returningSpeed){
				case 2: return VERY_FAST;
				case 3: return FAST; 
				case 4: return NORMAL;
				case 5: return SLOW;
				case 6: return VERY_SLOW;
			}
			return NORMAL;
		}
		
		Speed(int velocity, String name){
			this.velocity = velocity;
			this.name = name;
		}
}
