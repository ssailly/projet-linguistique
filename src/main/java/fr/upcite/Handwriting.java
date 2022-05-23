package fr.upcite;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Handwriting {
	private static final String DIR = Paths.get(".").toAbsolutePath().normalize().toString() + "/resources/handwriting/";
	private static final String DEFAULT_TRG = DIR + "trg.png";
	private static ArrayList<Kanji> kanji=Kanji.createList();

	private static void saveMat(Mat mat, String trg) {
		saveMat(mat, trg, false);
	}

	private static void saveMat(Mat mat, String trg, boolean transparent) {
		if(trg.equals("")) trg = DEFAULT_TRG;
		MatOfByte mob = new MatOfByte();
		Imgcodecs.imencode(".png", mat, mob);
		byte ba[] = mob.toArray();
		try {
			BufferedImage img = ImageIO.read(new ByteArrayInputStream(ba));
			if(transparent) img = PngManip.whiteToTransparent(img);
			ImageIO.write(img, "png", new File(trg));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String src = DIR + (args.length > 0 ? args[0].substring(0, args[0].length() - 4) : "src");
		nu.pattern.OpenCV.loadLocally();
		Mat orig = Imgcodecs.imread(src + ".png");
		Mat mat = new Mat();
		orig.copyTo(mat);
		Mat matGrey = new Mat();
		Imgproc.cvtColor(mat, matGrey, Imgproc.COLOR_BGR2GRAY);

		Mat canny = new Mat();
		Imgproc.Canny(matGrey, canny, 60, 60 * 3, 3, false);

		Mat cannyColor = new Mat();
		Imgproc.cvtColor(canny, cannyColor, Imgproc.COLOR_GRAY2BGR);

		Mat lines = new Mat();
		Imgproc.HoughLines(canny, lines, 1, Math.PI/180, 150);
		List<Integer> l = List.of(0, 1, 1999, 2000);
		ArrayList<ArrayList<Integer>> pts = new ArrayList<>();
		ArrayList<Integer> x = new ArrayList<>(), y = new ArrayList<>();
		x.add(0);
		y.add(0);
		pts.add(x);
		pts.add(y);
		for(int i = 0; i < lines.rows(); i++){
			double[] data = lines.get(i, 0);
			double rho = data[0], theta = data[1];
			double a = Math.cos(theta), b = Math.sin(theta);
			double x0 = a * rho, y0 = b * rho;
			Point p1 = new Point(Math.round(x0 + 1000 * (-b)), y0 + 1000 * a), p2 = new Point(Math.round(x0 - 1000 * (-b)), y0 - 1000 * a);
			
			Rect r = new Rect(p1, p2);
			if(l.contains(r.height) && l.contains(r.width)) {
				if(Math.abs((int)p1.x) != 1000) pts.get(0).add((int)p1.x);
				else pts.get(1).add((int)p1.y);
				Imgproc.line(mat, p1, p2, new Scalar(0, 255, 0), 2);
				Imgproc.line(cannyColor, p1, p2, new Scalar(0, 255, 0), 2);
			}
		}
		saveMat(mat, src + "_houghLines.png");
		saveMat(cannyColor, src + "_houghLinesCanny.png");
		for(ArrayList<Integer> list : pts) Collections.sort(list);

		int k = 0;
		ArrayList<String> roiFilenames = new ArrayList<>();
		for(int i = 0; i < pts.get(0).size() - 1; i++){
			for(int j = 0; j < pts.get(1).size() - 1; j++){
				int rowStart = pts.get(1).get(j), rowEnd = pts.get(1).get(j+1);
				int colStart = pts.get(0).get(i), colEnd = pts.get(0).get(i+1);
				int h1 = rowEnd - rowStart, h2 = colEnd - colStart;
				if(h1 < 1.1 * h2 && h1 > 0.9 * h2 && h1 > 5 && h2 > 5){
					Mat roi = orig.submat(rowStart, rowEnd, colStart, colEnd);
					Mat roiGrey = new Mat();
					Imgproc.cvtColor(roi, roiGrey, Imgproc.COLOR_BGR2GRAY);
					Mat roiBW = new Mat();
					Imgproc.adaptiveThreshold(roiGrey, roiBW, 255,Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 15, 40);
					String roiFilename = src + "_roi" + ++k + ".png";
					Mat roiScaled = new Mat();
					int size = PngManip.IMG_SIZE;
					boolean interpolationType = size < h1;//interpolation différente selon qu'il s'agisse d'un upscaling ou d'un downscaling
					Imgproc.resize(roiBW, roiScaled, new Size(size, size), 0, 0, interpolationType ? Imgproc.INTER_AREA : Imgproc.INTER_CUBIC);
					saveMat(roiScaled, roiFilename, true);
					roiFilenames.add(roiFilename);
				}
			}
		}
		try {
			File result = new File(src + "_output.txt");
			result.createNewFile();
			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(result), "UTF-8");

			for(String s : roiFilenames) {
				for(Kanji kj : kanji){
					kj.setSimilarity(PngManip.imagesSimilarity(s, PngManip.kanji_png+kj.filename));
				}
				Kanji sim = Collections.max(kanji, Kanji.getComparator());
				char toWrite = sim.kanji.charAt(0);
				if(sim.getSimilarity() < 0.5) toWrite = ' ';
				osw.write(toWrite);
				osw.flush();
			}
			osw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}