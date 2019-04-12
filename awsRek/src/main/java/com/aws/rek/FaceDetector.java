package com.aws.rek;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

import javax.imageio.ImageIO;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.BoundingBox;
import com.amazonaws.services.rekognition.model.CompareFacesMatch;
import com.amazonaws.services.rekognition.model.CompareFacesRequest;
import com.amazonaws.services.rekognition.model.CompareFacesResult;
import com.amazonaws.services.rekognition.model.ComparedFace;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.util.IOUtils;

/**
 * @author Fmtrain
 *
 */
/**
 * @author Fmtrain
 *
 */
public class FaceDetector {

	AmazonRekognition rekognitionClient;
	
	private Float similarityThreshold = 80F;

	public Float getSimilarityThreshold() {
		return similarityThreshold;
	}

	public void setSimilarityThreshold(Float similarityThreshold) {
		this.similarityThreshold = similarityThreshold;
	}

	public FaceDetector() {
		rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();
	}

	/*
	 * 
	 * To use S3 source image
	 */
	public Rectangle detectFace(Image source, BufferedImage targetImage, int panelWidth, int panelHeight) {
		
		String sourceImage = "C:/Users/Fmtrain/Desktop/img/me.jpg";
		// String targetImage = "C:/Users/Fmtrain/Desktop/img/me.jpg";
		ByteBuffer sourceImageBytes = null;
		ByteBuffer targetImageBytes = null;

		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(targetImage, "jpg", os);
			InputStream is = new ByteArrayInputStream(os.toByteArray());
			targetImageBytes = ByteBuffer.wrap(IOUtils.toByteArray(is));
		} catch (Exception e) {

		}

		// Load source and target images and create input parameters
		try (InputStream inputStream = new FileInputStream(new File(sourceImage))) {
			sourceImageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
		} catch (Exception e) {
			System.out.println("Failed to load source image " + sourceImage);
			System.exit(1);
		}

		// Image source = new Image().withBytes(sourceImageBytes);
		Image target = new Image().withBytes(targetImageBytes);

		CompareFacesRequest request = new CompareFacesRequest().withSourceImage(source).withTargetImage(target)
				.withSimilarityThreshold(similarityThreshold);

		// Call operation

		long startTime = System.currentTimeMillis();
		CompareFacesResult compareFacesResult = rekognitionClient.compareFaces(request);
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("REQUEST TIME: " + totalTime);

		// Display results
		List<CompareFacesMatch> faceDetails = compareFacesResult.getFaceMatches();
		for (CompareFacesMatch match : faceDetails) {
			ComparedFace face = match.getFace();
			BoundingBox position = face.getBoundingBox();
			System.out.println("Face at " + position.getLeft().toString() + " " + position.getTop() + " matches with "
					+ face.getConfidence().toString() + "% confidence.");

			return new Rectangle((int) (position.getLeft() * panelWidth), (int) (position.getTop() * panelHeight),
					(int) (position.getWidth() * panelWidth), (int) (position.getHeight() * panelHeight));
		}

		return null;
	}

	/*
	 * To use local source image
	 */
	public Rectangle detectFace(String sourceImage, BufferedImage targetImage, int panelWidth, int panelHeight) {
		
		//String sourceImage = "C:/Users/Fmtrain/Desktop/img/me.jpg";
		// String targetImage = "C:/Users/Fmtrain/Desktop/img/me.jpg";
		ByteBuffer sourceImageBytes = null;
		ByteBuffer targetImageBytes = null;

		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(targetImage, "jpg", os);
			InputStream is = new ByteArrayInputStream(os.toByteArray());
			targetImageBytes = ByteBuffer.wrap(IOUtils.toByteArray(is));
		} catch (Exception e) {

		}

		// Load source and target images and create input parameters
		try (InputStream inputStream = new FileInputStream(new File(sourceImage))) {
			sourceImageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
		} catch (Exception e) {
			System.out.println("Failed to load source image " + sourceImage);
			System.exit(1);
		}

		Image source = new Image().withBytes(sourceImageBytes);
		Image target = new Image().withBytes(targetImageBytes);

		CompareFacesRequest request = new CompareFacesRequest().withSourceImage(source).withTargetImage(target)
				.withSimilarityThreshold(similarityThreshold);

		// Call operation

		long startTime = System.currentTimeMillis();
		CompareFacesResult compareFacesResult = rekognitionClient.compareFaces(request);
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("REQUEST TIME: " + totalTime);

		// Display results
		List<CompareFacesMatch> faceDetails = compareFacesResult.getFaceMatches();
		for (CompareFacesMatch match : faceDetails) {
			ComparedFace face = match.getFace();
			BoundingBox position = face.getBoundingBox();
			System.out.println("Face at " + position.getLeft().toString() + " " + position.getTop() + " matches with "
					+ face.getConfidence().toString() + "% confidence.");

			return new Rectangle((int) (position.getLeft() * panelWidth), (int) (position.getTop() * panelHeight),
					(int) (position.getWidth() * panelWidth), (int) (position.getHeight() * panelHeight));
		}
		return null;

	}
}
