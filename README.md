# Anti-MOTD-Scanner (AMS)
## Please view the FAQ!
This plugin is meant for smaller PRIVATE servers that want to stay hidden (really helps cracked servers). It prevents ServerListPing events from people who have never joined the server. It prevents less specialized/advanced automatic 
scanners from finding your server. It can't prevent TCP scans (change your defaut port from 25565) and it can't prevent more advanced scanners from joining with an account as this plugin does not interfere with the join process. This is a newer plugin, all suggestions are welcome!

Download at:
https://modrinth.com/project/anti-motd-scanner

**BUG REPORTS IN ISSUES PLEASE!**

## What this plugin CAN do:
It can prevent automated scanners that dont join the server with a minecraft account. This plugin interrupts the ListServerPing event from clients that are not in ipcache.txt 
(ServerListPing What sends clients the servers MOTD, Status, Player count etc). Players who join the server are added to the ipcache.txt. While bots that use actual minecraft accounts
can bypass this (as this plugin only deals with ping requets) it blocks most of the automated scanners (such as pythons mcscan pared with masscan).Most scanners that use MC accounts
typically ping the IP first to see if theirs a running server, so this may even prevent some advanced scanners.

This plugin has been privately tested on a small server i host, before this plugin there were many many scanners who constantly pinged the server, even tried to join. After I 
changed the servers IP and added this plugin the rate is so much lower! This is because once automated scanners find a server they keep the IP in there database and continue to check it.

## What this plugin CAN'T DO:
It CAN'T prevent scanners that use a MC account from joining as theirs no real way to tell whos a scanner and a real player, if there is this plugin does not do this.
This plugin does not check if the IP is a VPN/PROX, theres plenty of other plugins that do that.

## Why scanners are bad/why this matters:
It may not matter for you, depends if you care.
Many scanners are used by malicious actors or just curious players. Ive used massscan & mcstatus to scan servers before (thankfully not for malicious reasons) and ive got 1000s of servers, its really easy. This plugin is meant to take out the lower level script kiddie type scanners and even automated campaigns. If your server can't use a whitelist for whatever reason or is cracked with no login plugin (for whatever reason) this may help.


Many scanners will try and join your server, maybe to greif? explore? be nice? There is no way to know and it only takes 1 to ruin the day.

## Commands
`/ams` - Info command

`/ams-reload` - Reloads the config, requires OP

`/ams-purge-cache` - Deletes content of ipcache.txt (you can also just delete the files data manually)


# FAQ
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

**This is a dumb idea/i dont like how you did this**
> Im new here, cut me some slack. Bug reports/suggestions are welcomed in issues.

## Testing
Ive tested this using the methods real scanners use (mcstatus) and simple minecraft clients.

# Config File.
Explanation on how to use the config is inside `config.yml` although heres a more in depth explanation;
```yml
# This blocks all pings that don't come from IP's inside ipcache.txt, for an IP to be inside
# this file the player must join or be manually added.
`block-unknown-pings: true`

# When a player joins the server there IP is stored into this file
# "block-unknown-pings" will block all IP's that are NOt inside this file.
# if disabled it will no longer add newer IP's, you can also manually add IP's
`enable-ipcache: true`

# Logs all instances of MOTD pings into motd-ping-logs.txt
`log-all-motd-pings: true`

# Logs all blocked MOTD pings into blocked-motd-logs.txt
`log-blocked-motd-pings: true`

# If the same IP gets logged into the file more than once in order it will stack it with (x2 or x5)
# This prevents the same message from being "spammed" many times. If you don't understand this set it to false.
stack-same-ips: true
```

Example with this setting off:<br>
Blocked StatusRequest from 127.0.0.1<br>
Blocked StatusRequest from 127.0.0.1<br>
Blocked StatusRequest from 127.0.0.1<br>
Blocked StatusRequest from 127.0.0.1<br>
Blocked StatusRequest from 127.0.0.3<br>
Blocked StatusRequest from 127.0.0.1<br>
Blocked StatusRequest from 127.0.0.1<br>
Blocked StatusRequest from 127.0.0.2<br>

Example with this setting on:<br>
Blocked StatusRequest from 127.0.0.1 (x3)<br>
Blocked StatusRequest from 127.0.0.3<br>
Blocked StatusRequest from 127.0.0.1 (x2)<br>
Blocked StatusRequest from 127.0.0.2<br>
