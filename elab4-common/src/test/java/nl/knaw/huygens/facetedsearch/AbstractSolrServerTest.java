package nl.knaw.huygens.facetedsearch;

/*
 * #%L
 * elab4-common
 * =======
 * Copyright (C) 2013 - 2019 Huygens ING
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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

import nl.knaw.huygens.Log;

public class AbstractSolrServerTest {

	@Test
	public void testExtractTerms() {
		String snippet1 = "bladie <em>bla</em> <em>bla1</em>.";
		String snippet2 = "bladie <em>bla</em> <em>bla2</em>.";
		Collection<String> extractTerms = AbstractSolrServer.extractTerms(ImmutableList.of(snippet1, snippet2));
		Log.info("terms={}", extractTerms);
		assertThat(extractTerms).containsExactly("bla", "bla1", "bla", "bla2");
	}

}
