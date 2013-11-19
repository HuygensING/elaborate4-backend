package elaborate.editor.solr;

import static elaborate.editor.solr.SolrFields.ANNOTATIONCS_PREFIX;
import static elaborate.editor.solr.SolrFields.ANNOTATION_PREFIX;
import static elaborate.editor.solr.SolrFields.ID;
import static elaborate.editor.solr.SolrFields.NAME;
import static elaborate.editor.solr.SolrFields.PROJECT_ID;
import static elaborate.editor.solr.SolrFields.PUBLISHABLE;
import static elaborate.editor.solr.SolrFields.TEXTLAYERCS_PREFIX;
import static elaborate.editor.solr.SolrFields.TEXTLAYER_PREFIX;

import java.util.Set;

import nl.knaw.huygens.solr.SolrUtils;
import nl.knaw.huygens.tei.Document;
import nl.knaw.huygens.tei.XmlContext;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

import com.google.common.collect.Sets;

import elaborate.editor.config.Configuration;
import elaborate.editor.model.orm.Annotation;
import elaborate.editor.model.orm.ProjectEntry;
import elaborate.editor.model.orm.Transcription;
import elaborate.util.StringUtil;
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
  }

  public static SolrInputDocument getSolrInputDocument(ProjectEntry projectEntry, boolean forPublication) {
    SolrInputDocument doc = new SolrInputDocument();
    doc.addField(ID, projectEntry.getId());
    doc.addField(NAME, projectEntry.getName());
    for (String field : projectEntry.getProject().getProjectEntryMetadataFieldnames()) {
      String facetName = SolrUtils.facetName(field);
      String value = projectEntry.getMetadataValue(field);
      doc.addField(facetName, StringUtils.defaultIfBlank(value, EMPTYVALUE_SYMBOL), 1.0f);
    }
    Set<String> textLayersProcessed = Sets.newHashSet();
    for (Transcription transcription : projectEntry.getTranscriptions()) {
      String tBody = convert(transcription.getBody());
      String textLayer = StringUtil.normalize(transcription.getTextLayer());
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

}
