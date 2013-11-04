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

import java.util.*;

/*******************************************************************************
 * Represents a <i>Roget's Thesaurus</i> Sub-section.
 * 
 * A Sub-section may or may not exist. Here is an example:
 * <UL>
 * <LI><b>Class one</b>: Abstract Relations</LI>
 * <LI><b>Section one</b>: Existence</LI>
 * <LI><b>Sub-section title</b>: Abstract</LI>
 * <LI><b>Head group</b>:1 Existence - 2 Nonexistence</LI>
 * </UL>
 * 
 * Sub-sections may contain several Head groups.
 * 
 * @author Mario Jarmasz and Alistsair Kennedy
 * @version 1.1 Aug 2007
 ******************************************************************************/

// MJ: May, 2003
// This class does not seem complete nor does it seem to be used
// much by the ELKB!
public class SubSection {

	// Attributes
	private String name;

	private int headCount;

	private int headStart;

	private int groupCount;

	private ArrayList<Group> groupList;

	/***************************************************************************
	 * Default constructor.
	 **************************************************************************/
	public SubSection() {
		headCount = 0;
		headStart = 0;
		groupCount = 0;
		groupList = new ArrayList<Group>();
		name = new String();
	}

	/***************************************************************************
	 * Constructor which sets the number of the first Head.
	 **************************************************************************/
	public SubSection(int start) {
		this();
		headStart = start;
	}

	/***************************************************************************
	 * Constructor which sets the name of the Section by parsing a string. An
	 * example of a string to parse is: <BR>
	 * <CODE> ^subSectionTitle>#Abstract#^/subSectionTitle> </CODE>
	 **************************************************************************/
	public SubSection(String sInfo) {
		headCount = 0;
		headStart = 0;
		groupCount = 0;
		groupList = new ArrayList<Group>();
		Parse(sInfo);
	}

	/***************************************************************************
	 * Constructor which sets the number of the first Head and the name of the
	 * Section supplied as a string to be parsed. An example of a string to
	 * parse is: <BR>
	 * <CODE> ^subSectionTitle>#Abstract#^/subSectionTitle> </CODE>
	 **************************************************************************/
	public SubSection(int start, String sInfo) {
		this(sInfo);
		headStart = start;
	}

	/***************************************************************************
	 * Method to parse a string of type: <subSectionTitle>#Abstract#</subSectionTitle>
	 **************************************************************************/
	private void Parse(String sInfo) {
		StringTokenizer st = new StringTokenizer(sInfo, "#");
		st.nextToken();
		name = st.nextToken().trim();
	}

	/***************************************************************************
	 * Returns the list of Head groups in this Sub-section.
	 **************************************************************************/
	public ArrayList<Group> getGroupList() {
		return groupList;
	}

	/***************************************************************************
	 * Adds a Head Group to this Sub-section.
	 **************************************************************************/
	public void addGroup(Group group) {
		groupList.add(group);
		groupCount++;
		headCount += group.getHeadCount();
	}

	/***************************************************************************
	 * Returns the number of Heads in this Sub-section.
	 **************************************************************************/
	public int getHeadCount() {
		return headCount;
	}

	/***************************************************************************
	 * Returns the number of Head groups in this Sub-section.
	 **************************************************************************/
	public int getGroupCount() {
		return groupCount;
	}

	/***************************************************************************
	 * Sets the number of the first Head in this Sub-section.
	 **************************************************************************/
	public void setHeadStart(int start) {
		headStart = start;
	}

	/***************************************************************************
	 * Returns the number of the first Head in this Sub-section.
	 **************************************************************************/
	public int getHeadStart() {
		return headStart;
	}

	/***************************************************************************
	 * Displays the content of a Sub-section in a similar way to <i>Roget's
	 * Thesaurus Tabular Synopisis of Categories</i> to the standard output. An
	 * example of this display is: <BR>
	 * <CODE> Absolute: 9 Relation 10 Unrelatedness <BR>
	 * 11 Consanguinity <BR>
	 * 12 Correlation <BR>
	 * 13 Identity 14 Contrariety <BR>
	 * 15 Difference </CODE>
	 **************************************************************************/
	public void print() {
		System.out.println(toString());
	}

	/***************************************************************************
	 * Converts to a string representation the <TT>SubSection</TT> object.
	 **************************************************************************/
	public String toString() {
		String info = new String();
		info += name + ": \t";
		// info +=
		// print all groups
		// info +=
		// print groups
		return info;
	}

}
