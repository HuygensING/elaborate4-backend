package elaborate.jaxrs.filters;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2022 Huygens ING
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

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

public class CORSFilter implements ContainerResponseFilter {
  @Override
  public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {

    //    response.getHttpHeaders().add("Access-Control-Allow-Origin", "*");
    //    response
    //        .getHttpHeaders()
    //        .add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
    //    response.getHttpHeaders().add("Access-Control-Allow-Credentials", "true");
    //    response
    //        .getHttpHeaders()
    //        .add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");

    return response;
  }
}
