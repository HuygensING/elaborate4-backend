package elaborate.editor.providers;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import nl.knaw.huygens.jaxrstools.exceptions.InternalServerErrorException;

@Provider
public class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {

	@Override
	public Response toResponse(RuntimeException exception) {
		if (exception instanceof WebApplicationException) {
			throw exception;
		}
		exception.printStackTrace();
		throw new InternalServerErrorException(exception.getMessage());
	}

}
