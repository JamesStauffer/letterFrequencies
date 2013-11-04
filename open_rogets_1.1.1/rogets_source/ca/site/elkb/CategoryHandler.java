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

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class loads the categories from an xml file.  The class
 * requires an instance of the Category class to be passed in
 * the constructor.  Methods from the category class are used
 * by this class.
 * 
 * @author Alistair Kennedy
 * @version 1.0 Jun 2007
 */
public class CategoryHandler extends DefaultHandler {
	private Section rtSection;
	private String subSectInfo;
	private int subSectNum;
	private ArrayList<String> sGroupInfo;
	private int headGroupNum;
	private int iSection;
	private RogetClass rtClass;
	private Category category;
	
	/**
	 * Initializes the CategoryHandler and is passed an instance
	 * of the Category class.
	 * 
	 * @param c
	 */
	public CategoryHandler(Category c) {
		rtSection = new Section();
		subSectInfo = new String();
		subSectNum = 0;
		headGroupNum = 0;
		sGroupInfo = new ArrayList<String>();
		iSection = 0;
		rtClass = new RogetClass();
		category = c;
	}
	
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		if(localName.equals("thesaurus")){
			//do nothing
		}
		else if(localName.equals("class")){
			String strClassNum = atts.getValue("number");
			String strClassName = atts.getValue("name");
			category.classCountIncrement();
			iSection = 0;
			rtClass = new RogetClass(category.getClassCount(), strClassNum,
					strClassName);
		}
		else if(localName.equals("section")){
			String strSectNum = atts.getValue("number");
			String strSectName = atts.getValue("name");
			iSection++;
			category.sectionCountIncrement();
			subSectInfo = "";
			rtSection = new Section(iSection, strSectNum, strSectName);
		}
		else if(localName.equals("subsection")){
			subSectInfo = atts.getValue("name");
			subSectNum = Integer.parseInt(atts.getValue("number"));
			category.subSectionCountIncrement();
		}
		else if(localName.equals("headGroup")){
			String start = atts.getValue("first");
			String end = atts.getValue("last");
			headGroupNum = Integer.parseInt(atts.getValue("number"));
			sGroupInfo = new ArrayList<String>();
			for(int i = Integer.parseInt(start); i <= Integer.parseInt(end); i++){
				sGroupInfo.add(i + "");
			}
			category.headGroupCountIncrement();
		}
		else if(localName.equals("head")){
			String headName = atts.getValue("name");
			String headNumber = atts.getValue("number");
			HeadInfo rogetHead = new HeadInfo(Integer.parseInt(headNumber), headName, category.getClassCount(), iSection,
					subSectNum, headGroupNum, subSectInfo, sGroupInfo);
			category.headCountIncrement();
			category.addToHeadList(rogetHead);
		}
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if(localName.equals("class")){
			category.addToClassList(rtClass);
		}
		if(localName.equals("section")){
			rtClass.addSection(rtSection);
		}
	}

}
