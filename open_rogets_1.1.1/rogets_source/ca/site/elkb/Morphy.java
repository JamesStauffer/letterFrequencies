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

package ca.site.elkb;

import java.io.*;
import java.util.*;

/**
 * Performs morphological transformations using the same rules as <i>WordNet</i>.
 * 
 * <p>
 * The following suffix substitutions are done for:
 * <UL>
 * <LI><B>nouns:</B>
 * <OL>
 * <LI>"s" -> ""</LI>
 * <LI>"ses" -> "s"</LI>
 * <LI>"xes" -> "x"</LI>
 * <LI>"zes" -> "z"</LI>
 * <LI>"ches" -> "ch"</LI>
 * <LI>"shes" -> "sh"</LI>
 * <LI>"men" -> "man"</LI>
 * </OL>
 * <LI><B>adjectives:</B>
 * <OL>
 * <LI>"er" -> ""</LI>
 * <LI>"est" -> ""</LI>
 * <LI>"er" -> "e"</LI>
 * <LI>"est" -> "e"</LI>
 * </OL>
 * <LI><B>verbs:</B>
 * <OL>
 * <LI>"s" -> ""</LI>
 * <LI>"ies" -> "y"</LI>
 * <LI>"es" -> "e"</LI>
 * <LI>"es" -> ""</LI>
 * <LI>"ed" -> "e"</LI>
 * <LI>"ed" -> ""</LI>
 * <LI>"ing" -> "e"</LI>
 * <LI>"ing" -> ""</LI>
 * </OL>
 * </UL>
 * </p>
 * 
 * The <TT>noun.exc</TT>, <TT>adj.exc</TT>, <TT>verb.exc</TT> and <TT>adv.exc</TT>
 * exception files, located in the <TT>$HOME/roget_elkb</TT> directory, are
 * searched before applying the rules of detachment.
 * 
 * @author Mario Jarmasz and Alistsair Kennedy
 * @version 1.1 Aug 2007
 */

