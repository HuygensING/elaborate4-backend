package elaborate.editor.export.mvn;

import nl.knaw.huygens.tei.Document;

public class TranscriptionHierarchyFixer {

  public String fix(String xml) {
    TextDecorationVisitor visitor = new TextDecorationVisitor();
    final Document document = Document.createFromXml(xml, false);
    document.accept(visitor);
    return visitor.getContext().getResult();
  }

}
