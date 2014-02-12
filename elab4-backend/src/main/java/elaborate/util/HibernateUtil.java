package elaborate.util;

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


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import elaborate.editor.model.ModelFactory;

public class HibernateUtil {
  final static EntityManagerFactory ENTITY_MANAGER_FACTORY = ModelFactory.INSTANCE.getEntityManagerFactory();
  static Logger LOG = LoggerFactory.getLogger(HibernateUtil.class);

  public static EntityManager beginTransaction() {
    EntityManager entityManager = getEntityManager();
    entityManager.getTransaction().begin();
    return entityManager;
  }

  public static void commitTransaction(EntityManager entityManager) {
    entityManager.getTransaction().commit();
    endTransaction(entityManager);
  }

  public static void endTransaction(EntityManager entityManager) {
    entityManager.close();
  }

  public static void rollbackTransaction(EntityManager entityManager) {
    entityManager.getTransaction().rollback();
    endTransaction(entityManager);
  }

  public static EntityManager getEntityManager() {
    return ENTITY_MANAGER_FACTORY.createEntityManager();
  }

}
