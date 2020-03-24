package elaborate.jaxrs.filters;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2020 Huygens ING
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

import com.sun.jersey.spi.container.*;

import javax.ws.rs.core.MultivaluedMap;

public class CORSResponseFilter
    implements ResourceFilter, ContainerResponseFilter, ContainerRequestFilter {

  @Override
  public ContainerRequest filter(ContainerRequest containerRequest) {
    return containerRequest;
  }

  @Override
  public ContainerResponse filter(
      ContainerRequest containerRequest, ContainerResponse containerResponse) {
    MultivaluedMap<String, Object> headers = containerResponse.getHttpHeaders();
//    headers.add("Access-Control-Allow-Origin", "*"); // Allow Access from everywhere
    //    headers.add("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
    //    headers.add("Access-Control-Allow-Headers", "X-Requested-With, Content-Type");
    return containerResponse;
  }

  @Override
  public ContainerRequestFilter getRequestFilter() {
    return this;
  }

  @Override
  public ContainerResponseFilter getResponseFilter() {
    return this;
  }
}
