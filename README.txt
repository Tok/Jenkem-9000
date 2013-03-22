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

Jenkem-9000 is a Web-Application to convert Images into IRC-style HTML&CSS and generate colored output for IRC.

This application is running on OpenShift at: http://jenkem-9000.rhcloud.com
Project page: http://tok.github.com/Jenkem-9000/

Frameworks and Technologies:
-- Scala
-- Vaadin 7 (user interface)
-- PircBot (IRC bot)
-- MongoDB (with JDO 3 and Datanucleus)
-- Maven (build and reporting)

Important Classes:
Jenkem Bot: /src/main/scala/jenkem/bot/JenkemBot.scala
ASCII Engine: /src/main/scala/jenkem/engine/Engine.scala
HTML-Util: /src/main/scala/jenkem/util/HtmlUtil.scala

![Jenkem-Octocat](http://tok.github.com/Jenkem-9000/images/jenkem-octocat.png)