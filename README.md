eLaborate (backend)
===================

[![Github build](https://github.com/HuygensING/elaborate4-backend/workflows/tests/badge.svg)](https://github.com/HuygensING/elaborate4-backend/actions)
[![Project Status: Inactive – The project has reached a stable, usable state but is no longer being actively developed; support/maintenance will be provided as time allows.](https://www.repostatus.org/badges/latest/inactive.svg)](https://www.repostatus.org/#inactive)

## What is eLaborate?

eLaborate is an online work environment in which scholars can upload scans, transcribe and annotate text, and publish the results as on online text edition which is freely available to all users.
eLaborate is developed by Huygens ING.

This project is the REST-based backend, which connects with database and SOLR index.
It compiles to a webapp.

Installation
===========

download the codebase
---------------------

```
git clone https://github.com/HuygensING/elaborate4-backend.git
```

Set up the database
-------------------

psql elaborate_projects < sql/schema.sql

Set up the solr webapp
----------------------

http://wiki.apache.org/solr/SolrTomcat
Use the config files in the solr/ directory.

Build the backend webapp
------------------------
```
cd elab4
mvn install
cd elab4-backend
mvn -P example package
```

copy the .war in target/ to your tomcat's webapp directory



REST API
========

### about

  `GET /about`

returns the backend build version as JSON

example output:

```
{
  builddate: "Mon, Jan 27, 2014 11:59:00 AM",
  build: "20140127115900",
  version: "4.0"
}
```

### sessions

To login as the root user with the default password:
  `POST /sessions/login/`

data to post: `"username=root&password=toor"`

All the following REST calls need and `Authorization` header.
All `POST`/`PUT` calls accept and return JSON, unless otherwise specified.

### users

* Create:
  `POST /users`

      {
        "username" : "new_user",
        "email" : "new.user@huygens.knaw.nl",
        "firstName" : "New",
        "lastName" : "User",
        "role" : "USER",
        "password" : "laswysdfas23"
      }

* Read:
  `GET /users`

* Update:
  `PUT /users/{user_id}`

      {
        "email" : "new.user@example.org",
        "firstName" : "New",
        "lastName" : "User",
        "role" : "USER",
        "password" : "laswysdfas23"
      }

* Delete:
  `DELETE /users/{user_id}`
  
* request a password reset (sends email)
  `POST /sessions/passwordresetrequest`
  
  send the user's e-mail address as data, sends an email with a link to the password reset frontent page
  
* reset password
  `POST /sessions/passwordreset`

      {
        "emailAddress" : "name@example.org",
        "newPassword" : "newpassword",
        "token" : "aasd13413as"
      }

  where token is the token given in the email
  
### projectmetadata

* Create:
  `POST /projectmetadatafields`

      {
        "fieldName" : "whatever"
      }

* Read:
  all:
  `GET /projectmetadatafields`
  specific:
  `GET /projectmetadatafields/{field_id}`

* Update:
  `PUT /projectmetadatafields/{field_id}`

      {
        "fieldName" : "new.field.name"
      }


* Delete:
  `DELETE /projectmetadatafields/{field_id}`

### annotation types

* Create:
  `POST /annotationtypes`

      {
        "name" : "short_name"
        "description" : "Longer description"
      }

* Read:

  all:
  `GET /annotationtypes`

  specific:
  `GET /annotationtypes/{type_id}`

* Update:
  `PUT /annotationtypes/{type_id}`

      {
        "name" : "different_short_name"
        "description" : "Updated description"
      }

* Delete:
  `DELETE /annotationtypes/{type_id}`

* get metadata:
  `GET /annotationtypes/{type_id}/metadataitems`

* add metadata:
  `POST /annotationtypes/{type_id}/metadataitems/{meta_id}`

      {
        "name" : "fieldname"
        "description" : "Description of the field"
      }

* update metadata:
  `PUT /annotationtypes/{type_id}/metadataitems/{meta_id}`

      {
        "name" : "different fieldname"
        "description" : "Different description"
      }

* delete metadata:
  `DELETE /annotationtypes/{type_id}/metadataitems/{meta_id}`


### projects

* Create:
  `POST /projects`

      {
        "title" : "project title",
        "textLayers" : [ "Diplomatic", "Critical", "Translation"]
      }

* Read:

  all:
  `GET /projects`

  specific:
  `GET /projects/{project_id}`

* Delete:
  `DELETE /projects/{project_id}`

#### project textlayers

* update project textlayer settings:
  `PUT /projects/{project_id}/textlayers`

      [ "first","second","third" ]

#### project sorting/grouping levels

* update project sorting/grouping levels:
  `PUT /projects/{project_id}/sortlevels`

      [ "level1","level2","level3" ]

#### project annotation types

* Read:
  `GET /projects/{project_id}/annotationtypes`

* Update:
  `PUT /projects/{project_id}/annotationtypes`

      [1,2,3]

#### project logentries

* Read:
  `GET /projects/{project_id}/logentries`

#### project users

* Read:
  `GET /projects/{project_id}/projectusers`
  gives an array of user ids

* Update:
  `PUT /projects/{project_id}/projectusers`

      [1,2,3]

#### project settings

* Read:
  `GET /projects/{project_id}/settings`

* update project settings:
  `PUT /projects/{project_id}/settings`

      {
        "Type" : "Letter project",
        "Version" : "0.1"
      }

  extra system-defined project settings variables (optional):<br>
  `text.font` - use when a special (unicode) font is necessary to display the textlayer texts.
  `publication.title` - use when the publication needs a different title from the title in the edit environment.

  `entry.term_singular` - the name used in this project to indicate a single entry (eg. 'letter')
  `entry.term_plural` - the name used in this project to indicate multiple entries (eg. 'letters')

  `annotationtype.b.name` - short name for the annotationtype to be displayed as <b>bold</b> for this project
  `annotationtype.b.description` - longer description for the annotationtype to be displayed as <b>bold</b> for this project
  `annotationtype.i.name` - short name for the annotationtype to be displayed as <i>italic</i>
  `annotationtype.i.description` - longer description for the annotationtype to be displayed as <i>italic</i>
  `annotationtype.u.name` - short name for the annotationtype to be displayed as <u>underline</u>
  `annotationtype.u.description` - longer description for the annotationtype to be displayed as <u>underline</u>
  `annotationtype.strike.name` - short name for the annotationtype to be displayed as <strike>strikethrough</strike>
  `annotationtype.strike.description` - longer description for the annotationtype to be displayed as <strike>strikethrough</strike>


#### project entry metadatafields

* Read:
  `GET /projects/{project_id}/entrymetadatafields`

* Update:
  `PUT /projects/{project_id}/entrymetadatafields`

      [ "field1", "field2" ]

#### project entries

* all entries for this project:
  `GET /projects/{project_id}/entries`

### entries

* Create:
  `POST /projects/{project_id}/entries`

      {
        "name" : "project-unique name",
        "publishable" : false
      }

  `publishable` is optional, default is false;

* Read:
  `GET /projects/{project_id}/entries/{entry_id}`

* Update:
  `PUT /projects/{project_id}/entries/{entry_id}`

      {
        "name" : "project-unique name",
        "publishable" : true
      }

  `publishable` is optional

* Delete:
  `DELETE /projects/{project_id}/entries/{entry_id}`

* get previous/next entry ids:
  `GET /projects/{project_id}/entries/{entry_id}/prevnext`

      {
        "prev" : 1234,
        "next" : 1235
      }

  when `prev=-1`, entry `{entry_id}` is the first.
  when `next=-1`, entry `{entry_id}` is the last.
  The entry ids are sorted in alphabetical order using the contents of the metadata fields as defined in `project.level1`, `project.level2` and `project.level3`, and the `entryname`.

* Read entry settings:
  `GET /projects/{project_id}/entries/{entry_id}/settings`

* entrysettings bijwerken:
  `PUT /projects/{project_id}/entries/{entry_id}/settings`

      {
        "key" : "value",
        ....
      }

   where the values are strings.

* update multiple entrysettings:
  `PUT /projects/{project_id}/multipleentrysettings`

      {
        "projectEntryIds" : [1,2,3],
        "settings" : {
          "Publishable" : false,
          "field1" : "value1",
          "field2" : "value2",
          ....
        }
      }

   where the (selected) projectEntityIds are longs, settings is a hashmap, Publishable is a boolean, and all other values are strings.


* all transcripties for this entry:
  `GET /projects/{project_id}/entries/{entry_id}/transcriptions`


### facsimiles

* Create:
  `POST /projects/{project_id}/entries/{entry_id}/facsimiles`

      {
        "name" : "page 1",
        "filename" : "00000001.jpg"
        "zoomableUrl" : "http://localhost:8080/jp2/13507286068671.jp2"
      }

* Read:
  `GET /projects/{project_id}/entries/{entry_id}/facsimiles/{facsimile_id}`

* Update:
  `PUT /projects/{project_id}/entries/{entry_id}/facsimiles/{facsimile_id}`

      {
        "name" : "pagina 1",
        "filename" : "00000001.jpg"
        "zoomableUrl" : "http://localhost:8080/jp2/13507286068671.jp2"
      }

* Delete:
  `DELETE /projects/{project_id}/entries/{entry_id}/facsimiles/{facsimile_id}`


### transcriptions

* Create:
  `POST /projects/{project_id}/entries/{entry_id}/transcriptions`

      {
        "body": "transcription body",
        "textLayer": "Diplomatic",
      }

* Read:
  `GET /projects/{project_id}/entries/{entry_id}/transcriptions/{transcription_id}`

      {
        "body": "transcription body",
        "textLayer": "Diplomatic",
        "id": 13413
      }


* Update:
  `PUT /projects/{project_id}/entries/{entry_id}/transcriptions/{transcription_id}`

      {
        "body": "new body",
      }

* Delete:
  `DELETE /projects/{project_id}/entries/{entry_id}/transcriptions/{transcription_id}`

* all annotations for this transcription:
  `GET /projects/{project_id}/entries/{entry_id}/transcriptions/{transcription_id}/annotations`

### annotations

* Create:
  `POST /projects/{project_id}/entries/{entry_id}/transcriptions/{transcription_id}/annotations`

      {
        "body" : "the annotation body",
        "typeId" : 1
        "metadata" : {
           "field1" : "value1",
           "field2" : "value"
        }
      }

  where `typeId` is the id of the AnnotationType used, default is 1
  `metadata` is a hashmap with values for the relevant fields defined by the AnnotationType

* Read:
  `GET /projects/{project_id}/entries/{entry_id}/transcriptions/{transcription_id}/annotations/{annotation_id}`

* Update:
  `PUT /projects/{project_id}/entries/{entry_id}/transcriptions/{transcription_id}/annotations/{annotation_id}`

      {
        "body" : "the annotation body",
        "typeId" : 1
        "metadata" : {
           "field1" : "value1",
           "field2" : "value"
        }
      }

* Delete:
  `DELETE /projects/{project_id}/entries/{entry_id}/transcriptions/{transcription_id}/annotations/{annotation_id}`

### search

* Create:
  `POST /projects/{project_id}/search`

      {
        "term" : "*",
        "caseSensitive" : false,
        "fuzzy" : false,
        "searchInTranscriptions" : true,
        "textLayers" : [ ],
        "searchInAnnotations" : false,
        "resultFields" : ["Document title","Author(s)"],
        "sortParameters":[
			    {"fieldname":"Language","direction":"desc"},
			    {"fieldname":"Signature"},
			    {"fieldname":"Notes"}
			  ],
        "facetFields" : [ ],
        "facetInfoMap" : null,
        "orderLevels" : [ ],
        "level1Field" : "name",
        "level2Field" : "name",
        "level3Field" : "name",
        "textFieldsToSearch" : { },
        "facetValues" : [ ]
      }


* Read:
  `GET /projects/{project_id}/search/{search_id}`

-

## publish

The _projectleader_ or _admin user_ can publish a draft of the project via `POST /projects/{project_id}/draft`

You can POST some json settings:

    {
      "projectEntryMetadataFields" : ["Page","Part"],
      "annotationTypeIds" : [1,2,3]
    }


where `projectEntryMetadataFields` is an array of those metadata fields that should be included in the publication (as facets), in the given order.
By default all metadata fields will be included, in the order set in the project.

`annotationTypeIds` is a list of ids of those annotationtypes that should be included in the publication.
By default all annotationtypes will be included.

For the publication, the project metadata, and the entry content will be exported to json, the entry text will be indexed, and everything will be packaged into a war file that is deployed to the tomcat server.
The post will start the publication process in the background, and return a `Location` header with a link to the progress of the publishing:
a json map with the following keys:

- `id` - the publication task id
- `done`  - boolean set to `true` when the publishing is finished.
- `fail`  - boolean set to `true` when the publishing has been cancelled due to an internal error, the cause will be logged in a logline.
- `url` - the url where the publication can be seen once the publishing has finished successfully.
- `loglines` - an array of log lines with timestamps, indicating the progress of the publishing process.

Only those entries that have `publishable=true` will be shown in the publication.

The publication has a `search` comparable to the backend, with the exception of the need to login, and to provide the projectId.

-
Last updated: 2014-03-27
