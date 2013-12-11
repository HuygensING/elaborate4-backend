Documentatie
============

## entiteiten

Voor de entiteit calls moet altijd een `Authorization` header meegestuurd worden.  
Alle POST/PUT calls sturen JSON en alle GET calls geven JSON terug, tenzij anders vermeld.  

### users

* Create/toevoegen:  
  `POST` naar `/users`

            {
              "username" : "new_user",
              "email" : "new.user@huygens.knaw.nl",
              "firstName" : "New",
              "lastName" : "User",
              "role" : "USER",
              "password" : "laswysdfas23"
            }

* Read/opvragen:  
  `GET` `/users`

* Update/bijwerken:  
  `PUT` naar `/users/{user_id}`

            {
              "email" : "new.user@example.org",
              "firstName" : "New",
              "lastName" : "User",
              "role" : "USER",
              "password" : "laswysdfas23"
            }

* Delete/wissen:  
  `DELETE`  `/users/{user_id}`
  
### projectmetadata

* Create/toevoegen:  
  `POST` naar `/projectmetadatafields`

          {
            "fieldName" : "whatever"
          }

* Read/opvragen:  
  alle:
  `GET` `/projectmetadatafields`
  specifieke:
  `GET` `/projectmetadatafields/{field_id}`

* Update/bijwerken:  
  `PUT` naar `/projectmetadatafields/{field_id}`

          {
            "fieldName" : "new.field.name"
          }


* Delete/wissen:  
  `DELETE`  `/projectmetadatafields/{field_id}`
  
### annotation types

* Create/toevoegen:  
  `POST` naar `/annotationtypes`

          {
            "name" : "korte naam"
            "description" : "langere beschrijving"
          }

* Read/opvragen:  
  alle:
  `GET` `/annotationtypes`
  
  specifieke:  
  `GET` `/annotationtypes/{type_id}`

* Update/bijwerken:    
  `PUT` naar `/annotationtypes/{type_id}`

          {
            "name" : "andere korte naam"
            "description" : "betere beschrijving"
          }


* Delete/wissen:    
  `DELETE`  `/annotationtypes/{type_id}`
  
* metadata opvragen:  
  `GET` `/annotationtypes/{type_id}/metadataitems`

* metadata toevoegen:  
  `POST` `/annotationtypes/{type_id}/metadataitems/{meta_id}`

          {
            "name" : "metadataveld"
            "description" : "beschrijving"
          }

* metadata bijwerken:  
  `PUT` `/annotationtypes/{type_id}/metadataitems/{meta_id}`

          {
            "name" : "andere veldnaam"
            "description" : "betere beschrijving"
          }

* metadata wissen:  
  `DELETE` `/annotationtypes/{type_id}/metadataitems/{meta_id}`


### projecten

* Create/toevoegen:  
  `POST` naar `/projects`

          {
            "title" : "project title",
            "textLayers" : [ "Diplomatic", "Critical", "Translation"]
          }

* Read/opvragen:
  alle:  
  `GET` `/projects`
  specifieke:
  `GET` `/projects/{project_id}`

* Delete/wissen:  
  `DELETE`  `/projects/{project_id}`
  
#### project textlayers

* project textlayer settings updaten:  
  `PUT` naar `/projects/{project_id}/textlayers`

          [ "first","second","third" ]

#### project sorting/grouping levels

* project sorting/grouping levels updaten:  
  `PUT` naar `/projects/{project_id}/sortlevels`

          [ "level1","level2","level3" ]

#### project annotationtypes

* Read/opvragen:
  `GET` naar `/projects/{project_id}/annotationtypes`

* Update/bijwerken:
  `PUT` naar `/projects/{project_id}/annotationtypes`

          [1,2,3]

#### project logentries

* Read/opvragen:
  `GET` naar `/projects/{project_id}/logentries`

#### project users

* Read/opvragen:
  `GET` naar `/projects/{project_id}/projectusers`
  geeft een array van user id's
  
