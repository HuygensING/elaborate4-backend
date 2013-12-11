package elaborate.jaxrs.filters;

import java.util.List;

import com.google.common.collect.Lists;
import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.spi.container.ResourceFilter;
import com.sun.jersey.spi.container.ResourceFilterFactory;

import elaborate.jaxrs.Annotations.AuthorizationRequired;

public class ElaborateResourceFilterFactory implements ResourceFilterFactory {
	private static final Class<AuthorizationRequired> ANNOTATION_CLASS = AuthorizationRequired.class;

	@Override
	public List<ResourceFilter> create(AbstractMethod am) {
		List<ResourceFilter> singletonList = Lists.newArrayList();
		//    singletonList.add(new LoggingResourceFilter());

		if (needsAuthorization(am)) {
			singletonList.add(new AuthenticationResourceFilter());
		}
		return singletonList;
	}

	private boolean needsAuthorization(AbstractMethod am) {
		return (am.getAnnotation(ANNOTATION_CLASS) != null) //
				|| (am.getResource().getAnnotation(ANNOTATION_CLASS) != null);
	}

}
