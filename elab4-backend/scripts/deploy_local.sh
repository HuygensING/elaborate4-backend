propfile=target/classes/version.properties

function svninfo {
  echo "build=`svnversion |awk -F":" '{print $2}'`" > $propfile
  echo "builddate=`date`" >> $propfile 
}

mvn clean test && svninfo && mvn war:war && cp target/elaborate-backend.war ~/tomcat6/webapps/