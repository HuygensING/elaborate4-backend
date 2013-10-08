package nl.knaw.huygens;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggableObject {
  protected Logger LOG = LoggerFactory.getLogger(getClass());

  protected static Logger getLOG(Class<?> clazz) {
    return LoggerFactory.getLogger(clazz);
  }
}
