import java.awt.image.BufferedImageFilter;
import java.util.Arrays;

public class wasser2d {
	static double[] wasser;

	public static void main(String[] args) {
		wasser = new double[4];
		wasser[1] = 1.0;
		
		print();
		for (int i = 0; i < 20; i++) {
			step();
			print();
		}
	}

	public static void step() {
		double[] buffer = new double[wasser.length];
		for (int i = 0; i < wasser.length; i++) {
			int flowableNeighbors = getWasserFlowableCount(i);
			if (flowableNeighbors != 0) {

				double flowVolume = wasser[i] - getMinimumWaterLevelNeighbor(i);
				addToBuffer(buffer,i,wasser[i]-flowVolume);
				flowVolume = flowVolume / (flowableNeighbors + 1);
				addToBuffer(buffer, i, flowVolume);
				

				try {
					if (wasser[i - 1] < wasser[i]) {
						addToBuffer(buffer, i - 1, flowVolume);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
				try {
					if (wasser[i + 1] < wasser[i]) {
						addToBuffer(buffer, i + 1, flowVolume);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			} else {
				addToBuffer(buffer, i, wasser[i]);
			}
		}
		for (int i = 0; i < wasser.length; i++) {
			wasser[i] = buffer[i];
		}

	}

	public static void addToBuffer(double[] buffer, int x, double volume) {
		try {
			buffer[x] += volume;
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static double getMinimumWaterLevelNeighbor(int x) {
		double a = Double.MAX_VALUE;
		double b = Double.MAX_VALUE;
		try {
			a = wasser[x - 1];
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			b = wasser[x + 1];
		} catch (Exception e) {
			// TODO: handle exception
		}
		return Math.min(a, b);
	}

	public static int getWasserFlowableCount(int x) {
		int count = 0;
		try {
			if (wasser[x - 1] < wasser[x]) {
				count++;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		try {
			if (wasser[x + 1] < wasser[x]) {
				count++;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return count;
	}
	public static void print() {
		String s = "";
		for (int i = 0 ; i< wasser.length; i++) {
			s += wasser [i] +"\t";
		}
		System.out.println(s);
	}
}
