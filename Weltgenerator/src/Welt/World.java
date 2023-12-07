package Welt;

import java.util.Random;
import application.Colormap;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import toolbox.FunctionalRandomGenerator;

public class World {
	///// [x][y][z]

	private Watermap watermap;
	double[][] topografie;
	private double[][] woodMap;
	private double[][] stoneMap;
	int[][] lakeMap;
	int[][] treeMap;

	private int seed;
	private int range;
	private int granularity;
	private int size;

	// __SEED_ good seed 181 & 2 & 51 &#
	private static final boolean GENERATOR = false;// false = Java; true = eigen

	// seeseed 723 &
	Random randomGenerator;
	FunctionalRandomGenerator rand;

	public World(int size, int granularity, int range) {
		this(size, granularity, range, 181);
	}

	public World(int size, int granularity, int range, int seed) {
		this.seed = seed;

		this.size = size;

		this.granularity = granularity;

		this.range = range;

		this.randomGenerator = new Random(this.seed);
		this.rand = new FunctionalRandomGenerator(this.seed);
		// TODO Auto-generated method stub
		topografie = createHightMap(size, granularity, range);

		woodMap = createWoodMap(size, granularity, range);
		// printWorldNum(woodMap);
		stoneMap = createStoneMap(size, granularity, range);

		lakeMap = findLocalMinimum();

		this.watermap = new Watermap(size);

		// this.watermap.rainSet( 100, 100);
		watermap.rain();
		// for( int i = 0; i< 50;i++) {
		// watermap.step(this);
		// }

	}

	public int getSeed() {
		return seed;
	}

	public int getRange() {
		return range;
	}

	public int getSize() {
		return size;
	}

	public double[][] getWoodMap() {
		return woodMap;
	}

	public double[][] getStoneMap() {
		return stoneMap;
	}

	public Watermap getWatermap() {
		return watermap;
	}

	public int[][] findLocalMinimum() {
		int[][] wm = new int[topografie.length][topografie.length];

		for (int i = 0; i < wm.length; i++) {
			for (int j = 0; j < wm[i].length; j++) {
				boolean i_am_the_local_minimum = true;
				for (int a = i - 5; a < i + 5; a++) {
					for (int b = j - 5; b < j + 5; b++) {
						if (getHight(a, b) < getHight(i, j)) {
							i_am_the_local_minimum = false;
							break;
						}

					}
					if (i_am_the_local_minimum == false) {
						break;
					}
				}
				if (i_am_the_local_minimum == true) {
					wm[i][j] = 1;
				} else {
					wm[i][j] = 0;
				}
			}
		}
		return wm;
	}

	private double[][] normalizeMap01(double[][] wm) {

		if (wm == null) {
			return null;
		}
		if (wm[0] == null) {
			return null;
		}
		for (int i = 0; i < wm.length; i++) {
			for (int j = 0; j < wm[i].length; j++) {
				if (wm[i][j] >= 0) {
					wm[i][j] = 1;
				} else {
					wm[i][j] = 0;
				}
			}
		}

		return wm;
	}

	private int[][] createTreesMap() {
		int[][] wm = new int[size][size];
		return wm;
	}

	private double[][] createStoneMap(int size, int granularity, int range) {
		double[][] wm = createHightMap(size, 32, 32);

		wm = normalizeMap01(wm);

		return wm;

	}

	private double[][] createWoodMap(int size, int granularity, int range) {
		double[][] wm = createHightMap(size, 64, 64);

		wm = normalizeMap01(wm);

		return wm;

	}

	private double[][] createHightMap(int size, int granularity, int range) {// = topografie
		double[][] m = new double[size + 1][size + 1];
		// ______________________________________|___minimal granularity //default 2
		for (int g = granularity, r = range; g > 2; g /= 2, r /= 2) {
			double[][] m1 = generateSingleLayer(size + 1, g, r);
			m = addMap(m, m1);

		}
		return m;
	}

