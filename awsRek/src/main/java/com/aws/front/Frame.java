package com.aws.front;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.S3Object;
import com.aws.rek.FaceRecognition;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.stage.Stage;

public class Frame extends Application {
	FaceRecognition rec;
	public static final String S3_BUCKET = "aacpbucket";
	private String currentPic = null;

	public static Image getImageUtil(String bucket, String key) {
		return new Image().withS3Object(new S3Object().withBucket(bucket).withName(key));
	}

	private String getWinnerPic(String key) {
		try (InputStream input = new FileInputStream("winners.properties")) {
			Properties prop = new Properties();
			// load a properties file
			prop.load(input);
			return prop.getProperty(key);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private Set<String> getWinners() {
		try (InputStream input = new FileInputStream("winners.properties")) {
			Properties prop = new Properties();
			// load a properties file
			prop.load(input);
			return prop.stringPropertyNames();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void start(Stage stage) throws Exception {
		rec = new FaceRecognition();

		Parent root = FXMLLoader.load(getClass().getResource("frame.fxml"));
		Scene scene = new Scene(root);

		ComboBox filterList = (ComboBox) scene.lookup("#filtercombo");
		filterList.getItems().addAll(new String[] { "Square", "Silverstar", "Goldstar", "Dog", "Dog1", "Troll", "Hearts" });
		filterList.valueProperty().addListener((obs, oldItem, newItem) -> {
			if (newItem == null) {

			} else {
				switch (newItem.toString()) {
				case "Square":
					rec.setFilterImg(null);
					break;
				default:
					rec.setFilterImg("/" + newItem.toString() + ".png");
					break;
				}
			}

		});

		ComboBox winnerList = (ComboBox) scene.lookup("#personcombo");
		winnerList.getItems().addAll(getWinners());
		winnerList.valueProperty().addListener((obs, oldItem, newItem) -> {
			if (newItem == null) {

			} else {
				currentPic = newItem.toString();
				Button startrek = (Button) scene.lookup("#rekbtn");
				if (startrek.getText().contains("Stop")) {
					rec.setSourceImg(getImageUtil(S3_BUCKET, getWinnerPic(currentPic)));
					System.out.println("New: " + newItem.toString());
				}
			}
		});

		Slider confSlider = (Slider) scene.lookup("#confbar");
		Label confLabel = (Label) scene.lookup("#confText");
		confSlider.valueProperty().addListener(new ChangeListener() {
			@Override
			public void changed(ObservableValue arg0, Object arg1, Object arg2) {
				confLabel.textProperty().setValue("Confidence: " + String.valueOf((int) confSlider.getValue()));
				rec.getDetector().setSimilarityThreshold((float) confSlider.getValue());
			}
		});

		Button startcam = (Button) scene.lookup("#startbtn");
		EventHandler<ActionEvent> buttonHandler = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (startcam.getText().contains("Start")) {
					startcam.setText("Stop Camera");
					rec.showCam(true);
				} else {
					startcam.setText("Start Camera");
					rec.showCam(false);
				}
			}
		};
		startcam.setOnAction(buttonHandler);

		Button startrek = (Button) scene.lookup("#rekbtn");
		EventHandler<ActionEvent> buttonHandler2 = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (currentPic != null) {
					if (startrek.getText().contains("Start")) {
						startrek.setText("Stop Rekognition");
						rec.setSourceImg(getImageUtil(S3_BUCKET, getWinnerPic(currentPic)));
					} else {
						startrek.setText("Start Rekognition");
						rec.setSourceImg(null);
					}
				}
			}
		};
		startrek.setOnAction(buttonHandler2);

		stage.setTitle("AWS Face Recognition");
		stage.setScene(scene);
		stage.show();
	}

	@Override
	public void stop() {
		rec.setRun(false);
		System.out.println("Stage is closing");
	}

	public static void main(String[] args) {
		launch(args);
	}

}
