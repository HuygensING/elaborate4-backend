tag=testdeploy_`date +"%Y%m%d%H%M%S"`
git tag $tag
propfile=src/main/resources/version.properties;
echo "build=$tag" > $propfile
echo "builddate=`date`" >> $propfile
mvn tomcat:redeploy -P testserver
# restore compiled code to default
mvn clean test