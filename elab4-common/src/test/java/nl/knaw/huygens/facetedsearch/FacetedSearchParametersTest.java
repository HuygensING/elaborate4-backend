package nl.knaw.huygens.facetedsearch;

/*
 * #%L
 * elab4-common
 * =======
 * Copyright (C) 2013 - 2018 Huygens ING
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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FacetedSearchParametersTest<T> {

	@Before
	public void setUp() {}

	@After
	public void tearDown() {}

	@Test
	public void testSetTerm() {
		FacetedSearchParameters<?> facetedSearchParameters = new FacetedSearchParameters()//
				.setCaseSensitive(true)//
				.setFacetFields(new String[] {});
		assertThat(facetedSearchParameters.isCaseSensitive()).isTrue();
	}
}
