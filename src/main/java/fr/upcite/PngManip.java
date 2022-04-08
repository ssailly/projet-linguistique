package fr.upcite;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class PngManip {
	private static final int IMG_SIZE=109;

	public static void saveImage(Pane drawingPane){
		SnapshotParameters sp=new SnapshotParameters();
		sp.setFill(Color.TRANSPARENT);
		WritableImage img = drawingPane.snapshot(sp, null);
		BufferedImage img2 = SwingFXUtils.fromFXImage(img, null);
		BufferedImage img3 = new BufferedImage(IMG_SIZE, IMG_SIZE, img2.getType());
		Graphics2D g = img3.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawImage(img2, 0, 0, IMG_SIZE, IMG_SIZE, 0, 0, img2.getWidth(), img2.getHeight(), null);
		g.dispose();
		try {
			ImageIO.write(img3, "png", new File(Paths.get(".").toAbsolutePath().normalize().toString()+"/resources/image_output/image.png"));
		} catch (IOException ex) {
			//Logger.getLogger(GuiClass.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public static double imagesSimilarity(String path1, String path2){
		File f1=new File(path1), f2=new File(path2);
		double res=0.0;
		try{
			res=imagesSimilarity(f1, f2);
		} catch(Exception e){
			App.catchException(e, path1, path2);
		}
		return res;
	}

	//on ne compare que la transparence 
	private static double imagesSimilarity(File f1, File f2){
		try{
			BufferedImage img1=ImageIO.read(f1), img2=ImageIO.read(f2);
			int w1=img1.getWidth(), h1=img1.getHeight();
			int w2=img2.getWidth(), h2=img2.getHeight();
			if(w1!=w2 || h1!=h2){
				throw new IllegalArgumentException("Images dimensions mismatch");
			}
			double diff=0.0, pertinent=0.0;
			for(int y=0;y<h1;y++){
				for(int x=0;x<w1;x++){
					int rgb1=img1.getRGB(x, y), rgb2=img2.getRGB(x, y);
					int alpha1=(rgb1>>24)&0xFF, alpha2=(rgb2>>24)&0xFF;
					if(alpha1!=0 || alpha2!=0) pertinent++;
					if(alpha1!=alpha2) diff++;
				}
			}
			return 1-(diff/pertinent);

		} catch(Exception e){
			App.catchException(e, f1.getAbsolutePath(), f2.getAbsolutePath());
		}
		return 0.0;
	}

	
}
