package elaborate.editor.model;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2018 Huygens ING
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

import java.util.Comparator;
import java.util.List;

import javax.ws.rs.core.Application;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import elaborate.jaxrs.JAXUtils;
import elaborate.jaxrs.JAXUtils.API;

@XmlRootElement
public class Sitemap {
	private static final Comparator<API> PATH_COMPARATOR = new Comparator<JAXUtils.API>() {
		@Override
		public int compare(API a1, API a2) {
			return a1.path.compareTo(a2.path);
		}
	};
	private static final Comparator<API> REQUESTTYPES_COMPARATOR = new Comparator<JAXUtils.API>() {
		@Override
		public int compare(API a1, API a2) {
			return a1.requestTypes.toString().compareTo(a2.requestTypes.toString());
		}
	};
	public final String description = "Elaborate backend sitemap";
	public final ImmutableList<API> availableAPIList;

	public Sitemap(Application application) {
		List<API> list = Lists.newArrayList();
		for (Class<?> cls : application.getClasses()) {
			List<API> apis = JAXUtils.generateAPIs(cls);
			list.addAll(apis);
		}
		availableAPIList = ImmutableList.copyOf(Ordering.from(PATH_COMPARATOR).compound(REQUESTTYPES_COMPARATOR).sortedCopy(list));
	}

}
