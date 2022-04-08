package fr.upcite;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.stage.Stage;

public class App extends Application {
	private int firstStrokeOutside=-1, lastStrokeInside=-1;

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
				PngManip.saveImage(drawingPane);
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
				PngManip.saveImage(drawingPane);
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

	public static void catchException(Exception e, String ... path) {
		for(String p:path) System.err.println("Path: "+p);
		e.printStackTrace();
	}

	public static void main(String[] args) {
		launch(args);
	}
}