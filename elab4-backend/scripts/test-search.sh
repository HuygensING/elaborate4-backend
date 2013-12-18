eb=http://10.152.32.82:2013
rootcode=`curl --silent --show-error --data "username=root&password=d3gelijk" $eb/sessions/login/|jq -r ".token"`; echo $rootcode
e4-login
curl -i -H "Authorization: SimpleAuth $rootcode" -H "Content-Type: application/json" -d '{"term":"deus","textLayers":["Diplomatic"],"searchInAnnotations":"true"}' $eb/projects/17/search/
curl -v -H "Authorization: SimpleAuth $rootcode"  $eb/projects/1/search/201|jq "."
