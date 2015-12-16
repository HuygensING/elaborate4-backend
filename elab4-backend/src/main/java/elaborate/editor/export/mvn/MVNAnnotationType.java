package elaborate.editor.export.mvn;

import java.util.Set;

import com.google.common.collect.Sets;

public enum MVNAnnotationType {
  AFKORTING("mvn:afkorting", AnnotatedTextUsage.use), //
  ALINEA("mvn:alinea", AnnotatedTextUsage.ignore), //
  CIJFERS("mvn:cijfers (romeins)", AnnotatedTextUsage.use), //
  DEFECT("mvn:defect", AnnotatedTextUsage.ignore), //
  DOORHALING("mvn:doorhaling", AnnotatedTextUsage.use), //
  INITIAAL("mvn:initiaal", AnnotatedTextUsage.use), //
  INSPRINGEN("mvn:inspringen", AnnotatedTextUsage.ignore), //
  KOLOM("mvn:kolom", AnnotatedTextUsage.ignore), //
  LETTERS("mvn:letters (zelfnoemfunctie)", AnnotatedTextUsage.use), //
  LINKERMARGEKOLOM("mvn:linkermargekolom", AnnotatedTextUsage.use), //
  RECHTERMARGEKOLOM("mvn:rechtermargekolom", AnnotatedTextUsage.use), //
  ONDERSCHRIFT("mvn:onderschrift", AnnotatedTextUsage.use), //
  ONDUIDELIJK("mvn:onduidelijk", AnnotatedTextUsage.use), //
  ONLEESBAAR("mvn:onleesbaar", AnnotatedTextUsage.ignore), //
  OPHOGING_ROOD("mvn:ophoging (rood)", AnnotatedTextUsage.use), //
  OPSCHRIFT("mvn:opschrift", AnnotatedTextUsage.use), //
  PALEOGRAFISCH("mvn:paleografisch", AnnotatedTextUsage.use), //
  POEZIE("mvn:poëzie", AnnotatedTextUsage.ignore), //
  REGELNUMMERING_BLAD("mvn:regelnummering (blad)", AnnotatedTextUsage.ignore), //
  TEKSTBEGIN("mvn:tekstbegin", AnnotatedTextUsage.use), //
  TEKSTEINDE("mvn:teksteinde", AnnotatedTextUsage.use), //
  TEKSTKLEUR_ROOD("mvn:tekstkleur (rood)", AnnotatedTextUsage.use), //
  VREEMDTEKEN("mvn:vreemdteken", AnnotatedTextUsage.use), //
  VERSREGEL("mvn:versregel", AnnotatedTextUsage.ignore), //
  WITREGEL("mvn:witregel", AnnotatedTextUsage.use), //

  // vervallen annotaties
  REGELNUMMERING_TEKST("mvn:regelnummering (tekst)", AnnotatedTextUsage.ignore), //
  GEBRUIKERSNOTITIE("mvn:gebruikersnotitie", AnnotatedTextUsage.ignore), //
  INCIPIT("mvn:incipit", AnnotatedTextUsage.ignore), //
  METAMARK("mvn:metamark", AnnotatedTextUsage.ignore);

  private enum AnnotatedTextUsage {
    use, ignore
  };

  private final String name;
  private final AnnotatedTextUsage ignoreText;
  private final static Set<String> allNames = Sets.newHashSet();

  private MVNAnnotationType(String name, AnnotatedTextUsage ignoreText) {
    this.name = name;
    this.ignoreText = ignoreText;
  }

  public String getName() {
    return name;
  }

  public static Set<String> getAllNames() {
    if (allNames.isEmpty()) {
      for (MVNAnnotationType mvnAnnotationType : MVNAnnotationType.values()) {
        allNames.add(mvnAnnotationType.getName());
      }
    }
    return allNames;
  }

  public boolean ignoreText() {
    return ignoreText.equals(AnnotatedTextUsage.ignore);
  }

  public static MVNAnnotationType fromName(String name) {
    for (MVNAnnotationType type : values()) {
      if (type.getName().equals(name)) {
        return type;
      }
    }
    throw new RuntimeException("name not recognized: " + name);
  }
}
