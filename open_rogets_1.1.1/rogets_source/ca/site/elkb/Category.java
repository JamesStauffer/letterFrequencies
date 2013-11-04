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

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Represents the <i>Roget's Thesaurus Tabular Synopsis of Categories</i>. The
 * topmost level of this ontology divides the <i>Thesaurus</i> into eight
 * Classes:
 * <OL>
 * <LI><i>Abstract Relations</i></LI>
 * <LI><i>Space</i></LI>
 * <LI><i>Matter</i></LI>
 * <LI><i>Intellect: the exercise of the mind (Formation of ideas)</i></LI>
 * <LI><i>Intellect: the exercise of the mind (Communication of ideas)</i></LI>
 * <LI><i>Volition: the exercise of the will (Individual volition)</i></LI>
 * <LI><i>Volition: the exercise of the will (Social volition)</i></LI>
 * <LI><i>Emotion, religion and morality</i></LI>
 * </OL>
 * <br>
 * <p>
 * Classes are further divided into Sections, Sub-sections, Head groups, and
 * Heads.
 * </p>
 * 
 * @author Mario Jarmasz and Alistsair Kennedy
 * @version 1.1 Aug 2007
 */

public class Category {
	// Attributes
	private int classCount;

	private int sectionCount;

	private int subSectionCount;

	private int headGroupCount;

	private int headCount;

	private ArrayList<RogetClass> classList;

	private ArrayList<HeadInfo> headList;

	// Constructors
	// 1. No params
	// 2. Filename

	/***************************************************************************
	 * Default constructor.
	 **************************************************************************/
	public Category() {
		classCount = 0;
		sectionCount = 0;
		headCount = 0;
		subSectionCount = 0;
		headGroupCount = 0;
		classList = new ArrayList<RogetClass>();
		headList = new ArrayList<HeadInfo>();
	}

	/***************************************************************************
	 * Constructor that builds the <TT>Category</TT> object using the
	 * information contained in a file. The default file for the <i>ELKB</i> is
	 * <TT>rogetMap.rt</TT> contained in the <TT>$HOME/roget_elkb</TT>
	 * directory.
	 **************************************************************************/
	public Category(String filename) {
		this();
		loadFromFile(filename);
	}

	// Get methods only

	/***************************************************************************
	 * Returns the number of <i>Roget's</i> Classes in this ontology.
	 **************************************************************************/
	public int getClassCount() {
		return classCount;
	}

	/***************************************************************************
	 * Returns the number of Sections in this ontology.
	 **************************************************************************/
	public int getSectionCount() {
		return sectionCount;
	}

	/***************************************************************************
	 * Returns the number of Sub-sections in this ontology.
	 **************************************************************************/
	public int getSubSectionCount() {
		return subSectionCount;
	}

	/***************************************************************************
	 * Returns the number of Head groups in this ontology.
	 **************************************************************************/
	public int getHeadGroupCount() {
		return headGroupCount;
	}

	/***************************************************************************
	 * Returns the number of Heads in this ontology.
	 **************************************************************************/
	public int getHeadCount() {
		return headCount;
	}

	// Methods for manipulating the classList

	private void addClass(RogetClass rogClass) {
		classCount++;
		rogClass.setClassNum(classCount);
		classList.add(rogClass);
	}

	private void deleteClass(RogetClass rogClass) {
		// Maybe this method should be boolean?
		// Does nothing for now
	}

	/***************************************************************************
	 * Returns the <i>Roget's</i> Class at the specified position in the array
	 * of Classes.
	 **************************************************************************/
	public RogetClass getRogetClass(int index) {
		RogetClass rogClass;
		index--;
		if ((index >= 0) && (index < classCount)) {
			rogClass = classList.get(index);
		} else {
			rogClass = null;
		}
		return rogClass;
	}

	/***************************************************************************
	 * Prints the <i>Roget's</i> Class at the specified position in the array
	 * of Classes to the standard output.
	 **************************************************************************/

	// It would be nice to have the same method with a string
	// I will have to override the equals method
	public void printRogetClass(int index) {
		RogetClass rogClass = getRogetClass(index);
		if (rogClass == null) {
			System.out.println(index + " is not a valid Class number");
		} else {
			rogClass.print();
		}
	}

