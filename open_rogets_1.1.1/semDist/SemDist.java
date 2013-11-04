/*
 *    Copyright (C) 2007 Mario Jarmasz, Alistair Kennedy and Stan Szpakowicz
 *    School of Information Technology and Engineering (SITE)
 *    University of Ottawa, 800 King Edward St.
 *    Ottawa, Ontario, Canada, K1N 6N5
 *    and
 *    Olena Medelyan
 *    Department of Computer Science, The University of Waikato
 *    Privat Bag 3105, Hamilton, New Zealand
 *    
 *     This file is part of Open Roget's Thesaurus ELKB.
 * 
 *     Open Roget's Thesaurus ELKB is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     Open Roget's Thesaurus ELKB is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 *     
 */

import java.io.*;
import java.util.*;

import ca.site.elkb.*;

/*******************************************************************************
 * SemDist : program that performs various experiments to calculate semantic
 * distance. This program was used to obtain the results of Roget's Thesaurus
 * and Semantic Similarity paper which can be found at:
 * http://www.site.uottawa.ca/~mjarmasz/pubs/jarmasz_roget_sim.pdf
 * 
 * @author Mario Jarmasz and Alistair Kennedy
 * @version 1.1 Jan 2007 Usage : java SemDist <inputFile> Format of input file:
 *          pairs of comma separated words and phrases, one pair per line. Extra
 *          information can be contained on the line as long as it is separated
 *          by a comma, for example: car,automobile,3.92
 ******************************************************************************/

class SemDist {
	public RogetELKB elkb;

	// Constructor
	public SemDist(int year) {
		// Initialize Roget
		elkb = new RogetELKB(year);
	}

	public static void main(String args[]) {
		if (args.length != 1) {
			System.out.println("java SemDist <inputFile>");
		} else {
			try {
				// should check that we have a valid argument first...
				SemDist semDist = new SemDist(1911);
				semDist.wordPairs(args[0]);
			} catch (Exception ex) {
				ex.printStackTrace();
				System.exit(-1);
			}
		}
	}

	/***************************************************************************
	 * To deal with Miller and Charles type experiment
	 **************************************************************************/
	private void wordPairs(String fileName) {

		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));

			for (;;) {
				String line = br.readLine();

				if (line == null) {
					br.close();
					break;
				} else {
					StringTokenizer st = new StringTokenizer(line, ",");
					String word1 = st.nextToken().trim();
					String word2 = st.nextToken().trim();
					String value = st.nextToken().trim();

					// in all of these experiments we're only dealing
					// with pairs of nouns
					int score = getMinDistance(word1, word2, "N.");
					System.out.println(word1 + "\t" + word2 + "\t" + value + "\t" + score);
					//break;
				}
			}
		} catch (Exception e) {
			System.out.println("Error:" + e);
		}
	}

	private int getMinDistance(String word1, String word2) {
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
	
	private int getMinDistance(String word1, String word2, String pos) {
		return getMinDistance(word1, pos, word2, pos);
	}
	
	private int getMinDistance(String word1, String pos1, String word2, String pos2) {
		ArrayList<int[]> list1 = elkb.index.getEntryList2(word1);
		ArrayList<int[]> list2 = elkb.index.getEntryList2(word2);
		if(list1.size() == 0 || list2.size() == 0){
			return 0;
		}
		int best = 0;
		for (int i = 0; i < list1.size(); i++) {
			int[] entry1 = list1.get(i);
			if(elkb.index.convertToPOS(entry1[5]).equals(pos1)){
				for (int j = 0; j < list2.size(); j++) {
					int[] entry2 = list2.get(j);
					if(elkb.index.convertToPOS(""+entry2[5]).equals(pos2)){
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
			}
		}
		return best;
	}

}
