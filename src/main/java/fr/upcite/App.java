package fr.upcite;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

import java.io.File;
import java.io.IOException;
import java.lang.System.Logger;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.stage.Stage;

import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class App extends Application {
	private int firstStrokeOutside=-1, lastStrokeInside=-1;
	private static final int IMG_SIZE=109;

	@Override
	public void start(Stage primaryStage) {
		Pane drawingPane=new Pane(), proposalsPane=new Pane();
		drawingPane.setStyle("-fx-background-color: #B6B6B6");
		drawingPane.setMinSize(200, 200);
		drawingPane.setMaxSize(200, 200);
		proposalsPane.setMinSize(200, 200);
		proposalsPane.setMaxSize(200, 200);
		proposalsPane.getChildren().add(new Label("Proposals"));
		
		BorderPane borderPane=new BorderPane();
		borderPane.setLeft(drawingPane);
		borderPane.setRight(proposalsPane);
		Scene scene=new Scene(borderPane);
		drawingPane.setStyle("-fx-background-color: transparent; ");
		

		Path path=new Path();
		//path.setStrokeWidth(1);
		path.setStroke(Color.BLACK);
		//path.setOpacity(1);
		drawingPane.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				firstStrokeOutside=-1;
				path.getElements().add(new MoveTo(mouseEvent.getX(), mouseEvent.getY()));
			}
		});
		drawingPane.setOnMouseDragged(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent mouseEvent) {
				double x=mouseEvent.getX(), y=mouseEvent.getY();
				boolean inDrawingPane=x<drawingPane.getWidth() && y<drawingPane.getHeight();
				if(inDrawingPane && lastStrokeInside==-1) {
					path.getElements().add(new LineTo(x, y));
					firstStrokeOutside++;
				}
				else lastStrokeInside=firstStrokeOutside;
				//save image
				WritableImage img = drawingPane.snapshot(new SnapshotParameters(), null);
				BufferedImage img2 = SwingFXUtils.fromFXImage(img, null);
				BufferedImage img3 = new BufferedImage(IMG_SIZE, IMG_SIZE, img2.getType());
				Graphics2D g = img3.createGraphics();
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g.drawImage(img2, 0, 0, IMG_SIZE, IMG_SIZE, 0, 0, img2.getWidth(), img2.getHeight(), null);
				g.dispose();
				try {
					ImageIO.write(img3, "png", new File(Paths.get(".").toAbsolutePath().normalize().toString()+"/resources/image_output/image.png"));
				} catch (IOException ex) {
					//Logger.getLogger(GuiClass.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		});

		drawingPane.getChildren().add(path);
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}