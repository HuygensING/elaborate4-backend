package elaborate.jaxrs;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2014 Huygens ING
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

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Lists;

public class JAXUtils {
	public static class API {
		public String path;
		public ImmutableList<String> requestTypes;
		public ImmutableList<String> requestContentTypes;
		public ImmutableList<String> responseContentTypes;
		public String description;

		public API(String path, ImmutableList<String> requestTypes, ImmutableList<String> requestContentTypes, ImmutableList<String> responseContentTypes, String desc) {
			this.path = path;
			this.requestTypes = requestTypes;
			this.responseContentTypes = responseContentTypes;
			this.requestContentTypes = requestContentTypes;
			this.description = desc;
		}

		public API modifyPath(String regex, String replacement) {
			String newPath = path.replaceFirst(regex, replacement);
			return new API(newPath, requestTypes, requestContentTypes, responseContentTypes, description);
		}
	}

	/**
	 * Returns an API description for each HTTP method in the specified
	 * class if it has a <code>Path</code> annotation, or an empty list
	 * if the <code>Path</code> annotation is missing.
	 */
	public static List<API> generateAPIs(Class<?> cls) {
		List<API> list = Lists.newArrayList();

		String basePath = pathValueOf(cls);
		if (!basePath.isEmpty()) {
			for (Method method : cls.getMethods()) {
				Builder<String> builder = ImmutableList.<String> builder();
				if (method.isAnnotationPresent(GET.class)) {
					builder.add(HttpMethod.GET);
				}
				if (method.isAnnotationPresent(POST.class)) {
					builder.add(HttpMethod.POST);
				}
				if (method.isAnnotationPresent(PUT.class)) {
					builder.add(HttpMethod.PUT);
				}
				if (method.isAnnotationPresent(DELETE.class)) {
					builder.add(HttpMethod.DELETE);
				}

				ImmutableList<String> reqs = builder.build();
				if (!reqs.isEmpty()) {
					String subPath = pathValueOf(method);
					String fullPath = subPath.isEmpty() ? basePath : basePath + "/" + subPath;
					fullPath = fullPath.replaceAll("\\{([^:]*):[^}]*\\}", "{$1}");
					list.add(new API(fullPath, reqs, requestContentTypesOf(method), responseContentTypesOf(method), descriptionOf(method)));
				}
			}
		}

		return list;
	}

	/**
	 * Returns the path of the annotated element,
	 * or an empty string if no annotation is present.
	 */
	static String pathValueOf(AnnotatedElement element) {
		Path annotation = element.getAnnotation(Path.class);
		String value = (annotation != null) ? annotation.value() : "";
		return StringUtils.removeStart(value, "/");
	}

	static ImmutableList<String> requestContentTypesOf(Method method) {
		Consumes annotation = method.getAnnotation(Consumes.class);
		return annotation != null ? ImmutableList.copyOf(annotation.value()) : ImmutableList.<String> of();
	}

	static ImmutableList<String> responseContentTypesOf(Method method) {
		Produces annotation = method.getAnnotation(Produces.class);
		return annotation != null ? ImmutableList.copyOf(annotation.value()) : ImmutableList.<String> of();
	}

	static String descriptionOf(Method method) {
		APIDesc annotation = method.getAnnotation(APIDesc.class);
		return (annotation != null) ? annotation.value() : "";
	}

}
