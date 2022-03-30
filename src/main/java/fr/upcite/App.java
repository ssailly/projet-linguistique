package fr.upcite;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.stage.Stage;

public class App extends Application {
	@Override
	public void start(Stage primaryStage) {
		Group root=new Group();
		Scene scene=new Scene(root, 300, 300);

		Path path=new Path();
		path.setStrokeWidth(1);
		path.setStroke(Color.BLACK);
		
		EventHandler<MouseEvent> mouseHandler=new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if(mouseEvent.getEventType()==MouseEvent.MOUSE_PRESSED){
					path.getElements().add(new MoveTo(mouseEvent.getX(), mouseEvent.getY()));
				}
				else if(mouseEvent.getEventType()==MouseEvent.MOUSE_DRAGGED){
					path.getElements().add(new LineTo(mouseEvent.getX(), mouseEvent.getY()));
				}
			}
		};

		scene.setOnMousePressed(mouseHandler);
		scene.setOnMouseDragged(mouseHandler);

		root.getChildren().add(path);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}