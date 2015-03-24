QURT
======================
Customers wish to view a quarterly report of replicated Virtual Machines, including values per month for the quarter, based on the (averaged) total number of VMs replicated per country & per type (local, remote) per month. 

 - The report may also include source VMs. 
 - The monthly value is the average of weekly report captured each Friday at noon. 
 - The report should indicate for which country / region the invoice should be prepared. 



## Description
A standalone web-container exposing REST services and AngularJS client and sampling RecoverPoint systems for virtual machines (VM) data.

 - A scheduled task samples all pre-configured RecoverPoint clusters periodically for virtual machines and determines their role (Source/Local Replication/Remote Replication)
 - Using the API one can generate monthly usage report or access the entire database.
 - The AngularJS client suggests a configuration view and a reports view

## Installation
Download the code, compile and package it using maven.
Since it uses spring-boot and H2 embedded database, it does not have any prerequisites.
Run Application class from the WAR file and you can access it using port 8080 by default.
You can also deploy the WAR on your own web-container.

## Usage Instructions
You can either use the AngularJS client or the REST API, using basic authentication user:user and password:qurt123 as default
```GET http://{server:port}/report/quarterReport/{year}/{quarter}```
Generates quarterly report for a specific quarter

```GET http://{server:port}/report/quarterReportCsv/{year}/{quarter}```
Generates quarterly report for a specific quarter as CSV file

```GET http://{server:port}/app/rest/dbdump```
List all listings in the embedded database

```GET http://{server:port}/systems```
List all configured RecoverPoint systems in QURT

```GET http://{server:port}/app/rest/testSystem/{id}```
Test connection to a specific configured RecoverPoint system - Send HTTP OK (200) if successful or exception if failed

```POST http://{server:port}/app/rest/addSystem```
Add new system using the JSON structure for SystemSettings class. Results with HTTP CREATED (201) if successful or exception if failed


#### Future
In the future I plan to add a loading wheel while handling HTTP requests in the AngularJS client.

## Contribution
Create a fork of the project into your own reposity. Make all your necessary changes and create a pull request with a description on what was added or removed and details explaining the changes in lines of code. If approved, project owners will merge it.

Licensing
---------
The MIT License (MIT)

Copyright (c) 2015, Daniel Moran

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

Support
-------
Please file bugs and issues at the Github issues page. For more general discussions you can contact the EMC Code team at <a href="https://groups.google.com/forum/#!forum/emccode-users">Google Groups</a> or tagged with **EMC** on <a href="https://stackoverflow.com">Stackoverflow.com</a>. The code and documentation are released with no warranties or SLAs and are intended to be supported through a community driven process.
