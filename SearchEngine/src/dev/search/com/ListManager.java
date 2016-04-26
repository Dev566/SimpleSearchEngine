package dev.search.com;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.Map.Entry;

public class ListManager {
	private TreeMapInvertedList theTreeMapList;
	private InvertedList theInvertedList;
	TreeMap<String, Integer> documentFrequency = new TreeMap<String, Integer>();
	TreeMap<String, Integer> termFrequency = new TreeMap<String, Integer>();
	TreeMap<String, Double> inverseDocumentFrequency = new TreeMap<String, Double>();
	static double[][] FrequencyMatric;
	TreeMap<String, Double> LogatithmicDocF = new TreeMap<String, Double>();
	ArrayList<String> KeyList = new ArrayList<String>();
	Double[][] tfIdf;
	static double[] DocMagnitude;
	static int Document=-1;

	// Creating the list
	public boolean CreateList() {
		if (theTreeMapList == null) {
			theTreeMapList = new TreeMapInvertedList();
			return true;
		} else
			return false;
	}

	public InvertedList GetLastNode(InvertedList pNode) {
		if (pNode.GetNextNode() == null)
			return pNode;
		else
			return GetLastNode(pNode.GetNextNode());
	}

	public void AddInList(String pWord, int pDocID, int pPosition) {
		InvertedList LastNode;
		InvertedList theTempList = theTreeMapList.GetInvertedList(pWord);
		if (theTempList == null) {
			theTreeMapList.AddKeyValuePair(pWord, new InvertedList(pDocID, 1, pPosition));
			return;
		}

		LastNode = GetLastNode(theTempList);
		if (LastNode.GetDocID() == pDocID)
			LastNode.AddFrequency(pPosition);
		else {
			LastNode.AddNxtNode(new InvertedList(pDocID, 1, pPosition));
		}

	}

	public void DisplayRowForWord(String pWord) {
		InvertedList LastNode;
		InvertedList theTempList = theTreeMapList.GetInvertedList(pWord);
		if (theTempList == null) {
			System.out.println("Word Not found");
		} else {

			while (theTempList.GetNextNode() != null) {
				System.out.println("Doc ID " + theTempList.GetDocID());
				theTempList = theTempList.GetNextNode();
			}
			System.out.println("Doc ID" + theTempList.GetDocID());
		}
	}

	public void DisplayKey() {
		theTreeMapList.DisplayAllKeySet();
	}

	public int GetDocumentFrequency(String pWord) {
		/*
		 * InvertedList LastNode; InvertedList theTempList =
		 * theTreeMapList.GetInvertedList(pWord); int frequency=0; if
		 * (theTempList == null) { System.out.println("Word Not found"); }else{
		 * 
		 * while(theTempList.GetNextNode() != null){ System.out.println(
		 * "Doc ID " + theTempList.GetDocID());
		 * frequency=frequency+theTempList.GetFrequency();
		 * theTempList=theTempList.GetNextNode(); } System.out.println("Doc ID "
		 * + theTempList.GetDocID());
		 * frequency=frequency+theTempList.GetFrequency(); } return frequency;
		 */
		dfCalculator();
		int p = termFrequency.get(pWord);
		System.out.println(p);
		return p;
		// return 2;
	}

	public void CreateMatric(int pDocNumber) {
		// theTreeMapList.
		InvertedList LastNode;
		InvertedList theTempList;
		if (theTreeMapList != null) {
			FrequencyMatric = new double[theTreeMapList.theTreeMap.size()][pDocNumber];
			Set<String> keys = theTreeMapList.theTreeMap.keySet();
			int i = 0;
			for (String key : keys) {
				// System.out.println(key);

				KeyList.add(key);
				theTempList = theTreeMapList.GetInvertedList(key);
				if (theTempList == null) {
					System.out.println("Word Not found");
				} else {

					while (theTempList != null) {
						int frequencyNode = theTempList.GetFrequency();
						FrequencyMatric[i][theTempList.GetDocID()] = frequencyNode;
						// System.out.print( " "+ frequencyNode);
						if (theTempList.GetNextNode() != null) {
							theTempList = theTempList.GetNextNode();
						} else
							break;
					}
				}
				i++;
				System.out.println("");
			}
			i = 0;
			DecimalFormat Dft = new DecimalFormat("0.000");

			for (String key : keys) {
				int df = 0;
				for (int j = 0; j < pDocNumber; j++) {
					System.out.print(FrequencyMatric[i][j] + "  ");
					if (FrequencyMatric[i][j] > 0)
						df += 1;
				}
				double divide = Math.log10(pDocNumber / (double) df);
				String strDouble = String.format("%.4f", divide);
				LogatithmicDocF.put(key, divide);

				System.out.print(key + "  " + strDouble + "\n");

				i++;
			}
			i=0;
			DocMagnitude=new double[pDocNumber];
			for (String key : keys) {
				for (int k = 0; k < pDocNumber; k++) {
					if (FrequencyMatric[i][k] > 0)
						DocMagnitude[k]+=Math.pow(FrequencyMatric[i][k]*LogatithmicDocF.get(key), 2);
				}
				i++;
			}
			for (int k = 0; k < pDocNumber; k++) {
				DocMagnitude[k]=Math.sqrt(DocMagnitude[k]);
				System.out.println(DocMagnitude[k]);
			}
			// System.out.println(KeyList.get(index));
		}
	}

