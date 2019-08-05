Die Datei server.cer muss f�r testzwecke der jvm hinzugef�gt.
(selbsterstelltes Zertifikat um ssl Verbindung zum mailserver zu testen)
In jdk Ordner lib/security gehen und folgenden Befehl ausf�hren:
keytool -import -alias tt.mailserver -file server.cert -keystore cacerts �storepass changeit