package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.BoundingBox;
import com.amazonaws.services.rekognition.model.CompareFacesMatch;
import com.amazonaws.services.rekognition.model.CompareFacesRequest;
import com.amazonaws.services.rekognition.model.CompareFacesResult;
import com.amazonaws.services.rekognition.model.ComparedFace;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.util.IOUtils;

public class Test1 {

	public static final String S3_BUCKET = "aacpbucket";

	private static Image getImageUtil(String bucket, String key) {
		return new Image().withS3Object(new S3Object().withBucket(bucket).withName(key));
	}

	public static void main(String[] args) throws InterruptedException {
		Float similarityThreshold = 80F;
		String sourceImage = "C:/Users/acortes/Desktop/img/me.jpg";
		String targetImage = "C:/Users/acortes/Desktop/img/me3.jpg";
		
		ByteBuffer sourceImageBytes = null;
		ByteBuffer targetImageBytes = null;

		AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();

		// Load source and target images and create input parameters
		try (InputStream inputStream = new FileInputStream(new File(sourceImage))) {
			sourceImageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
		} catch (Exception e) {
			System.out.println("Failed to load source image " + sourceImage);
			System.exit(1);
		}
		try (InputStream inputStream = new FileInputStream(new File(targetImage))) {
			targetImageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
		} catch (Exception e) {
			System.out.println("Failed to load target images: " + targetImage);
			System.exit(1);
		}

//		Image source = new Image().withBytes(sourceImageBytes);
		Image target = new Image().withBytes(targetImageBytes);

		Image source = getImageUtil(S3_BUCKET, "me.jpg");
//		Image target = getImageUtil(S3_BUCKET, "me3.jpg");

		CompareFacesRequest request = new CompareFacesRequest().withSourceImage(source).withTargetImage(target)
				.withSimilarityThreshold(similarityThreshold);

		// Call operation

		for (int i = 0; i < 10; i++) {
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
				System.out.println("Face at " + position.getLeft().toString() + " " + position.getTop()
						+ " matches with " + face.getConfidence().toString() + "% confidence.");

			}

			List<ComparedFace> uncompared = compareFacesResult.getUnmatchedFaces();

			System.out.println("There was " + uncompared.size() + " face(s) that did not match");
			System.out.println("Source image rotation: " + compareFacesResult.getSourceImageOrientationCorrection());

			Thread.sleep(333);

		}
	}
}
