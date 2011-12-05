package my.action.algorithm;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.collections.CollectionUtils;

/**
 * Using Jaccard similarity, based on qgram
 * @author Shockley
 *
 */
public class SimMatcher{
	/**
	 * This algorithm can be found on 
	 * <author>Bing Liu</author>'s 
	 * <book>Web Data Mining</book>#204 
	 */
	public double jaccardSimilarity(String s1, String s2, int q) {
		// We tokenize them, first!!!
		List<String> tokens1=new ArrayList<String>();;
		List<String> tokens2=new ArrayList<String>();
		StringTokenizer tokenizer = new StringTokenizer(s1);
		
		while(tokenizer.hasMoreTokens()){
			tokens1.add(tokenizer.nextToken());
		}
		tokenizer = new StringTokenizer(s2);
		while(tokenizer.hasMoreTokens()){
			tokens2.add(tokenizer.nextToken());
		}
		
		List<String> qgrams1 = new ArrayList<String>();
		List<String> qgrams2 = new ArrayList<String>();
		
		for(String w : tokens1){
			List<String> qgrams = this.findQgrams(w, q);
			for(String qgram : qgrams){
				//Cautiously eliminate duplicants while adding
				if(!qgrams1.contains(qgram))
					qgrams1.add(qgram);
			}
		}
		for(String w : tokens2){
			List<String> qgrams = this.findQgrams(w, q);
			for(String qgram : qgrams){
				//Cautiously eliminate duplicants while adding
				if(!qgrams2.contains(qgram))
					qgrams2.add(qgram);
			}
		}
		int unions = CollectionUtils.union(qgrams1, qgrams2).size();
		int intersect = CollectionUtils.intersection(qgrams1, qgrams2).size();
		//logger.println((double) intersect/unions);
		return (double)intersect/unions;
	}
	
	/**
	 * Please send lower cased string to this one
	 * @Shockley
	 * @return
	 */
	public boolean whetherMatches(String s1, String s2){
		if(jaccardSimilarity(s1, s2, 3)>THRESHOLD)
			return true;
		else
			return false;
	}
	
	/**
	 * @param w
	 * @param q
	 * @return null, if w has format problem
	 *         a list with the only element(w itself) if q>=w.length()
	 */
	public List<String> findQgrams(String w, int q){
		/*for(String delimiter : delimiters){
			if(w.contains(delimiter)){
				logger.println("Contains delimiters!");
				return null;
			}
		}*/
		
		//No delimiters, so we continue
		
		List<String> toReturn = new ArrayList<String>();
		if(q>=w.length())
			toReturn.add(w);
		//i is the index of the first char of this current q-gram
		for(int i = 0; i <= w.length()-q; i ++){
			toReturn.add(w.substring(i, i+q));
		}
		return toReturn;
	}
	public static double THRESHOLD = 0.9;
	public int gramLength = 3;
	
	public int getGramLength() {
		return gramLength;
	}

	public void setGramLength(int gramLength) {
		this.gramLength = gramLength;
	}
	public static List<String> delimiters;
	public static PrintStream logger = System.out;
	static{
		delimiters = new ArrayList<String>();
		delimiters.add(" ");
		delimiters.add("\t");
		delimiters.add("\n");
		delimiters.add("\\");
		delimiters.add("/");
		delimiters.add("\"");
		delimiters.add("\'");
		delimiters.add(",");
		delimiters.add(".");
		delimiters.add("~");
		delimiters.add("`");
		delimiters.add(":");
		delimiters.add(";");
		delimiters.add("+");
		delimiters.add(")");
		delimiters.add("(");
		delimiters.add("[");
		delimiters.add("]");
		delimiters.add("{");
		delimiters.add("}");
		delimiters.add("|");
		delimiters.add("*");
		delimiters.add("&");
		delimiters.add("^");
		delimiters.add("%");
		delimiters.add("$");
		delimiters.add("#");
		delimiters.add("@");
		delimiters.add("!");
	}
	
	public static void main(String []args){
		String s1 = "Operating System";
		String s2 = "Operatings System";
		SimMatcher m = new SimMatcher();
		m.jaccardSimilarity(s1, s2,3);
	}
}
