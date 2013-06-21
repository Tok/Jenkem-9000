http://jenkem-9000.rhcloud.com/

Setup new app:
Login to openshift website and create new app.
Select latest jboss and add the mongodb cartridge.

There is no need to set the mongodb credentials in the code, since they are looked up (in PMS.scala)
Consider to add the rockmongo cartridge to manage mongodb.

Don't clone the openshift repo (because the gwt compilation didn't work with openshift when Jenkem had a GWT UI)
Instead build the war locally from the pom.xml in this repo and submit it to openshift by sftp.
(there are launchers for eclipse)

Depoly war to local jBoss by running the eclipse laucher or do it manually:
http://127.0.0.1:9990/console/App.html#deployments --> manually submit war from /target
(make sure mongodb is running locally with default settings)
enable-welcome-root must be disabled in standalone.xml before deploying.
http://127.0.0.1:8080/

Submit to openshift by sftp:
The first time do a 'git rm -r src/ pom.xml'.
connect to 
sftp://[hexUsername]@[appname]-[namespace].rhcloud.com/app-root/repo/deployments
and submit the ROOT.war from /deployments
Then restart the app using "rhc app restart -a [appname]" or ssh into the shell and do it there.
(in eclipse this can be done by selecting the "export" feature after installing and configuring a plugin for sftp.)

delete logs and temp files by:
rhc app tidy -a [appname]
