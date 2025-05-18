package acsse.csc3a.gui;

import java.awt.image.BufferedImage;
import java.io.File;

import acsse.csc3a.analyser.AbstractObserver;
import acsse.csc3a.analyser.Result;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Scale;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;


public class WaterQualityPane extends BorderPane implements AbstractObserver {

	private Button loadFolderButton;
	private HBox topMenu;
	private Stage primaryStage;
	private ImageView fullImageView;
	private Label labelResults;
	private VBox thumbnailGallery;
	public Button analyzeButton;
	public static final String IMAGES_DIR = "data/images/";
	
	public WaterQualityPane(Stage primaryStage) {

		this.primaryStage = primaryStage;

		this.setPadding(new Insets(10));
		Timeline timeline = new Timeline();
		timeline.setCycleCount(Animation.INDEFINITE);

		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(30), event -> {
			long time = System.currentTimeMillis();
			double shift = (time % 10000) / 100.0; // smooth cycle every 10 seconds

			// Optional: sinusoidal motion for a fluid feel
			double angle = (shift % 100) / 100.0 * 360;
			double x = 50 + 50 * Math.sin(Math.toRadians(angle));
			double y = 50 + 50 * Math.cos(Math.toRadians(angle));

			this.setStyle(String.format(
					"-fx-background-color: linear-gradient(from %.0f%% %.0f%% to %.0f%% %.0f%%, #FFDEE9, #B5FFFC);", x,
					y, 100 - x, 100 - y));
		}));

		timeline.play();

		setTopMenu();
		setLeftThumbnailGallery();
		setCenter();

		loadThumbnails(new File(IMAGES_DIR));
	}

	private void setTopMenu() {
		loadFolderButton = new Button("📁 Load Image Folder");

		loadFolderButton.setStyle(
				"-fx-background-color: #4A90E2; -fx-cursor: hand; -fx-padding: 10 20 10 20; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 5;");

		loadFolderButton.setOnAction(e -> loadImageFolder(primaryStage));
		setHover(loadFolderButton);

		topMenu = new HBox(10);
		topMenu.setPadding(new Insets(10));
		topMenu.setAlignment(Pos.BASELINE_LEFT);
		HBox.setMargin(loadFolderButton, new Insets(0, 0, 0, 5)); // top, right, bottom, **left**
		Label heading = new Label("Water Quality Detector");
		heading.setFont(Font.font("Verdana", FontWeight.BOLD, FontPosture.ITALIC, 16));

		Region spacerLeft = new Region();
		Region spacerRight = new Region();
		HBox.setHgrow(spacerLeft, Priority.ALWAYS);
		HBox.setHgrow(spacerRight, Priority.ALWAYS);

		topMenu.getChildren().addAll(loadFolderButton, spacerLeft, heading, spacerRight);

		setTop(topMenu);
	}

	private void setLeftThumbnailGallery() {
		thumbnailGallery = new VBox(10);
		thumbnailGallery.setPadding(new Insets(10));
		thumbnailGallery.setAlignment(Pos.TOP_CENTER);
		thumbnailGallery.setStyle("-fx-background-color: transparent;");

		ScrollPane scrollPane = new ScrollPane(thumbnailGallery);
		scrollPane.setPrefWidth(200);
		scrollPane.setFitToWidth(true);
		scrollPane.setStyle("-fx-background: transparent; -fx-border-color: transparent;");
		scrollPane.addEventFilter(ScrollEvent.SCROLL, e -> {
			double delta = e.getDeltaY();
			scrollPane.setVvalue(scrollPane.getVvalue() - delta / 1000);
			e.consume();
		});
		this.setLeft(scrollPane);
	}

	private void setCenter() {
		fullImageView = new ImageView();
		fullImageView.setFitWidth(350);
		fullImageView.setFitHeight(300);
		fullImageView.setPreserveRatio(true);
		fullImageView.setCache(true);
		fullImageView.setSmooth(true);
		fullImageView.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);");

		StackPane imagePane = new StackPane(fullImageView);
		imagePane.setPadding(new Insets(10));
		imagePane.setMinHeight(300);
		imagePane.setStyle("-fx-border-color: black; -fx-border-width: 1px; -fx-border-radius: 3px; -fx-padding: 5px;");

		analyzeButton = new Button("🔍 Analyze Image");
		analyzeButton.setStyle(
				"-fx-background-color: #27AE60; -fx-cursor: hand; -fx-padding: 10 20 10 20; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");

		setHover(analyzeButton);

		labelResults = new Label("Select an image to analyze.");
		labelResults.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

		// You can make it indeterminate (spinning) or determinate (showing progress)
		
		VBox rightPane = new VBox(20, imagePane, analyzeButton, labelResults);
		rightPane.setAlignment(Pos.BOTTOM_CENTER);
		rightPane.setPadding(new Insets(20, 40, 20, 40));
		this.setCenter(rightPane);

		analyzeButton.setOnAction(e -> {
			labelResults.setText("Analyzing image (this mat take a while)...");
			this.labelResults.setTextFill(Color.BLACK);
		});
	}

	// Load fixed-size thumbnails into the gallery without white space
	/*
	 * public void loadThumbnails() { thumbnailGallery.getChildren().clear(); for
	 * (String imagePath : imagePaths) { Image image = new Image(imagePath);
	 * ImageView thumbView = new ImageView(image); thumbView.setFitWidth(140);
	 * thumbView.setFitHeight(100); thumbView.setPreserveRatio(false);
	 * thumbView.setSmooth(true); thumbView.setCache(true); thumbView.
	 * setStyle("-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 5, 0, 0, 1);"
	 * );
	 * 
	 * setHover(thumbView); thumbView.setOnMouseClicked(event ->
	 * showFullImage(imagePath, event));
	 * thumbnailGallery.getChildren().add(thumbView); } }
	 */

	// Show full image on the right when thumbnail is clicked
	private void showFullImage(String imagePath, MouseEvent event) {
		if (event.getClickCount() == 1) {
			Image image = new Image(imagePath);
			fullImageView.setImage(image);
			labelResults.setText("Ready to analyze selected image.");
		}
	}

	private void loadImageFolder(Stage stage) {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Select Image Folder");

		File folder = directoryChooser.showDialog(stage);

		loadThumbnails(folder);
	}

	private void loadThumbnails(File folder) {
		if (folder != null && folder.isDirectory()) {
			thumbnailGallery.getChildren().clear();

			File[] imageFiles = folder.listFiles(file -> file.isFile() && (file.getName().endsWith(".png")
					|| file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg")));

			if (imageFiles != null && imageFiles.length != 0) {
				for (File file : imageFiles) {
					Image image = new Image(file.toURI().toString());
					ImageView thumbView = new ImageView(image);
					thumbView.setFitWidth(120);
					thumbView.setFitHeight(90);
					thumbView.setPreserveRatio(false);

					thumbView.setOnMouseClicked(event -> showFullImage(file.toURI().toString(), event));
					thumbnailGallery.getChildren().add(thumbView);
				}
			} else {
				showMessage("Could not load images", "Not images were found in this directory or an error occured",
						AlertType.INFORMATION);
			}
		} else {
			showMessage("Not a directory", "This is not a valid directory", AlertType.ERROR);
		}
	}

	private void showMessage(String title, String message, AlertType alertType) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null); // Optional: remove header
		alert.setContentText(message);
		alert.showAndWait();
	}

	private void setHover(Node node) {
		Scale scale = new Scale(1, 1, 0, 0); // Default scale
		node.getTransforms().add(scale);

		node.setOnMouseEntered(e -> {
			scale.setX(1.05);
			scale.setY(1.05);
			node.setCursor(Cursor.HAND);
			node.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.15)));
		});

		node.setOnMouseExited(e -> {
			scale.setX(1);
			scale.setY(1);
			node.setCursor(Cursor.DEFAULT);
			node.setEffect(null);
		});
	}

	public BufferedImage getImage() {
		this.fullImageView.getImage();
		Image image = fullImageView.getImage();

		if (image == null) {
			showMessage("No image selected", "Please select an image to analyse", AlertType.WARNING);
			return null;
		}

		// Convert the JavaFX Image to BufferedImage
		BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);

		if (bufferedImage == null) {
			showMessage("Error converting image", "The selected image could not be converted to a readable format",
					AlertType.ERROR);
			return null;
		}

		return bufferedImage;
	}

	@Override
	public void update(Result result) {
		// TODO Auto-generated method stub

		this.labelResults.setText(
				"analyzation complete: category: " + result.getCategory() + " quality: " + result.getQuality());

		this.labelResults.setTextFill(result.textColour());
	}
	
}
