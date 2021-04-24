# Weekly report 5

This week I tried to implement somekind of proofing system for the maze. I started by looking in to creating an implementation of an A* pathfinding algorithm, this may be a bit of an overkill, but it lead me to thinking about structuring the maze into rooms and corridors. These could then be arranged into a network, and that network then could be used to determine if the maze is valid. The network could also be used to find isolated parts of the maze and in choosing the start and end -points. 
I managed to write an algorithm that clusters adjacent room-tiles together and another one that connects room clusters with corridors. I did not have the time to implement the actual network yet.

Next week I'll finish the network building code. Look into connecting isolated parts of the maze together and selectign the start- and endpoints. I'll also continue the performance testing side and polish the ui code a bit.

[Hourly report](https://github.com/juhakaup/WFC_dungeon_gen/blob/master/documentation/hourly_report.md)