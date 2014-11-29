eFoods
======

Project C of EECS4413 @ YU

### B2C
* Download the supporting tools package (includes jetty­run and other apache libraries)
  https://www.dropbox.com/s/h4spx292i3w2k9x/efoods­runner.zip?dl=0
* Extract the tools package and place “eFoods.war” in the /app subfolder.  
```
    ~/efoods-runner$> tree .
    ├── app
    │ └──eFoods.war
    ├── jetty-runner.jar 
    └── lib
        ├── catalina-ant.jar
        ├── catalina-ha.jar
        ├── catalina-storeconfig.jar
        ├── catalina-tribes.jar
        ├── catalina-jar
        ├── derbyclient.jar
        ├── jstl.jar
        ├── standard.jar
        ├── tomcat-jdbc.jar
        ├── tomcat-jni.jar
        ├── tomcat-juli.jar
        └── tomcat-util.jar
```
* At the root of the archive, execute the following command to start the webserver.   
  `java -jar jetty-runner.jar --port 4413 --lib lib/ --path /eFoods app/eFoods.war`
* Navigate to URL to use the app. http://localhost:4413/eFoods 

### B2B
* Make sure B2C app is running.
* Extract B2B.zip
* Open a command line and `cd` in to root of the extracted archive.
* Type command `javac src/**/*.java -d bin/` to compile.
* `cd` in to the `bin` folder.
* Type command `java ctrl.Manager` to run.

### Auth
* Extract Auth.zip and place it in a directory that is served by apache of red.cse.yorku.ca
* Fix permissions (while at the root of the extracted folder.)
```
find . -type f -exec chmod o+r {} +
find . -type d -exec chmod o+rx {} +
```
* Load the url of index.php in the extracted archive.