* Update/bijwerken:  
  `PUT` naar `/projects/{project_id}/projectusers`

          [1,2,3]

#### project settings

* Read/opvragen:
  `GET` naar `/projects/{project_id}/settings`

* project settings updaten:  
  `PUT` naar `/projects/{project_id}/settings`

          {
            "Type" : "Brievenproject",
            "Version" : "0.1"
          }
          
  extra systeemgedefinieerde projectsettingsvariabelen (niet verplicht):<br>
  `text.font` - invullen indien een speciaal (unicode) font nodig is om de textlayer tekst weer te geven<br>
  `publication.title` - invullen indien de publicatie een andere titel moet krijgen dan in de werkomgeving<br>

  `entry.term_singular` - de naam die binnen dit project gebruikt moet worden om 1 entry aan te geven (bv. 'letter')<br>
  `entry.term_plural` - de naam die binnen dit project gebruikt moet worden om meerdere entries aan te geven (bv. 'letters')<br>

  `annotationtype.b.name` - korte naam voor het annotatietype dat binnen dit project weergegeven wordt als <b>bold</b><br>
  `annotationtype.b.description` - omschrijving voor het annotatietype dat binnen dit project weergegeven wordt als <b>bold</b><br>
  `annotationtype.i.name` - korte naam voor het annotatietype dat binnen dit project weergegeven wordt als <i>italic</i><br>
  `annotationtype.i.description` - omschrijving voor het annotatietype dat binnen dit project weergegeven wordt als <i>italic</i><br>
  `annotationtype.u.name` - korte naam voor het annotatietype dat binnen dit project weergegeven wordt als <u>underline</u><br>
  `annotationtype.u.description` - omschrijving voor het annotatietype dat binnen dit project weergegeven wordt als <u>underline</u><br>
  `annotationtype.strike.name` - korte naam voor het annotatietype dat binnen dit project weergegeven wordt als <strike>strikethrough</strike><br>
  `annotationtype.strike.description` - omschrijving voor het annotatietype dat binnen dit project weergegeven wordt als <strike>strikethrough</strike><br>


#### project entry metadatafields

* Read/opvragen:
  `GET` naar `/projects/{project_id}/entrymetadatafields`

* Update/bijwerken:  
  `PUT` naar `/projects/{project_id}/entrymetadatafields`
  
            [ "field1", "field2" ]  

#### project entries

* alle entries van dit project:  
  `GET` `/projects/{project_id}/entries`

### entries

* Create/toevoegen:  
  `POST` naar `/projects/{project_id}/entries`

          {
            "name" : "project-unique name",
            "publishable" : false
          }
          
  `publishable` is optional, default false;
          
* Read/opvragen:  
  `GET` `/projects/{project_id}/entries/{entry_id}`

* Update/bijwerken:  
  `PUT` naar `/projects/{project_id}/entries/{entry_id}`

          {
            "name" : "project-unique name",
            "publishable" : true
          }
  
  `publishable` is optional
  
* Delete/wissen:  
  `DELETE`  `/projects/{project_id}/entries/{entry_id}`

* ids ophalen van voorgaande/volgende entries:  
  `GET` `/projects/{project_id}/entries/{entry_id}/prevnext`
  
          {
            "prev" : 1234,
            "next" : 1235
          }
  
  als `prev=-1`, dan is entry `{entry_id}` de eerste.<br>
  als `next=-1`, dan is entry `{entry_id}` de laatste.<br>
  De entryids zijn gesorteerd op de alfabetische volgorde van de inhoud van de metadatavelden zoals gedefinieerd in project.level1, project.level2 en project.level3, en daarna op entryname. 

* entrysettings opvragen:  
  `GET` `/projects/{project_id}/entries/{entry_id}/settings`

* entrysettings bijwerken:  
  `PUT` `/projects/{project_id}/entries/{entry_id}/settings`

        {
          "key" : "value",
          ....
        }

   waarbij de values strings zijn.

