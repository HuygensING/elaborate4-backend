package elaborate.editor.providers;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import nl.knaw.huygens.LoggableObject;

import com.sun.jersey.api.container.MappableContainerException;

@Provider
public class RuntimeExceptionMapper extends LoggableObject implements ExceptionMapper<RuntimeException> {
	@Override
	public Response toResponse(RuntimeException exception) {
		LOG.info("RuntimeExceptionMapper.toResonse()");
		if (exception instanceof WebApplicationException) {
			WebApplicationException internalException = (WebApplicationException) exception;
			return internalException.getResponse();
		}
		throw new MappableContainerException(exception);

		//		ResponseBuilder builder = Response.status(Status.INTERNAL_SERVER_ERROR);
		//		builder.type(MediaType.TEXT_PLAIN_TYPE);
		//		builder.entity(exception.getStackTrace());
		//		return builder.build();

		//		if (exception instanceof WebApplicationException) {
		//			throw exception;
		//		}
		//		LOG.error("{}", exception.getMessage());
		//		exception.printStackTrace();
		//		throw new InternalServerErrorException(exception.getMessage());
	}

}
