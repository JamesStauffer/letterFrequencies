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
 * This class loads a head file using an instance of the Head
 * class who's methods are used.
 * 
 * @author Alistair Kennedy
 * @version 1.0 Jun 2007
 */
public class HeadHandler extends DefaultHandler {

	private Head head;
	private StringBuffer sb;
	private Paragraph currentPara;
	private SG currentSG;
	private ArrayList<String> currentSGWords;
	private String currentPOS;
	private boolean recordWord;
	private int refCount;
	private int headNumber;
	private int currentSGNum;
	
	/**
	 * Initializes the class, when it is passed an instance of
	 * the Head class
	 * 
	 * @param h
	 */
	public HeadHandler(Head h){
		super();
		head = h;
		sb = new StringBuffer();
		currentPara = new Paragraph();
		currentSG = new SG();
		currentPOS = "";
		recordWord = false;
		currentSGWords = new ArrayList<String>();
		refCount = 0;
		headNumber = 0;
		currentSGNum = 0;
	}

	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		if(localName.equals("head")){
			head.setClassNum(Integer.parseInt(atts.getValue("class")));
			head.setSectionNum(Integer.parseInt(atts.getValue("section")));
			head.setSubSectionNum(Integer.parseInt(atts.getValue("subSection")));
			head.setHeadGroupNum(Integer.parseInt(atts.getValue("headGroup")));
			head.setHeadName(atts.getValue("name"));
			headNumber = Integer.parseInt(atts.getValue("number"));
			head.setHeadNum(headNumber);
		}
		else if(localName.equals("pos")){
			currentPOS = atts.getValue("type");
			head.setPOSStart(currentPOS);
		}
		else if(localName.equals("paragraph")){
			head.incrementParaCount();
			currentPara = new Paragraph(headNumber, Integer.parseInt(atts.getValue("number")), atts.getValue("name"), currentPOS);
		}
		else if(localName.equals("sg")){
			currentSGWords = new ArrayList<String>();
			currentSGNum = Integer.parseInt(atts.getValue("number"));
			refCount = 0;
		}
		else if(localName.equals("word")){
			recordWord = true;
			if(atts.getIndex("headRef") == 1){
				refCount++;
			}
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		if(localName.equals("word")){
			recordWord = false;
			currentSGWords.add(sb.toString());
			sb = new StringBuffer();
		}
		else if(localName.equals("sg")){
			currentSG = new SG(currentSGNum, currentPara.getParaNum(), headNumber, currentSGWords, currentPOS);
			currentPara.addSG(currentSG);
		}
		else if(localName.equals("paragraph")){
			head.addPara(currentPara, currentPOS);
		}
	}

	public void characters(char[] chars, int start, int length) throws SAXException {
		if(recordWord){
			sb.append(new String(chars, start, length));
		}
	}
}