* meerdere entrysettings tegelijkertijd bijwerken:  
  `PUT` `/projects/{project_id}/multipleentrysettings`

        {
          "projectEntryIds" : [1,2,3],
          "settings" : {
            "Publishable" : false,
            "field1" : "value1",
            "field2" : "value2",
            ....
          }
        }

   waarbij de (geselecteerde) projectEntityIds longs, settings een hashmap, Publishable een boolean, de rest van de values zijn strings.


* alle transcripties van deze entry:  
  `GET` `/projects/{project_id}/entries/{entry_id}/transcriptions`


### facsimiles

* Create/toevoegen:  
  `POST` naar `/projects/{project_id}/entries/{entry_id}/facsimiles`

          {
            "name" : "pagina 1",
            "filename" : "00000001.jpg"
            "zoomableUrl" : "http://localhost:8080/jp2/13507286068671.jp2"
          }

* Read/opvragen:  
  `GET` `/projects/{project_id}/entries/{entry_id}/facsimiles/{facsimile_id}`

* Update/bijwerken:  
  `PUT` naar `/projects/{project_id}/entries/{entry_id}/facsimiles/{facsimile_id}`

          {
            "name" : "pagina 1",
            "filename" : "00000001.jpg"
            "zoomableUrl" : "http://localhost:8080/jp2/13507286068671.jp2"
          }

* Delete/wissen:  
  `DELETE`  `/projects/{project_id}/entries/{entry_id}/facsimiles/{facsimile_id}`


### transcripties

* Create/toevoegen:  
  `POST` naar `/projects/{project_id}/entries/{entry_id}/transcriptions`

          {
            "body": "transcription body",
            "textLayer": "Diplomatic",
          }

* Read/opvragen:  
  `GET` `/projects/{project_id}/entries/{entry_id}/transcriptions/{transcription_id}`

          {
            "body": "transcription body",
            "textLayer": "Diplomatic",
            "id": 13413
          }
  

* Update/bijwerken:  
  `PUT` naar `/projects/{project_id}/entries/{entry_id}/transcriptions/{transcription_id}`

          {
            "body": "new body",
          }

* Delete/wissen:  
  `DELETE`  `/projects/{project_id}/entries/{entry_id}/transcriptions/{transcription_id}`

* alle annotaties van deze transcriptie:  
  `GET` `/projects/{project_id}/entries/{entry_id}/transcriptions/{transcription_id}/annotations`

### annotaties

* Create/toevoegen:  
  `POST` naar `/projects/{project_id}/entries/{entry_id}/transcriptions/{transcription_id}/annotations`

          {
            "body" : "de annotatie body",
            "typeId" : 1
            "metadata" : {
               "field1" : "value1", 
               "field2" : "value" 
            }
          }

  waarbij `typeId` het id is van het gebruikte AnnotationType, default is 1
  `metadata` is een hashmap met waardes voor de relevante velden bij het gebruikte AnnotationType
  
* Read/opvragen:  
  `GET` `/projects/{project_id}/entries/{entry_id}/transcriptions/{transcription_id}/annotations/{annotation_id}`

* Update/bijwerken:  
  `PUT` naar `/projects/{project_id}/entries/{entry_id}/transcriptions/{transcription_id}/annotations/{annotation_id}`

          {
            "body" : "de annotatie body",
            "typeId" : 1
            "metadata" : {
               "field1" : "value1", 
               "field2" : "value" 
            }
          }

* Delete/wissen:  
  `DELETE`  `/projects/{project_id}/entries/{entry_id}/transcriptions/{transcription_id}/annotations/{annotation_id}`
  
### search

* Create:  
  `POST` naar `/projects/{project_id}/search`

			{
			  "term" : "*",
			  "caseSensitive" : false,
			  "fuzzy" : false,
			  "searchInTranscriptions" : true,
			  "textLayers" : [ ],
			  "searchInAnnotations" : false,
			  "sort" : null,
			  "sortDir" : "asc",
			  "ascending" : true,
			  "facetFields" : [ ],
			  "facetInfoMap" : null,
			  "orderLevels" : [ ],
			  "level1Field" : "name",
			  "level2Field" : "name",
			  "level3Field" : "name",
			  "textFieldsToSearch" : { },
			  "facetValues" : [ ]
			}

  
