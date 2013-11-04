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

/**
 * Represents a <i>Roget's Thesaurus</i> relation between a word or phrase.
 * This can be a Cross-reference or a See reference. For example:
 * <ul>
 * <li>See <i>drug taking</i></li>
 * <li>646 <i>perfect</i></li>
 * </ul>
 * Relation types currently used by the <i>ELKB</i> are <TT>cref</TT> and
 * <TT>see</TT>.
 * 
 * @author Mario Jarmasz and Alistsair Kennedy
 * @version 1.1 Aug 2007
 */

public class SemRel extends Reference {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5128354271966119550L;
	// Attributes
	private String type;

	// Constructors
	// 1. No params
	// 2. Roget reference (type, headNum, refName)
	// 3. ELKB reference (same as Roget plus pos, paraNum, sgNum)

	/***************************************************************************
	 * Default constructor.
	 **************************************************************************/
	public SemRel() {
		super();
		type = new String();
	}

	/***************************************************************************
	 * Constructor which sets the relation type, Head number and Reference name.
	 **************************************************************************/
	public SemRel(String t, int headNum, String refName) {
		this();
		type = t;
		setHeadNum(headNum);
		setRefName(refName);
	}

	private SemRel(String t, int headNum, String refName, String pos, int paraNum, int sgNum) {
		this(t, headNum, refName);
		setPos(pos);
		// setParaNum(paraNum);
		// setSgNum(sgNum);
	}

	// Methods

	/***************************************************************************
	 * Returns the relation type.
	 **************************************************************************/
	public String getType() {
		return type;
	}

	/***************************************************************************
	 * Sets the relation type.
	 **************************************************************************/
	public void setType(String t) {
		type = t;
	}

	/***************************************************************************
	 * Converts to a string representation the <TT>SemRel</TT> object.
	 **************************************************************************/
	public String toString() {
		String info = new String();
		info = "SemRel" + "@" + Integer.toHexString(hashCode());
		info += "@" + getType();
		info += "@" + getHeadNum() + "@" + getRefName();
		// info += getPos() + "@" + getParaNum() + "@" + getSgNum();
		return info;
	}

	/***************************************************************************
	 * Prints this relation to the standard output.
	 **************************************************************************/
	public void print() {
		String info = new String();
		info = getType() + ": " + getHeadNum() + " " + getRefName();
		System.out.println(info);
	}

} // End of SemRel class
