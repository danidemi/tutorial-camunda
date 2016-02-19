First you need to set up your development environment and install the Camunda BPM Platform and Camunda Modeler.

Camunda is available as Docker container.

	<https://hub.docker.com/r/camunda/camunda-bpm-platform/>
	<https://github.com/camunda/docker-camunda-bpm-platform>
	
Tutorial available on...

	<https://docs.camunda.org/get-started/bpmn20/install/>

It is possible to run an instance of Camunda...

	docker run -d --name camunda -p 8080:8080 camunda/camunda-bpm-platform:tomcat-7.4.0
	
	docker run -d --name camunda -p 8080:8080 \
           -v /workspace/repos/nht/tutorial-camunda/tutorial-camunda-first-steps/target/tutorial-camunda-first-steps-0.0.1-SNAPSHOT.war:/camunda/webapps/tutorial-camunda-first-steps.war \
           camunda/camunda-bpm-platform:tomcat-7.4.0

           
	mvn clean package war:war
	
	docker run --name camunda -d -p 8080:8080 -v /workspace/repos/nht/tutorial-camunda-first-steps/target/tutorial-camunda-first-steps-0.0.1-SNAPSHOT.war:/camunda/webapps/tutorial-camunda-first-steps.war camunda/camunda-bpm-platform:tomcat-7.4.0
	
	http://localhost:8080/camunda/app	           
	
...this should give something like...

	docker run -d --name camunda -p 8080:8080 camunda/camunda-bpm-platform:tomcat-latest
	Unable to find image 'camunda/camunda-bpm-platform:tomcat-latest' locally
	tomcat-latest: Pulling from camunda/camunda-bpm-platform
	d634beec75db: Pull complete
	ec60dd9d03d7: Download complete
	913265148a13: Download complete
	00933036ba8e: Download complete
	9bbcf44534fc: Download complete
	2e0f7640971c: Download complete
	d38aba5e823b: Download complete
	f3a7673fae85: Download complete
	a4e4d2188cf4: Download complete
	51785a45224e: Download complete
	f6c9c592a0a4: Downloading [>                                                  ] 3.238 MB/187.5 MB
	baeb89773258: Downloading [==>                                                ] 3.241 MB/55.88 MB
	bead45348435: Download complete
	03e877a61421: Downloading [===>                                               ] 2.453 MB/39.45 MB
	5bd2140e20ae: Download complete
	fecedb7b8eb5: Download complete
	cb59a8b8461f: Download complete
	1374d5789086: Download complete
	
Camunda should be up and running on...

	<http://192.168.99.100:8080/camunda-welcome/index.html>
	
...or whatever IP Docker is running on.

Then, Camunda Modeler is needed. Can be downloaded here...

	<https://camunda.org/download/modeler/>
	
Draw a BPMN process.

It should be marked as "EXECUTABLE" !!!

In conditions you should execute JUEL - Java Unified Expression Language - Unified Expression Language

		