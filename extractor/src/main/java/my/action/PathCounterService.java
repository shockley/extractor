package my.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This singleton service calculate all the instance counts
 * @author Shockley
 */
public class PathCounterService {
	private static PathCounterService theInstance = null;
	/**
	 * key -- aname
	 * value -- xpathes matched
	 * value.key  -- the ith path
	 * value.value   -- support of the ith path
	 * Deprecated after we use db
	 */
	@Deprecated
	private HashMap<String,HashMap<String,Integer>> MP = new HashMap<String,HashMap<String,Integer>>();

	public static PathCounterService getInstance(){
		if(theInstance==null)
			theInstance = new PathCounterService();
		return theInstance;
	}
	
	/**
	 * Accumulate suport count while training using hashmap 
	 * @param aName
	 * @param xpath
	 */
	public void addMP(String aName, String xpath){
		HashMap<String,Integer> pathes = MP.get(aName);
		if(pathes == null){
			pathes = new HashMap<String,Integer>();
		}
		Integer support = pathes.get(xpath);
		if(pathes.get(xpath)==null){
			pathes.put(xpath, 1);
		}else{
			//the old value is replaced through put method
			pathes.put(xpath, support+1);
		}
		//the old value is replaced through put method
		MP.put(aName, pathes);
	}
	
	public HashMap<String,HashMap<String,Integer>> getMP() {
		return MP;
	}
	
	/**
	 * Get the support count
	 * @param aName
	 * @param xpath
	 * @return 0 if no such aName or no such path is found under that aName
	 */
	public int getSupport(String aName, String xpath) {
		int s = 0;
		HashMap<String,Integer> paths = MP.get(aName);
		if(paths!=null){
			Integer support = paths.get(xpath);
			if(support!=null)
				s = support;
		}
		return s;
	}
	
	/**
	 * Get the first one of the pathes with max-support
	 * @param aName : name of the attribute
	 * @return null if no paths for that attribute
	 */
	public String getMaxSupport(String aName){
		int max = 0;
		String maxPath = null;
		HashMap<String,Integer> paths= MP.get(aName);
		if(paths!=null){
			for(String p : paths.keySet()){
				int support = paths.get(p);
				if(support>max){
					max = support;
					maxPath = p;
				} 
			}
		}
		return maxPath;
	}
	
	public String printMP(){
		String s = "";
		for(String a : MP.keySet()){
			HashMap<String,Integer> paths = MP.get(a);
			for(String p : paths.keySet()){
				Integer i = paths.get(p);
				s += "ATTRI -- "+a+"; PATH --"+p+"; SUPPORT --"+i+'\n';
			}
		}
		return s;
	}
	
	public String printHITs(){
		String s = "";
		for(String a : MP.keySet()){
			HashMap<String,Integer> paths = MP.get(a);
			for(String p : paths.keySet()){
				Integer i = paths.get(p);
				s += "ATTRI -- "+a+"; PATH --"+p+"; SUPPORT --"+i+'\n';
			}
		}
		return s;
	}
}
