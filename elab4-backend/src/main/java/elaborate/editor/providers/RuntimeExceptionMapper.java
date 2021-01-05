package elaborate.editor.providers;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2021 Huygens ING
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

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import nl.knaw.huygens.Log;

@Provider
public class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {
  @Override
  public Response toResponse(RuntimeException exception) {
    Log.info("RuntimeExceptionMapper.toResponse()");
    if (exception instanceof WebApplicationException) {
      WebApplicationException internalException = (WebApplicationException) exception;
      return internalException.getResponse();
    }
    exception.printStackTrace();
    return Response.serverError().entity(exception.getMessage()).build();

    // ResponseBuilder builder = Response.status(Status.INTERNAL_SERVER_ERROR);
    // builder.type(MediaType.TEXT_PLAIN_TYPE);
    // builder.entity(exception.getStackTrace());
    // return builder.build();

    // if (exception instanceof WebApplicationException) {
    // throw exception;
    // }
    // Log.error("{}", exception.getMessage());
    // exception.printStackTrace();
    // throw new InternalServerErrorException(exception.getMessage());
  }

}
