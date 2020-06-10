# PhoenixLib
<p align="center">
  <img width="200" height="200" src="https://cdn.discordapp.com/attachments/299945099301748737/600498370007662605/Ikke_navngitt.png">
</p>

## What is PhoenixLib
PhoenixLib is an easy to use API for Bukkit plugins. It was designed to work with legacy versions like 1.4.7.
Even though it was designed for older version it should run on newer versions aswell.

### Features:
*Currently unstable* Easy to use MySQL-API so even devs with little to no experience should be able to do simple SQL.<br>
Easy to use Cooldown-API, this only requires 3 lines to be functional on your commands or actions.<br>
Easy to use Config-API, literally a Java noob can do configs now.
### How to implement PhoenixLib:
REPO is currently down, use TinyPhoenix
* gradle
    * simply add these lines to your gradle.build<br>
	in repositories: `maven { url "http://tekxit.kingsoftekkit.com:8624/maven2/Default-Maven/" }`<br>
	in dependencies: `compile "com.erksterk:PhoenixLib:0.5.1"`

You can also use the TinyPhoenix and include this inside your plugin, all you need to do is TinyPhoenix.kill() in onDisable!
## Build Instructions
* Clone Project
    * You can use an IDE or clone from a terminal
    `git clone https://github.com/ErkSterk/PhoenixLib`
* Build
    * Linux / Git Bash / MacOS
    `./gradlew build`
    * Windows
    `.\gradlew.bat build`



PhoenixLib is licensed under the GNU General Public License v3.0
