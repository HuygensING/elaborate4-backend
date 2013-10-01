propfile=target/classes/elaborate/editor/resources/version.properties
function svninfo {
	echo "build=`svnversion |awk -F":" '{print $2}'`" > $propfile
	echo "builddate=`date`" >> $propfile
#  svn info|grep "Last Changed Rev"  | sed -e "s/Last Changed Rev: /build=/"      > $propfile
#  svn info|grep "Last Changed Date" | sed -e "s/Last Changed Date: /builddate=/" >> $propfile
}

svninfo
~/bin/mvn_test.sh
#mvn deploy