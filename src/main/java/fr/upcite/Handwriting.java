package fr.upcite;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Handwriting {
	private static final String DIR = Paths.get(".").toAbsolutePath().normalize().toString() + "/resources/handwriting/";
	private static final String TRG = "trg.png";

	private static void saveMat(Mat mat) {
		MatOfByte mob = new MatOfByte();
		Imgcodecs.imencode(".png", mat, mob);
		byte ba[] = mob.toArray();
		try {
			BufferedImage img = ImageIO.read(new ByteArrayInputStream(ba));
			ImageIO.write(img, "png", new File(DIR + TRG));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String src = args.length > 0 ? args[0] : DIR + "src.png";
		nu.pattern.OpenCV.loadLocally();
		Mat mat = Imgcodecs.imread(src, Imgcodecs.IMREAD_GRAYSCALE);
		Mat matBlackAndWhite = new Mat();
		Imgproc.adaptiveThreshold(mat, matBlackAndWhite, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 15, 40);
		saveMat(matBlackAndWhite);
	}
}
