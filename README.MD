# jetty-fileservice-servlets
### Prerequisites
- Java 7
- Maven 3.x or later
### How to create a JAR file
1. Execute: mvn clean package
2. Once build has completed, JAR file will be available in target directory.
### How To Run
1. Modify fileprovider.properties with the appropriate parameters.
2. Run Jetty server by executing: java -jar jetty-fileservice-servlets.jar [listening port number]. e.g. java-jar jetty-fileservice-servlets.jar 8080 
3. Alternatively, you may use Maven to run the Jetty Server: mvn exec java 8080
### Available Services
- /FileUpload - file upload
- /FileDownload?filename=<filename> - file download