package elaborate.editor.solr;

import static elaborate.editor.solr.SolrFields.*;

import java.io.IOException;
import java.util.Set;

import nl.knaw.huygens.tei.Document;
import nl.knaw.huygens.tei.XmlContext;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import elaborate.editor.config.Configuration;
import elaborate.editor.model.orm.Annotation;
import elaborate.editor.model.orm.ProjectEntry;
import elaborate.editor.model.orm.Transcription;
import elaborate.util.StringUtil;
import elaborate.util.XmlUtil;

public class SolrIndexer {
  private static final String EMPTYVALUE_SYMBOL = ":empty";
  static final boolean INDEX = true; // should be true for release
  private static final Logger LOG = LoggerFactory.getLogger(SolrIndexer.class);
  private static final int STATUS_OK = 0;
  private final SolrServer server;
  private final String solrURL;

  public SolrIndexer() {
    solrURL = Configuration.instance().getSetting(Configuration.SOLR_URL_KEY);
    //    LOG.info("connecting with SOLR server on {}", solrURL);
    this.server = new HttpSolrServer(solrURL);
  }

  // -- public methods
  public void clear() {
    try {
      this.server.deleteByQuery("*:*");
    } catch (SolrServerException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void index(ProjectEntry projectEntry, boolean commitNow) {
    index(getSolrInputDocument(projectEntry, false), commitNow);
  }

  public void commit() {
    try {
      this.server.commit();
      this.server.optimize();
    } catch (SolrServerException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @SuppressWarnings("boxing")
  public boolean isUp() {
    boolean isUp = false;
    try {
      int status = this.server.ping().getStatus();
      LOG.info("solrserver status = {}", status);
      isUp = (status == STATUS_OK);
    } catch (SolrServerException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return isUp;
  }

  public String getURL() {
    return solrURL;
  }

  public void deindex(ProjectEntry e) {
    try {
      this.server.deleteById(String.valueOf(e.getId()));
    } catch (SolrServerException ex) {
      ex.printStackTrace();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
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

  private void index(SolrInputDocument doc, boolean commitNow) {
    if (INDEX) {
      try {
        String id = String.valueOf(doc.getField(ID).getValue());
        this.server.deleteById(id);
        //        LOG.info("doc={}", doc);
        this.server.add(doc);
        if (commitNow) {
          this.server.commit();
        }
      } catch (SolrServerException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

}
