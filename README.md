# TimeLimit
TimeLimit is a Minecraft PVP combat minigame plugin with the main goal of being the last player standing. At the start of the game, each player has 20 minutes left to live and they must prolong their elimination by defeating other players. Results of the game are saved in a database and can be viewed on a website.

## Installation
Check that the project is using the correct Spigot API version as the Minecraft server. For example, if the project uses Spigot-API-1.20.1-R0.1, then the Minecraft server needs to be on version 1.20.1 running on Spigot.

1. On IntelliJ, go to **Build > Build Artifacts.. > TimeLimit.jar > Build** to create a jar of the TimeLimit plugin.
2. Place the TimeLimit.jar file inside the plugins folder.
3. Run the server.

## How to Play
As explained in the countdown period before the game starts:
- Kill your opponents to take minutes off their time (-5 mins) and gain time (+2 mins).
- Break gold blocks to gain time (+1 min).
- The last player standing wins the game.

Gold blocks regenerate every five minutes.

## Command
| Command | Description |  
| ----------- | ----------- |  
| `/tl start` | Starts a new game of TimeLimit. |  
| `/tl stop` | Stops the game. |
| `/tl pause` | Toggles pause state of the game. |
| `/tl set lobby` | Sets the game lobby spawn location. |
| `/tl set spawn [num]` | Sets game spawn locations for different players. |
| `/tl set goldspawnarea [num]` | Sets the areas for generating gold blocks. |

## Permissions
`timelimit.admin` - Administrator permissions to all TimeLimit commands.