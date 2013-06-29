===Run in embedded Tomcat 7===
Run the runjenkem.sh or runjemkem.bat in deployment/standalone/jenkem

===Local jBoss deployment (optional)===
Install the latest jBoss AS (wildfly not tested yet).
enable-welcome-root must be disabled in standalone.xml before deploying.

Depoly war to local jBoss by running the eclipse laucher for maven or do it manually:
http://127.0.0.1:9990/console/App.html#deployments --> manually submit the war from /target or ROOT.war
http://127.0.0.1:8080/

If you want to store conversions make sure mongodb is running locally with default settings.

===Setup new application on openshift===
Login to the openshift website and create new app. (suggested mode "non-scaling")
Select latest jboss and add the mongodb cartridge.

There is no need to set the mongodb credentials in the code, since they are looked up (in PMS.scala)
Consider to add the rockmongo cartridge to manage mongodb.

===Deploy to openshift===
https://github.com/openshift/origin-server/blob/master/cartridges/openshift-origin-cartridge-jbossas/README.md

checkout the initial app to a different folder:
git clone ssh://111111111111111111111111@jenkem-9000.rhcloud.com/~/git/jenkem.git/

copy the ROOT war into the new "deployment folder".

remove pom.xml and source folder:
git rm pom.xml
git rm -r src

commit and push (should trigger a restart of jboss):
git commit -m "deploy"
git push

delete logs and temp files by:
rhc app tidy -a [appname]

===Use Project in Eclipse===
- Use Eclispe Juno (or latest version supported by Scala IDE)
- Install Plugins (see below)
- Import Project with *minimal* settings
- Activated Project Facets: Dynamic Web Module, Java, Javascript
- Add Maven Nature (Adds deps to classpath that are needed for compilation, adds ability to run launchers)
- Add Scalastyle Nature
- Add src/main/scala, src/main/resources, src/main/webapp and src/test/scala as sourcepath.

==Recommended Plugins===
Scala IDE (The provided version of Scala should match the one in the POM):
http://download.scala-ide.org/sdk/e38/scala210/stable/site (or equivalent)

Scalastyle Plugin (point it to scalastyle_config.xml):
http://www.scalastyle.org/downloads/

==Optional Plugins==
Scalatest for Scala IDE (allows to run tests separated from maven):
http://www.scalatest.org/user_guide/using_scalatest_with_eclipse

ANSI Escape in Console (shows colors in scalatest output):
http://www.mihai-nita.net/eclipse

Egit (if project contains a git repo):
http://www.eclipse.org/egit/
