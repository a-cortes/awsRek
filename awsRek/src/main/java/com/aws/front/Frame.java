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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

public class Frame extends Application {
	FaceRecognition rec;
	public static final String S3_BUCKET = "aacpbucket";
	
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
		ComboBox winnerList = (ComboBox) scene.lookup("#personcombo");
		winnerList.getItems().addAll(getWinners());
		winnerList.valueProperty().addListener((obs, oldItem, newItem) -> {
			if (newItem == null) {
                
            } else {
            	rec.setSourceImg(getImageUtil(S3_BUCKET, getWinnerPic(newItem.toString())));
            	System.out.println("New: "+newItem.toString());
            }
            
        });

		stage.setTitle("AWS Face Recognition");
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
