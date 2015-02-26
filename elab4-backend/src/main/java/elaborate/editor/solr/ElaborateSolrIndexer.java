package elaborate.editor.solr;

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

import static nl.knaw.huygens.facetedsearch.SolrFields.ANNOTATIONCS_PREFIX;
import static nl.knaw.huygens.facetedsearch.SolrFields.ANNOTATION_PREFIX;
import static nl.knaw.huygens.facetedsearch.SolrFields.ID;
import static nl.knaw.huygens.facetedsearch.SolrFields.NAME;
import static nl.knaw.huygens.facetedsearch.SolrFields.PROJECT_ID;
import static nl.knaw.huygens.facetedsearch.SolrFields.PUBLISHABLE;
import static nl.knaw.huygens.facetedsearch.SolrFields.TEXTLAYERCS_PREFIX;
import static nl.knaw.huygens.facetedsearch.SolrFields.TEXTLAYER_PREFIX;
import static nl.knaw.huygens.facetedsearch.SolrUtils.EMPTYVALUE_SYMBOL;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import nl.knaw.huygens.facetedsearch.SolrUtils;
import nl.knaw.huygens.tei.Document;
import nl.knaw.huygens.tei.XmlContext;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import elaborate.editor.config.Configuration;
import elaborate.editor.model.orm.Annotation;
import elaborate.editor.model.orm.Project;
import elaborate.editor.model.orm.ProjectEntry;
import elaborate.editor.model.orm.Transcription;
import elaborate.util.XmlUtil;

public class ElaborateSolrIndexer extends SolrIndexer {
	public ElaborateSolrIndexer() {
		super(getServer(), ID);
	}

	private static SolrServer getServer() {
		String solrURL = Configuration.instance().getSetting(Configuration.SOLR_URL_KEY);
		LOG.info("connecting with SOLR server on {}", solrURL);
		return new HttpSolrServer(solrURL);
	}

	public void index(ProjectEntry projectEntry, boolean commitNow) {
		super.index(getSolrInputDocument(projectEntry, false), commitNow);
	}

	public void deindex(ProjectEntry e) {
		super.deleteById(String.valueOf(e.getId()));
		commit();
	}

	public void deindex(long entry_id) {
		super.deleteById(String.valueOf(entry_id));
		commit();
	}

	public static SolrInputDocument getSolrInputDocument(ProjectEntry projectEntry, boolean forPublication) {
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField(ID, projectEntry.getId());
		doc.addField(NAME, projectEntry.getName());
		Project project = projectEntry.getProject();
		for (String field : project.getProjectEntryMetadataFieldnames()) {
			String facetName = SolrUtils.facetName(field);
			String value = projectEntry.getMetadataValue(field);
			doc.addField(facetName, StringUtils.defaultIfBlank(value, EMPTYVALUE_SYMBOL).replaceAll("\\r?\\n|\\r", "/"), 1.0f);
			// TODO: This is CNW specific, refactoring needed
			//			handleCNWCorrespondents(facetName, value, doc);
		}
		Set<String> textLayersProcessed = Sets.newHashSet();
		for (Transcription transcription : projectEntry.getTranscriptions()) {
			String tBody = convert(transcription.getBody());
			String textLayer = SolrUtils.normalize(transcription.getTextLayer());
			if (textLayersProcessed.contains(textLayer)) {
				LOG.error("duplicate textlayer {} for entry {}", textLayer, projectEntry.getId());
			} else {
				doc.addField(TEXTLAYER_PREFIX + textLayer, tBody);
				doc.addField(TEXTLAYERCS_PREFIX + textLayer, tBody);
				for (Annotation annotation : transcription.getAnnotations()) {
					String body = annotation.getBody();
					if (body != null) {
						String aBody = convert(body);
						doc.addField(ANNOTATION_PREFIX + textLayer, aBody);
						doc.addField(ANNOTATIONCS_PREFIX + textLayer, aBody);
					}
				}
				textLayersProcessed.add(textLayer);
			}
		}
		if (!forPublication) {
			doc.addField(PUBLISHABLE, projectEntry.isPublishable(), 1.0f);
			doc.addField(PROJECT_ID, projectEntry.getProject().getId());
		}
		return doc;
	}

	private static void handleCNWCorrespondents(String facetName, String value, SolrInputDocument doc) {
		if ("metadata_afzender_s".equals(facetName) || "metadata_ontvanger_s".equals(facetName)) {
			for (String correspondent : extractCorrespondents(value)) {
				doc.addField("metadata_correspondents", correspondent, 1.0f);
			}
		}
	}

	protected static List<String> extractCorrespondents(String value) {
		List<String> correspondents = Lists.newArrayList();
		if (value.contains("-->")) {
			String[] subValues = value.split("-->");
			for (String subValue : subValues) {
				correspondents.addAll(extractCorrespondents(subValue));
			}
		} else if (value.contains("/")) {
			String[] subValues = value.split("/");
			for (String subValue : subValues) {
				correspondents.addAll(extractCorrespondents(subValue));
			}
		} else if (value.contains("#")) {
			String[] subValues = value.split("#");
			correspondents.addAll(extractCorrespondents(subValues[0]));
		} else {
			correspondents.add(value);
		}
		return correspondents;
	}

	// -- private methods

	static String convert(String xmlContent) {
		final SolrIndexerVisitor visitor = new SolrIndexerVisitor();
		String xml = XmlUtil.wrapInXml(XmlUtil.fixXhtml(xmlContent)).replaceAll("\n", " ");
		try {
			final Document document = Document.createFromXml(xml, false);
			document.accept(visitor);
			final XmlContext c = visitor.getContext();
			String rawResult = c.getResult();
			return rawResult;

		} catch (Exception e) {
			LOG.error(e.getMessage());
			return XmlUtil.removeXMLtags(xml);
		}
	}

	public void deindexProject(long project_id) {
		try {
			SolrServer server = getServer();
			server.deleteByQuery("project_id:" + project_id);
			server.commit();
		} catch (SolrServerException e) {
			LOG.error("deindexProject failed:");
			e.printStackTrace();
		} catch (IOException e) {
			LOG.error("deindexProject failed:");
			e.printStackTrace();
		}
	}

}