* Read/opvragen:  
  `GET` `/projects/{project_id}/search/{search_id}`


****************************************

## publication

De _projectleader_ of _admin user_ mag publiceren via een `POST` naar `/projects/{project_id}/draft`

Je kunt een json setting meeposten:

      {
        "projectEntryMetadataFields" : ["Pagina","Deel"],
        "annotationTypeIds" : [1,2,3]
      }


waarbij `projectEntryMetadataFields` een array is van de metadatavelden die mee moeten komen met de publicatie, in de aangegeven volgorde te tonen.
Default worden alle metadatavelden getoond, in de volgorde zoals ze in het project staan.

`annotationTypeIds` bevat een lijst van ids van die annotationtypes die getoond moeten worden in de publicatie, default worden ze allemaal getoond.

Na de post wordt de publicatie op de achtergrond gemaakt: metadata van het project, en de inhoud van de entries wordt naar json bestanden geschreven, de entry teksten worden geindexeerd, en van het geheel wordt een warfile gemaakt.
De post komt meteen terug met een `Location` waarop de voortgang uitgelezen kan worden: een json map met de volgende keys:

- `id` - het id van de publicatieactie
- `done`  - boolean die aangeeft of het aanmaken van de publicatie klaar is
- `fail`  - boolean die aangeeft of het aanmaken van de publicatie onderbroken is door een error, de oorzaak is gelogd in de loglines
- `url` - zodra de het publicatieproces done is staat hierin het url waarop de publicatie te zien is.
- `loglines` - een array van logregels die aangeven waar het publicatieproces mee bezig is, met timestamps.

Alleen de entries met `publishable=true` worden in de publicatie opgenomen.

De publicatie heeft een `search` api vergelijkbaar met die van de elaborate backend, met als verschil dat `projectId` niet meegegeven hoeft te worden, en dat er niet ingelogd hoeft te worden.
  
************************************************************************************************************************

<!--
## TODO

- annotationTypes groeperen
- projectType: CKCC
- milestones plaatsen: chapter/page/etc.
- behalve publishable ook published als metadata op de entry
-->

  <!--
eb=http://10.152.32.82:2013
eb=http://rest.elaborate.huygens.knaw.nl

rootcode=`curl --silent --show-error --data "username=root&password=toor" $eb/sessions/login/|jq -r ".token"`; echo $rootcode
rootcode=`curl --silent --show-error --data "username=root&password=d3gelijk" $eb/sessions/login/|jq -r ".token"`; echo $rootcode

curl -i -H "Authorization: SimpleAuth $rootcode" -H "Content-Type: application/json" -d '{"term":"deus","textLayers":["Diplomatic"],"searchInAnnotations":"true"}' $eb/projects/17/search/
curl -v -H "Authorization: SimpleAuth $rootcode"  $eb/projects/1/search/201|jq "."

curl -s -H "Authorization: SimpleAuth $rootcode" $eb/projects|jq "."
curl -i -H "Authorization: SimpleAuth $rootcode" -X OPTIONS $eb/projects

project_id=1
curl -s -H "Authorization: SimpleAuth $rootcode" $eb/projects/$project_id|jq "."
curl -s -H "Authorization: SimpleAuth $rootcode" $eb/projects/$project_id/settings|jq "."
curl -s -H "Authorization: SimpleAuth $rootcode" $eb/projects/$project_id/entrymetadatafields|jq "."
curl -s -H "Authorization: SimpleAuth $rootcode" $eb/projects/$project_id/annotationtypes|jq "."
curl -s -H "Authorization: SimpleAuth $rootcode" $eb/projects/$project_id/users|jq "."
curl -s -H "Authorization: SimpleAuth $rootcode" $eb/projects/$project_id/statistics|jq "."

