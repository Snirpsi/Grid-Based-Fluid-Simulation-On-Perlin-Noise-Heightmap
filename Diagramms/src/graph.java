/**
 * 
 */

/**
 * @author Severin
 *
 */
public class graph {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		for (int y = 0; y < 2000; y++) {
			double yd = y/1000.0;
			
			if (yd>0.1) {
			System.out.println(
					 0.5 / Math.pow(Math.pow(yd - 0.5, 2) + 1, 20)); // watermap.wasser[x][y]*10;
			}else {
				System.out.println(0.0);
			}
		}
	}

}
