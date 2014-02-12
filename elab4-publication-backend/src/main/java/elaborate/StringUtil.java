package elaborate;

/*
 * #%L
 * elab4-publication-backend
 * =======
 * Copyright (C) 2013 - 2014 Huygens ING
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class StringUtil extends LoggableObject {
	private static final String PAGETITLESEPARATOR = " :: ";
	private static final String APPNAME = "eLaborate3";
	static final String DELIM = "+-*/(),.;'`\"<> ";

	static List<String> hostProtocols = Lists.newArrayList(new String[] { "http", "https" });

	/**
	 * change ULRs in <code>textWithURLs</code> to links
	 * @param textWithURLs text with URLs
	 * @return text with links
	 */
	public static String activateURLs(String textWithURLs) {
		StringTokenizer tokenizer = new StringTokenizer(textWithURLs, "<> ", true);
		StringBuilder replaced = new StringBuilder(textWithURLs.length());
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			//      LOG.info("token={}", token);
			try {
				URL url = new URL(token);
				// If possible then replace with anchor...
				String linktext = token;
				String file = url.getFile();
				if (StringUtils.isNotBlank(file)) {
					linktext = file;
				}
				String protocol = url.getProtocol();
				if (hostProtocols.contains(protocol)) {
					linktext = url.getHost() + linktext;
				}
				replaced.append("<a target=\"_blank\" href=\"" + url + "\">" + linktext + "</a>");
			} catch (MalformedURLException e) {
				replaced.append(token);
			}
		}

		return replaced.toString();
	}

	//  public static String activateURLs(String textWithURLs) {
	//    StringTokenizer tokenizer = new StringTokenizer(textWithURLs, DELIM, true);
	//
	//    List<String> htmlParts = Lists.newArrayList();
	//    for (String part : textWithURLs.split("\\s")) {
	//      try {
	//        URL url = new URL(part);
	//        // If possible then replace with anchor...
	//        String linktext = part;
	//        String file = url.getFile();
	//        if (StringUtils.isNotBlank(file)) {
	//          linktext = file;
	//        }
	//        String protocol = url.getProtocol();
	//        if (hostProtocols.contains(protocol)) {
	//          linktext = url.getHost() + linktext;
	//        }
	//        htmlParts.add("<a target=\"_blank\" href=\"" + url + "\">" + linktext + "</a>");
	//      } catch (MalformedURLException e) {
	//        htmlParts.add(part);
	//      }
	//    }
	//    return Joiner.on(" ").join(htmlParts);
	//  }

	public static String pageTitle(String... subtitles) {
		List<String> parts = Lists.newArrayList(APPNAME);
		parts.addAll(Lists.newArrayList(subtitles));
		return Joiner.on(PAGETITLESEPARATOR).join(parts);
	}

	public static String escapeQuotes(String string) {
		return string.replaceAll("\"", "\\\\\"").replaceAll("'", "\\\\'");
	}

	static List<String> XML_ENTITIES = Lists.newArrayList("quot", "amp", "apos", "lt", "gt");

	public static String fixXML(String brokenxml) {
		String fixedXml = brokenxml.replaceAll("&", "&amp;");
		for (String entity : XML_ENTITIES) {
			fixedXml = fixedXml.replaceAll("&amp;" + entity + ";", "&" + entity + ";");
		}
		Pattern p = Pattern.compile("<([^<]*?)>");
		Matcher matcher = p.matcher(fixedXml);
		while (matcher.find()) {
			String group = matcher.group();
			String replacement = "&0&" + matcher.group(1) + "&1&";
			fixedXml = fixedXml.replace(group, replacement);
		}
		fixedXml = fixedXml.replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("&0&", "<").replaceAll("&1&", ">");
		return fixedXml;
	}

	private StringUtil() {
		throw new AssertionError("Non-instantiable class");
	}
}
