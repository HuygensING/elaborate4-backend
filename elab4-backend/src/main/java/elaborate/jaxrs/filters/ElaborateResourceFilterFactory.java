package elaborate.jaxrs.filters;

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
    singletonList.add(new LoggingResourceFilter());
    singletonList.add(new CacheHeaderFilter());
    //    singletonList.add(new CORSResponseFilter());

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
