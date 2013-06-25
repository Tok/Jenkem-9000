This is a standalone version of Jenkem 9000 that is bundled with a tomcat server for local use.

CURRENT DOWNLOAD LINK:
http://bitshare.com/files/j95eu82j/Jenkem-1.0.zip.html
http://netload.in/dateiyjOk3uQrRe/Jenkem-1.0.zip.htm
https://mega.co.nz/#!m8YF3Jyb!Mi5bFH2WoI74NVzzaMsESlm0TnMbW4Nur3LBlDJtaPA

PREREQUITITES:
Jenkem needs Java: http://java.com/download

OPTIONAL:
If you want to use the database feature, a local instance of mongodb is needed:
http://www.mongodb.org

HOW TO USE:
Run runjenkem.sh or runjenkem.bat (Windows).
After a few seconds the embedded Tomcat server should start.
Go To: http://localhost:8080/

IRC CONNECTION:
Change the default IRC information according to your preferences and hit the "Connect" Button.
After a few seconds the bot should connect and join the specified channel.
Otherwise a reason for the error should be seen in the status label.
Click "Refresh Bot Status" and/or change your settings and try again until it works.

BOT COMMANDS:
Show this help-text:
JENKEM HELP

Play image from url:
JENKEM [url]

Let jenkem search for an image to play:
JENKEM [search term]

Show configuration:
JENKEM CONFIG

Change configuration:
JENKEM [ConfigItem] [Value]
ConfigItems are: DELAY, WIDTH, SCHEME, CHARSET, CHARS, POWER")

Reset configuration:
JENKEM RESET

The official instance of Jenkem is running at: https://jenkem-9000.rhcloud.com
The source-code can be found at: https://github.com/Tok/Jenkem-9000
