package sayden;

public enum Speed {
		VERY_SLOW(5),
		SLOW(4),
		NORMAL(3),
		FAST(2),
		VERY_FAST(1),
		INS(0);
		
		private int velocity;
		public int velocity() { return velocity; }
	
		Speed(int velocity){
			this.velocity = velocity;
		}
}