# create project
curl -i -H "Authorization: SimpleAuth $rootcode" -H "Content-Type: application/json" -X POST -d "{\"title\":\"Project X ($(date))\", \"textLayers\":[\"Diplomatic\"]}" $eb/projects

# update project settings
curl -i -H "Authorization: SimpleAuth $rootcode" -H "Content-Type: application/json" -X PUT -d '{"Type":"Brievenproject","Version":"0.1","Start date":"nu","Release date":"onbekend"}' $eb/projects/$project_id/settings

# ProjectMetadataField
curl -s -H "Authorization: SimpleAuth $rootcode" $eb/projectmetadatafields|jq "."
curl -i -H "Authorization: SimpleAuth $rootcode" -H "Content-Type: application/json" -X POST -d '{"fieldName":"EntryDescription"}' $eb/projectmetadatafields

# project users
curl -s -H "Authorization: SimpleAuth $rootcode" $eb/projects/$project_id/users/$user_id|jq "."

# project tei export
curl -i -H "Authorization: SimpleAuth $rootcode" $eb/projects/$project_id/tei


# read users
curl -s -H "Authorization: SimpleAuth $rootcode" $eb/users|jq "."

# create user
curl -i -H "Authorization: SimpleAuth $rootcode" -H "Content-Type: application/json" -X POST -d '{"username":"new_user","email":"new.user@huygens.knaw.nl","firstName":"New","lastName":"User","role":"USER","password":"laswysdfas23"}' $eb/users

# read user
curl -s -H "Authorization: SimpleAuth $rootcode" $eb/users/$user_id|jq "."

# update user
curl -i -H "Authorization: SimpleAuth $rootcode" -H "Content-Type: application/json" -X PUT -d '{"email":"new.user@example.com","firstName":"John","lastName":"Doe"}' $eb/users/$user_id

# delete user
curl -i -H "Authorization: SimpleAuth $rootcode" -X DELETE $eb/users/$user_id


curl -H "Authorization: SimpleAuth $rootcode" $eb/projects/$project_id/projectusers|jq "."
curl -i -H "Authorization: SimpleAuth $rootcode" -X PUT $eb/projects/$project_id/projectusers/$user_id
curl -i -H "Authorization: SimpleAuth $rootcode" -X DELETE $eb/projects/$project_id/projectusers/$user_id

# loglines
curl -H "Authorization: SimpleAuth $rootcode" $eb/projects/$project_id/logentries|jq "."

# project entries
curl -s -H "Authorization: SimpleAuth $rootcode" $eb/projects/$project_id/entries|jq "."

# add project entry
curl -i -H "Authorization: SimpleAuth $rootcode" -H "Content-Type: application/json" -d '{"name":"project-unique entryname"}' $eb/projects/$project_id/entries

# update project entry
curl -i -H "Authorization: SimpleAuth $rootcode" -H "Content-Type: application/json" -X PUT -d '{"name":"new project-unique entryname"}' $eb/projects/$project_id/entries/$entry_id

# delete project entry
curl -i -H "Authorization: SimpleAuth $rootcode" -X DELETE $eb/projects/$project_id/entries/$entry_id

# project entry facsimiles
curl -s -H "Authorization: SimpleAuth $rootcode" $eb/projects/$project_id/entries/$entry_id/facsimiles|jq "."

# project entry transcriptions
curl -s -H "Authorization: SimpleAuth $rootcode" $eb/projects/$project_id/entries/$entry_id/transcriptions|jq "."

curl -s -H "Authorization: SimpleAuth $rootcode" $eb/projects/$project_id/entries/$entry_id/transcriptions/$transcription_id|jq "."

#add transcription
curl -i -H "Authorization: SimpleAuth $rootcode" -H "Content-Type: application/json" -X POST -d '{"body":"dit is mijn lichaam","textLayer":"layer"}' $eb/projects/$project_id/entries/$entry_id/transcriptions

