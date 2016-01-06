dest=../elab4-backend/src/main/resources/publication
#propfile=src/main/resources/about.properties
tag=publication_backend_`date +"%Y.%m.%d.%H.%M.%S"`
git tag $tag
# echo "build=$tag" > $propfile
# echo "builddate=`date`" >> $propfile
rm $dest/WEB-INF/lib/*.jar
mvn clean compile war:exploded && rsync -cav target/elab4-publication-backend*/* $dest/
