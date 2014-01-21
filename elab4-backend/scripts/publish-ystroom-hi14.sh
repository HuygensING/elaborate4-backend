eb=http://rest.elaborate.huygens.knaw.nl
rootcode=`curl --silent --show-error --data "username=root&password=toor" $eb/sessions/login/|jq -r ".token"`; echo $rootcode
project_id=70
curl -i -H "Authorization: SimpleAuth $rootcode" -H "Content-Type: application/json" -X POST $eb/projects/$project_id/draft
