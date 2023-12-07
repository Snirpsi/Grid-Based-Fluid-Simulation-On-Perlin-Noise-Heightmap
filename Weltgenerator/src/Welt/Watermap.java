package Welt;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import com.sun.javafx.iio.common.SmoothMinifier;

import application.Colormap;

public class Watermap {
	double wasser[][];
	int size;

	public Watermap(int size) {
		this.size = size;
		this.wasser = new double[size][size];
	}

	public double getWaterDepth(int x, int y) {
		if (x < 0) {
			return 0;
		}
		if (x >= wasser.length)
			return 0;
		if (y < 0) {
			return 0;
		}
		if (y >= wasser.length)
			return 0;

		return wasser[x][y];

	}

	public double getWaterPlusLandHeight(World w, int x, int y) {
		return getWaterDepth(x, y) + w.getHight(x, y);
	}

	public void rain() {
		rain(0.01, 1.0);
	}

	public void rain(double amount, double coverage) {
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (Math.random() < coverage) {
					wasser[i][j] += amount;
				}
			}
		}
	}

	public void rainSet(int x, int y) {
		wasser[x][y] += 1.0;
	}

	public void step(World world) {
		double[][] buffer = new double[size][size];
//		for (int i = 0; i < buffer.length; i++) {
//			for (int j = 0; j < buffer.length; j++) {
//				buffer[i][j] = 0.0;
//			}
//		}

		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				/*
				 	double factor = 1 + world.getSmalerEqualNeighborCount(x, y);

					double WaterHeight = getWaterPlusLandHeight(world, x, y);

					double waterFlowVolume = getFlowVolume(world, x, y) / factor;
					//wasser an stelle behalten dass nicht fließen kann
					insertBuffer(buffer, x, y, waterFlowVolume + (wasser[x][y] - getFlowVolume(world, x, y)));
				 */
				
				
				if (/* wasser[x][y] < 0.1 || */ world.getHight(x, y) < -2.0) {
					wasser[x][y] = 0.0;
				}
				int numSmalerNeighbors = 1 + getSmalerNeighborCount(world, x, y);

				double flowWater = getFlowVolume(world, x, y);
								
				double h = getWaterPlusLandHeight(world, x, y);
				
				insertBuffer(buffer, x, y, getWaterDepth(x, y)-flowWater + (flowWater / numSmalerNeighbors));
				// norden
				if (h > getWaterPlusLandHeight(world, x - 1, y)) {// n
					insertBuffer(buffer, x - 1, y, flowWater / numSmalerNeighbors);
				}
				if (h > getWaterPlusLandHeight(world, x, y + 1)) {// o
					insertBuffer(buffer, x, y + 1, flowWater / numSmalerNeighbors);
				}
				if (h > getWaterPlusLandHeight(world, x + 1, y)) {// s
					insertBuffer(buffer, x + 1, y, flowWater / numSmalerNeighbors);
				}
				if (h > getWaterPlusLandHeight(world, x, y - 1)) {// w
					insertBuffer(buffer, x, y - 1, flowWater / numSmalerNeighbors);
				}
				if (h > getWaterPlusLandHeight(world, x - 1, y + 1)) {// no
					insertBuffer(buffer, x - 1, y + 1, flowWater / numSmalerNeighbors);
				}
				if (h > getWaterPlusLandHeight(world, x + 1, y + 1)) {// os
					insertBuffer(buffer, x + 1, y + 1, flowWater / numSmalerNeighbors);
				}
				if (h > getWaterPlusLandHeight(world, x + 1, y - 1)) {// sw
					insertBuffer(buffer, x + 1, y - 1, flowWater / numSmalerNeighbors);
				}
				if (h > getWaterPlusLandHeight(world, x - 1, y - 1)) {// wn
					insertBuffer(buffer, x - 1, y - 1, flowWater / numSmalerNeighbors);
				}
			}
		}
		for (int i = 0; i < buffer.length; i++) {
			for (int j = 0; j < buffer.length; j++) {
				wasser[i][j] = buffer[i][j];
			}
		}
	}

	public double getFlowVolume(World world, int x, int y) {
		if (world.getHight(x, y) > getSmalestNeighborDepth(world, x, y)) {
			return wasser[x][y];
		} else if (getWaterPlusLandHeight(world, x, y) >= getSmalestNeighborDepth(world, x, y)) {
			return getWaterPlusLandHeight(world, x, y) - getSmalestNeighborDepth(world, x, y);
		}
		return 0;
	}

	public double getSmalestNeighborDepth(World world, int x, int y) {
		double[] depth = new double[8];// p, n, o, s, w, no, os, sw, wn;
		depth[0] = getWaterPlusLandHeight(world, x - 1, y);
		depth[1] = getWaterPlusLandHeight(world, x, y + 1);
		depth[2] = getWaterPlusLandHeight(world, x + 1, y);
		depth[3] = getWaterPlusLandHeight(world, x, y - 1);
		depth[4] = getWaterPlusLandHeight(world, x - 1, y + 1);
		depth[5] = getWaterPlusLandHeight(world, x + 1, y + 1);
		depth[6] = getWaterPlusLandHeight(world, x + 1, y - 1);
		depth[7] = getWaterPlusLandHeight(world, x - 1, y - 1);

		double min = Double.MAX_VALUE;
		for (int i = 0; i < depth.length; i++) {
			if (depth[i] < min) {
				min = depth[i];
			}
		}
		return min;
	}

	public int getSmalerNeighborCount(World world, int x, int y) {
		int count = 0;
		double p, n, o, s, w, no, os, sw, wn;
		p = getWaterPlusLandHeight(world, x, y);
		n = getWaterPlusLandHeight(world, x - 1, y);
		o = getWaterPlusLandHeight(world, x, y + 1);
		s = getWaterPlusLandHeight(world, x + 1, y);
		w = getWaterPlusLandHeight(world, x, y - 1);
		no = getWaterPlusLandHeight(world, x - 1, y + 1);
		os = getWaterPlusLandHeight(world, x + 1, y + 1);
		sw = getWaterPlusLandHeight(world, x + 1, y - 1);
		wn = getWaterPlusLandHeight(world, x - 1, y - 1);

		if (p > n) {
			count++;
		}
		if (p > o) {
			count++;
		}
		if (p > s) {
			count++;
		}
		if (p > w) {
			count++;
		}
		if (p > no) {
			count++;
		}
		if (p > os) {
			count++;
		}
		if (p > sw) {
			count++;
		}
		if (p > wn) {
			count++;
		}
		return count;
	}
	/*
	public void stepOld(World world) {
		double[][] buffer = new double[size][size];
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				if (wasser[x][y] > 0.1 /* && world.getHight(x, y) > 0.0 ) {
					double factor = 1 + world.getSmalerEqualNeighborCount(x, y);

					double WaterHeight = getWaterPlusLandHeight(world, x, y);

					double waterFlowVolume = getFlowVolume(world, x, y) / factor;
					//wasser an stelle behalten dass nicht fließen kann
					insertBuffer(buffer, x, y, waterFlowVolume + (wasser[x][y] - getFlowVolume(world, x, y)));

					if (WaterHeight > getWaterPlusLandHeight(world, x - 1, y)) {
						insertBuffer(buffer, x - 1, y, waterFlowVolume);
					}
					if (WaterHeight > getWaterPlusLandHeight(world, x, y + 1)) {
						insertBuffer(buffer, x, y + 1, waterFlowVolume);
					}
					if (WaterHeight > getWaterPlusLandHeight(world, x + 1, y)) {
						insertBuffer(buffer, x + 1, y, waterFlowVolume);
					}
					if (WaterHeight > getWaterPlusLandHeight(world, x, y - 1)) {
						insertBuffer(buffer, x, y - 1, waterFlowVolume);
					}

					if (WaterHeight > getWaterPlusLandHeight(world, x - 1, y + 1)) {
						insertBuffer(buffer, x - 1, y + 1, waterFlowVolume);
					}
					if (WaterHeight > getWaterPlusLandHeight(world, x + 1, y + 1)) {
						insertBuffer(buffer, x + 1, y + 1, waterFlowVolume);
					}
					if (WaterHeight > getWaterPlusLandHeight(world, x + 1, y - 1)) {
						insertBuffer(buffer, x + 1, y - 1, waterFlowVolume);
					}
					if (WaterHeight > getWaterPlusLandHeight(world, x - 1, y - 1)) {
						insertBuffer(buffer, x - 1, y - 1, waterFlowVolume);
					}

				}
				wasser[x][y] = 0.0;
			}
		}	
 			
		for (int i = 0; i < buffer.length; i++) {
			for (int j = 0; j < buffer.length; j++) {
				wasser[i][j] = buffer[i][j];
			}
		}

	}
	*/
	private double[][] insertBuffer(double[][] buffer, int x, int y, double value) {
		if (x < 0) {
			x = 0;
		}
		if (y < 0) {
			y = 0;
		}
		if (x >= buffer.length) {
			x = buffer.length - 1;
		}
		if (y >= buffer.length) {
			y = buffer.length - 1;
		}
		buffer[x][y] += value;
		return buffer;
	}
	public double[][] smooth(int iterations) {
		if (iterations <= 0) {
			return wasser;
		}

		double[][] ret = new double[wasser.length][wasser.length];
		for (int i = 1; i < wasser.length - 1; i++) {
			for (int j = 1; j < wasser.length - 1; j++) {
				ret[i][j] = (int) ((wasser[i][j] + wasser[i][j + 1] + wasser[i + 1][j]
						+ wasser[i + 1][j + 1] + wasser[i][j - 1] + wasser[i - 1][j]
						+ wasser[i - 1][j - 1] + wasser[i + 1][j - 1] + wasser[i - 1][j + 1]) / 9.0);
			}
		}
		wasser = ret;

		return smooth(iterations - 1);
	}
	public void addWater(int x , int y , double value){
		try {
			wasser[x][y] += value;
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	public void drawFXgc(GraphicsContext gc) {
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (wasser[i][j] != 0.0) {
					gc.getPixelWriter().setColor(i, j, Colormap.getWaterMapedColor(wasser[i][j]));
				}

			}
		}
	}

	public void remove(int x, int y) {
		wasser[x][y] = 0.0;
		
	}
}
