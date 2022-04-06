package fr.upcite;

import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.io.File;
import java.io.IOException;
import java.lang.System.Logger;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;

public class App extends Application {
	private int firstStrokeOutside=-1, lastStrokeInside=-1;
	private static final int IMG_SIZE=109;

	@Override
	public void start(Stage primaryStage) {
		Pane drawingPane=new Pane();
		BorderPane proposalsPane=new BorderPane();
		drawingPane.setMinSize(200, 200);
		drawingPane.setMaxSize(200, 200);
		drawingPane.setStyle("-fx-background-color: transparent; ");
		
		proposalsPane.setMinSize(200, 200);
		proposalsPane.setMaxSize(200, 200);
		proposalsPane.setTop(new Label("Proposals"));

		Path path=new Path();
		//path.setStrokeWidth(1);
		path.setStroke(Color.BLACK);
		//path.setOpacity(1);

		Button clear=new Button("Clear");
		clear.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				path.getElements().clear();
				saveImage(drawingPane);
			}
		});

		proposalsPane.setBottom(new HBox(clear));
		
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
				saveImage(drawingPane);
			}
		});
		drawingPane.getChildren().add(path);

		BorderPane borderPane=new BorderPane();
		borderPane.setLeft(drawingPane);
		borderPane.setRight(proposalsPane);
		Scene scene=new Scene(borderPane);
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
	}

	private static void saveImage(Pane drawingPane){
		SnapshotParameters sp=new SnapshotParameters();
		sp.setFill(Color.TRANSPARENT);
		WritableImage img = drawingPane.snapshot(sp, null);
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

	public static void main(String[] args) {
		launch(args);
	}
}