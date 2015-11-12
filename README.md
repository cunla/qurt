
Quarterly Utility Reports Tool (QURT)
===============================
#User Guide

###INTRODUCTION
The Quarterly Utility Reports Tool (QURT) is an add-on utility for EMC’s RecoverPoint for VMs product, that is available as open source software.
QURT provides an easy way to monitor the usage of one or more RecoverPoint for VM systems. It is intended for users who wish to generate a quarterly report of replicated Virtual Machines.

###OVERVIEW
The QURT quarterly reports displays average number of VMs per month for the quarter.

* The report includes both source and replica VMs
* The monthly value is based the average of weekly sample values captured each Friday at noon
* The report also indicates the country / region for which the invoice should be prepared
 

QURT is deployed as a standalone jar running as a windows service. A standalone web-container periodically collects VM data from RecoverPoint for VMs systems and exposes REST services and an AngularJS client.

 * A scheduled task samples all pre-configured systems periodically for their VMs data based on their role (Source/Local Replication/Remote Replication)
 * Using the API one can generate a quarterly usage report or access the entire database
 * The AngularJS client provides a configuration view and a reports view

###INSTALLATION
####Installation prerequisites:
 1. Java ver 1.8 and above
 2. 100 MB of available disk space
 3. Connectivity to all RecoverPoint systems
 4. Open port (default 8080) on machine where QURT is deployed
 
####Installation instructions:
 1. Download the QURT code from GIT
 2. Compile and package it using maven, using command:  `mvn package` Note: Since QURT uses spring-boot and H2 embedded database, it does not have any prerequisite software installed.
 3. Run Application class from the WAR file using the command `java -jar qurt.war`
 4. You can also deploy the WAR on your own web-container (tomcat/etc.)

####INSTALLATION – WINDOWS
It is recommended to configure QURT to run as a Windows Service as follows:
`sc create qurt_service_name binPath="java -jar qurt.war"`

##USER OPERATIONS
To access QURT either AngularJS client or the REST API can be used
##QURT UI – AngularJS Client
The QURT Web client can be accessed in: http://<hostname>/index.html:8080  (8080 is the default port) 
Basic user/password authentication is required. The defaults are:
 - User: user
 - Password:qurt123 

The password can be changed by:
 - Changing WebSecurityConfig class
 - Write the password encoded in base64 in file curtpassword 

 QURT UI provides the following functionality:
 1. QURT Configuration – Define RP systems to collect data from
	 - List RP clusters for system
	 - Assign country per cluster
	 - Update settings (i.e., change RP user/password)
 2. Generate report of #VMs per month per country
     - Select report quarter (past or current)
 
####QURT REST API
The following methods are supported in the QURT RESP API
 - `GET http://{server:port}/report/quarterReport/{year}/{quarter}` – Generates quarterly report for a specific quarter
 - `GET http://{server:port}/report/quarterReportCsv/{year}/{quarter}` – Generates quarterly report for a specific quarter as CSV file
 - `GET http://{server:port}/app/rest/dbdump` – List all listings in the embedded database
  - `GET http://{server:port}/app/rest/dbdumpCsv` – Download all listings in the embedded database in CSV format
 - `GET http://{server:port}/systems` - List all configured RecoverPoint systems in QURT
 - ```GET http://{server:port}/app/rest/testSystem/{id}``` – Test connection to a specific configured RecoverPoint system - Sends HTTP OK (200) if successful or exception if failed
 - `POST http://{server:port}/app/rest/addSystem` – Add new system using the JSON structure for SystemSettings class. Results with HTTP CREATED (201) if successful or exception if failed

###FUTURE
In the future we plan to add a loading wheel while handling HTTP requests in the AngularJS client.

###CONTRIBUTION INSTRUCTIONS
Create a fork of the project into your own repository. Make all your necessary changes and create a pull request with a description on what was added or removed and details explaining the changes in lines of code. If approved, project owners will merge it.

###LICENSING
The MIT License (MIT)
Copyright (c) 2015, Daniel Moran

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

###SUPPORT
Please file bugs and issues at the Github issues page. For more general discussions you can contact the EMC Code team at [Google Groups](https://groups.google.com/forum/#!forum/emccode-users) or tagged with EMC on [Stackoverflow.com](https://stackoverflow.com/). The code and documentation are released with no warranties or SLAs and are intended to be supported through a community driven process.

