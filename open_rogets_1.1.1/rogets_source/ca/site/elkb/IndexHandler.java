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
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
/**
 * This class loads the Index html file and used functions from 
 * the Index class to create a new index.
 * 
 * @author Alistair Kennedy
 * @version 1.0 Jun 2007
 */
public class IndexHandler extends DefaultHandler {

	private String keyWord;
	private String paragraphWord;
	private boolean isKeyWord;
	private boolean isLocation;
	private Index index;
	private StringBuffer sb;
	
	/**
	 * Loads constructs the class and is passed an Index object
	 * who's methods will be called when building the new index. 
	 * 
	 * @param ind
	 */
	public IndexHandler(Index ind){
		super();
		index = ind;
		keyWord = "";
		paragraphWord = "";
		sb = new StringBuffer();
	}

	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		if (localName.equals("word")){
			isKeyWord = true;
		}
		if(localName.equals("location")){
			isLocation = true;
			paragraphWord = atts.getValue("para");
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (localName.equals("location")){
			isLocation = false;
			String location = sb.toString();
			String[] parts = location.split(" ");
			if(parts.length != 9){
				System.err.println("Parse Error: " + parts.length + " : " + keyWord + " - " + paragraphWord + " " + location);
			}
			else{
				String entry = paragraphWord + "," + parts[0] + "," + parts[1] + "," + parts[2] + "," + parts[3] + "," + parts[4] + "," + parts[5] + "," + parts[6];
				String strPtr = "";
				strPtr = index.addReference(strPtr, entry, parts[7], parts[8]);
				index.addEntry(keyWord, strPtr);
			}
			sb = new StringBuffer();
		}
		if(localName.equals("word")){
			isKeyWord = false;
			keyWord = sb.toString();
			sb = new StringBuffer();

		}
		if (localName.equals("entry")){
			keyWord = "";
		}
	}

	public void characters(char[] chars, int start, int length) throws SAXException {
		if(isKeyWord){
			sb.append(new String(chars, start, length));
		}
		if(isLocation){
			sb.append(new String(chars, start, length));
		}
	}
	
}
