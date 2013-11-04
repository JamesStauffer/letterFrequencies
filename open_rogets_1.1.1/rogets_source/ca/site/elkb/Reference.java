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
 * Represents a symbolic pointer to a location where a specific word or phrase
 * can be found in <i>Roget's Thesaurus</i>. A reference is identified by a
 * keyword, head number and part of speech sequence.
 * <p>
 * An example of a Reference is: <i>obstetrics</i> 167 n. This instance of a
 * <TT>Reference</TT> is represented as:
 * <ul>
 * <li><b>Reference name</b>: obstetrics</li>
 * <li><b>Head number</b>: 167</li>
 * <li><b>Part-of-speech</b>: N.</li>
 * </ul>
 * A Reference is always liked to an index entry, for example: <i>stork</i>.
 * 
 * @serial refName
 * @serial pos
 * @serial headNum
 * @serial indexEntry
 * 
 * @author Mario Jarmasz and Alistsair Kennedy
 * @version 1.1 Aug 2007
 */

public class Reference implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1968676003308407486L;

	// Attributes
	private String refName;

	private String pos;

	private int headNum;

	private String indexEntry;

	// Constructors
	// 1. No params
	// 2. Roget reference (name, pos, head)
	// 3. Single string to be parsed

	/***************************************************************************
	 * Default constructor.
	 **************************************************************************/
	public Reference() {
		this.refName = new String();
		this.pos = new String();
		this.headNum = 0;
		this.indexEntry = new String();
	}

	/***************************************************************************
	 * Constructor which sets the reference name, Head number and
	 * part-of-speech.
	 **************************************************************************/
	public Reference(String name, int head, String p) {
		this.refName = name;
		this.headNum = head;
		this.pos = p;
	}

	/***************************************************************************
	 * Constructor which sets the referebnce name, Head number, part-of-speech,
	 * and Index entry.
	 **************************************************************************/
	public Reference(String name, int head, String p, String entry) {
		this(name, head, p);
		this.indexEntry = entry;
	}

	/***************************************************************************
	 * Constructor that creates a <TT>Reference</TT> object by parsing a
	 * string. An example of a reference that can be parsed is: <CODE>light 417
	 * N.</CODE>. The Index entry is not parsed with the reference.
	 **************************************************************************/
	public Reference(String ref) {
		this();

		StringTokenizer st = new StringTokenizer(ref);
		int phraseLength = st.countTokens() - 2;

		for (int i = 0; i < phraseLength; i++) {
			this.refName += st.nextToken() + " ";
		}
		this.refName = refName.trim();

		Integer head = new Integer(st.nextToken());
		this.headNum = head.intValue();
		this.pos = st.nextToken();
	}

	// Methods

	/***************************************************************************
	 * Converts to a string representation the <TT>Reference</TT> object. The
	 * returned string will be similar to: <BR>
	 * <CODE> word list 87 n. [thesaurus] </CODE><BR>
	 * <CODE> RefName HeadNum POS [IndexEntry] </CODE><BR>
	 * The Index entry is printed only if it has been assigned.
	 **************************************************************************/
	public String toString() {
		return (getIndexEntry().equals("") ? getRefName() + " " + getHeadNum()
				+ " " + getPos() : getRefName() + " " + getHeadNum() + " "
				+ getPos() + " [" + getIndexEntry() + "]");
	}

	/***************************************************************************
	 * Prints this Reference to the standard output. The Refernce is printed
	 * similar to: <BR>
	 * <CODE> word list 87 n. </CODE><BR>
	 * <CODE> RefName HeadNum POS </CODE>
	 **************************************************************************/
	public void print() {
		System.out.println(getRefName() + " " + getHeadNum() + " " + getPos());
	}

	/***************************************************************************
	 * Returns the name of this Reference.
	 **************************************************************************/
	public String getRefName() {
		return this.refName;
	}

	/***************************************************************************
	 * Sets the name of this Reference.
	 **************************************************************************/
	public void setRefName(String name) {
		this.refName = name;
	}

	/***************************************************************************
	 * Returns the part-of-speech of this Reference.
	 **************************************************************************/
	public String getPos() {
		return this.pos;
	}

	/***************************************************************************
	 * Sets the part-of-speech of this Reference.
	 **************************************************************************/
	public void setPos(String p) {
		this.pos = p;
	}

	/***************************************************************************
	 * Returns the Head number of this Reference.
	 **************************************************************************/
	public int getHeadNum() {
		return this.headNum;
	}

	/***************************************************************************
	 * Sets the Head number of this Reference.
	 **************************************************************************/
	public void setHeadNum(int head) {
		this.headNum = head;
	}

	// New methods, added Oct. 2002

	/***************************************************************************
	 * Returns the Index entry of this Reference.
	 **************************************************************************/
	public String getIndexEntry() {
		return this.indexEntry;
	}

	/***************************************************************************
	 * Sets the Index entry of this Reference.
	 **************************************************************************/
	public void setIndexEntry(String entry) {
		this.indexEntry = entry;
	}

} // End of Reference class
