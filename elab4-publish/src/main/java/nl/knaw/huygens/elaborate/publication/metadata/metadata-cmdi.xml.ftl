<cmdi:CMD
  CMDVersion="1.1"
  xmlns:cmdi="http://www.clarin.eu/cmd/"
  xmlns:database="http://www.oclc.org/pears/"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd http://www.clarin.eu/cmd/ http://catalog.clarin.eu/ds/ComponentRegistry/rest/registry/profiles/clarin.eu:cr1:p_1345561703673/xsd">

  <cmdi:Header>
    <cmdi:MdCollectionDisplayName>Elaborate</cmdi:MdCollectionDisplayName>
    <cmdi:MdProfile>clarin.eu:cr1:p_1345561703673</cmdi:MdProfile>
    <cmdi:MdCreator>${mdCreator}</cmdi:MdCreator>
    <cmdi:MdCreationDate>${mdCreationDate?string("yyyy-MM-dd")}</cmdi:MdCreationDate>
    <cmdi:MdSelfLink>${mdSelfLink}</cmdi:MdSelfLink>
  </cmdi:Header>

  <cmdi:Resources>
    <cmdi:ResourceProxyList>
      <cmdi:ResourceProxy id="${resourceProxy.id}">
        <cmdi:ResourceType>Metadata</cmdi:ResourceType>
        <cmdi:ResourceRef>${resourceProxy.ref}</cmdi:ResourceRef>
      </cmdi:ResourceProxy>
    </cmdi:ResourceProxyList>
    <cmdi:JournalFileProxyList/>
    <cmdi:ResourceRelationList/>
  </cmdi:Resources>

  <cmdi:Components>
    <cmdi:Elaborate>
      <cmdi:edition>
        <cmdi:CLARIN_PROFILE>clarin.eu:cr1:p_1345561703673</cmdi:CLARIN_PROFILE>
        <cmdi:id>MAN0000000001</cmdi:id>
        <cmdi:city></cmdi:city>
        <cmdi:library>Private Collection</cmdi:library>
        <cmdi:dates/>
        <cmdi:origins/>
      </cmdi:edition>
    </cmdi:Elaborate>
  </cmdi:Components>
</cmdi:CMD>