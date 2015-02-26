package elaborate.jaxrs;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2015 Huygens ING
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

import com.sun.jersey.api.wadl.config.WadlGeneratorConfig;
import com.sun.jersey.api.wadl.config.WadlGeneratorDescription;

public class CustomWadlGeneratorConfig extends WadlGeneratorConfig {
	@Override
	public List<WadlGeneratorDescription> configure() {
		return generator(CustomJAXBWadlGenerator.class)//
				//    .generator(WadlGeneratorApplicationDoc.class)//
				//    .prop("applicationDocsFile", "classpath:/application-doc.xml")//
				//    .generator(WadlGeneratorGrammarsSupport.class)//
				//    .prop("grammarsFile", "classpath:/application-grammars.xml")//
				//    .generator(WadlGeneratorResourceDocSupport.class)//
				//    .prop("resourceDocFile", "classpath:/resourcedoc.xml")//
				.descriptions();
	}
}
