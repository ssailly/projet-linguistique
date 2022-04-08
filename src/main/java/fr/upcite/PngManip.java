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
}
