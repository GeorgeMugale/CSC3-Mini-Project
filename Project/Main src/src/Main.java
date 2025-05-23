import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.stage.*;

import acsse.csc3a.gui.WaterQualityPane;
import acsse.csc3a.analyser.ImageAnalyser;

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Water Quality Detector");

		WaterQualityPane root = new WaterQualityPane(primaryStage);

		ImageAnalyser imageAnalyser = new ImageAnalyser();
		imageAnalyser.attach(root);

		root.analyzeButton.setOnAction(e -> {
			root.performTask((updateProgress) -> {
				imageAnalyser.analyze(root.getImage(), updateProgress);
			});
		});

		// Setup scene and stage
		Scene scene = new Scene(root, 900, 550);
		String css = getClass().getResource("style.css").toExternalForm();
		scene.getStylesheets().add(css);

		primaryStage.setScene(scene);
		primaryStage.show();

	}

}