	private void loadFromFile(String fileName) {
		try{
			//System.out.println("Loading from: " + fileName);
			
			System.setProperty("org.xml.sax.driver","org.apache.crimson.parser.XMLReaderImpl");
			
			XMLReader xr = XMLReaderFactory.createXMLReader();
			CategoryHandler handler = new CategoryHandler(this);
			xr.setContentHandler(handler);
			xr.setErrorHandler(handler);
			
			xr.parse(fileName);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*private void loadFromFile2(String filename) {
		// Loads the entire classification system
		int iSection = 0;
		line = new String();
		RogetClass rogClass;
		String subSectInfo = new String();
		String sGroupInfo = new String();
		String sHeadInfo = new String();
		HeadInfo rogetHead;
		RogetClass rtClass = new RogetClass();
		Section rtSection = new Section();

		try {
			br = new BufferedReader(new FileReader(filename));
			line = br.readLine();
			while (!(line == null)) {

				if (line.startsWith("<classNumber>")) {
					String strClassNum = line;
					String strClassName = br.readLine();
					classCount++;
					iSection = 0;
					rtClass = new RogetClass(classCount, strClassNum, strClassName);

				} else if (line.startsWith("<sectionNumber>")) {
					String strSectNum = line;
					String strSectName = br.readLine();
					iSection++;
					subSectInfo = "";
					rtSection = new Section(iSection, strSectNum, strSectName);

				} else if (line.startsWith("<subSectionTitle>")) {
					subSectInfo = new String(line);
					subSectionCount++;
					// rtSection.addSubSection(subSectInfo);

				} else if (line.startsWith("<headGroup")) {
					sGroupInfo = new String(line);
					headGroupCount++;

				} else if (line.startsWith("<headword")) {
					sHeadInfo = new String(line);
					rogetHead = new HeadInfo(sHeadInfo, classCount, iSection,
							subSectInfo, sGroupInfo);
					headList.add(rogetHead);

				} else if (line.startsWith("</section")) {
					rtClass.addSection(rtSection);

				} else if (line.startsWith("</class")) {
					classList.add(rtClass);
				}

				line = br.readLine();

			}
			br.close();
		} catch (IOException ioe) {
			System.out.println("IO error:" + ioe);
		}
	}*/

	/***************************************************************************
	 * Returns the array of <TT>RogetClass</TT> objects.
	 **************************************************************************/
	public ArrayList<RogetClass> getClassList() {
		return classList;
	}

	/***************************************************************************
	 * Returns the array of <TT>HeadInfo</TT> objects.
	 **************************************************************************/
	public ArrayList<HeadInfo> getHeadList() {
		return headList;
	}

	/***************************************************************************
	 * Prints the array of <TT>HeadInfo</TT> objects to the standard output.
	 **************************************************************************/
	public void printHeadInfo() {
		Iterator<HeadInfo> iter = headList.iterator();
		while (iter.hasNext())
			System.out.println(iter.next());
	}

	/***************************************************************************
	 * Converts to a string representation the <TT>Category</TT> object. The
	 * following following format is used - <TT>Category:classCount:sectionCount:subSectionCount:headGroupCount:headCount</TT>.
	 **************************************************************************/
	public String toString() {
		StringBuffer sbInfo = new StringBuffer();

		sbInfo.append("Category:");
		sbInfo.append(getClassCount());
		sbInfo.append(":");
		sbInfo.append(getSectionCount());
		sbInfo.append(":");
		sbInfo.append(getSubSectionCount());
		sbInfo.append(":");
		sbInfo.append(getHeadGroupCount());
		sbInfo.append(":");
		sbInfo.append(getHeadCount());

		return sbInfo.toString();
	}

	public void classCountIncrement() {
		classCount++;
	}

	public void addToClassList(RogetClass rtClass) {
		classList.add(rtClass);
	}

	public void subSectionCountIncrement() {
		subSectionCount++;
	}
	
	public void headGroupCountIncrement() {
		headGroupCount++;
	}

	public void addToHeadList(HeadInfo rogetHead) {
		headList.add(rogetHead);
	}

	public void sectionCountIncrement() {
		sectionCount++;
	}
	
	public void headCountIncrement() {
		headCount++;
	}
	
}

// End of the Category class
