How to run the Management System

Idk upload the databases/sql files first 

go to the directory the jar file is in

C:\...Auto-ComPAnIon\InventoryManagementSystem-master\InventoryManagementSystem-master\InventoryManagement\dist


run it in cmd as

'java -jar InventoryManagement.jar

username root
password root
(If I recall correctly, can always change it in the files)



How to run the POS System

Needs MAVEN to compile btw

Idk upload the databases/sql files first too
Have to build and compile this first for some reason

go to the directory the POM.XML file is in
C:\...Auto-ComPAnIon\app_posSystemJar-main\app_posSystemJar-main


run it in cmd as

mvn compile
mvn exec:java -Dexec.mainClass="org.MiniDev.Home.MiniDevPOS" -Dexec.classpathScope="compile"

Password that I used is
user : admin
pass : admin123

They use hashing, BCrypt and stuff ig