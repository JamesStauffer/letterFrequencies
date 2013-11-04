
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

import ca.site.elkb.HeadInfo;
import ca.site.elkb.RogetELKB;

/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 *    WordCluster.java
 *    Copyright (C) 2006 Mario Jarmasz, Alistair Kennedy and Stan Szpakowicz
 *    School of Information Technology and Engineering (SITE)
 *    University of Ottawa, 800 King Edward St.
 *    Ottawa, Ontario, Canada, K1N 6N5
 *    and
 *    Olena Medelyan
 *    Department of Computer Science, The University of Waikato
 *    Privat Bag 3105, Hamilton, New Zealand
 */

/*******************************************************************************
 * WordCluster: program that calculates the semantic similarity for a set of
 * words and phrases. The following calculations are preformed: 1. pairwise
 * semantic similarity for all combinations of two words or phrases 2. list of
 * words or phrases with its most similar word or phrase 3. list of words or
 * phrases in the set who are of low similarity with any other word or phrase in
 * the set 4. clusters of words and phrases depending on their memberships in
 * given Heads
 * 
 * This program is developped for Ralf Steinberger at the EC JRC
 * 
 * Author : Mario Jarmasz Created: October, 2003 Usage : java WordCluster
 * <inputFile> <outputFile> Format of input file: a list of words and phrases,
 * one word or phrase per line.
 ******************************************************************************/

class WordCluster {

	// Attributes
	ArrayList<String> wordList;

	ArrayList<String> notInRogetList;

	RogetELKB elkb;

	BufferedWriter bw;

	/***************************************************************************
	 * Constructor.
	 * @param year 
	 **************************************************************************/
	public WordCluster(int year) {
		wordList = new ArrayList<String>();
		notInRogetList = new ArrayList<String>();
		elkb = new RogetELKB(year);
		System.out.println();
	}

	/***************************************************************************
	 * main(String args[])
	 **************************************************************************/
	public static void main(String args[]) {
		if (args.length != 2) {
			System.out.println("java WordCluster <inputFile> <outputFile>");
		} else {
			try {
				WordCluster wc = new WordCluster(1911);
				wc.bw = new BufferedWriter(new FileWriter(args[1]));
				wc.loadFile(args[0]);
				wc.semDist(wc.wordList);
				wc.cluster(wc.wordList);
				wc.bw.write("Words and phrases not in Roget's Thesaurus ("
						+ wc.notInRogetList.size() + "/"
						+ (wc.wordList.size() + wc.notInRogetList.size())
						+ "): ");
				wc.bw.newLine();
				wc.bw
						.write("--------------------------------------------------");
				wc.bw.newLine();
				wc.bw.write(wc.notInRogetList.toString());
				wc.bw.newLine();

				wc.bw.flush();
				wc.bw.close();
			} catch (Exception e) {
				e.printStackTrace();

			}
		}
	}

	/***************************************************************************
	 * loadFile(String): loads the file containing words and phrases into an
	 * array The format of the file is one word or phrase per line
	 **************************************************************************/
	private void loadFile(String fname) {
		String line = new String();

		try {
			BufferedReader br = new BufferedReader(new FileReader(fname));

			for (;;) {

				line = br.readLine();

				if (line == null) {
					br.close();
					break;
				} else if (elkb.index.containsEntry(line)) {
					wordList.add(line);
				} else {
					notInRogetList.add(line);
				}
			}

		} catch (Exception e) {
			System.out.println(line);
			e.printStackTrace();
		}
	}

