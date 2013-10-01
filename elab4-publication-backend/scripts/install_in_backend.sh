propfile=src/main/resources/version.properties
echo "build=`svnversion |awk -F":" '{print $2}'`" > $propfile
echo "builddate=`date`" >> $propfile
rm ../elaborate_backend/src/main/resources/publication/WEB-INF/lib/*.jar
mvn compile war:exploded && rsync -cav target/publication-0.0.1/* ../elaborate_backend/src/main/resources/publication/