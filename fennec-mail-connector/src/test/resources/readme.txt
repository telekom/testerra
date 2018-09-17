Die Datei server.cer muss für testzwecke der jvm hinzugefügt.
(selbsterstelltes Zertifikat um ssl Verbindung zum mailserver zu testen)
In jdk Ordner lib/security gehen und folgenden Befehl ausführen:
keytool -import -alias fennec.mailserver -file server.cert -keystore cacerts –storepass changeit