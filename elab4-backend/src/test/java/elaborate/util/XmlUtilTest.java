package elaborate.util;

import static org.assertj.core.api.Assertions.assertThat;
import nl.knaw.huygens.LoggableObject;

import org.junit.Test;

public class XmlUtilTest extends LoggableObject {

	@Test
	public void testFixXhtml() {
		String badxml = "<interpGrp>not really bad</interpGrp>";
		String fixedXml = XmlUtil.fixXhtml(badxml);
		assertThat(fixedXml).isEqualTo(badxml.toLowerCase());
	}

	@Test
	public void testFixTagEndings() throws Exception {
		String in = "<tag>bla<bla>babal<tag>\n</tag></bla></tag>whatever";
		String fixed = "<tag>bla<bla>babal<tag></tag></bla></tag>\nwhatever";
		assertThat(XmlUtil.fixTagEndings(in)).isEqualTo(fixed);
	}

	@Test
	public void test() {
		assertThat(40 % 5).isEqualTo(0);
		assertThat(41 % 5).isNotEqualTo(0);
	}

	@Test
	public void testRemoveXMLtags() throws Exception {
		assertThat(XmlUtil.removeXMLtags("<body>kaal</body>")).isEqualTo("kaal");
		assertThat(XmlUtil.removeXMLtags("<1>aap\n <2>noot\n mies</2></1>")).isEqualTo("aap\n noot\n mies");
	}

	//	@Test
	//	public void testSelect() {
	//		ProjectService ps = ProjectService.instance();
	//		ps.openEntityManager();
	//		EntityManager em = ps.getEntityManager();
	//		String textlayer = "Translation";
	//		Project project = new Project().setId(1l);
	//		List resultList = em//
	//				.createQuery("select e.id, t.id from ProjectEntry e join e.transcriptions as t where e.project=:project and t.text_layer=:textlayer")//
	//				.setParameter("project", project)//
	//				.setParameter("textlayer", textlayer)//
	//				.getResultList();
	//		LOG.info("result={}", resultList);
	//		for (Object object : resultList) {
	//			Object[] objects = (Object[]) object;
	//			LOG.info("id[0]={}", objects[0]);
	//			LOG.info("id[1]={}", objects[1]);
	//		}
	//		ps.closeEntityManager();
	//	}
}
