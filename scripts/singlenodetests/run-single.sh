#
java -ea -DgigapaxosConfig=../../conf/gigapaxos.gnsApp.properties -Djavax.net.ssl.trustStorePassword=qwerty -Djavax.net.ssl.trustStore=../../conf/trustStore/node100.jks -Djavax.net.ssl.keyStorePassword=qwerty -Djavax.net.ssl.keyStore=../../conf/keyStore/node100.jks -cp ../../dist/GNS.jar edu.umass.cs.gns.gnsApp.AppReconfigurableNode -test -configFile ns.properties &
java -ea -Djavax.net.ssl.trustStorePassword=qwerty -Djavax.net.ssl.trustStore=../../conf/trustStore/node100.jks -Djavax.net.ssl.keyStorePassword=qwerty -Djavax.net.ssl.keyStore=../../conf/keyStore/node100.jks -cp ../../dist/GNS.jar edu.umass.cs.gns.localnameserver.LocalNameServer -configFile lns.properties  &
