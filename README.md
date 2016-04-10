# jetty-fileservice-servlets

## Prerequisites
- Java 7
- Maven 3.x or later

## Available Services
- URL /FileUpload - file upload
- URL /FileDownload?filename=\<filename\> - file download

## How to create a JAR file
1. Execute: mvn clean package
2. Once build has completed, JAR file will be available in target directory.

## Running

1. Modify fileprovider.properties with the appropriate parameters.
2. You may start the Jetty server by **either** of the following ways:
	1. Run: java -jar jetty-fileservice-servlets.jar [listening port number]. e.g. java-jar jetty-fileservice-servlets.jar 8080
	2. Use Maven exec plugin to run the Jetty Server: mvn exec java 8080