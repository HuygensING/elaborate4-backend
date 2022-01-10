package elaborate.util;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2022 Huygens ING
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

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import nl.knaw.huygens.Log;
import nl.knaw.huygens.jaxrstools.filters.CORSFilter;

public class CNWUtil {
  CORSFilter c = null;

  public String convertDate(String dateString) {
    String edtf =
        dateString
            .replaceAll("-XX", "")
            .replaceAll("[\\[\\]]", "")
            .replaceAll(" wsch.", "?")
            .replaceAll("X", "u")
            .replace("{", "")
            .replace("}", "")
            .replace(",", "/")
            .replace("..", "")
            .replaceFirst("/.*/", "/");

    if (edtf.contains("±")) {
      edtf = edtf.replaceAll("±", "") + "~";
    }
    if (edtf.contains("(") || edtf.contains(")")) {
      edtf = edtf.replaceAll("[()]", "");
    }
    if (edtf.contains("/unknown")) {
      edtf = edtf.replaceAll("/unknown", "") + "~";
    }
    if (edtf.contains("~?")) {
      edtf = edtf.replaceAll("~\\?", "") + "?";
    }
    if (edtf.contains("?~")) {
      edtf = edtf.replaceAll("\\?~", "") + "?";
    }
    if (edtf.contains("?")) {
      edtf = edtf.replaceAll("\\?", "") + "?";
    }
    if (edtf.contains("~")) {
      edtf = edtf.replaceAll("~", "") + "~";
    }
    if (edtf.contains("u")) {
      edtf = edtf.replace("u", "0") + "/" + edtf.replace("u", "9");
    }
    if (edtf.contains(" CHECK")) {
      edtf = edtf.replace(" CHECK", "");
    }

    if (dateString.contains("_")) {
      String[] parts = dateString.replaceAll("-XX", "").split("_", 2);
      int length0 = parts[0].replaceAll(" .*", "").length();
      int length1 = parts[1].replaceAll(" .*", "").length();
      if (length0 > length1) {
        parts[1] = parts[0].substring(0, length0 - length1) + parts[1];
        // } else if (length1 > length0) {
        // parts[0] = parts[0] + parts[1].substring(length0);
      }
      String first = convertDate(parts[0]);
      if (first.contains("/")) {
        String[] firstParts = first.split("/", 2);
        first = firstParts[0];
      }
      String last = convertDate(parts[1]);
      if (last.contains("/")) {
        String[] lastParts = last.split("/", 2);
        last = lastParts[1];
      }
      edtf = first + "/" + last;

    } else if (dateString.contains("[ca.]")) {
      edtf = edtf.replace(" ca.", "");
      if (edtf.length() == 10) {
        Date date = extractDate(edtf);
        String lower = getDateByAdding(date, -1, Calendar.MONTH);
        String upper = getDateByAdding(date, 1, Calendar.MONTH);
        edtf = lower + "/" + upper;
      } else {
        String[] parts = edtf.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        if (month == 1) {
          edtf =
              MessageFormat.format(
                  "{0}-12/{1}-02", String.valueOf((year - 1)), String.valueOf(year));
        } else if (month == 12) {
          edtf =
              MessageFormat.format(
                  "{0}-11/{1}-01", String.valueOf(year), String.valueOf((year + 1)));
        } else {
          edtf =
              MessageFormat.format(
                  "{0}-{1}/{2}-{3}",
                  String.valueOf(year),
                  String.format("%02d", month - 1),
                  String.valueOf(year),
                  String.format("%02d", month + 1));
        }
      }

    } else if (dateString.contains("[vóór]")) {
      edtf = edtf.replace(" vóór", "");
      if (edtf.length() == 10) {
        Date date = extractDate(edtf);
        String lower = getDateByAdding(date, -6, Calendar.MONTH);
        String upper = getDateByAdding(date, -1, Calendar.DAY_OF_MONTH);
        edtf = lower + "/" + upper;
      } else {
        String[] parts = edtf.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int firstYear = year;
        int lastYear = year;
        int firstMonth = month - 6;
        int lastMonth = month - 1;
        if (month < 7) {
          firstYear = year - 1;
          firstMonth = 12 + firstMonth;
        }
        if (month == 1) {
          lastYear = year - 1;
          firstMonth = 7;
        }
        edtf =
            MessageFormat.format(
                "{0}-{1}/{2}-{3}",
                String.valueOf(firstYear),
                String.format("%02d", firstMonth),
                String.valueOf(lastYear),
                String.format("%02d", lastMonth));
      }

    } else if (dateString.contains("[of vóór]")) {
      edtf = edtf.replace(" of vóór", "");
      Date date = extractDate(edtf);
      String lower = getDateByAdding(date, -6, Calendar.MONTH);
      String upper = getDateByAdding(date, 0, Calendar.DAY_OF_MONTH);
      edtf = lower + "/" + upper;

    } else if (dateString.contains("[na]")) {
      edtf = edtf.replace(" na", "");
      Date date = extractDate(edtf.replaceAll(" ", ""));
      String lower = getDateByAdding(date, 1, Calendar.DAY_OF_MONTH);
      String upper = getDateByAdding(date, 6, Calendar.MONTH);
      edtf = lower + "/" + upper;

    } else if (dateString.contains("[of na]")) {
      edtf = edtf.replace(" of na", "");
      Date date = extractDate(edtf);
      String lower = getDateByAdding(date, 0, Calendar.DAY_OF_MONTH);
      String upper = getDateByAdding(date, 6, Calendar.MONTH);
      edtf = lower + "/" + upper;

    } else if (dateString.contains("[begin]")) {
      edtf = edtf.replace(" begin", "");
      if (edtf.length() == 7) {
        edtf = MessageFormat.format("{0}-01/{1}-14", edtf, edtf);
      } else {
        edtf = MessageFormat.format("{0}-01/{1}-06", edtf, edtf);
      }

    } else if (dateString.contains("[eind]")) {
      edtf = edtf.replace(" eind", "");
      if (edtf.length() == 7) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Integer.parseInt(edtf.substring(0, 4)));
        cal.set(Calendar.MONTH, Integer.parseInt(edtf.substring(5, 7)) - 1);
        int day2 = cal.getActualMaximum(Calendar.DATE);
        int day1 = day2 - 13;
        edtf = MessageFormat.format("{0}-{1}/{0}-{2}", edtf, day1, day2);
      } else {
        edtf = MessageFormat.format("{0}-07/{0}-12", edtf);
      }

    } else if (dateString.contains("[voorjaar]")) {
      edtf = edtf.replace(" voorjaar", "");
      edtf = MessageFormat.format("{0}-03/{1}-05", edtf, edtf);

    } else if (dateString.contains("[voorjaar/zomer]")) {
      edtf = edtf.replace(" voorjaar/zomer", "");
      edtf = MessageFormat.format("{0}-03/{1}-08", edtf, edtf);

    } else if (dateString.contains("[zomer]")) {
      edtf = edtf.replace(" zomer", "");
      edtf = MessageFormat.format("{0}-06/{1}-08", edtf, edtf);

    } else if (dateString.contains("[najaar]")) {
      edtf = edtf.replace(" najaar", "");
      edtf = MessageFormat.format("{0}-09/{1}-11", edtf, edtf);
    }

    if (edtf.contains("+")) {
      String[] options = edtf.split("\\+");
      // edtf = "{" + options[0] + "," + options[0].substring(0, 8) + options[1] + "}";
      if (options[1].length() == 2) {
        edtf = fixDate(options[0]) + "/" + fixDate(options[0].substring(0, 8) + options[1]);
      } else if (options[1].length() == 7) {
        edtf = fixDate(options[0]) + "/" + fixDate(options[1] + "-31");
      } else if (options[1].length() == 10) {
        edtf = fixDate(options[0]) + "/" + fixDate(options[1]);
      }
    }

    edtf = edtf.replace(" ", "");
    if (edtf.contains("/")) {
      // String extra = "";
      if (edtf.endsWith("?") || edtf.endsWith("~")) {
        // extra = edtf.substring(edtf.length() - 1);
        edtf = edtf.substring(0, edtf.length() - 1);
      }
      Iterable<String> parts = Splitter.on("/").split(edtf);
      Iterator<String> iterator = parts.iterator();
      String from = iterator.next();
      if (from.length() < 10) {
        List<String> dmy = Lists.newArrayList(Splitter.on("-").split(from));
        if (dmy.size() == 1) {
          from = from + "-01-01";

        } else if (dmy.size() == 2) {
          from = from + "-01";

        } else {
          Log.warn("Unexpected: from={}", from);
        }
      }
      String to = iterator.next();
      if (to.length() < 10) {
        List<String> dmy = Lists.newArrayList(Splitter.on("-").split(to));
        if (dmy.size() == 1) {
          to = to + "-12-31";

        } else if (dmy.size() == 2) {
          to = to + "-" + lastDayOfTheMonth(dmy);

        } else {
          Log.warn("Unexpected: from={}", from);
        }
      }
      edtf = from + "/" + to; // + extra;
    }

    if (!dateString.equals(edtf)) {
      // Log.info("'{}'->'{}'", dateString, edtf);
    }
    return edtf.replaceAll(" ", "");
  }

  private int lastDayOfTheMonth(List<String> dmy) {
    // Log.info("dmy={}", dmy);
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, Integer.parseInt(dmy.get(0)));
    cal.set(Calendar.MONTH, Integer.parseInt(dmy.get(1)));
    cal.set(Calendar.DAY_OF_MONTH, 1);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    cal.roll(Calendar.DAY_OF_YEAR, -1);
    return cal.get(Calendar.DAY_OF_MONTH);
  }

  private String fixDate(String datestring) {
    if (datestring.length() == 10) {
      return datestring;
    }
    String[] parts = datestring.split("-");
    int year = Integer.parseInt(parts[0]);
    int month = Integer.parseInt(parts[1]);
    int day = Integer.parseInt(parts[2]);
    return String.format("%04d", year)
        + "-"
        + String.format("%02d", month)
        + "-"
        + String.format("%02d", day);
  }

  private Date extractDate(String edtf) {
    Date date;
    try {
      date = new SimpleDateFormat("yyyy-MM-dd").parse(edtf);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
    return date;
  }

  private String getDateByAdding(Date date, int amount, int field) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.add(field, amount);
    return new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
  }
}
