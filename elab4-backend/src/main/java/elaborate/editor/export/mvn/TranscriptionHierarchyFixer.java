package elaborate.editor.export.mvn;

import nl.knaw.huygens.tei.Document;

public class TranscriptionHierarchyFixer {

  public String fix(String xml) {
    TranscriptionHierarchyVisitor visitor = new TranscriptionHierarchyVisitor();
    final Document document = Document.createFromXml(xml, false);
    document.accept(visitor);
    return visitor.getContext().getResult();
  }

}
