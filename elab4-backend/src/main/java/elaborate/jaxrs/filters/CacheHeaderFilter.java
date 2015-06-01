package elaborate.jaxrs.filters;

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

import javax.ws.rs.core.MultivaluedMap;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;

public class CacheHeaderFilter   implements ResourceFilter, ContainerResponseFilter, ContainerRequestFilter {

	@Override
	public ContainerRequestFilter getRequestFilter() {
		return this;
	}

	@Override
	public ContainerRequest filter(ContainerRequest request) {
		return request;
	}

	@Override
	public ContainerResponseFilter getResponseFilter() {
		return this;
	}

	@Override
	public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
		MultivaluedMap<String, Object> httpHeaders = response.getHttpHeaders();
		httpHeaders.putSingle("Pragma", "no-cache");
		httpHeaders.putSingle("Cache-Control", "no-cache, must-revalidate");
		return response;
	}

}
