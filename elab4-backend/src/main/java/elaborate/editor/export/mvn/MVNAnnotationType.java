package elaborate.editor.export.mvn;

/*
 * #%L
 * elab4-backend
 * =======
 * Copyright (C) 2011 - 2022 Huygens ING
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

import java.util.Set;

import com.google.common.collect.Sets;

public enum MVNAnnotationType {
  AFKORTING("mvn:afkorting", AnnotatedTextUsage.use),
  ALINEA("mvn:alinea", AnnotatedTextUsage.ignore),
  CIJFERS("mvn:cijfers (romeins)", AnnotatedTextUsage.use),
  DEFECT("mvn:defect", AnnotatedTextUsage.ignore),
  DOORHALING("mvn:doorhaling", AnnotatedTextUsage.use),
  INITIAAL("mvn:initiaal", AnnotatedTextUsage.use),
  INSPRINGEN("mvn:inspringen", AnnotatedTextUsage.ignore),
  KOLOM("mvn:kolom", AnnotatedTextUsage.ignore),
  LETTERS("mvn:letters (zelfnoemfunctie)", AnnotatedTextUsage.use),
  LINKERMARGEKOLOM("mvn:linkermargekolom", AnnotatedTextUsage.use),
  RECHTERMARGEKOLOM("mvn:rechtermargekolom", AnnotatedTextUsage.use),
  ONDERSCHRIFT("mvn:onderschrift", AnnotatedTextUsage.use),
  ONDUIDELIJK("mvn:onduidelijk", AnnotatedTextUsage.use),
  ONLEESBAAR("mvn:onleesbaar", AnnotatedTextUsage.ignore),
  OPHOGING_ROOD("mvn:ophoging (rood)", AnnotatedTextUsage.use),
  OPSCHRIFT("mvn:opschrift", AnnotatedTextUsage.use),
  PALEOGRAFISCH("mvn:paleografisch", AnnotatedTextUsage.use),
  POEZIE("mvn:poëzie", AnnotatedTextUsage.ignore),
  REGELNUMMERING_BLAD("mvn:regelnummering (blad)", AnnotatedTextUsage.ignore),
  TAAL("mvn:taal", AnnotatedTextUsage.use),
  TEKSTBEGIN("mvn:tekstbegin", AnnotatedTextUsage.use),
  TEKSTEINDE("mvn:teksteinde", AnnotatedTextUsage.use),
  TEKSTKLEUR_ROOD("mvn:tekstkleur (rood)", AnnotatedTextUsage.use),
  VREEMDTEKEN("mvn:vreemdteken", AnnotatedTextUsage.use),
  VERSREGEL("mvn:versregel", AnnotatedTextUsage.ignore),
  WITREGEL("mvn:witregel", AnnotatedTextUsage.use),

  // vervallen annotaties
  REGELNUMMERING_TEKST("mvn:regelnummering (tekst)", AnnotatedTextUsage.ignore),
  GEBRUIKERSNOTITIE("mvn:gebruikersnotitie", AnnotatedTextUsage.use),
  INCIPIT("mvn:incipit", AnnotatedTextUsage.use),
  METAMARK("mvn:metamark", AnnotatedTextUsage.use);

  private enum AnnotatedTextUsage {
    use,
    ignore
  }

  private final String name;
  private final AnnotatedTextUsage ignoreText;
  private static final Set<String> allNames = Sets.newHashSet();

  MVNAnnotationType(final String name, final AnnotatedTextUsage ignoreText) {
    this.name = name;
    this.ignoreText = ignoreText;
  }

  public String getName() {
    return name;
  }

  public static Set<String> getAllNames() {
    if (allNames.isEmpty()) {
      for (final MVNAnnotationType mvnAnnotationType : MVNAnnotationType.values()) {
        allNames.add(mvnAnnotationType.getName());
      }
    }
    return allNames;
  }

  public boolean ignoreText() {
    return ignoreText.equals(AnnotatedTextUsage.ignore);
  }

  public static MVNAnnotationType fromName(final String name) {
    for (final MVNAnnotationType type : values()) {
      if (type.getName().equals(name)) {
        return type;
      }
    }
    throw new RuntimeException("name not recognized: " + name);
  }
}