#update transcription
curl -i -H "Authorization: SimpleAuth $rootcode" -H "Content-Type: application/json" -X PUT -d '{"body":"brand new body<br/>one time only"}' $eb/projects/$project_id/entries/$entry_id/transcriptions/$transcription_id

# project entry setting
curl -s -H "Authorization: SimpleAuth $rootcode" $eb/projects/$project_id/entries/$entry_id/settings|jq "."

# delete entry
curl -i -H "Authorization: SimpleAuth $rootcode" -X DELETE $eb/projects/$project_id/entries/$entry_id

# update entry settings
curl -H "Authorization: SimpleAuth $rootcode" -H "Content-Type: application/json" -X PUT -d '{"field1":"value1","field2":"value2"}'$eb/projects/$project_id/entries/$entry_id/settings|jq "."

# update entry settings for multiple entries
curl -H "Authorization: SimpleAuth $rootcode" -H "Content-Type: application/json" -X PUT -d '{"projectEntryIds":[1,2,3],"settings":{"Publishable":false,"field1":"value1","field2":"value2"}}' $eb/projects/$project_id/multipleentrysettings|jq "."

# publish project
curl -i -H "Authorization: SimpleAuth $rootcode" -H "Content-Type: application/json" -X POST -d "{}" $eb/projects/$project_id/publication
curl -H "Authorization: SimpleAuth $rootcode" $eb/projects/$project_id/publication/$status_id | jq "."
  

curl -H "Content-Type: application/json" -H "Authorization: SimpleAuth $rootcode" $eb/projects/$project_id/entries/17152/transcriptions/21224|jq "."
curl -H "Content-Type: application/json" -H "Authorization: SimpleAuth $rootcode" $eb/projects/$project_id/entries/17152/transcriptions/21224/annotations|jq "."
curl -v -H "Content-Type: application/json" -H "Authorization: SimpleAuth $rootcode" -X POST -d '{"body":"whatever"}'  $eb/projects/$project_id/entries/17152/transcriptions/21224/annotations
curl -v -H "Content-Type: application/json" -H "Authorization: SimpleAuth $rootcode" -X POST -d '{"body":"whatever", "typeId":1}'  $eb/projects/$project_id/entries/17152/transcriptions/21224/annotations
curl -v -H "Content-Type: application/json" -H "Authorization: SimpleAuth $rootcode" -X POST -d '{"body":"whatever", "typeId":31, "metadata":{"person id":"PERSONID"}}'  $eb/projects/$project_id/entries/17152/transcriptions/21224/annotations

curl -v -H "Content-Type: application/json" -H "Authorization: SimpleAuth $rootcode" -X PUT -d '{"body":"whatever", "typeId":1}'  $eb/projects/$project_id/entries/17152/transcriptions/21224/annotations/aa
-->

<!--  
= annotatietab
facet: type

indexeren:
per annotatie:
- annotation.id
- annotated text
- project.id
- type
- metadata
- annotatievolgorde in tekst (positie <ae>)


=project metadata

extra projectmetadata velden

publication.font to use (ivm special utf8 characters)
publication.title = de titel van de publicatie
entry.name_singular = hoe een entry genoemd word.
entry.name_plural = het woord voor meerdere entries 
annotationtype.b.name = De (korte) annotatietypenaam voor <b> in transcriptiebody
annotationtype.b.description = De annotatietypebeschrijving voor <b> in transcriptiebody
annotationtype.i.name = De (korte) annotatietypenaam voor <i> in transcriptiebody
annotationtype.i.description = De annotatietypebeschrijving voor <i> in transcriptiebody
annotationtype.u.name = De (korte) annotatietypenaam voor <u> in transcriptiebody
annotationtype.u.description = De annotatietypebeschrijving voor <u> in transcriptiebody
annotationtype.strike.name = De (korte) annotatietypenaam voor <strike> in transcriptiebody
annotationtype.strike.description = De annotatietypebeschrijving voor <strike> in transcriptiebody


publicatie fase 2:
project entries aanmelden bij oaipmh

-->
Last updated: 2013-10-29