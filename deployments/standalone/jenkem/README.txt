This is a Tomcat bundled standalone version of Jenkem-9000 for local use.

CURRENT DOWNLOAD LINKS:
http://bitshare.com/files/8yw19ysl/Jenkem-1.0-SNAPSHOT.zip.html
http://netload.in/dateiCxcy8axqfe/Jenkem-1.0-SNAPSHOT.zip.htm
https://mega.co.nz/#!N1I0RZJY!ZAUixWuzgursL6WdiXYiPnt2U74b1UDjxlCMYfcPMwQ

PREREQUISITES:
Jenkem requires Java: http://java.com/download

OPTIONAL:
If you want to use the database feature, a local instance of mongodb is needed:
http://www.mongodb.org

HOW TO USE:
Run runjenkem.sh or runjenkem.bat (Windows).
After a few seconds the embedded Tomcat server should start and open up the web-interface in
your default browser. Otherwise manually go to: http://localhost:8080/

CONNECTING TO IRC:
Change the default IRC information in the web-interface 
according to your preferences and hit the "Connect" Button.
After a few seconds the bot should connect and join the specified channel.
Otherwise a reason for the error should be seen in the status label.
Click "Refresh Bot Status" and/or change your settings and try again until it works.

BOT COMMANDS:
Show help-text:
JENKEM HELP

Play image from url:
JENKEM [url]

Let jenkem search for an image to play (powered by Google):
JENKEM [search term]

Let jenkem search for a 40x40 icon to play:
JENKEM [search term] imagesize:40x40

Show configuration:
JENKEM CONFIG

Change configuration:
JENKEM [ConfigItem] [Value]
ConfigItems are: DELAY, WIDTH, SCHEME, CHARSET, CHARS, POWER and FULLBLOCK

Reset configuration:
JENKEM RESET

Stop jenkem from playing a conversion:
JENKEM STOP

The official instance of Jenkem is running at: https://jenkem-9000.rhcloud.com
The source-code can be found at: https://github.com/Tok/Jenkem-9000
