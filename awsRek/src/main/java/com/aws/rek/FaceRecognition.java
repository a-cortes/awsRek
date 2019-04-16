package com.aws.rek;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.amazonaws.services.rekognition.model.Image;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

/**
 * Paint troll smile on all detected faces.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class FaceRecognition extends JFrame implements Runnable, WebcamPanel.Painter {

	private static final long serialVersionUID = 1L;
	private static final Executor EXECUTOR = Executors.newSingleThreadExecutor();
	private static final Stroke STROKE = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f,
			new float[] { 1.0f }, 0.0f);

	private Webcam webcam = null;
	private WebcamPanel.Painter painter = null;
	private Rectangle face = null;
	private BufferedImage filterImg = null;
	private String filterImgS = null;
	private WebcamPanel panel = null;
	private FaceDetector detector;
	private Image sourceImg = null;
	private boolean show = false;
	private boolean run = true;

	public boolean isRun() {
		return run;
	}

	public void setRun(boolean run) {
		this.run = run;
	}

	public BufferedImage getFilterImg() {
		return filterImg;
	}

	public void setFilterImg(String img) {
		this.filterImg = null;
		this.filterImgS = null;
		try {
			this.filterImg = ImageIO.read(getClass().getResourceAsStream(img));
			this.filterImgS = img;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void showCam(boolean show) {
		this.show = show;
		setVisible(show);
	}

	public Image getSourceImg() {
		return sourceImg;
	}

	public void setSourceImg(Image sourceImg) {
		this.sourceImg = sourceImg;
	}

	public FaceDetector getDetector() {
		return detector;
	}

	public FaceRecognition() throws IOException {

		super();
		detector = new FaceDetector();
//		filterImg = ImageIO.read(getClass().getResourceAsStream("/troll-face.png"));
//		filterImg = ImageIO.read(getClass().getResourceAsStream("/goku1.png"));

		List<Webcam> cams = Webcam.getWebcams();
		for (Webcam cam : cams) {
//			System.out.println(cam.getName());
			if (cam.getName().contains("Logitech")) {
				webcam = cam;
			}
		}
		if (webcam == null) {
			webcam = Webcam.getDefault();
		}

		Dimension resolution = new Dimension(1920, 1080); // HD 720p
		webcam.setCustomViewSizes(new Dimension[] { resolution }); // register custom resolution
		webcam.setViewSize(resolution);

		// webcam.setViewSize(WebcamResolution.VGA.getSize());
		// webcam.open(true);

		panel = new WebcamPanel(webcam, false);
		panel.setPreferredSize(WebcamResolution.VGA.getSize());
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
		showCam(false);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		showCam(false);
		EXECUTOR.execute(this);
	}

	@Override
	public void run() {
		while (run) {
			if (!webcam.isOpen()) {
				return;
			}

			if (sourceImg != null) {
				System.out.println("Detecting");
				try {
					face = detector.detectFace(sourceImg, webcam.getImage(), panel.getWidth(), panel.getHeight());
				} catch (Exception e) {
					face = null;
				}
			} else {
				face = null;
			}
		}
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
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

		if (face == null) {
			return;
		}

		Rectangle bounds = face;

		int dx = (int) (0.1 * bounds.width);
		int dy = (int) (0.2 * bounds.height);
		int x = (int) bounds.x - dx;
		int y = (int) bounds.y - dy;
		int w = (int) bounds.width + 2 * dx;
		int h = (int) bounds.height + dy;

		if (filterImgS != null) {
			if (filterImgS.contains("star")) {
				x -= (int) (bounds.width / 2);
				y -= (int) (bounds.height / 2);
				w += (int) (bounds.width / 1);
				h += (int) (bounds.height / 1);
			}
//		System.out.println(x + " : " + y + " : " + w + " : " + h);
		}else {
			
				g2.setStroke(STROKE);
				g2.setColor(Color.RED);
				g2.drawRect(x, y, w, h);
		}

		g2.drawImage(filterImg, x, y, w, h, null);
	}

	public static void main(String[] args) throws IOException {
		new FaceRecognition();
	}
}
