package elaborate.editor.export.mvn;

import java.util.Set;

import com.google.common.collect.Sets;

public enum MVNAnnotationType {
  AFKORTING("mvn:afkorting"), //
  ALINEA("mvn:alinea"), //
  CIJFERS("mvn:cijfers (romeins)"), //
  DEFECT("mvn:defect"), //
  DOORHALING("mvn:doorhaling"), //
  GEBRUIKERSNOTITIE("mvn:gebruikersnotitie"), //
  INCIPIT("mvn:incipit"), //
  INITIAAL("mvn:initiaal"), //
  INSPRINGEN("mvn:inspringen"), //
  KOLOM("mvn:kolom"), //
  LETTERS("mvn:letters (zelfnoemfunctie)"), //
  LINKERMARGEKOLOM("mvn:linkermargekolom"), //
  METAMARK("mvn:metamark"), //
  ONDERSCHRIFT("mvn:onderschrift"), //
  ONDUIDELIJK("mvn:onduidelijk"), //
  ONLEESBAAR("mvn:onleesbaar"), //
  OPHOGING_ROOD("mvn:ophoging (rood)"), //
  OPSCHRIFT("mvn:opschrift"), //
  PALEOGRAFISCH("mvn:paleografisch"), //
  POEZIE("mvn:poëzie"), //
  RECHTERMARGEKOLOM("mvn:rechtermargekolom"), //
  REGELNUMMERING_BLAD("mvn:regelnummering (blad)"), //
  REGELNUMMERING_TEKST("mvn:regelnummering (tekst)"), //
  TEKSTBEGIN("mvn:tekstbegin"), //
  TEKSTEINDE("mvn:teksteinde"), //
  TEKSTKLEUR_ROOD("mvn:tekstkleur (rood)"), //
  VREEMDTEKEN("mvn:vreemdteken"), //
  VERSREGEL("mvn:versregel"), //
  WITREGEL("mvn:witregel");

  private final String name;
  private final Set<String> allNames = Sets.newHashSet();

  private MVNAnnotationType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public Set<String> getAllNames() {
    if (allNames.isEmpty()) {
      for (MVNAnnotationType mvnAnnotationType : MVNAnnotationType.values()) {
        allNames.add(mvnAnnotationType.getName());
      }
    }
    return allNames;
  }
}
