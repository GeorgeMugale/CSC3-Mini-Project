package acsse.csc3a.gui;

import java.awt.image.BufferedImage;
import java.io.File;

import acsse.csc3a.analyser.AbstractObserver;
import acsse.csc3a.analyser.Result;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
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

/**
 * a custom Pane while UI components for water analyzing functionality
 */
public class WaterQualityPane extends BorderPane implements AbstractObserver {

	private Button loadFolderButton;
	private HBox topMenu;
	private Stage primaryStage;
	private ImageView fullImageView;
	private Label labelCatResults;
	private Label labelMatchResults;
	private VBox thumbnailGallery;
	public Button analyzeButton;
	public static final String IMAGES_DIR = "data/images/";
	private ProgressBar progressBar;

	/**
	 * Constructs a Pane with the primary stage for file chooser
	 * 
	 * @param primaryStage
	 */
	public WaterQualityPane(Stage primaryStage) {

		this.primaryStage = primaryStage;

		this.setPadding(new Insets(10));

		// Create a new Timeline animation
		Timeline timeline = new Timeline();

		// Set the animation to repeat indefinitely
		timeline.setCycleCount(Animation.INDEFINITE);

		// Add a keyframe that triggers every 30 milliseconds
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(30), event -> {

			// get the current systems time
			long time = System.currentTimeMillis();

			/*
			 * Calculate a shifting value that cycles every 10 seconds (10000ms) Divided by
			 * 100 to get a smaller range (0-100)
			 */
			double shift = (time % 10000) / 100.0; // smooth cycle every 10 seconds

			/*
			 * Create circular motion using trigonometric functions: - angle cycles through
			 * 360 degrees - x and y positions follow sine/cosine waves (circular path)
			 */
			double angle = (shift % 100) / 100.0 * 360;
			double x = 50 + 50 * Math.sin(Math.toRadians(angle));
			double y = 50 + 50 * Math.cos(Math.toRadians(angle));

			/*
			 * Apply a gradient background that changes based on the x,y positions The
			 * gradient goes from position (x,y) to (100-x,100-y) Using two colors: #FFDEE9
			 * (light pink) and #B5FFFC (light cyan)
			 */
			this.setStyle(String.format(
					"-fx-background-color: linear-gradient(from %.0f%% %.0f%% to %.0f%% %.0f%%, #FFDEE9, #B5FFFC);", x,
					y, 100 - x, 100 - y));
		}));

		// start the timeline with the keyframes
		timeline.play();

		// set GUI components
		setTopMenu();
		setLeftThumbnailGallery();
		setCenter();

		loadThumbnails(new File(IMAGES_DIR));
	}

	/**
	 * Initializes the label text
	 */
	public void initLabelText() {
		labelCatResults.setText("Analyzing image (this may take a while)...");
		labelMatchResults.setText("");
		labelMatchResults.setTextFill(Color.BLACK);
	}

	/**
	 * Sets controls of the top menu
	 */
	private void setTopMenu() {
		loadFolderButton = new Button("📁 Load Image Folder");
		loadFolderButton.setStyle(
				"-fx-background-color: #4A90E2; -fx-cursor: hand; -fx-padding: 10 20 10 20; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 5;");

		// when the load image button is pressed call load filde functionality
		loadFolderButton.setOnAction(e -> loadImageFolder());
		setHover(loadFolderButton);

		topMenu = new HBox(10);
		topMenu.setPadding(new Insets(10));
		topMenu.setAlignment(Pos.BASELINE_LEFT);
		HBox.setMargin(loadFolderButton, new Insets(0, 0, 0, 5)); // top, right, bottom, **left**
		Label heading = new Label("Water Quality Detector");
		heading.setFont(Font.font("Verdana", FontWeight.BOLD, FontPosture.ITALIC, 16));

		// make sure heading is in the middle og the hbox
		Region spacerLeft = new Region();
		Region spacerRight = new Region();
		HBox.setHgrow(spacerLeft, Priority.ALWAYS);
		HBox.setHgrow(spacerRight, Priority.ALWAYS);

		// add controls
		topMenu.getChildren().addAll(loadFolderButton, spacerLeft, heading, spacerRight);

		setTop(topMenu);
	}

	/**
	 * Set the left thumbnail gallery
	 */
	private void setLeftThumbnailGallery() {
		thumbnailGallery = new VBox(10);
		thumbnailGallery.setPadding(new Insets(10));
		thumbnailGallery.setAlignment(Pos.TOP_CENTER);
		thumbnailGallery.setStyle("-fx-background-color: transparent;");

		ScrollPane scrollPane = new ScrollPane(thumbnailGallery);
		scrollPane.setPrefWidth(200);
		scrollPane.setFitToWidth(true);
		scrollPane.setStyle("-fx-background: transparent; -fx-border-color: transparent;");

		// when the scroll pane is scrolled
		scrollPane.addEventFilter(ScrollEvent.SCROLL, e -> {
			// smooth scrolling
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

		labelCatResults = new Label("Select an image to analyze.");
		labelCatResults.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

		labelMatchResults = new Label("");
		labelMatchResults.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

		// create a progress bar instance
		progressBar = new ProgressBar();

		// add controlls to the right pane
		VBox rightPane = new VBox(20, imagePane, progressBar, analyzeButton, labelCatResults, labelMatchResults);
		rightPane.setAlignment(Pos.BOTTOM_CENTER);
		rightPane.setPadding(new Insets(20, 40, 20, 40));
		this.setCenter(rightPane);
	}

	// Show full image on the right when thumbnail is clicked
	private void showFullImage(String imagePath, MouseEvent event) {
		// loads an image onto an ImageView using the path
		if (event.getClickCount() == 1) {
			Image image = new Image(imagePath);
			fullImageView.setImage(image);
			labelCatResults.setText("Ready to analyze selected image.");
		}
	}

	/**
	 * Loads images from a directory
	 */
	private void loadImageFolder() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Select Image Folder");

		File folder = directoryChooser.showDialog(primaryStage);

		loadThumbnails(folder);

	}

	/**
	 * Loads images from a specified directory onto the left scroll pane
	 * 
	 * @param folder
	 */
	private void loadThumbnails(File folder) {
		if (folder != null && folder.isDirectory()) {
			thumbnailGallery.getChildren().clear();

			// create a filter to only show valid image files
			File[] imageFiles = folder.listFiles(file -> file.isFile() && (file.getName().endsWith(".png")
					|| file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg")));

			// if they are images that pass th filter
			if (imageFiles != null && imageFiles.length != 0) {
				for (File file : imageFiles) {
					// create an mage and load it on to the image view
					Image image = new Image(file.toURI().toString());
					ImageView thumbView = new ImageView(image);
					thumbView.setFitWidth(120);
					thumbView.setFitHeight(90);
					thumbView.setPreserveRatio(false);

					// when the image is clicked it should show the full image on the right main
					// image area
					thumbView.setOnMouseClicked(event -> showFullImage(file.toURI().toString(), event));
					thumbnailGallery.getChildren().add(thumbView);
				}
			} else {
				showMessage("Could not load images", "No images were found in this directory or an error occured",
						AlertType.INFORMATION);
			}
		} else {
			showMessage("Not a directory", "This is not a valid directory", AlertType.ERROR);
		}
	}

	/**
	 * Shows a message using an alert
	 * 
	 * @param title     the title of the message
	 * @param message   the main text body of the message
	 * @param alertType the alert type of the message
	 */
	private void showMessage(String title, String message, AlertType alertType) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null); // Optional: remove header
		alert.setContentText(message);
		alert.showAndWait();
		progressBar.setVisible(true);
	}

	/**
	 * Sets a Control to hover when it is hovered over
	 * 
	 * @param node
	 */
	private void setHover(Node node) {
		Scale scale = new Scale(1, 1, 0, 0); // Default scale
		// add scale to the control
		node.getTransforms().add(scale);

		// when the mouse has hovered
		node.setOnMouseEntered(e -> {
			// set the scale
			scale.setX(1.05);
			scale.setY(1.05);
			// turn the cursor into a pointer
			node.setCursor(Cursor.HAND);
			// show shadow effect
			node.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.15)));
		});

		// when the mouse has exited
		node.setOnMouseExited(e -> {
			// reset the sale and cursor and shadow effect
			scale.setX(1);
			scale.setY(1);
			node.setCursor(Cursor.DEFAULT);
			node.setEffect(null);
		});
	}

	/**
	 * Gets the image that is currently loaded into the full image view
	 * 
	 * @return a buffered image instance of the image
	 */
	public BufferedImage getImage() {
		this.fullImageView.getImage();
		// get the image
		Image image = fullImageView.getImage();

		// if there is not image
		if (image == null) {
			showMessage("No image selected", "Please select an image to analyse", AlertType.WARNING);
			return null;
		}

		// Convert the JavaFX Image to BufferedImage
		BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);

		// if the image could not be converted
		if (bufferedImage == null) {
			showMessage("Error converting image", "The selected image could not be converted to a readable format",
					AlertType.ERROR);
			return null;
		}

		// return the image
		return bufferedImage;
	}

	@Override
	public void updateMatch(Result result) {
		// TODO Auto-generated method stub
		Platform.runLater(() -> {
			labelMatchResults.setText("Similarity detection complete: " + result.getQuality());
			try {
				labelMatchResults.setTextFill(result.textColour());

			} catch (Exception e) {
				e.printStackTrace();
			}

		});
	}

	@Override
	public void updateCat(Result result) {
		// TODO Auto-generated method stub
		Platform.runLater(() -> {
			labelCatResults.setText("Classification Complete: " + result.getCategory());
		});
	}

	@Override
	public void update(String result) {
		// TODO Auto-generated method stub
		Platform.runLater(() -> {
			String[] tokens = result.split("---");

			AlertType updateType = tokens[0] == "ERR" ? AlertType.ERROR : AlertType.INFORMATION;

			showMessage(tokens[1], tokens[2], updateType);
		});

	}

	/**
	 * Performs a task handled within a lambda function which realizes he CallBack
	 * interface
	 * 
	 * @param callback the operations that need to be performed separate from the UI
	 *                 thread, this method must accept a {@link #BiConsumer} and
	 *                 return nothing
	 */
	public void performTask(CallBack callback) {

		analyzeButton.setDisable(true);
		progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS); // show animation
		progressBar.setVisible(true);

		initLabelText();

		Task<Void> task = new Task<>() {

			/**
			 * This method defines the operations to be performed in the background thread
			 */
			@Override
			protected Void call() throws Exception {
				// pass the update progress method to be able to keep track
				callback.apply(this::updateProgress);
				return null;
			}

			/**
			 * When the task has succeeded
			 */
			@Override
			protected void succeeded() {
				progressBar.setVisible(false);
				analyzeButton.setDisable(false);
			}

			/**
			 * When the task has failed
			 */
			@Override
			protected void failed() {
				System.out.println("Task failed: " + getException());
				progressBar.setVisible(false);
			}
		};

		// bind the task to the progress bar's progress property
		progressBar.progressProperty().bind(task.progressProperty());

		// run the task on its own thread
		new Thread(task).start();
		System.out.println("all good");
	}

}
