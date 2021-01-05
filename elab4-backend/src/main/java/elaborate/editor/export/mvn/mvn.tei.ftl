<#--
 #%L
 elab4-backend
 =======
 Copyright (C) 2011 - 2021 Huygens ING
 =======
 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as
 published by the Free Software Foundation, either version 3 of the
 License, or (at your option) any later version.
 
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 
 You should have received a copy of the GNU General Public
 License along with this program.  If not, see
 <http://www.gnu.org/licenses/gpl-3.0.html>.
 #L%
-->
<?xml version="1.0" encoding="utf-8"?>
<MVN xmlns="http://www.tei-c.org/ns/1.0">
  <teiHeader>
    <fileDesc>
      <titleStmt>
        <title>${title}</title>
        <title type="sub">${subtitle}</title>
      </titleStmt>
      <publicationStmt>
        <p></p>
      </publicationStmt>
      <sourceDesc>
        <msDesc>
          <msIdentifier>
            <placeName>${place}</placeName>
            <institution>${institution}</institution>
            <idno>${idno}</idno>
          </msIdentifier>
        </msDesc>
      </sourceDesc>
    </fileDesc>
  </teiHeader>
  <text xml:id="${sigle}">
    <group>
${body}
    </group>
  </text>
</MVN>
