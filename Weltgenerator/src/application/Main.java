package application;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;


import Welt.World;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

/*
 * differenzen dirchschnitt bilden bis kleinster teil nicht vom ganzen wasserstapel
 */

public class Main extends Application {

	public static final int WORLDSIZE = 512;
	int mouseX = 0;
	int mouseY = 0;
	int iteration = 0;

	boolean rumspielenFlag = false;

	@Override
	public void start(Stage primaryStage) {
		try {
			Random rd = new Random(System.currentTimeMillis());
			rd.nextInt();

			World world = new World(WORLDSIZE, 128, 255,rd.nextInt(1000000000) /*571900453 */);
			// world.smooth(5);
			System.out.println(world.getSeed());

			Canvas heightMap = new Canvas(WORLDSIZE, WORLDSIZE);
			GraphicsContext gchm = heightMap.getGraphicsContext2D();
			world.drawFXgc(gchm);

			BorderPane root = new BorderPane();
			root.setLeft(heightMap);

			Canvas water = new Canvas(WORLDSIZE, WORLDSIZE);
			world.getWatermap().drawFXgc(water.getGraphicsContext2D());
			root.setRight(water);

			Label label = new Label("test");

			water.setOnMouseMoved(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent event) {
					int x = (int) event.getX();
					int y = (int) event.getY();
					mouseX = x;
					mouseY = y;
				}
			});
			heightMap.setOnMouseMoved(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent event) {
					int x = (int) event.getX();
					int y = (int) event.getY();
					mouseX = x;
					mouseY = y;
				}
			});

			root.setBottom(label);

		 	//Button printshape = new Button("add Water to mouse position");
		 	
			//printshape.setOnAction((event) -> {
			//	rumspielenFlag = ! rumspielenFlag;
			//});

			CheckBox printshapeCheckBox = new CheckBox("Add Water to Mouse Position");

			printshapeCheckBox.setOnAction((event) -> {
			    rumspielenFlag = printshapeCheckBox.isSelected();
			});
			
			root.setCenter(printshapeCheckBox);

			Scene scene = new Scene(root, WORLDSIZE * 2 + 300, WORLDSIZE + 100);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();

			world.getWatermap().rain(1, 1.0);

			// world.printWorldTopographie();
			new AnimationTimer() {
				@Override
				public void handle(long currentNanoTime) {

					world.getWatermap().step(world);
					world.getWatermap().drawFXgc(water.getGraphicsContext2D());
					world.drawFXgc(gchm);

					world.getWatermap().rain(0.001, 1.0);

					// world.getWatermap().addWater(mouseX, mouseY, 100.0);

					iteration++;
					String s = "Terrain\n";
					s += Double.toString(world.getHight(mouseX, mouseY));
					s += "\nWaterDepth\t";
					s += world.getWatermap().getWaterDepth(mouseX, mouseY);
					s += "\nHeight\t";
					s += world.getWatermap().getWaterPlusLandHeight(world, mouseX, mouseY);
					s += "\nIteration\t";
					s += iteration;
					label.setText(s);

					if (rumspielenFlag) {
						
						world.getWatermap().addWater(mouseX, mouseY, 1000.0);
						// world.printWorldTopographie();
						// System.out.println();
						// System.out.println();
						// System.out.println();
					}
					
					if (iteration % 100 == 0 || (iteration > 100 && iteration % 10 == 0)) {
						// world.getWatermap().smooth(1);
						world.erosion();

						// print out world if button presst
						

						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
							// Do nothing
						}
					}
					if (false) {// iteration%10 == 0) {// Bild speichern
						// Heightmap Speichern
						WritableImage writableImage = new WritableImage(WORLDSIZE, WORLDSIZE);
						heightMap.snapshot(null, writableImage);
						File outFile = new File("kartendrucke\\Maps2\\height\\" + iteration + " heightMap.png");
						try {
							ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", outFile);
						} catch (IOException ex) {
							System.out.println(ex.getMessage());
						}

						// wasser speichern
						WritableImage writableImage2 = new WritableImage(WORLDSIZE, WORLDSIZE);
						water.snapshot(null, writableImage2);
						File outFile2 = new File("kartendrucke\\Maps2\\water\\" + iteration + " watermap.png");
						try {
							ImageIO.write(SwingFXUtils.fromFXImage(writableImage2, null), "png", outFile2);
						} catch (IOException ex) {
							System.out.println(ex.getMessage());
						}
					}

				}
			}.start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
