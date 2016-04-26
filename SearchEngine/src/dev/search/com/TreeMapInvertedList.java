package dev.search.com;

import java.util.Set;
import java.util.TreeMap;

public class TreeMapInvertedList {

	static TreeMap<String, InvertedList>  theTreeMap;

	//Initializing the Tree Map
	public TreeMapInvertedList() {
		theTreeMap=new TreeMap<String,InvertedList> ();
	}
	
	public InvertedList GetInvertedList(String pKey){
		
		return theTreeMap.get(pKey);
	}
	
	public void AddKeyValuePair(String pKey, InvertedList pInvertedList){
		theTreeMap.put(pKey, pInvertedList);
	}
	
	public static void DisplayAllKeySet(){
		Set<String> keys = theTreeMap.keySet();
		
        for(String key: keys){
            System.out.println(key);
        }
	}
	
	public static void DisplayList(){
		Set<String> keys = theTreeMap.keySet();
		int m= keys.size();
		for(int i=0;i<m;i++){
			//String s= keys.;
			
		}
	}
	
	
}