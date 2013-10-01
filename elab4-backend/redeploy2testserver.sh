propfile=src/main/resources/version.properties
echo "build=`svnversion |awk -F":" '{print $2}'`" > $propfile
echo "builddate=`date`" >> $propfile
mvn tomcat:redeploy -P testserver
# restore compiled code to default
mvn clean test