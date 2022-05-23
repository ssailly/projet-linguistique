package fr.upcite;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.shape.Path;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class PngManip {
	public static final int IMG_SIZE=109;
	public static final String output_path=Paths.get(".").toAbsolutePath().normalize().toString()+"/resources/image_output/image.png";
	public static final String kanji_png=Paths.get(".").toAbsolutePath().normalize().toString()+"/resources/kanji_png/";

	public static void saveImage(Pane drawingPane){
		SnapshotParameters sp=new SnapshotParameters();
		sp.setFill(Color.TRANSPARENT);
		WritableImage img = drawingPane.snapshot(sp, null);
		BufferedImage img2 = SwingFXUtils.fromFXImage(img, null);
		BufferedImage img3 = new BufferedImage(IMG_SIZE, IMG_SIZE, img2.getType());
		Graphics2D g = img3.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawImage(img2, 0, 0, IMG_SIZE, IMG_SIZE, 0, 0, img2.getWidth(), img2.getHeight(), null);
		/*AffineTransform affTrans=new AffineTransform();
		g.drawImage(img2, affTrans, null);*/
		g.dispose();
		try {
			ImageIO.write(img3, "png", new File(output_path));
		} catch (IOException ex) {
			//Logger.getLogger(GuiClass.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public static BufferedImage whiteToTransparent(BufferedImage img){
		BufferedImage res = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
		try {
			int[] pixels = img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());
			for(int i=0;i<pixels.length;i++){
				int color = pixels[i];
				int a = (color>>24)&0xFF, r = (color>>16)&0xFF, g = (color>>8)&0xFF, b = (color)&0xFF;
				if(r == 255 && g == 255 && b == 255) a = 0;
				pixels[i] = (a<<24) | (r<<16) | (g<<8) | (b);
			}
			res.setRGB(0, 0, img.getWidth(), img.getHeight(), pixels, 0, img.getWidth());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public static void whiteToTransparent(){
		try {
			BufferedImage bi = ImageIO.read(new File("src_roi1.png"));
			int[] pixels = bi.getRGB(0, 0, bi.getWidth(), bi.getHeight(), null, 0, bi.getWidth());

			for(int i=0;i<pixels.length;i++){
					int color = pixels[i];
					int a = (color>>24)&255;
					int r = (color>>16)&255;
					int g = (color>>8)&255;
					int b = (color)&255;

					if(r == 255 && g == 255 && b == 255){
							a = 0;
					}

					pixels[i] = (a<<24) | (r<<16) | (g<<8) | (b);
			}

			BufferedImage biOut = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
			biOut.setRGB(0, 0, bi.getWidth(), bi.getHeight(), pixels, 0, bi.getWidth());
			ImageIO.write(biOut, "png", new File("src_roiT.png"));
		} catch (Exception e) {
			//TODO: handle exception
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
					ArrayList<Integer> alphaAround=new ArrayList<>();
					for(int j=y-3;j<=y+3;j++){
						for(int i=x-3;i<=x+3;i++){
							if(i!=x && j!=y && i>0 && j>0 && i<w1 && j<h1){
								int rgb=img2.getRGB(i, j);
								alphaAround.add((rgb>>24)&0xFF);
							}
						}
					}
					if(alpha2!=0) {
						pertinent++;
						if(alpha1==0) diff++;
						for(Integer i:alphaAround){
							if(i!=0) diff-=1.0/alphaAround.size();
						}
					}
				}
			}
			double ratio=1-(diff/pertinent), res=ratio>1.0?1.0:ratio;
			return res;

		} catch(Exception e){
			App.catchException(e, f1.getAbsolutePath(), f2.getAbsolutePath());
		}
		return 0.0;
	}

	public static void main(String[] args) {
		//imagesSimilarity(PngManip.output_path, PngManip.kanji_png+"0608b.png");
		whiteToTransparent();
	}
}
