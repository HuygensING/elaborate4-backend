propfile=src/main/resources/version.properties
function svninfo {
  svn info|grep "Last Changed Rev"  | sed -e "s/Last Changed Rev: /build=/"      > $propfile
  svn info|grep "Last Changed Date" | sed -e "s/Last Changed Date: /builddate=/" >> $propfile
}

svninfo
mvn clean test package assembly:assembly
#cd ..
#unzip elaborate_backend/target/elab4_backend*-server.zip
