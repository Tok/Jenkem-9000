Jenkem-9000 is a Web-Application to convert Images into IRC-style HTML&CSS and generate colored output for IRC.

<pre>
  _---_
 (  J  )
  \   /
   | |
  =|_|=        _           _                        ______  _____  _____  _____
  /   \       | |         | |                      /  __  \/  _  \/  _  \/  _  \
 /_____\      | |___ _ ___| | _ ___ _ __ __       (  (__|  | | |  | | |  | | |  |
 |     |   _  | | _ \ '_  \ |/ / _ \ '  '  \  ____ \_____  | | |  | | |  | | |  |
 |_____|  / \_| | __/ | | |   (  __/ || || | (____) ____/  | |_|  | |_|  | |_|  |
 | | | |  \____/\___)_| |_|_|\_\___)_||_||_|       (______/\_____/\_____/\_____/
 (_|_|_)
</pre>

This application is running on OpenShift at: http://jenkem-9000.rhcloud.com

Frameworks and Technologies:
* Scala
* Vaadin 7 (user interface)
* PircBot (for IRC connection)
* MongoDB (with JDO 3 and Datanucleus)
* Jcrop (with jQuery)
* Maven (for build and reporting)
* Tomcat7 (for [standalone version](/deployments/standalone/jenkem) only)

Important Classes:

[ASCII Engine](/src/main/scala/jenkem/engine/Engine.scala)

[Jenkem Bot](/src/main/scala/jenkem/bot/JenkemBot.scala)

[HTML-Util](/src/main/scala/jenkem/util/HtmlUtil.scala)

![Jenkem-Octocat](jenkem-octocat.png)

Project page: http://tok.github.com/Jenkem-9000/

![WTFPL](http://www.wtfpl.net/wp-content/uploads/2012/12/wtfpl-badge-1.png)
