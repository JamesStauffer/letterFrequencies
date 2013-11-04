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
 * Allows to obtain a variant of an English spelling. A British spelling variant
 * can be obtained form an American spelling and vice-versa.
 * 
 * <p>
 * The default American and British word list is <TT>AmBr.lst</TT> contained
 * in the <TT>$HOME/roget_elkb</TT> directory. It is loaded by the default
 * constructor.
 * </p>
 * 
 * @author Mario Jarmasz and Alistsair Kennedy
 * @version 1.1 Aug 2007
 */

public class Variant implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6871983966165029804L;

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
	 * Location of the default American and British spelling word list.
	 **************************************************************************/
	// Name of file that contains American to British spelling
	public static final String AMBR_FILE = ELKB_PATH + "/AmBr.lst";

	// only contains one hastable?
	private HashMap<String, String> amBrHash;

	private HashMap<String, String> brAmHash;

	// Constructors
	// 1. Default
	// 2. Name of the text file to load

	/***************************************************************************
	 * Default constructor.
	 **************************************************************************/
	public Variant() {
		amBrHash = new HashMap<String, String>();
		brAmHash = new HashMap<String, String>();
		loadFromFile(AMBR_FILE);
	}

	/***************************************************************************
	 * Constructor that builds the <TT>Variant</TT> object using the
	 * information contained in the specified file. This file must contain only
	 * the American and British spellings in the following format: <BR>
	 * <CODE>American spelling:British spellling</CODE>. </BR> For example:
	 * <BR>
	 * <CODE>airplane:aeroplane</CODE> <BR>
	 **************************************************************************/
	public Variant(String filename) {
		amBrHash = new HashMap<String, String>();
		brAmHash = new HashMap<String, String>();
		loadFromFile(filename);
	}

	private void loadFromFile(String filename) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			StringTokenizer st;

			for (;;) {
				String line = new String();
				String american = new String();
				String british = new String();

				line = br.readLine();

				if (line == null) {
					br.close();
					break;
				} else {
					st = new StringTokenizer(line, ":");
					american = st.nextToken();
					british = st.nextToken();
					amBrHash.put(american, british);
					brAmHash.put(british, american);
				}
			}
		} catch (Exception e) {
			// System.out.println(line);
			System.out.println("Error:" + e);
		}
	}

	/***************************************************************************
	 * Returns the British spelling of a word, or <TT>null</TT> if the word
	 * cannot be found.
	 **************************************************************************/
	public String amToBr(String american) {
		return amBrHash.get(american);
	}

	/***************************************************************************
	 * Returns the American spelling of a word, or <TT>null</TT> if the word
	 * cannot be found.
	 **************************************************************************/
	public String brToAm(String british) {
		return brAmHash.get(british);
	}

}
