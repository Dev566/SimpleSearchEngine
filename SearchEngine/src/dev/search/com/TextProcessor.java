package dev.search.com;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NavigableMap;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.tartarus.snowball.ext.EnglishStemmer;

public class TextProcessor {

	String DirLoc = "F:/Acedamics/Information_Retrival/Files/";
	BufferedReader SWStopWord;
	Set<String> StopWords = new LinkedHashSet<String>();
	// Directory of the parsed files
	String DirLHTMLTagRemoved = "F:/IR/TagRemoved/";
	String DirLLowerCase = "F:/IR/LowerCase/";
	String DirLStopRemoved = "F:/IR/StopWRemoved/";
	String DirLStemProcessed = "F:/IR/StemProcessed/";
	static ListManager theListManager;
	static int NumberOfDoc;
	Double[][] tfIdf;
	static Boolean OnlineCrawler = false;
	static NavigableMap ResultSimilarity = null;
	static NavigableMap FinalResult = null;
	static Spider spider;
	TextProcessor() throws IOException {
		theListManager = new ListManager();
		if (theListManager.CreateList())
			System.out.println("Created the List manager");
		else
			System.out.println("Failed to initiate the List manager");
		SWStopWord = new BufferedReader(new FileReader("F:/Acedamics/Information_Retrival/StopList.txt"));
		for (String line; (line = SWStopWord.readLine()) != null;)
			StopWords.add(line.trim());// System.out.println(stopWords);
		SWStopWord.close();
		File DirectoryTOClear = new File(DirLHTMLTagRemoved);
		FileUtils.cleanDirectory(DirectoryTOClear);
		DirectoryTOClear = new File(DirLLowerCase);
		FileUtils.cleanDirectory(DirectoryTOClear);
		DirectoryTOClear = new File(DirLStemProcessed);
		FileUtils.cleanDirectory(DirectoryTOClear);
		DirectoryTOClear = new File(DirLStopRemoved);
		FileUtils.cleanDirectory(DirectoryTOClear);
	}

	public static void SetOnlineCrawler(String pDomain) throws IOException {
		String DirLoc = "F:/IR/CrawledPages/";
		File DirectoryTOClear = new File(DirLoc);
		FileUtils.cleanDirectory(DirectoryTOClear);
		spider = new Spider();
		spider.visit(pDomain);
		OnlineCrawler = true;
	}

	public Boolean StartTextProcessing() throws IOException {
		String processedFileLocation;
		String FileName;

		File file = new File(DirLoc);
		if (OnlineCrawler == true)
			file = new File("F:/IR1/CrawledPages/");
		File[] AllFiles = file.listFiles();
		ArrayList<String> StopWordProcessList = new ArrayList<String>();
		ArrayList<String> StemmedList = new ArrayList<String>();
		int i;
		for (i = 0; i < AllFiles.length; i++) {
			// Extracting the file name
			FileName = AllFiles[i].getName();
			// Changing Extension
			FileName = FileName.replaceAll(".htm", ".txt");
			processedFileLocation = ProcessHTMLFile(AllFiles[i], FileName);
			processedFileLocation = ProcessToLowerCase(processedFileLocation, FileName);
			StopWordProcessList = ProcessStopWords(processedFileLocation, FileName);
			StemmedList = StemmerProcess(StopWordProcessList, FileName);
			StopWordProcessList.clear();
			System.out.println(StemmedList);
			Iterator iter = StemmedList.iterator();
			int count = 0;
			while (iter.hasNext()) {
				String word = (String) iter.next();
				count++;
				theListManager.AddInList(word, i, 0);
			}
			System.out.println("Count " + count);
		}
		NumberOfDoc = i;
		return true;

	}

