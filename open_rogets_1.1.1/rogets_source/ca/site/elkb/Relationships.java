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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Relationships implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5293972580226784101L;

	private HashMap<String, ArrayList<String>> hypernyms;

	private HashMap<String, ArrayList<String>> hyponyms;

	public Relationships(String fileName) {
		hypernyms = new HashMap<String, ArrayList<String>>();
		hyponyms = new HashMap<String, ArrayList<String>>();
		loadFromFile(fileName);
	}

	/***************************************************************************
	 * loadFromFile
	 **************************************************************************/
	private void loadFromFile(String fileName) {
		String line = new String();

		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			for (;;) {
				line = br.readLine();

				if (line == null) {
					br.close();
					break;
				} else {
					String[] parts = line.split("\t");
					if (!hypernyms.containsKey(parts[1] + ":" + parts[2])) {
						ArrayList<String> al = new ArrayList<String>();
						al.add(parts[4]);
						hypernyms.put(parts[1] + ":" + parts[2], al);
					} else {
						ArrayList<String> al = hypernyms.get(parts[1] + ":" + parts[2]);
						al.add(parts[4]);
						hypernyms.put(parts[1] + ":" + parts[2], al);
					}
					if (!hyponyms.containsKey(parts[3] + ":" + parts[4])) {
						ArrayList<String> al = new ArrayList<String>();
						al.add(parts[2]);
						hyponyms.put(parts[3] + ":" + parts[4], al);
					} else {
						ArrayList<String> al = hyponyms.get(parts[3] + ":" + parts[4]);
						al.add(parts[2]);
						hyponyms.put(parts[3] + ":" + parts[4], al);
					}
				}
			}
		} catch (Exception e) {
			System.out.println(line);
			System.out.println("Error:" + e);
		}
	}

	/**
	 * This method takes in a word with its head and paragraph number and
	 * returns an ArrayList of hypernyms for that sense of the word. Only
	 * immediate hypernyms are returned.
	 * 
	 * @param head
	 * @param para
	 * @param word
	 * @return
	 */
	public ArrayList<String> getHypernyms(int head, int para, String word) {
		ArrayList<String> al = new ArrayList<String>();
		if (hypernyms.containsKey(head + ":" + para + ":" + word)) {
			al = hypernyms.get(head + ":" + para + ":" + word);
		}
		return al;
	}

	/**
	 * This method takes ina word with its head and paragraph umber and returns
	 * an ArrayList of hyponyms for that sense of the word. Only immediate
	 * hyponyms are returned.
	 * 
	 * @param head
	 * @param para
	 * @param word
	 * @return
	 */
	public ArrayList<String> getHyponyms(int head, int para, String word) {
		ArrayList<String> al = new ArrayList<String>();
		if (hyponyms.containsKey(head + ":" + para + ":" + word)) {
			al = hyponyms.get(head + ":" + para + ":" + word);
		}
		return al;
	}

}
