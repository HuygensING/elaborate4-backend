package elaborate.editor.model.orm.service;

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
import static elaborate.editor.model.orm.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import nl.knaw.huygens.jaxrstools.exceptions.UnauthorizedException;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import elaborate.editor.AbstractTest;
import elaborate.editor.model.ModelFactory;
import elaborate.editor.model.orm.Project;
import elaborate.editor.model.orm.User;

@Ignore
public class ProjectServiceTest extends AbstractTest {
	private static UserService userService;
	private static ProjectService projectService;
	private static User root;
	private static User notRoot;

	@Before
	public void setUp() throws Exception {
		userService = UserService.instance();
		projectService = ProjectService.instance();
		root = new User().setRoot(true).setUsername("root");
		userService.beginTransaction();
		userService.create(root);
		notRoot = new User().setUsername("notroot");
		projectService.beginTransaction();
	}

	@After
	public void tearDown() throws Exception {
		projectService.rollbackTransaction();
		userService.delete(root.getId());
		userService.rollbackTransaction();
	}

	@Test(expected = UnauthorizedException.class)
	public void testCreateAsNotRoot() throws Exception {
		Project project = ModelFactory.createTrackedEntity(Project.class, notRoot).setName("name");
		Project created = projectService.create(project, notRoot);
		assertThat(created).isNotNull();
	}

	@Test
	public void testCreateAsRoot() throws Exception {
		Project project = ModelFactory.createTrackedEntity(Project.class, root).setName("name");
		Project created = projectService.create(project, root);
		long project_id = created.getId();
		Project read = projectService.read(project_id, root);
		assertThat(read).hasName("name");
	}

	@Test
	public void testGetAll() throws Exception {
		List<Project> all = projectService.getAll(root);
		assertThat(all).isNotEmpty();
		LOG.info("{}", all.size());
	}

	@Test
	public void testGetProjectEntryIdsInOrder() throws Exception {
		List<Long> idList = projectService.getProjectEntryIdsInOrder(1);
		LOG.info("ids:{}", idList);
		assertThat(idList).isNotEmpty();
	}

	@Test
	public void testGetAnnotationTypesForProject() throws Exception {
		Map<Integer, String> annotationTypesForProject = projectService.getAnnotationTypesForProject(44l);
		assertThat(annotationTypesForProject).isNotEmpty();
		LOG.info("annotationTypesForProject={}", annotationTypesForProject);
	}

	//  @Test
	//  public void testExportPdf() throws Exception {
	//    projectService.exportPdf(1, root, "editie.pdf");
	//  }
}
