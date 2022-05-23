package fr.upcite;

/*import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;*/

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.Scanner;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;


public class SVGtoPNG {
	/**
	 * Exécuter depuis la racine du projet
	 */
	private static void createUsableKanji(){
		String pathSrcDir=Paths.get(".").toAbsolutePath().normalize().toString()+"/resources/kanji_svg_src";
		String pathTrgDir=Paths.get(".").toAbsolutePath().normalize().toString()+"/resources/kanji_svg_trg";
		String pathPngDir=Paths.get(".").toAbsolutePath().normalize().toString()+"/resources/kanji_png";
		System.out.println(pathSrcDir);
		File srcDir=new File(pathSrcDir);
		File trgDir=new File(pathTrgDir);
		File pngDir=new File(pathPngDir);
		trgDir.mkdir();
		pngDir.mkdir();
		int i=1;
		for(File file:srcDir.listFiles()){
			String newSvgCode=removeStrokeNumbers(file.getAbsolutePath());
			String newSvgPath=pathTrgDir+"/"+file.getName();
			String pngPath=pathPngDir+"/"+file.getName().substring(0, file.getName().length()-3)+"png";
			createSvg(newSvgCode, newSvgPath);
			svgToPng(newSvgPath, pngPath);
			System.out.println(i+++"/"+srcDir.listFiles().length);
		}
	}

	private static String removeStrokeNumbers(String path){
		String s="";
		boolean toRemove=false;
		try (Scanner sc=new Scanner(new File(path), "UTF-8")) {
			while(sc.hasNextLine()) {
				String line=sc.nextLine();
				toRemove=line.contains("kvg:StrokeNumbers");
				if(!toRemove) s+=line+"\n";
				else {
					while(sc.hasNextLine() && !line.equals("</g>")) line=sc.nextLine();
				}
			}
		} catch (Exception e) {
			App.catchException(e, path);
		}
		return(s);
	}

	private static void createSvg(String svgCode, String path){
		File file=new File(path);
		try {
			if(file.createNewFile()){
				FileWriter writer=new FileWriter(file);
				writer.write(svgCode);
				writer.close();
			}
		} catch (Exception e) {
			App.catchException(e, path);
		}
	}

	private static void svgToPng(String svgPath, String pngPath){
		try{
			TranscoderInput input_svg_image = new TranscoderInput(new FileInputStream(svgPath));
			OutputStream png_ostream = new FileOutputStream(pngPath);
			TranscoderOutput output_png_image = new TranscoderOutput(png_ostream);
			PNGTranscoder my_converter = new PNGTranscoder();
			my_converter.transcode(input_svg_image, output_png_image);
			png_ostream.flush();
			png_ostream.close();
		} catch(Exception e){
			App.catchException(e, svgPath);
		}
	}

	public static void main(String[] args){
		createUsableKanji();
	}
}
