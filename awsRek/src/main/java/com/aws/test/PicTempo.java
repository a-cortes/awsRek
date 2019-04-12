package com.aws.test;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;

import org.openimaj.image.processing.face.detection.DetectedFace;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.github.sarxos.webcam.WebcamPanel.Painter;

/**
 * Paint troll smile on all detected faces.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class PicTempo extends JFrame implements Runnable, WebcamPanel.Painter {

	private static final long serialVersionUID = 1L;

	private static final Executor EXECUTOR = Executors.newSingleThreadExecutor();
	private static final HaarCascadeDetector detector = new HaarCascadeDetector();
	private static final Stroke STROKE = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f,
			new float[] { 1.0f }, 0.0f);

	private Webcam webcam = null;
	private WebcamPanel.Painter painter = null;
	private List<DetectedFace> faces = null;
	private BufferedImage troll = null;

	public PicTempo() throws IOException {

		super();

		List<Webcam> cams = Webcam.getWebcams();
		for (Webcam cam : cams) {
			// System.out.println(cam.getName());
			if (cam.getName().contains("Logitech")) {
				webcam = cam;
			}
		}
		if (webcam == null) {
			webcam = Webcam.getDefault();
		}
		//webcam.setViewSize(WebcamResolution.VGA.getSize());
		
		webcam.setCustomViewSizes(new Dimension[] { WebcamResolution.HD.getSize() }); // register custom size
		webcam.setViewSize(WebcamResolution.HD.getSize()); // set size
		webcam.open(true);
		

		WebcamPanel panel = new WebcamPanel(webcam, false);
		panel.setPreferredSize(WebcamResolution.HD.getSize());
		panel.setPainter(this);
		panel.setFPSDisplayed(true);
		panel.setFPSLimited(true);
		panel.setFPSLimit(20);
		panel.setPainter(this);
		panel.start();

		painter = panel.getDefaultPainter();

		add(panel);

		setTitle("Face Detector Example");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);

		EXECUTOR.execute(this);
	}

	@Override
	public void run() {
		while (true) {
			if (!webcam.isOpen()) {
				return;
			}
			try {
				Thread.sleep(6000);
				BufferedImage img = webcam.getImage();
//				playSound("camerasound.wav");
				File outputfile = new File("image"+((int)(Math.random()*10000000))+".jpg");
				ImageIO.write(img, "jpg", outputfile);
				System.out.println("PIC taken: "+outputfile.getAbsolutePath());
			} catch (InterruptedException | IOException  e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	}

	@Override
	public void paintPanel(WebcamPanel panel, Graphics2D g2) {
		if (painter != null) {
			painter.paintPanel(panel, g2);
		}
	}

	@Override
	public void paintImage(WebcamPanel panel, BufferedImage image, Graphics2D g2) {
		if (painter != null) {
			painter.paintImage(panel, image, g2);
		}
	}

	public static void main(String[] args) throws IOException {
		new PicTempo();
	}
	
	
}
