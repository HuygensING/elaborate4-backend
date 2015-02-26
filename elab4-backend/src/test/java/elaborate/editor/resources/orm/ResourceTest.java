package elaborate.editor.resources.orm;

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

import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;

import elaborate.editor.model.ModelFactory;

public class ResourceTest extends JerseyTest {
	public Logger LOG = LoggerFactory.getLogger(getClass());
	protected static EntityManagerFactory entityManagerFactory = ModelFactory.INSTANCE.getEntityManagerFactory();

	public ResourceTest() {
		super(new WebAppDescriptor.Builder()//
				.initParam("com.sun.jersey.config.property.packages", "elaborate.editor.resources;elaborate.editor.providers;nl.knaw.huygens.jaxrstools.resources;nl.knaw.huygens.jaxrstools.providers")//
				.initParam("com.sun.jersey.spi.container.ResourceFilters", "elaborate.jaxrs.filters.ElaborateResourceFilterFactory")//
				.build());
	}

}
