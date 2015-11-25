<?xml version="1.0" encoding="utf-8"?>
<TEI xmlns="http://www.tei-c.org/ns/1.0">
  <teiHeader>
    <fileDesc>
      <titleStmt>
        <title>${title}</title>
      </titleStmt>
      <publicationStmt>
        <p></p>
      </publicationStmt>
      <sourceDesc>
        <msDesc>
          <msIdentifier>
            <idno>${idno}</idno>
          </msIdentifier>
        </msDesc>
      </sourceDesc>
    </fileDesc>
  </teiHeader>
  <text xml:id="${sigle}">
    <group>
      <text>
        <body>

<#list pages as page>
<pb n="${page.n}" xml:id="${page.id}" facs="${page.facs}"/>
${page.body}

</#list>

        </body>
      </text>
    </group>
  </text>
</TEI>