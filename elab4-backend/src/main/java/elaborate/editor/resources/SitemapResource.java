package elaborate.editor.resources;

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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

import nl.knaw.huygens.jaxrstools.resources.UTF8MediaType;
import elaborate.editor.model.Sitemap;
import elaborate.jaxrs.APIDesc;

@Path("api")
public class SitemapResource extends AbstractElaborateResource {

	@GET
	@Produces(UTF8MediaType.APPLICATION_JSON)
	@APIDesc("Generates a structured sitemap.")
	public Sitemap getSitemap(@Context Application app) {
		return new Sitemap(app);
	}

}
