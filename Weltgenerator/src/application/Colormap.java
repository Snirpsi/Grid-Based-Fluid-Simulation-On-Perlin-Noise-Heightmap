package application;

import javafx.scene.paint.Color;

public class Colormap {

	private static final boolean worldInColor = true;

	public static Color mix(Color ac, Color bc, double ratio) {

		double r = ac.getRed() * (1.0 - ratio) + bc.getRed() * ratio;
		double g = ac.getGreen() * (1.0 - ratio) + bc.getGreen() * ratio;
		double b = ac.getBlue() * (1.0 - ratio) + bc.getBlue() * ratio;
		double a = ac.getOpacity() * (1.0 - ratio) + bc.getOpacity() * ratio;

		if (r < 0 | r > 1 | g < 0 | g > 1 | b < 0 | b > 1) {
			System.err.println("Invalid Color " + r + " " + b + " " + g);
		}

		return new Color(r, g, b, a);

	}

	public static Color getHeightMapedColor(double x) {

		if (!worldInColor) {

			return mix(Color.BLACK, Color.WHITE, x);
		} else {

			// COLOR
			if (x > 360) {
				return Color.hsb(360, 1.0, 1.0);
			}
			if (x < 0) {
				return Color.hsb(0, 1.0, 1.0);
			}
			if (x < 0.45) {
				return mix(Color.BLACK, Color.BLUE, x / 0.45);
			} else if (x < 0.5) {
				return mix(Color.BLUE, Color.AQUA, (x - 0.45) / 0.05);
			} else if (x < 0.55) {
				return mix(Color.YELLOW, Color.GREEN, (x - 0.5) / 0.05);
			} else if (x < 0.6) {
				return mix(Color.GREEN, Color.DARKGREEN, (x - 0.55) / 0.05);
			} else if (x < 0.65) {
				return mix(Color.DARKGREEN, Color.LIGHTGRAY, (x - 0.6) / 0.05);
			} else
				return Color.WHITE;

		}
	}

	public static Color getWaterMapedColor(double x) {
		if (x <= 0.0) {
			return Color.WHITE;
		}

		return mix(Color.rgb(0, 0, 0, 0), Color.BLACK, -(1 / (x + 1.0)) + 1.0);

		/*
		 * if (x <= 1) { return mix (Color.WHITE, Color.LIGHTGRAY,x); } if (x <= 10) {
		 * return mix (Color.LIGHTGRAY,Color.GRAY,(x-1)/9); } if (x <= 100) { return mix
		 * (Color.GRAY,Color.BLACK,(x-10)/90); } return Color.BLACK;
		 */
	}

}
