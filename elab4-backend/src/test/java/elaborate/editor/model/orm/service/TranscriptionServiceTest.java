package elaborate.editor.model.orm.service;

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
import static elaborate.editor.model.orm.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;

import elaborate.editor.AbstractTest;
import elaborate.editor.model.AnnotationInputWrapper;
import elaborate.editor.model.orm.Annotation;
import elaborate.editor.model.orm.User;
import nl.knaw.huygens.Log;

public class TranscriptionServiceTest extends AbstractTest {

	@Before
	public void setUp() throws Exception {}

	@After
	public void tearDown() throws Exception {}

	// @Test
	public void testAddAnnotation() throws Exception {
		TranscriptionService ts = TranscriptionService.instance();
		AnnotationInputWrapper annotationInput = new AnnotationInputWrapper();
		annotationInput.body = "body";
		User root = mock(User.class);
		when(root.isRoot()).thenReturn(true);
		Annotation annotation = ts.addAnnotation(1, annotationInput, root);
		Log.info("annotation={}", annotation);
		assertThat(annotation).isNotNull();
		assertThat(annotation).hasBody("body");
	}

}
