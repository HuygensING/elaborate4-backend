package elaborate.util;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2015 Huygens ING
 * =======
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import nl.knaw.huygens.Log;

public class XmlUtil {
	private static final String XML_CLOSE_TAG = "</xml>";
	private static final String XML_OPEN_TAG = "<xml>";

	public static String unwrapFromXml(String xml) {
		return xml.replaceFirst(XML_OPEN_TAG, "").replaceFirst(XML_CLOSE_TAG, "").replaceAll("&apos;", "'");
	}

	public static String wrapInXml(String xmlContent) {
		return XML_OPEN_TAG + xmlContent + XML_CLOSE_TAG;
	}

	public static boolean isWellFormed(String body) {
		try {
			SAXParser parser;
			parser = SAXParserFactory.newInstance().newSAXParser();
			DefaultHandler dh = new DefaultHandler();
			parser.parse(new InputSource(new StringReader(body)), dh);
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
			return false;
		} catch (SAXException e1) {
			e1.printStackTrace();
			Log.error("body={}", body);
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static String fixXhtml(String badxml) {
		Document doc = Jsoup.parse(badxml);
		doc.outputSettings().indentAmount(0).prettyPrint(false).escapeMode(Entities.EscapeMode.xhtml).charset("UTF-8");
		return doc.body().html().replaceAll(" />", "/>").replace("\u00A0", "&#160;");
		//    return Jsoup.clean(badxml, Whitelist.relaxed());
	}

	static final Pattern ENDTAG_AFTER_NEWLINE_PATTERN = Pattern.compile("\n(</.*?>)"); // endtag at the beginning of line, should be at end of previouse line

	public static String fixTagEndings(String body) {
		String newBody = body;
		Matcher matcher = ENDTAG_AFTER_NEWLINE_PATTERN.matcher(body);
		while (matcher.find()) {
			String endTag = matcher.group(1);
			newBody = newBody.replaceFirst("\n" + endTag, endTag + "\n");
			matcher = ENDTAG_AFTER_NEWLINE_PATTERN.matcher(newBody);
		}
		return newBody;
	}

	public static String removeXMLtags(String xml) {
		return xml.replaceAll("<.*?>", "");
	}

	private XmlUtil() {
		throw new AssertionError("Non-instantiable class");
	}

}
