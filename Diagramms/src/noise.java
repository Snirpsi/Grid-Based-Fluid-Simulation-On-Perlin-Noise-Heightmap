import java.util.Random;

public class noise {
	
	public static double [][] wn ;

	
	public static void main (String[] args) {
		wn = new double[3][41];
		
		Random rand = new Random();
		for (int i = 0;i <10; i++) {
			wn[0][i*4] = rand.nextDouble()- 0.5;
		}
		for (int i = 0;i <wn[0].length-1; i++) {
			double y1 = wn[0][((int)i/4)*4];
			double y2 = wn[0][(i+4)/4*4];
			double x1 = i/4*4;
			double x2 = (i+4)/4*4;
			
			if (i%4!=0) {
				wn[0][i] = y1+(y2-y1)/(x2-x1)*(i-x1);
			}
			
			System.out.println(wn[0][i]);
		}
		System.out.println("====================");
		for (int i = 0;i <20; i++) {
			wn[1][i*2] = rand.nextDouble()/2- 1.0/4;
		}
		for (int i = 0;i <wn[1].length-1; i++) {
			double y1 = wn[1][((int)i/2)*2];
			double y2 = wn[1][(i+2)/2*2];
			double x1 = i/2*2;
			double x2 = (i+2)/2*2;
			
			if (i%2!=0) {
				wn[1][i] = y1+(y2-y1)/(x2-x1)*(i-x1);
			}
			
			System.out.println(wn[1][i]);
		}
		System.out.println("====================");
		for (int i = 0;i <40; i++) {
			wn[1][i] = rand.nextDouble()/4- 1.0/8;
			System.out.println(wn[1][i]);
		}
	}
	
}
