  _---_
 (  J  )
  \   /
   | |
  =|_|=
  /   \        _           _                         _____  _____  _____  _____
 /_____\      | |___ _ ___| | _ ___ _ __ __         / ___ \/  _  \/  _  \/  _  \
 |     |   _  | | _ \ '_  \ |/ / _ \ '  '  \  ____  \____  | | |  | | |  | | |  |
 |_____|  / \_| | __/ | | |   (  __/ || || | (____)  ___/  | |_|  | |_|  | |_|  |
 | | | |  \____/\___)_| |_|_|\_\___)_||_||_|        (_____/\_____/\_____/\_____/
 (_|_|_)

Jenkem-9000 is a Web-Application to convert Images into IRC-style HTML&CSS and generate colored output for IRC.

This application is running on OpenShift at: http://jenkem-9000.rhcloud.com
Project page: http://tok.github.com/Jenkem-9000/

Frameworks and Technologies:
-- Scala (backend)
-- Java (engine)
-- Vaadin 7 (user interface)
-- PircBot (IRC bot)
-- MongoDB (with JDO 3 and Datanucleus)
-- Maven (build and reporting)

Important Classes:
Jenkem Bot: /src/main/scala/jenkem/server/JenkemBot.scala
ASCII Engine: /src/main/java/jenkem/shared/Engine.java
HTML-Util: /src/main/java/jenkem/shared/HtmlUtil.java

![Jenkem-Octocat](http://tok.github.com/Jenkem-9000/images/jenkem-octocat.png)