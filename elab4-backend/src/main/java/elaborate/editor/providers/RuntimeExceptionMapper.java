package elaborate.editor.providers;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import nl.knaw.huygens.LoggableObject;
import nl.knaw.huygens.jaxrstools.exceptions.InternalServerErrorException;

@Provider
public class RuntimeExceptionMapper extends LoggableObject implements ExceptionMapper<RuntimeException> {

	@Override
	public Response toResponse(RuntimeException exception) {
		if (exception instanceof WebApplicationException) {
			throw exception;
		}
		LOG.error("{}", exception.getMessage());
		exception.printStackTrace();
		throw new InternalServerErrorException(exception.getMessage());
	}

}
