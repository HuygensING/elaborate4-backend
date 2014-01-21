package elaborate.editor.model.orm;

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

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import elaborate.AbstractTest;

public class StoredEntityTest extends AbstractTest {

  protected static EntityManagerFactory entityManagerFactory;

  @BeforeClass
  public static void setUpClass() throws Exception {
    entityManagerFactory = Persistence.createEntityManagerFactory("nl.knaw.huygens.elaborate.test.jpa");
    //    entityManagerFactory = Persistence.createEntityManagerFactory("nl.knaw.huygens.elaborate.test.psql.jpa");
  }

  @AfterClass
  public static void tearDownClass() throws Exception {
    if (entityManagerFactory != null) {
      entityManagerFactory.close();
    }
  }

}
