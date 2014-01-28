package elaborate.jaxrs.filters;

import java.text.MessageFormat;

import nl.knaw.huygens.LoggableObject;

import org.joda.time.DateTime;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;

public class LoggingResourceFilter extends LoggableObject implements ResourceFilter, ContainerRequestFilter, ContainerResponseFilter {
	//  private final StopWatch sw = new StopWatch();

	@Override
	public ContainerRequestFilter getRequestFilter() {
		return this;
	}

	@Override
	public ContainerRequest filter(ContainerRequest request) {
		LOG.info("request={}", verbalize(request));
		//    sw.reset();
		//    sw.start();
		return request;
	}

	@Override
	public ContainerResponseFilter getResponseFilter() {
		return this;
	}

	@Override
	public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
		//    sw.stop();
		//    LOG.info("request took {} ms", sw.getTime());
		System.out.println(commonLogLine(request, response));
		return response;
	}

	private String commonLogLine(ContainerRequest request, ContainerResponse response) {
		// 127.0.0.1 user-identifier frank [10/Oct/2000:13:55:36 -0700] "GET /apache_pb.gif HTTP/1.0" 200 2326
		return MessageFormat.format(//
				"{0} {1} {2} [{3}] \"{4} /{5} {6}\" {7} {8}",//
				"-",//
				//        req.getRemoteAddr(),//
				"-",//
				"-",//
				//        req.getRemoteUser(),//
				new DateTime().toString("dd/MMM/yyyy:HH:mm:ss ZZ"),//
				request.getMethod(),//
				request.getPath(),//
				"-",//
				//        req.getProtocol(),//
				response.getStatus(),//
				"-"//
		);
	}

	private Object verbalize(ContainerRequest request) {
		return MessageFormat.format(//
				"{0} {1} # cookies={2} queryParameters={3} formParameters={4} acceptableMediaTypes={5}",//
				request.getMethod(),//
				request.getAbsolutePath(),//
				request.getCookies(),//
				request.getQueryParameters(),//
				request.getFormParameters(),//
				request.getAcceptableMediaTypes()//
				);
	}

}