	public double[][] addMap(double[][] m, double[][] m2) {
		if (m == null || m2 == null) {
			return null;
		}
		if (m.length != m2.length) {
			return null;
		}
		if (m[0] == null || m2[0] == null) {
			return null;
		}
		if (m[0].length != m2[0].length) {
			return null;
		}
		double[][] m3 = new double[m.length][m[0].length];

		for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < m.length; j++) {

				m3[i][j] = m[i][j] + m2[i][j];
			}
		}

		return m3;
	}

	public double[][] smooth(int iterations) {
		if (iterations <= 0) {
			return topografie;
		}

		double[][] ret = new double[topografie.length][topografie.length];
		for (int i = 1; i < topografie.length - 1; i++) {
			for (int j = 1; j < topografie.length - 1; j++) {
				ret[i][j] = (int) ((topografie[i][j] + topografie[i][j + 1] + topografie[i + 1][j]
						+ topografie[i + 1][j + 1] + topografie[i][j - 1] + topografie[i - 1][j]
						+ topografie[i - 1][j - 1] + topografie[i + 1][j - 1] + topografie[i - 1][j + 1]) / 9.0);
			}
		}
		topografie = ret;

		return smooth(iterations - 1);
	}

	public double[][] generateSingleLayer(int mapsize, int granularity, int range) {

		double[][] mapLayer = new double[mapsize][mapsize];

		for (int i = 0; i < mapsize; i += granularity) {
			for (int j = 0; j < mapsize; j += granularity) {
				if (GENERATOR) {// eigener generator
					mapLayer[i][j] = Math.abs((int) rand.generate2seedetFSRN(i, j) % range) - range / 2;
				} else {
					mapLayer[i][j] = randomGenerator.nextInt(range) - range / 2;
					/*
					 * flacht etwas die extreme ab sorgt andererseits dafür dass nur die hälfte der
					 * range tazächlich genutzt wird (abgesehen von extremen zufallsereignisen)
					 */
				}
			}

		}
		// for (int i = 0; i < mapsize; i += 1) {
		// for (int j = 0; j < mapsize; j += 1) {
		// System.out.format("%3d",map1[i][j]);
		// }
		// System.out.println();
		// }
		for (int i = 0; i + 1/* + granularity */ < mapsize; i += 1) {
			for (int j = 0; j + 1 /* + granularity */ < mapsize; j += 1) {
				if (!(i % granularity == 0 && j % granularity == 0))
					// intrtpolation linear
					mapLayer[i][j] = (int) bilinearInterpolation((i / granularity) * granularity,
							(j / granularity) * granularity, ((i + granularity) / granularity) * granularity,
							((j + granularity) / granularity) * granularity, i, j, mapLayer);
			}

		}
		return mapLayer;
	}

	private double bilinearInterpolation(int x1, int y1, int x2, int y2, int xe, int ye, double[][] mapLayer) {
		double r = (y2 - ye) / (double) (y2 - y1) * linearInterpolation(x1, x2, xe, y1, mapLayer)
				+ (ye - y1) / (double) (y2 - y1) * linearInterpolation(x1, x2, xe, y2, mapLayer);
		return r;
	}

	private double linearInterpolation(int x1, int x2, int xe, int y, double[][] map) {
		return ((double) (x2 - xe)) / (x2 - x1) * map[x1][y] + ((double) (xe - x1)) / (x2 - x1) * map[x2][y];
	}

	public void printWorldTopographie() {
		printWorldNum(topografie);
	}
	
	public void printWorldNum(double[][] map) {
		// int[][] map = this.topografie;
		for (int i = 0; i < map.length; i += 1) {
			for (int j = 0; j < map.length; j += 1) {

			//	System.out.format("%f", map[i][j]);
				System.out.print( map[i][j]+"\t");
			}
			System.out.println();
		}
	}

	public void printWorldPictuer() {
		double[][] map = this.topografie;
		for (int i = 0; i < map.length; i += 1) {
			for (int j = 0; j < map.length; j += 1) {

				if (map[i][j] < 0) {
					if (map[i + 1][j] > 15) {
						System.out.print("~É…~");
					} else {
						System.out.print("~~~");
					}
				} else if (map[i][j] == 0 || map[i][j] == 1) {
					if (map[i + 1][j] > 15) {
						System.out.print(".É….");
					} else {
						System.out.print(".;.");
					}

				} else if (map[i][j] >= 2 && map[i][j] <= 5) {
					if (map[i + 1][j] > 15) {
						System.out.print("\"É…\"");
					} else {
						System.out.print("\"*\"");
					}

				} else if (map[i][j] >= 6 && map[i][j] <= 15) {
					if (map[i + 1][j] > 15) {
						System.out.print("-É…-");
					} else {
						System.out.print("âˆ©Î©âˆ©");
					}

				} else {

					if (map[i + 1][j] > 15) {
						System.out.print("/É…\\");
					} else {
						System.out.print("/ \\");
					}
				}
			}
			System.out.println();
		}
	}

	public double getHight(int x, int y) {

		if (x < 0 || y < 0 || x >= topografie.length) {
			return 0;
		}
		if (y >= topografie[0].length) {
			return 0;
		}
		return topografie[x][y];

	}

	/*
	 * get number of neighbors that are equal ore less than height of this point
	 * alias water can flow here
	 */
	public int getSmalerEqualNeighborCount(int x, int y) {
		int count = 0;
		double p, n, o, s, w;
		p = getHight(x, y);
		n = getHight(x - 1, y);
		o = getHight(x, y + 1);
		s = getHight(x - 1, y);
		w = getHight(x, y - 1);

		if (p >= n) {
			count++;
		}
		if (p >= o) {
			count++;
		}
		if (p >= s) {
			count++;
		}
		if (p >= w) {
			count++;
		}

		return count;
	}

	/*
	 * TODO:does not work DO NOT USE! Returning number of minimal direction 0: North
	 * 1:East 2:south 3:west
	 *
	 * - 0 -
	 * 
	 * 3 * 1
	 * 
	 * - 2 -
	 * 
	 */
	public int getMinNeighborDirection(int x, int y) {
		double n, o, s, w;
		n = getHight(x - 1, y);
		o = getHight(x, y + 1);
		s = getHight(x - 1, y);
		w = getHight(x, y - 1);

		if (n <= o && n <= s && n <= w) {
			return 0;
		} else if (o <= n && o <= s && o <= w)
			return 1;
		else if (s <= n && s <= o && s <= w)
			return 2;
		else
			return 3;
	}

	public double getMaxNeighborHeight(int x, int y) {
		return max(max(getHight(x + 1, y), getHight(x - 1, y)), max(getHight(x, y + 1), getHight(x, y - 1)));
	}

	public double getMinNeighborHeight(int x, int y) {
		return min(min(getHight(x + 1, y), getHight(x - 1, y)), min(getHight(x, y + 1), getHight(x, y - 1)));
	}

	private double max(double d, double e) {
		if (d >= e)
			return d;
		return e;
	}

	private double min(double d, double e) {
		if (d >= e)
			return e;
		return d;
	}
	public void erosion() {
		for (int x = 0; x < this.size; x++) {
			for (int y = 0; y < this.size; y++) {

				if (topografie[x][y] < -20) {
					watermap.remove(x,y);
				}	
				if (watermap.getWaterDepth(x, y)>0.1) {
				topografie[x][y] = topografie[x][y]
						-( 0.5 / Math.pow(Math.pow(watermap.getWaterDepth(x, y) - 0.5, 2) + 1, 20)*4); // watermap.wasser[x][y]*10;
				}
			}
		}

	}

	public void drawFXgc(GraphicsContext gc) {
		for (int i = 0; i < this.getSize(); i++) {
			for (int j = 0; j < this.getSize(); j++) {
				gc.setFill(Color.AQUA);
				// grayscale
				// gc.getPixelWriter().setColor(i, j, new Color(world.getHight(i, j)/1024d+0.5,
				// world.getHight(i, j)/1024d+0.5, world.getHight(i, j)/1024d+0.5, 1.0));
				gc.getPixelWriter().setColor(i, j, Colormap.getHeightMapedColor(this.getHight(i, j) / 1024d + 0.5));
				if (this.getHight(i, j) % 10 == 0) {
					// gc.getPixelWriter().setColor(i, j, Color.rgb(255, 0, 0, 1.0));
				}
				if (this.getWoodMap()[i][j] == 1 && this.getHight(i, j) > 20 && this.getHight(i, j) < 100
						&& this.getStoneMap()[i][j] == 0) {
					// gc.getPixelWriter().setColor(i, j,Colormap.mix(Color.BLACK,
					// Colormap.getHeightMapedColor(this.getHight(i, j) / 1024d + 0.5), 0.5));
				}
				if (this.getStoneMap()[i][j] == 1 && this.getHight(i, j) > 20 && this.getHight(i, j) < 100) {
					// gc.getPixelWriter().setColor(i, j, Color.LIGHTGRAY);
				}
				if (this.watermap.getWaterDepth(i, j) != 0) {
					// gc.getPixelWriter().setColor(i,
					// j,Colormap.mix(Colormap.getWaterMapedColor(watermap.getWaterDepth(i,
					// j)),Colormap.getHeightMapedColor(this.getHight(i, j) / 1024d + 0.5), 0.5));
				}

			}
		}

	}

	

	public class BrokenJunkException extends Exception {
		public BrokenJunkException(String message) {
			super(message);
		}
	}

}
// final static ModelBuilder modelBuilder = new ModelBuilder();
// final static Model water = modelBuilder.createBox(1f, 1f, 1f,
// new Material(ColorAttribute.createDiffuse(new Color(0 / 255f, 0 / 255f, 255 /
// 255f, 0.5f))),
// Usage.Position | Usage.Normal);
// final static Model sand = modelBuilder.createBox(1f, 1f, 1f,
// new Material(ColorAttribute.createDiffuse(Color.YELLOW)), Usage.Position |
// Usage.Normal);;
// final static Model grass = modelBuilder.createBox(1f, 1f, 1f,
// new Material(ColorAttribute.createDiffuse(Color.GREEN)), Usage.Position |
// Usage.Normal);;
// final static Model wood = modelBuilder.createBox(1f, 1f, 1f,
// new Material(ColorAttribute.createDiffuse(new Color(0 / 255f, 100 / 255f, 0 /
// 255f, 1))),
// Usage.Position | Usage.Normal);
// final static Model stone = modelBuilder.createBox(1f, 1f, 1f,
// new Material(ColorAttribute.createDiffuse(Color.GRAY)), Usage.Position |
// Usage.Normal);
// final static Model snow = modelBuilder.createBox(1f, 1f, 1f,
// new Material(ColorAttribute.createDiffuse(new Color(230f / 255f, 230f / 255f,
// 255f / 255f, 1))),
// Usage.Position | Usage.Normal);
//
// public Array<ModelCache> createJunkCache(int x, int z, int junksize) throws
// BrokenJunkException {
//
// // create
// ModelCache cache = new ModelCache();
// ModelInstance waterBlock = new ModelInstance(water);
// ModelInstance sandBlock = new ModelInstance(sand);
// ModelInstance grassBlock = new ModelInstance(grass);
// ModelInstance woodBlock = new ModelInstance(wood);
// ModelInstance stoneBlock = new ModelInstance(stone);
// ModelInstance snowBlock = new ModelInstance(snow);