	public String ProcessHTMLFile(File pFile, String pFileName) throws IOException {
		String temp = null;
		if (pFile == null) {
			System.out.println("Please check the input parameter for ProcessHTMLFile");
		} else {

			// Attaching to the directory
			temp = DirLHTMLTagRemoved.concat(pFileName);
			// System.out.println(temp);

			PrintWriter write = new PrintWriter(temp);
			StringBuilder fileContents = new StringBuilder((int) pFile.length());
			Scanner scanner = new Scanner(pFile);
			String lineSeparator = System.getProperty("line.separator");
			String HTMLFree;

			try {
				while (scanner.hasNextLine()) {
					fileContents.append(scanner.nextLine() + lineSeparator);
				}
				HTMLFree = fileContents.toString();
				int cBodyStarts = HTMLFree.indexOf("<body");
				int cBodyEnds = HTMLFree.indexOf("</body");

				if ((cBodyStarts != -1) && (cBodyEnds != -1)) {

					HTMLFree = HTMLFree.substring(cBodyStarts, cBodyEnds);
					int cScriptStarts = HTMLFree.indexOf("<script");
					int cScriptEnds = HTMLFree.indexOf("</script") + 9;
					while ((cScriptStarts != -1) && (cScriptEnds != -1)) {
						String CtxtScript = HTMLFree.substring(cScriptStarts, cScriptEnds);
						StringBuffer text = new StringBuffer(HTMLFree);
						text.replace(cScriptStarts, cScriptEnds, "");
						if (HTMLFree.indexOf("CtxtScript") != -1)
							HTMLFree = HTMLFree.replace(CtxtScript, "");
						HTMLFree = text.toString();
						// HTMLFree = HTMLFree.replaceAll("CtxtScript","");
						cScriptStarts = HTMLFree.indexOf("<script");
						cScriptEnds = HTMLFree.indexOf("</script") + 9;
					}
				}
				HTMLFree = HTMLFree.replaceAll("\\<.*?>", "");
				HTMLFree = HTMLFree.replaceAll("&nbsp;", "");
				HTMLFree = HTMLFree.replaceAll("&amp;", "");
				HTMLFree = HTMLFree.replaceAll("&#160;", "");
				HTMLFree = HTMLFree.replaceAll("&ldquo", "");
				HTMLFree = HTMLFree.replaceAll("&rdquo", "");
				HTMLFree = HTMLFree.replaceAll(":", "");
				// HTMLFree = HTMLFree.replaceAll("[", "");
				// HTMLFree = HTMLFree.replaceAll("]", "");
				HTMLFree = HTMLFree.replaceAll("\"", "");
				HTMLFree = HTMLFree.trim();
				System.out.println(HTMLFree);
				write.println(HTMLFree);
			} finally {
				scanner.close();
				write.close();
			}
		}
		return temp;

	}

	// Code TO convert Lower case
	public String ProcessToLowerCase(String pFile, String pFileName) throws IOException {
		File file = new File(pFile);
		String temp = null;
		if (pFile == null) {
			System.out.println("Please check the input parameter for ProcessToLowerCase");
		} else {
			// Attaching to the directory
			temp = DirLLowerCase.concat(pFileName);
			// System.out.println(temp);

			BufferedReader in = (new BufferedReader(new FileReader(file)));
			PrintWriter out = (new PrintWriter(new FileWriter(temp)));

			int ch;
			// Reading each character and converting to lowercase if in
			// uppercase
			while ((ch = in.read()) != -1) {
				if (Character.isUpperCase(ch)) {
					ch = Character.toLowerCase(ch);
				}
				out.write(ch);
			}

			in.close();
			out.close();
		}
		return temp;
	}

	// Processing through Stop Words

	public ArrayList<String> ProcessStopWords(String pFile, String pFileName) throws IOException {
		ArrayList<String> arraylist = new ArrayList<String>();
		Scanner s = new Scanner(new File(pFile));
		while (s.hasNext()) {
			arraylist.add(s.next());
		}
		s.close();
		arraylist = deletStopWord(StopWords, arraylist);
		return arraylist;
	}

	public static ArrayList<String> deletStopWord(Set<String> stopWords, ArrayList<String> arraylist) {

		// System.out.println(stopWords.contains("?"));
		// System.out.println(stopWords);
		ArrayList<String> NewList = new ArrayList<String>();
		int i = 0;
		while (i < arraylist.size()) {
			if (!stopWords.contains(arraylist.get(i))) {
				String str = (String) arraylist.get(i);
//				while (str.lastIndexOf('.') == (str.length() - 1))
//					str = str.substring(0, str.length() - 1);
				NewList.add(str);
			}
			i++;
		}
		System.out.println(NewList);
		return NewList;
	}

	public ArrayList<String> StemmerProcess(ArrayList<String> pWordList, String pfileName) throws IOException {
		ArrayList<String> NewStemList = new ArrayList<String>();
		EnglishStemmer english = new EnglishStemmer();

		Iterator iter = pWordList.iterator();
		// Attaching to the directory
		String temp = DirLStemProcessed.concat(pfileName);
		PrintWriter write = new PrintWriter(temp);
		// System.out.println(temp);
		while (iter.hasNext()) {
			String word = (String) iter.next();
			System.out.println(word);
			english.setCurrent(word);
			english.stem();
			String StemmedWord = english.getCurrent();
			write.println(StemmedWord);

			NewStemList.add(StemmedWord);
		}
		write.close();
		return NewStemList;

	}