	/***************************************************************************
	 * SemDist(ArryaList): calculates the semantic distance between all
	 * combinations of pairs of words in the ArrayList
	 **************************************************************************/
	private void semDist(ArrayList<String> tokenList) {
		int count = tokenList.size();
		String word1, word2, simMeaning;
		int semDist;

		for (int i = 0; i < count; i++) {
			try {
				word1 = tokenList.get(i);
				bw.write("*** " + word1 + " ***");
				bw.newLine();
				for (int j = 0; j < count; j++) {
					if (i != j) {
						word2 = tokenList.get(j);
						semDist = semDistVal(word1, word2);
						simMeaning = simMeaning(semDist);
						bw.write(word1 + " - " + word2 + ", " + +semDist + ", "
								+ simMeaning + " similarity");
						bw.newLine();
					}
				}
				bw.newLine();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/***************************************************************************
	 * SemDistVal(String, String): returns the integer semantic distance value
	 * of two words or phrases This method should be part of the RogetELKB class
	 * I should take a close look at semantics, semantic distance vs. similarity
	 **************************************************************************/
	private int semDistVal(String word1, String word2) {
		ArrayList<int[]> list1 = elkb.index.getEntryList2(word1);
		ArrayList<int[]> list2 = elkb.index.getEntryList2(word2);
		if(list1.size() == 0 || list2.size() == 0){
			return 0;
		}
		int best = 0;
		for (int i = 0; i < list1.size(); i++) {
			int[] entry1 = list1.get(i);
			for (int j = 0; j < list2.size(); j++) {
				int[] entry2 = list2.get(j);
				int diff = 16;
				for (int k = 0; k < 8; k++) {
					if (entry1[k] != entry2[k]){
						if(2*k < diff){
							diff = 2*k;
						}
					}
				}
				if(best < diff){
					best = diff;
				}
			}
		}
		return best;
	}

	/***************************************************************************
	 * SimMeaning(int): returns a string translating the semantic similarity +
	 * 16 = high similarity + 12 to 14 = intermediate similarity + below 10 =
	 * low similarity
	 **************************************************************************/
	private String simMeaning(int value) {
		String meaning = new String();

		switch (value) {
		case 16:
			meaning = "high";
			break;
		case 14:
		case 12:
		case 10:
			meaning = "intermediate";
			break;
		case 8:
		case 6:
		case 4:
		case 2:
		case 0:
			meaning = "low";
			break;
		default:
			meaning = "incorrect similarity value";
		}

		return meaning;
	}

	/***************************************************************************
	 * cluster(ArrayList): clusters the words and phrases in the array list
	 * according to their membership in a head group
	 **************************************************************************/
	private void cluster(ArrayList<String> tokenList) {
		ArrayList<String> headList; // could be a simple array
		TreeMap<Integer, ArrayList<String>> headMap = new TreeMap<Integer, ArrayList<String>>();
		int count = tokenList.size();
		String word;
		ArrayList<String> list;

		for (int i = 0; i < count; i++) {
			word = tokenList.get(i);

			headList = new ArrayList<String>(elkb.index.getHeadNumbers(word));
			Iterator<String> iter = headList.iterator();
			while (iter.hasNext()) {
				Integer headNo = new Integer(iter.next());
				ArrayList<String> single = new ArrayList<String>();
				single.add(word);
				list = headMap.put(headNo, single);
				// if there is already something in the map, list != null
				if (list != null) {
					list.add(word);
					headMap.put(headNo, list);
				}
			}
		}

		printCluster(headMap);
	}

	/***************************************************************************
	 * printCluster(TreeMap): prints clusters containing more than one word or
	 * phrase
	 **************************************************************************/
	private void printCluster(TreeMap<Integer, ArrayList<String>> cluster) {
		TreeSet<Integer> keySet = new TreeSet<Integer>(cluster.keySet());
		TreeSet<ComparableArray> clusterSet = new TreeSet<ComparableArray>();
		ArrayList<ComparableArray> wordsInGroups = new ArrayList<ComparableArray>();
		ArrayList<HeadInfo> headInfoList = elkb.category.getHeadList();

		try {
			bw.write("Possible Roget's Thesaurus clusters:");
			bw.newLine();
			bw.write("------------------------------------");
			bw.newLine();

			Iterator<Integer> iter = keySet.iterator();

			while (iter.hasNext()) {
				Integer key = iter.next();
				ComparableArray list = new ComparableArray(cluster.get(key));
				if (list.size() > 1) {
					int headNo = key.intValue();
					try {
						HeadInfo headInfo = headInfoList
								.get(headNo - 1);
						bw.write("Head: " + key + " " + headInfo.getHeadName());
						bw.newLine();
						bw.write(list.toString());
						bw.newLine();
						list.add(0, headInfo);
						clusterSet.add(list);
						bw.newLine();
						wordsInGroups.addAll(list);
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("Head number:" + (headNo - 1));
					}
				}
			}

			// Should be another method
			bw.write("The 10 largest clusters:");
			bw.newLine();
			bw.write("------------------------");
			bw.newLine();

			// Could use toArray method instead, would be safer and should work
			// better...
			ArrayList<ComparableArray> clusterArray = new ArrayList<ComparableArray>();
			clusterArray.addAll(clusterSet);
			int arrSize = clusterArray.size();
			for (int i = 1; i < 11; i++) {

				ArrayList listObj = clusterArray.get(arrSize - i);
				HeadInfo hi = (HeadInfo) listObj.get(0);
				listObj.remove(0);
				bw.write("Head: " + hi.getHeadNum() + " " + hi.getHeadName());
				bw.newLine();
				bw.write(listObj.toString());
				bw.newLine();
				clusterSet.remove(listObj);
				bw.newLine();
			}

			// Should be another method
			ArrayList<String> allWords = new ArrayList<String>(wordList);
			allWords.removeAll(wordsInGroups);
			if (allWords.size() > 0) {
				bw.write("Words and phrases not in clusters:");
				bw.newLine();
				bw.write("----------------------------------");
				bw.newLine();
				bw.write(allWords.toString());
				bw.newLine();
				bw.newLine();
			}
		} catch (Exception e) {
			// e.printStackTrace();

		}

	}

}