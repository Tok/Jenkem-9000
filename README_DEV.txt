===Local jBoss deployment===
Install the latest jBoss AS (wildfly not tested yet).
enable-welcome-root must be disabled in standalone.xml before deploying.

Depoly war to local jBoss by running the eclipse laucher for maven or do it manually:
http://127.0.0.1:9990/console/App.html#deployments --> manually submit war from /target
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
