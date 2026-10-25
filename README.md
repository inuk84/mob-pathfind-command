# Mob Pathfind Command

Minecraft mod to add `/mobpathfind` command. Useful for datapacks and maps.

## Features

- Make mobs pathfind to some coordinates.
- Make mobs follow other entities.
- Set custom movement speeds.
- Set max pathfinding length.
- Stop mobs pathfinding.
- Uses Minecraft's normal pathfinding and mob AI.

## Commands

### Move to coordinates
```
/mobpathfind <mob> <x> <y> <z> [speed] [maxPathLength]
```
Supports relative (`~`) and local (`^`) coordinates.

### Follow an entity
```
/mobpathfind <mob> <target> [speed] [maxPathLength]
```

### Stop moving
```
/mobpathfind <mob> stop
```
<br/>
[speed] is optional and multiplies the mob's walk speed, so 2 makes it twice as fast as it normally walks. Default is 1.<br/>
[maxPathLength] sets the maximum distance the mob's pathfinder can search when finding a route. Low values make it stand against a wall instead of searching for a way to get around it. Defaults to 64.

## Examples
```
Move the nearest zombie to coordinates
/mobpathfind @e[type=zombie,limit=1] 100 64 100

Move all villagers at 1.2x their walk speed
/mobpathfind @e[type=villager] ~ ~ ~ 1.2

Make a wandering trader follow the nearest player
/mobpathfind @e[type=wandering_trader] entity @p

Move a zombie with a larger pathfinding length
/mobpathfind @e[type=zombie,limit=1] 100 64 100 1 256

Stop all zombies
/mobpathfind @e[type=zombie] stop
```

## Compatibility
- Minecraft 26.1-26.3
- Fabric only currently

## Installation
1. Install [Fabric Loader](https://fabricmc.net/use/).
2. Install [Fabric API](https://modrinth.com/mod/fabric-api).
3. Put the mod in your mods folder.

## License
See [LICENSE](LICENSE).
