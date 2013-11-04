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

/**
 * Object used to store the information that defines a Head but not its words
 * and phrases. It contains the following attributes:
 * <UL>
 * <LI>Head number</LI>
 * <LI>Head name</LI>
 * <LI>Class number</LI>
 * <LI>Section number</LI>
 * <LI>Sub-section name</LI>
 * <LI>Head group, defined as a list of <TT>HeadInfo</TT> objects</LI>
 * </UL>
 * 
 * @author Mario Jarmasz and Alistsair Kennedy
 * @version 1.1 Aug 2007
 */

public class HeadInfo {
	private int headNum;

	private String headName;

	private int classNum;

	private int sectNum;
	
	private int subSectNum;
	
	private int headGroupNum;

	private String subSectName;

	private ArrayList<String> headGroup;

	/***************************************************************************
	 * Default constructor.
	 **************************************************************************/
	public HeadInfo() {
		headName = new String();
		subSectName = new String();
		headGroup = new ArrayList<String>();
	}

	/***************************************************************************
	 * Constructor which sets the Head number and name, as well as the Class and
	 * Section number, Sub-section name and Head group list.
	 **************************************************************************/
	public HeadInfo(int number, String name, int cn, int sn, String subName,
			ArrayList<String> groupList) {

		headNum = number;
		headName = name;
		classNum = cn;
		sectNum = sn;
		subSectName = subName;
		headGroup = groupList;
		subSectNum = 0;
		headGroupNum = 0;
	}
	
	/***************************************************************************
	 * Constructor which sets the Head number and name, as well as the Class and
	 * Section number, Sub-section name and Head group list.  Contains sub-section
	 * and head group numbers.
	 **************************************************************************/
	public HeadInfo(int number, String name, int cn, int sn, int ssn, int hgn, String subName,
			ArrayList<String> groupList) {

		headNum = number;
		headName = name;
		classNum = cn;
		sectNum = sn;
		subSectNum = ssn;
		headGroupNum = hgn;
		subSectName = subName;
		headGroup = groupList;
	}

	/***************************************************************************
	 * Constructor which sets the Head number and name, as well as the Class and
	 * Section number, Sub-section name and Head group list.
	 * <p>
	 * This constructor parses strings containing the Sub-section name, Head
	 * group list and Head passed in the following format:
	 * 
	 * <PRE>
	 * 
	 * ^subSectionTitle>#Abstract#^/subSectionTitle> ^headGroup 1 2 >
	 * ^headword>#^b>#[001] #1# Existence #^/b>#^/headword>
	 * 
	 * </PRE>
	 * 
	 * The Class and Section numbers are represented by and int. The Sub-section
	 * is an optional element. It can also be an empty String.
	 **************************************************************************/
	public HeadInfo(String sInfo, int cn, int sn, String subSectInfo,
			String sGroupInfo) {
		classNum = cn;
		sectNum = sn;
		parseHead(sInfo);
		if (subSectInfo.equals("") == false) {
			parseSubSect(subSectInfo);
		} else {
			subSectName = "¤";
		}
		parseGroup(sGroupInfo);
	}

	private void parseHead(String sInfo) {
		StringTokenizer st = new StringTokenizer(sInfo, "#");
		st.nextToken();
		st.nextToken();
		st.nextToken();
		headNum = (new Integer(st.nextToken())).intValue();
		headName = st.nextToken().trim();
	}

	private void parseSubSect(String sInfo) {
		StringTokenizer st = new StringTokenizer(sInfo, "#");
		st.nextToken();
		subSectName = st.nextToken().trim();
	}

	// parses a string of type: <headGroup 1 2 >
	private void parseGroup(String sInfo) {
		headGroup = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(sInfo);
		st.nextToken();
		int iTokens = st.countTokens();

		for (int i = 1; i < iTokens; i++) {
			headGroup.add(st.nextToken().trim());
		}

	}

	// add set and get methods...

	/***************************************************************************
	 * Returns the number of this Head.
	 **************************************************************************/
	public int getHeadNum() {
		return headNum;
	}

	/***************************************************************************
	 * Returns the Class number of this Head.
	 **************************************************************************/
	public int getClassNum() {
		return classNum;
	}

	/***************************************************************************
	 * Returns the Section number of this Head.
	 **************************************************************************/
	public int getSectNum() {
		return sectNum;
	}
	
	/***************************************************************************
	 * Returns the Sub-Section number of this Head.
	 **************************************************************************/
	public int getSubSectNum() {
		return subSectNum;
	}
	
	/***************************************************************************
	 * Returns the Head Group number of this Head.
	 **************************************************************************/
	public int getHeadGroupNum() {
		return headGroupNum;
	}

	/***************************************************************************
	 * Returns the Sub-section name of this Head.
	 **************************************************************************/
	public String getSubSectName() {
		return subSectName;
	}

	/***************************************************************************
	 * Returns the array of <TT>HeadGroup</TT> objects of this Head.
	 **************************************************************************/
	public ArrayList<String> getHeadGroup() {
		return headGroup;
	}

	/**
	 * Sets the value of the Head number
	 * 
	 * @param num
	 *            the new value of the Head Number
	 * @return void
	 */

	/***************************************************************************
	 * Sets the number of this Head.
	 **************************************************************************/
	public void setHeadNum(int num) {
		headNum = num;
	}

	/***************************************************************************
	 * Returns the name of this Head.
	 **************************************************************************/
	public String getHeadName() {
		return headName;
	}

	/***************************************************************************
	 * Sets the name of this Head.
	 **************************************************************************/
	public void setHeadName(String name) {
		headName = name;
	}

	/***************************************************************************
	 * Sets the number of this Head.
	 **************************************************************************/
	public void setClassNum(int num) {
		classNum = num;
	}

	/***************************************************************************
	 * Sets the Section number of this Head.
	 **************************************************************************/
	public void setSectNum(int num) {
		sectNum = num;
	}

	/***************************************************************************
	 * Sets the Section name of this Head.
	 **************************************************************************/
	public void setSubSectName(String name) {
		subSectName = name;
	}

	/***************************************************************************
	 * Sets the array of <TT>HeadGroup</TT> objects of this Head.
	 **************************************************************************/
	public void setHeadGroup(ArrayList<String> group) {
		headGroup = group;
	}

	/***************************************************************************
	 * Converts to a string representation the <TT>HeadInfo</TT> object.
	 **************************************************************************/
	public String toString() {
		String info = new String();
		info += classNum + ", " + sectNum + ", " + subSectName + ", "
				+ headGroup + ", " + headNum + ", " + headName;
		return info;
	}

	public void print() {
		System.out.println(toString());
	}

}
