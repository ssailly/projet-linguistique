package fr.upcite;

import java.io.File;
import java.nio.file.Paths;
import java.util.Scanner;

public class SVGtoPNG {
	private static void createUsableKanji(){
		String pathDir=Paths.get(".").toAbsolutePath().normalize().toString()+"/resources/kanji_svg_src";//depuis la racine
		File dir=new File(pathDir);
		for(File file:dir.listFiles()){
			removeStrokeNumbers(file.getAbsolutePath());
			//TODO Créer les SVG à partir du nouveau code utilisable
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

	private static void catchException(String path, Exception e){
		System.err.println("Path: "+path);
		e.printStackTrace();
	}

	public static void main(String[] args){
		createUsableKanji();
	}
}
