package com.aws.client;

import java.io.IOException;

import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.S3Object;
import com.aws.rek.FaceRecognition;

public class RekTest {
	
	public static final String S3_BUCKET = "aacpbucket";

	private static Image getImageUtil(String bucket, String key) {
		return new Image().withS3Object(new S3Object().withBucket(bucket).withName(key));
	}
	
	public static void main(String[] args) throws Exception {
		FaceRecognition rec = new FaceRecognition();
		rec.setSourceImg(getImageUtil(S3_BUCKET, "me.jpg"));
		Thread.sleep(10000);
		rec.setSourceImg(null);
		Thread.sleep(10000);
		rec.setSourceImg(getImageUtil(S3_BUCKET, "me.jpg"));
	}
}
