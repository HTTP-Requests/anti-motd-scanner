# Anti MOTD Scanner (AMS)

**Please view the FAQ!**

This plugin is meant for smaller private servers that want to stay hidden. It prevents Server Ping events from IP's who have never joined the server before. It prevents scanners from finding your server. It can't prevent TCP scans (change your defaut port from 25565) and it can't prevent more advanced scanners from joining with an account as this plugin does not interfere with the join process. This is a newer plugin, all suggestions are welcome!

<hr>

Requires:
https://www.spigotmc.org/resources/protocollib.1997/

**BUG REPORTS IN ISSUES PLEASE!**


## What this plugin CAN do:
It can prevent automated scanners that don't join the server with a minecraft account. This plugin interrupts the ListServerPing and StatusRequest events from clients that are not in `/ip-data/ipcache.txt` 
(ServerListPing/StatusRequest What sends clients the servers MOTD, Status, Player count etc). Players who join the server are added to the ipcache.txt. While bots that use actual minecraft accounts
can bypass this (as this plugin only deals with ping requets) it blocks most of the automated scanners (such as pythons mcscan pared with massscan). 

Most scanners that use MC accounts typically ping the IP first to see if theirs a running server, so this may even prevent some advanced scanners. Instead of using of a blacklist approach (like [Anti Scan](https://modrinth.com/mod/antiscan), great plugin btw!) it uses a whitelist and blocks everything else as scanners can use new IP's or may not be in PebbleHosts system/other databases. Although a blacklist may not be whats right for you. If not try [Anti Scan](https://modrinth.com/mod/antiscan).

This plugin has been tested on a small server I host, before this plugin there were many many scanners who constantly pinged the server, even tried to join. After I 
changed the servers IP and added this plugin the rate is so much lower! This is because once automated scanners find a server they keep the IP in there database and continue to check it.


## What this plugin CAN'T DO:
It CAN'T prevent scanners that use a MC account from joining as theirs no real way to tell whos a scanner and a real player, if there is this plugin does not do this.
This plugin does not check if the IP is a VPN/PROX, theres plenty of other plugins that do that.

**Why scanners are bad/why this matters:**
It may not matter for you, depends if you care.
Many scanners are used by malicious actors or just curious players. Ive used massscan & mcstatus to scan servers before (thankfully not for malicious reasons) and ive got 1000s of servers, its really easy. This plugin is meant to take out the lower level script kiddie type scanners and even automated campaigns. If your server can't use a whitelist for whatever reason or is cracked with no login plugin (for whatever reason) this may help.


Many scanners will try and join your server, maybe to greif? explore? be nice? There is no way to know and it only takes 1 to ruin the day.

<hr>

**Commands**

`/ams` - Info command

`/ams-reload` - Reloads the config, requires OP

`/ams-purge-cache` - Deletes content of ipcache.txt (you can also just delete the files data manually)

**Testing**
Ive tested this using the methods real scanners use (mcstatus) and simple minecraft clients.

**Server scanner when AMS is enabled VS disabled*:**<br>
![Server scanner when AMS is enabled VS disabled.](https://cdn.modrinth.com/data/cached_images/238924445f31224b054063bd51ef4bda5b9f56a0_0.webp)

**What you see if your not in ipcache:**<br>
![What you see if your not in ipcache](https://cdn.modrinth.com/data/cached_images/c6574fd9e94e6f3905a10803ffe166b3a98bb7d0.png)<br>


**FAQ**
**Why does this matter? So what if scanners have my servers IP i have a whitelist and it isnt cracked!**
> Don't want it? Then dont install it. This is helpful for;
> - Servers that don't have a whitelist
> - Cracked servers with no long-in plugin
> - Servers you host on your home IP
> - When you dont want your servers IP in a active mc server database.

**Can simple TCP/IP Scanners still ping the IP/PORT**
> Yes, the port isn't closed as this works at the plugin level not the network layer. Its suggested to change your default port (25565) to something random. Many server hosts already do this. 

**If joining the server adds there IP to cache (whitelist)... what about scanners that use MC accounts?**
> As said before, this plugin doesn't deal with the client join process. If your server isn't cracked turn on the whitelist if you can.

**Is this meant for big servers with 100+ players or public servers?**
> No, if your server is public and you block ServerListPings players who have never joined/has a new IP will not see your motd and you may want this. It is meant for private servers that wish to stay hidden.

**Can I use ipcache as a whitelist and turn updating off when players join?**
> Yes, set enable-ipcache to false

**What about players who have Dynamic IP's or join from VPNS?**
> They will not be able to see the server's status before joining.
> **NOTE**: Many scanners use VPN's to hide there IPs and the more VPN IP's that get added may slightly weaken this system, although there are many VPN IPs, Concider adding an Anti VPN/PROXY plugin.

**Should i occasionally clear ipcache.txt if many players use VPNS/Dynamic IP's?**
> There are 4 billion IP's chances are none of them will be eventually used by scanners. Although it cant hurt

**Will there be a paper version? My server runs PaperMC/Purpur!**
> This plugin is compatible with Paper/Purpur. If it ever needs 2 versions then i will make it.

**What about versions below 1.21.8?**
> If it is requested enough i may support 1.20.x, thats as low as I will go. Newer versions will be updated fast.

**This is a dumb idea/I dont like how you did this**
> Im new here, cut me some slack. Bug reports/suggestions are welcomed in issues.

**Why did you make this?**
> I operate a smaller private server and scanners kept pinging and trying to join the server, it is cracked with no longin plugins (per requested by owners) so i did something about it and decided to make it public.


## Known Issues:
This plugin will not allow players to use ViaFabricPlus's "Detect Version" and will have to automaticlly select a version.


## Config File.
Explanation on how to use the config is inside `config.yml` although heres a more in depth explanation;
```yml
# This blocks all pings that don't come from IP's inside ipcache.txt, for an IP to be inside
# this file the player must join or be manually added.
block-unknown-pings: true

# When a player joins the server there IP is stored into this file
# "block-unknown-pings" will block all IP's that are NOt inside this file.
# if disabled it will no longer add newer IP's, you can also manually add IP's
enable-ipcache: true

# Logs all instances of MOTD pings into motd-ping-logs.txt
log-all-motd-pings: true

# Logs all blocked MOTD pings into blocked-motd-logs.txt
log-blocked-motd-pings: true
```
