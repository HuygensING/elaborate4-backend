<#macro dc field value>
  <#if value?has_content>
  <${field}>${value}</${field}>
  </#if>
</#macro>

<cmdi:CMD
  CMDVersion="1.1"
  xmlns:cmdi="http://www.clarin.eu/cmd/"
  xmlns:database="http://www.oclc.org/pears/"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://catalog.clarin.eu/ds/ComponentRegistry/rest/registry/profiles/${mdProfile}/xsd">

  <cmdi:Header>
    <cmdi:MdCollectionDisplayName>${mdCollectionDisplayName}</cmdi:MdCollectionDisplayName>
    <cmdi:MdProfile>${mdProfile}</cmdi:MdProfile>
    <cmdi:MdCreator>${mdCreator}</cmdi:MdCreator>
    <cmdi:MdCreationDate>${mdCreationDate?string("yyyy-MM-dd")}</cmdi:MdCreationDate>
    <cmdi:MdSelfLink>${mdSelfLink}</cmdi:MdSelfLink>
  </cmdi:Header>

  <cmdi:Resources>
    <cmdi:ResourceProxyList>
    <#if resourceProxy?exists>
      <cmdi:ResourceProxy id="${resourceProxy.id}">
        <cmdi:ResourceType>Metadata</cmdi:ResourceType>
        <cmdi:ResourceRef>${resourceProxy.ref}</cmdi:ResourceRef>
      </cmdi:ResourceProxy>
    </#if>
    </cmdi:ResourceProxyList>
    <cmdi:JournalFileProxyList/>
    <cmdi:ResourceRelationList/>
  </cmdi:Resources>

  <cmdi:Components>
    <DcmiTerms>
      <@dc field="contributor" value=dublinCoreRecord.contributor/>  
		  <@dc field="coverage" value=dublinCoreRecord.coverage/>  
		  <@dc field="creator" value=dublinCoreRecord.creator/>  
		  <@dc field="date" value=dublinCoreRecord.date/>  
		  <@dc field="description" value=dublinCoreRecord.description/>  
		  <@dc field="format" value=dublinCoreRecord.format/>  
		  <@dc field="identifier" value=dublinCoreRecord.identifier/>  
		  <@dc field="language" value=dublinCoreRecord.language/>  
		  <@dc field="publisher" value=dublinCoreRecord.publisher/>  
		  <@dc field="relation" value=dublinCoreRecord.relation/>  
		  <@dc field="rights" value=dublinCoreRecord.rights/>  
		  <@dc field="source" value=dublinCoreRecord.source/>  
		  <@dc field="subject" value=dublinCoreRecord.subject/>  
		  <@dc field="title" value=dublinCoreRecord.title/>  
		  <@dc field="type" value=dublinCoreRecord.type/>  
    </DcmiTerms>
  </cmdi:Components>
</cmdi:CMD>