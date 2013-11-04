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

# projectusers
curl -s -H "Authorization: SimpleAuth $rootcode" $eb/projects/$project_id/projectusers|jq "."
curl -i -H "Authorization: SimpleAuth $rootcode"  -H "Content-Type: application/json" -X PUT -d '[1,2,3]' $eb/projects/$project_id/projectusers

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
curl -i -H "Authorization: SimpleAuth $rootcode" -H "Content-Type: application/json" -X POST -d "{}" $eb/projects/$project_id/draft
curl -H "Authorization: SimpleAuth $rootcode" $eb/projects/$project_id/draft/$status_id | jq "."
 	

curl -H "Content-Type: application/json" -H "Authorization: SimpleAuth $rootcode" $eb/projects/$project_id/entries/17152/transcriptions/21224|jq "."
curl -H "Content-Type: application/json" -H "Authorization: SimpleAuth $rootcode" $eb/projects/$project_id/entries/17152/transcriptions/21224/annotations|jq "."
curl -v -H "Content-Type: application/json" -H "Authorization: SimpleAuth $rootcode" -X POST -d '{"body":"whatever"}'  $eb/projects/$project_id/entries/17152/transcriptions/21224/annotations
curl -v -H "Content-Type: application/json" -H "Authorization: SimpleAuth $rootcode" -X POST -d '{"body":"whatever", "typeId":1}'  $eb/projects/$project_id/entries/17152/transcriptions/21224/annotations
curl -v -H "Content-Type: application/json" -H "Authorization: SimpleAuth $rootcode" -X POST -d '{"body":"whatever", "typeId":31, "metadata":{"person id":"PERSONID"}}'  $eb/projects/$project_id/entries/17152/transcriptions/21224/annotations

curl -v -H "Content-Type: application/json" -H "Authorization: SimpleAuth $rootcode" -X PUT -d '{"body":"whatever", "typeId":1}'  $eb/projects/$project_id/entries/17152/transcriptions/21224/annotations/aa
