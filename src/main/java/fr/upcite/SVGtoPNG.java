package fr.upcite;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.Scanner;

public class SVGtoPNG {
	/**
	 * Exécuter depuis la racine du projet
	 */
	private static void createUsableKanji(){
		String pathSrcDir=Paths.get(".").toAbsolutePath().normalize().toString()+"/resources/kanji_svg_src";
		String pathTrgDir=Paths.get(".").toAbsolutePath().normalize().toString()+"/resources/kanji_svg_trg";
		File srcDir=new File(pathSrcDir);
		File trgDir=new File(pathTrgDir);
		for(File file:srcDir.listFiles()){
			String newSvgCode=removeStrokeNumbers(file.getAbsolutePath());
			createSvg(newSvgCode, pathTrgDir+"/"+file.getName());
			//TODO conversion SVG -> PNG
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
			catchException(path, e);
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
			catchException(path, e);
		}
	}

	private static void catchException(String path, Exception e){
		System.err.println("Path: "+path);
		e.printStackTrace();
	}

	public static void main(String[] args){
		createUsableKanji();
	}
}
