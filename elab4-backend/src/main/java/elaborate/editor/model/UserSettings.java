package elaborate.editor.model;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2014 Huygens ING
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


public class UserSettings {
  public static final String MAGIC_NUMBER = "magic_number";
  public static final String ENTRY_ORDER = "entryorder";
  public static final String ENTRY_NAMES = "entrynames";
  public static final String ONLINE_STATUS = "onlinestatus";
  public static final String LOGOUT_TIME = "logging_out_at";
  public static final String LOGIN_TIME = "logging_in_at";

  public static String projectLevel(String project_id, int i) {
    return "project_" + project_id + "_level_" + i;
  }
}