public class Morphy implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1474175109654783742L;

	// Lists of prefixes and endings
	private ArrayList<String> nounSub;

	private ArrayList<String> verbSub;

	private ArrayList<String> adjSub;

	// Hastables of inflected word followed by base form
	private HashMap<String, ArrayList<String>> nounMap;

	private HashMap<String, ArrayList<String>> verbMap;

	private HashMap<String, ArrayList<String>> adjMap;

	private HashMap<String, ArrayList<String>> advMap;

	/***************************************************************************
	 * Location of user's <TT>Home</TT> directory.
	 **************************************************************************/
	public static final String USER_HOME = System.getProperty("user.home");

	/***************************************************************************
	 * Location of the <i>ELKB</i> data directory.
	 **************************************************************************/
	public static final String ELKB_PATH = System.getProperty("elkb.path",
			USER_HOME + "/roget_elkb");

	/***************************************************************************
	 * Location of the <TT>noun.exc</TT> file.
	 **************************************************************************/
	public static final String NOUN_EXC = ELKB_PATH + "/noun.exc";

	/***************************************************************************
	 * Location of the <TT>verb.exc</TT> file.
	 **************************************************************************/
	public static final String VERB_EXC = ELKB_PATH + "/verb.exc";

	/***************************************************************************
	 * Location of the <TT>adj.exc</TT> file.
	 **************************************************************************/
	public static final String ADJ_EXC = ELKB_PATH + "/adj.exc";

	/***************************************************************************
	 * Location of the <TT>adv.exc</TT> file.
	 **************************************************************************/
	public static final String ADV_EXC = ELKB_PATH + "/adv.exc";

	/***************************************************************************
	 * Default constructor.
	 **************************************************************************/
	public Morphy() {

		nounSub = new ArrayList<String>();
		verbSub = new ArrayList<String>();
		adjSub = new ArrayList<String>();

		nounSub.add("s");
		nounSub.add("");
		nounSub.add("ses");
		nounSub.add("s");
		nounSub.add("xes");
		nounSub.add("x");
		nounSub.add("zes");
		nounSub.add("z");
		nounSub.add("ches");
		nounSub.add("ch");
		nounSub.add("shes");
		nounSub.add("sh");
		nounSub.add("men");
		nounSub.add("man");
		nounSub.add("ies");
		nounSub.add("y");

		verbSub.add("s");
		verbSub.add("");
		verbSub.add("ies");
		verbSub.add("y");
		verbSub.add("es");
		verbSub.add("e");
		verbSub.add("es");
		verbSub.add("");
		verbSub.add("ed");
		verbSub.add("e");
		verbSub.add("ed");
		verbSub.add("");
		verbSub.add("ing");
		verbSub.add("e");
		verbSub.add("ing");
		verbSub.add("");
		verbSub.add("er");
		verbSub.add("");

		adjSub.add("er");
		adjSub.add("");
		adjSub.add("est");
		adjSub.add("");
		adjSub.add("er");
		adjSub.add("e");
		adjSub.add("est");
		adjSub.add("e");

		nounMap = loadHashFromFile(NOUN_EXC);
		verbMap = loadHashFromFile(VERB_EXC);
		adjMap = loadHashFromFile(ADJ_EXC);
		advMap = loadHashFromFile(ADV_EXC);
	}

	/***************************************************************************
	 * Allows the <TT>Morphy</TT> class to be used via the command line. All
	 * the possible results of the input string are returned.
	 * <p>
	 * Usage: java Morphy <word or phrase>
	 * </p>
	 **************************************************************************/
	public static void main(String args[]) {
		Morphy morphy = new Morphy();

		String phrase = morphy.oneString(args);

		if (phrase.equals("") == false) {
			HashSet<String> lemmas = morphy.getBaseForm(phrase);
			System.out.println(lemmas);
		} else {
			System.out.println("Usage: java Morphy <word or phrase>");
		}

	}

	/***************************************************************************
	 * Reruns all the base forms for a given word. Returns an empty HashSet if
	 * no base forms were found.
	 **************************************************************************/
	public HashSet<String> getBaseForm(String words) {
		HashSet<String> allLemmas = new HashSet<String>();

		// look in exception files first, then apply rules of detachment
		if (nounMap.containsKey(words))
			allLemmas.addAll(nounMap.get(words));
		allLemmas.addAll(detach(words, nounSub));

		if (verbMap.containsKey(words))
			allLemmas.addAll(verbMap.get(words));
		allLemmas.addAll(detach(words, verbSub));

		if (adjMap.containsKey(words))
			allLemmas.addAll(adjMap.get(words));
		allLemmas.addAll(detach(words, adjSub));

		if (advMap.containsKey(words))
			allLemmas.addAll(advMap.get(words));

		return allLemmas;
	}

	// have a method identical to getBaseForm but which specifies a POS

	/***************************************************************************
	 * Detach: rules of detachment. Takes a string a the POS map. Returns all
	 * possible base forms
	 **************************************************************************/
	private HashSet<String> detach(String words, ArrayList<String> posSub) {
		HashSet<String> lemmas = new HashSet<String>();

		// cycle through the entire replacement set
		Iterator<String> iter = posSub.iterator();
		while (iter.hasNext()) {
			// JDK 1.4 uses Perl regular expressions
			// $ is used to match an expression at the end of a word
			String suffix = iter.next() + "$";
			String ending = iter.next();
			String newWord = words.replaceFirst(suffix, ending);
			if (words.equals(newWord) == false) {
				lemmas.add(newWord);
			}
		}
		return lemmas;
	}

	/***************************************************************************
	 * loadHashFromFile - loads a hastable from a file the format of the file is
	 * inflected_word base_form with a space between both words
	 **************************************************************************/
	private HashMap<String, ArrayList<String>> loadHashFromFile(String filename) {
		HashMap<String, ArrayList<String>> hashtable = new HashMap<String, ArrayList<String>>();

		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			StringTokenizer st;

			for (;;) {

				String line = br.readLine();

				if (line == null) {
					br.close();
					break;
				} else {
					st = new StringTokenizer(line, " ");
					ArrayList<String> baseList = new ArrayList<String>();
					String inflected = st.nextToken();
					while (st.hasMoreTokens()) {
						baseList.add(st.nextToken());
					}
					hashtable.put(inflected, baseList);
				}
			}
		} catch (Exception e) {
			// System.out.println(line);
			System.out.println("Error:" + e);
		}

		return hashtable;
	}

	/***************************************************************************
	 * oneString - builds one string from an array of strings
	 **************************************************************************/
	private String oneString(String words[]) {
		String phrase = new String();
		int iLength = words.length;

		for (int i = 0; i < iLength; i++) {
			phrase = phrase.concat(words[i] + " ");
		}

		phrase = phrase.trim();
		// System.out.println(phrase);

		return phrase;
	}

}
