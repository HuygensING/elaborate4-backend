<#--
 #%L
 elab4-backend
 =======
 Copyright (C) 2011 - 2016 Huygens ING
 =======
 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as
 published by the Free Software Foundation, either version 3 of the
 License, or (at your option) any later version.
 
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 
 You should have received a copy of the GNU General Public
 License along with this program.  If not, see
 <http://www.gnu.org/licenses/gpl-3.0.html>.
 #L%
-->
handlers = org.apache.juli.FileHandler, java.util.logging.ConsoleHandler

org.apache.juli.FileHandler.level = WARNING
org.apache.juli.FileHandler.directory = ${r"${catalina.base}"}/logs
org.apache.juli.FileHandler.prefix = ${prefix}.
org.apache.juli.FileHandler.bufferSize = -1

java.util.logging.ConsoleHandler.level = WARNING
java.util.logging.ConsoleHandler.formatter = java.util.logging.SimpleFormatter