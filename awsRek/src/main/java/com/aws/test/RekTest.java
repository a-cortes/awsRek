package com.aws.test;

import java.io.IOException;

import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.S3Object;
import com.aws.rek.FaceRecognition;

public class RekTest {
	
	public static final String S3_BUCKET = "aacpbucket";

	public static Image getImageUtil(String bucket, String key) {
		return new Image().withS3Object(new S3Object().withBucket(bucket).withName(key));
	}
	
	public static void main(String[] args) throws Exception {
		FaceRecognition rec = new FaceRecognition();
//		rec.setSourceImg(getImageUtil(S3_BUCKET, "mehd.jpg"));
//		Thread.sleep(10000);
//		rec.setSourceImg(null);
//		System.out.println("Sleeping");
//		Thread.sleep(5000);
//		
//		rec.setSourceImg(getImageUtil(S3_BUCKET, "melq.jpg"));
//		Thread.sleep(10000);
//		rec.setSourceImg(null);
//		System.out.println("Sleeping");
//		Thread.sleep(5000);
//		
//		System.out.println("Change Threshold");
//		rec.setSourceImg(getImageUtil(S3_BUCKET, "mehd.jpg"));
//		rec.getDetector().setSimilarityThreshold(70f);
//		
//		Thread.sleep(10000);
		System.out.println("Change Threshold");
		rec.setSourceImg(getImageUtil(S3_BUCKET, "mehd.jpg"));
		rec.getDetector().setSimilarityThreshold(20f);
		
	}
}
