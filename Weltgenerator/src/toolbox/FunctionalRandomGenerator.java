package toolbox;

import java.util.Random;

public class FunctionalRandomGenerator {
	
	
//	long seed = 181;
//	FunctionalRandomGenerator(long seed) {
//		this.seed =seed;
//	}
	
//	long generate2seedetFSRN(int x, int y) {
//		Random randX = new Random(x);
//		Random randY = new Random(randX.nextInt(Integer.MAX_VALUE)+y);
//		return randY.nextInt(Integer.MAX_VALUE);
//	}
	
	long seed = 181;

	long a = 5931668747091289804l; // prim
	long b = 3715987458l;

	int[] chaos ;

	public FunctionalRandomGenerator(long seed) {	
		this.seed = seed;
		Random randi = new Random(seed);
		randi.nextInt();
		randi.nextInt();
		randi.nextInt();
		chaos = new int [100];
		for (int i = 0; i < 100; i++) {
			chaos [i] = randi.nextInt();
		}
	
		
	}

	long generateFSRN(int x) {
		return abs(seed * x + chaos[x % chaos.length]);
	}

	public long generate2seedetFSRN(int x, int y) {
		long sr = abs(y * chaos[(int) (generateFSRN(x) % chaos.length)] + x);
		if (sr % 10 < 5) {
			if (sr % 2 == 1) {
				return abs(generateFSRN(y) + b*x);
			}
			return abs(a * generateFSRN(y) + x);
		}
		return abs(sr * x + y);
	}

	private long abs(long x) {
		return (x > 0)?x:-x; 
	}
}
