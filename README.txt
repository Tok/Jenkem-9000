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

Frameworks and Technologies:
-- Scala
-- Vaadin 7 (user interface)
-- PircBot (for IRC connection)
-- MongoDB (with JDO 3 and Datanucleus)
-- jCrop (with jQuery)
-- Maven (for build and reporting)

Important Classes:
[ASCII Engine](https://github.com/Tok/Jenkem-9000/blob/master/src/main/scala/jenkem/engine/Engine.scala)
[Jenkem Bot](https://github.com/Tok/Jenkem-9000/blob/master/src/main/scala/jenkem/bot/JenkemBot.scala)
[HTML-Util](https://github.com/Tok/Jenkem-9000/blob/master/src/main/scala/jenkem/util/HtmlUtil.scala)

![Jenkem-Octocat](https://raw.github.com/Tok/Jenkem-9000/master/jenkem-octocat.png "Jenkem Octocat")

Project page (typically outdated): http://tok.github.com/Jenkem-9000/