	public void dfCalculator() {
		Map<String, InvertedList> mp = TreeMapInvertedList.theTreeMap;
		Iterator<Entry<String, InvertedList>> it = mp.entrySet().iterator();
		InvertedList theTempList;
		while (it.hasNext()) {
			Map.Entry<String, InvertedList> pair = it.next();
			theTempList = theTreeMapList.GetInvertedList(pair.getKey());
			int df = 0, tf = 0;
			if (theTempList == null) {
				System.out.println("Word Not found");
			} else {
				while (theTempList.GetNextNode() != null) {
					df = df + 1;
					tf = tf + theTempList.GetFrequency();
					theTempList = theTempList.GetNextNode();
				}
				df = df + 1;
				tf = tf + theTempList.GetFrequency();
			}
			documentFrequency.put(pair.getKey(), df);
			termFrequency.put(pair.getKey(), tf);
		}
	}

	public void idfCalculator(int n) {
		Map<String, Integer> mp = documentFrequency;
		Iterator<Entry<String, Integer>> it = mp.entrySet().iterator();
		Double iFrequency;
		while (it.hasNext()) {
			Map.Entry<String, Integer> df = it.next();
			iFrequency = Math.log10(n) - Math.log10(df.getValue());
			inverseDocumentFrequency.put(df.getKey(), iFrequency);
		}
	}

	public double[] tfIdfCalculator(String[] docVector) {
		Map<String, Integer> mp = documentFrequency;
		Iterator<Entry<String, Integer>> it = mp.entrySet().iterator();
		double[] result = new double[documentFrequency.size()];
		List<String> list = Arrays.asList(docVector);
		TreeMap<String, Integer> hash = new TreeMap<String, Integer>();
		for (int i = 0; i < list.size(); i++) {
			if (hash.containsKey(list.get(i)))
				hash.put(list.get(i), hash.get(list.get(i)) + 1);
			else
				hash.put(list.get(i), 1);
		}
		int count = 0;
		while (it.hasNext()) {
			Map.Entry<String, Integer> pair = it.next();
			String term = pair.getKey();
			if (hash.containsKey(term))
				result[count] = hash.get(term) * inverseDocumentFrequency.get(term);
			else
				result[count] = 0;
			count++;
		}
		return result;
	}

	public Double[][] tfIdfCalculator1(int n) {
		Map<String, InvertedList> mp = TreeMapInvertedList.theTreeMap;
		Iterator<Entry<String, InvertedList>> it = mp.entrySet().iterator();
		InvertedList theTempList;
		tfIdf = new Double[documentFrequency.size()][n];
		int i = 0, p;
		double q;
		while (it.hasNext()) {
			Map.Entry<String, InvertedList> pair = it.next();
			theTempList = theTreeMapList.GetInvertedList(pair.getKey());
			q = inverseDocumentFrequency.get(pair.getKey());
			p = theTempList.GetDocID();
			for (int j = 0; j < n; j++) {
				if (p == j) {
					tfIdf[i][j] = (double) (q * theTempList.GetFrequency());
					if (theTempList.GetNextNode() != null)
						theTempList = theTempList.GetNextNode();
				} else
					tfIdf[i][j] = (double) 0;
				p = theTempList.GetDocID();
			}
			i++;
		}
		return tfIdf;
	}

	public double cosineSimilarity(double[] docVector1, double[] docVector2) {
		double dotProduct = 0.0;
		double magnitude1 = 0.0;
		double magnitude2 = 0.0;
		double cosineSimilarity = 0.0;

		for (int i = 0; i < docVector1.length; i++) // docVector1 and docVector2
													// must be of same length
		{
			dotProduct += docVector1[i] * docVector2[i]; // a.b
			magnitude1 += Math.pow(docVector1[i], 2); // (a^2)
			magnitude2 += Math.pow(docVector2[i], 2); // (b^2)
		}

		magnitude1 = Math.sqrt(magnitude1);// sqrt(a^2)
		if(Document>-1)
			magnitude1=DocMagnitude[Document];
		magnitude2 = Math.sqrt(magnitude2);// sqrt(b^2)
		
		if (Double.compare(magnitude1, 0) > 0 && Double.compare(magnitude2, 0) > 0) {
			cosineSimilarity = dotProduct / (magnitude1 * magnitude2);
		} else {
			return 0.0;
		}
		return cosineSimilarity;
	}

	public void findSimilarity(String[] message, int n) {
		// TODO Auto-generated method stub
		Map<Integer, Double> sim = new TreeMap<Integer, Double>();
		double cosSim;
		double[] queryVector = tfIdfCalculator(message);
		double[] docVector = new double[tfIdf.length];
		for (int column = 0; column < n; column++) {
			for (int row = 0; row < tfIdf.length; row++) {
				docVector[row] = tfIdf[row][column];
			}
			cosSim = cosineSimilarity(queryVector, docVector);
			sim.put(column + 1, cosSim);
		}
		// sim = (Map<Integer, Double>) entriesSortedByValues(sim);
		System.out.println("Similarity " + entriesSortedByValues(sim));
	}

	static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> entriesSortedByValues(Map<K, V> map) {
		SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<Map.Entry<K, V>>(new Comparator<Map.Entry<K, V>>() {
			@Override
			public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
				int res = e1.getValue().compareTo(e2.getValue());
				return res != 0 ? res : 1;
			}
		});
		sortedEntries.addAll(map.entrySet());
		return sortedEntries;
	}

}
