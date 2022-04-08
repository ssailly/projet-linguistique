package fr.upcite;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;

public class Kanji {
	public final String kanji, filename;
	private double similarity=0.0;

	public Kanji(String kanji, String filename) {
		this.kanji = kanji;
		this.filename = filename;
	}

	public static ArrayList<Kanji> createList(){
		ArrayList<Kanji> res=new ArrayList<>();
		File folder=new File("H:/[2021-2022] L3/S2/Linguistique/projet-linguistique/resources/kanji_png/");
		File[] files=folder.listFiles();
		for(int i=0;i<files.length;i++) {
			String filename=files[i].getName();
			char k=(char)Integer.parseInt(filename.substring(0, 5), 16);
			res.add(new Kanji(""+k, filename));
		}
		return res;
	}

	public static Comparator<Kanji> getComparator(){
		return new Comparator<Kanji>() {
			public int compare(Kanji k1, Kanji k2){
				return k1.getSimilarity()<k2.getSimilarity()?-1:k1.getSimilarity()>k2.getSimilarity()?1:0;
			}
		};
	}

	@Override
	public String toString() {
		return this.kanji+" "+this.similarity;
	}

	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}

	public void resetSimilarity(){
		this.similarity=0.0;
	}

	public double getSimilarity(){
		return this.similarity;
	}

	public static void main(String[] args) {
		ArrayList<Kanji> list=createList();
		for(Kanji k:list) System.out.println(k);
	}
}