	public void tfIdf() {
		theListManager.dfCalculator();
		theListManager.idfCalculator(NumberOfDoc);
		tfIdf = theListManager.tfIdfCalculator1(NumberOfDoc);

		for (int j = 0; j < theListManager.documentFrequency.size(); j++) {
			for (int i = 0; i < NumberOfDoc; i++)
				System.out.println("Document vector Return " + tfIdf[j][i]);
			System.out.println("Next Term ");
		}
	}

	public static void ProcessQuery(String pMessage) {
		TreeMap<String, Integer> hash = new TreeMap<String, Integer>();
		;
		EnglishStemmer english = new EnglishStemmer();
		for (String str : pMessage.split(" ")) {
			str = str.toLowerCase();
			english.setCurrent(str);
			english.stem();
			String StemmedWord = english.getCurrent();

			if (hash.containsKey(StemmedWord))
				hash.put(StemmedWord, hash.get(StemmedWord) + 1);
			else
				hash.put(StemmedWord, 1);
		}
		int i = 0;
		if (hash != null) {
			double[][] QueryMatric = new double[NumberOfDoc][hash.size()];
			Set<String> keys = hash.keySet();
			double[] QueryVector = new double[hash.size()];
			for (String key : keys) {
				int df = 0;
				int keyIndex = theListManager.KeyList.indexOf(key);
				double LogIDF = theListManager.LogatithmicDocF.get(key);
				for (int j = 0; j < NumberOfDoc; j++) {
					QueryMatric[j][i] = (double) ListManager.FrequencyMatric[keyIndex][j] * LogIDF;
					// if(ListManager.DocMagnitude[j]>0)
					// QueryMatric[j][i]
					// =QueryMatric[j][i]/ListManager.DocMagnitude[j];
					String strDouble = String.format("%.4f", QueryMatric[j][i]);
					System.out.print(strDouble + "  ");
					if (QueryMatric[j][i] > 0)
						df += 1;
				}
				double divide = Math.log10(NumberOfDoc / (double) df);
				String strDouble = String.format("%.4f", divide);
				System.out.print(key + "  " + strDouble + "\n");
				QueryVector[i] = LogIDF * (double) hash.get(key);
				i++;
			}
			i = 0;
			System.out.println("Query Vector : ");
			for (String key : keys) {
				String strDouble = String.format("%.4f", QueryVector[i]);
				System.out.print(key + "  " + strDouble + "\n");
				i++;
			}
			TreeMap<String, ArrayList<String>> Similarity = new TreeMap<String, ArrayList<String>>();
			TreeMap<String, String> SimilarityMap = new TreeMap<String, String>();
			for (int j = 0; j < NumberOfDoc; j++) {
				theListManager.Document = j;
				double similarity = theListManager.cosineSimilarity(QueryMatric[j], QueryVector);
				String strDouble = String.format("%.7f", similarity);
				SimilarityMap.put(strDouble, Integer.toString(j));
				ArrayList tempArray;
				tempArray = Similarity.get(strDouble);
				if (tempArray == null) {
					tempArray = new ArrayList();
					tempArray.add(Integer.toString(j));
					Similarity.put(strDouble, tempArray);
				} else {
					tempArray.add(Integer.toString(j));
				}
				System.out.println("Similarity " + j + " " + similarity);
			}
			theListManager.Document = -1;
			ResultSimilarity = SimilarityMap.descendingMap();
			Set<String> ResultKey = ResultSimilarity.keySet();
			for (String tempKey : ResultKey) {
				System.out.println("Doc ID : " + ResultSimilarity.get(tempKey) + " : " + tempKey);
			}

			FinalResult = Similarity.descendingMap();

			Set<String> ResultKey1 = FinalResult.keySet();
			ArrayList<String> tempArray1;
			for (String tempKey : ResultKey1) {
				System.out.println("Similar : " + tempKey);
				tempArray1 = (ArrayList<String>) FinalResult.get(tempKey);
				for (String str : tempArray1) {
					System.out.print("   " + str);
				}
				System.out.println();
			}
		}
	}

	public void findSimilarity(String pmessage) {
		// TODO Auto-generated method stub
		String MessageTOSplit = null;
		EnglishStemmer english = new EnglishStemmer();
		for (String str : pmessage.split(" ")) {
			str = str.toLowerCase();
			english.setCurrent(str);
			english.stem();
			String StemmedWord = english.getCurrent();
			if (MessageTOSplit == null)
				MessageTOSplit = StemmedWord;
			else
				MessageTOSplit += " " + StemmedWord;
		}
		String str[] = MessageTOSplit.split(" ");
		theListManager.findSimilarity(str, NumberOfDoc);
	}
}
