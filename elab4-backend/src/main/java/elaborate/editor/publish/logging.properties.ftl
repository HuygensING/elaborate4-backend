handlers = org.apache.juli.FileHandler, java.util.logging.ConsoleHandler

org.apache.juli.FileHandler.level = WARNING
org.apache.juli.FileHandler.directory = ${r"${catalina.base}"}/logs
org.apache.juli.FileHandler.prefix = ${prefix}.
org.apache.juli.FileHandler.bufferSize = -1

java.util.logging.ConsoleHandler.level = WARNING
java.util.logging.ConsoleHandler.formatter = java.util.logging.SimpleFormatter