// cache.begin();
// // TODO:later i - x ; j -y for area relativ to (0,0,0) ;
// for (int i = x; i < x + junksize; i++) {
// for (int j = z; j < z + junksize; j++) {
// int bis = 0;
// int von = getMinNeighbor(i, j);
// if (getHight(i, j) <= getMinNeighbor(i, j)) {
// bis = getHight(i, j);
// von = getHight(i, j) - 1;
// } else {
// bis = getHight(i, j);
// }
// for (; von < bis; von++) {
// if (getHight(i, j) < -0) {
// waterBlock.transform.set(i, von, j, 0, 0, 0, 0);
// cache.add(waterBlock);
// } else if (getHight(i, j) >= 0 && getHight(i, j) <= 4) {
// sandBlock.transform.set(i, von, j, 0, 0, 0, 0);
// cache.add(sandBlock);
// // graß
// } else if (getHight(i, j) >= 5 && getHight(i, j) <= 10) {
// grassBlock.transform.set(i, von, j, 0, 0, 0, 0);
// cache.add(grassBlock);
// // graß wald lake
// } else if (getHight(i, j) >= 11 && getHight(i, j) <= 60) {
// if (lakeMap[i][j] == 1) {
// waterBlock.transform.set(i, von, j, 0, 0, 0, 0);
// cache.add(waterBlock);
// } else if (woodMap[i][j] == 0) {
// grassBlock.transform.set(i, von, j, 0, 0, 0, 0);
// cache.add(grassBlock);
// } else {
// woodBlock.transform.set(i, von, j, 0, 0, 0, 0);
// cache.add(woodBlock);
// }
// // stone graß
// } else if (getHight(i, j) >= 61 && getHight(i, j) <= 75) {
// if (stoneMap[i][j] == 0) {
// grassBlock.transform.set(i, von, j, 0, 0, 0, 0);
// cache.add(grassBlock);
// } else {
// stoneBlock.transform.set(i, von, j, 0, 0, 0, 0);
// cache.add(stoneBlock);
// }
// } else if (getHight(i, j) >= 76 && getHight(i, j) <= 85) {
// stoneBlock.transform.set(i, von, j, 0, 0, 0, 0);
// cache.add(stoneBlock);
// } else if (getHight(i, j) >= 86 && getHight(i, j) <= 95) {
// if (stoneMap[i][j] == 0) {
// stoneBlock.transform.set(i, von, j, 0, 0, 0, 0);
// cache.add(stoneBlock);
// } else {
// snowBlock.transform.set(i, von, j, 0, 0, 0, 0);
// cache.add(snowBlock);
// }
// } else {
// snowBlock.transform.set(i, von, j, 0, 0, 0, 0);
// cache.add(snowBlock);
// }
// }
// }
// }
// // dispose
//
// try {
// cache.end();
// } catch (IllegalArgumentException e) {
// System.err.println("Broken junk:I try to fix it. Junksize = " + junksize);
// if (junksize <= 1) {
// // throw new BrokenJunkException("Fail to create minimal Junk");
// System.err.println("Fail to create minimal Junk");
// return null;
// }
//
// Array<ModelCache> junkFragmente = new Array<ModelCache>();
// junkFragmente.addAll(createJunkCache(x, z, junksize / 2));
// junkFragmente.addAll(createJunkCache(x + junksize / 2, z, junksize / 2));
// junkFragmente.addAll(createJunkCache(x, z + junksize / 2, junksize / 2));
// junkFragmente.addAll(createJunkCache(x + junksize / 2, z + junksize / 2,
// junksize / 2));
//
// ModelCache junkFragmentCache = new ModelCache();
// junkFragmentCache.begin();
// if (junkFragmente.size == 0)
// return null;
// junkFragmentCache.add(junkFragmente);
//
// try {
// junkFragmentCache.end();
// } catch (IllegalArgumentException e2) {
// // TODO: handle exception
// System.err.println(
// "faild to kombine junk fragments. Junksize = " + junksize + "\nInstead
// returning fragments");
// junkFragmente.shrink();
// return junkFragmente;
// }
// Array<ModelCache> qwer = new Array<ModelCache>();
// qwer.add(junkFragmentCache);
// qwer.shrink();
// return qwer;
// }
// System.out.println("junk OK");
// Array<ModelCache> qwert = new Array<ModelCache>();
// qwert.add(cache);
// qwert.shrink();
// return qwert;
//
// }
