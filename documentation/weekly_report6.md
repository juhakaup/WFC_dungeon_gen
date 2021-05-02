# Weekly report 6
Miscellaneous tasks this week. Replaced the random number generator and ArrayList with custom implementations. Continued to work on the ui. Setting the text area to correct size is a bit difficult, as the actual size of the font is not easily accessable. Re-structured the ui code as well, although it is still a bit messy.
I created yet another tileset, just to see if it would work. The new tileset turned out to work so well, that I decided to replace the old one with it. The old tileset would have required some post processing, that I can do away with the new one. 
I have also been working on the validation of the maze, re-wrote some parts of the code to simplify things a bit. There is still no actual validation, but if I'll have time, I'll implement floyd warshall algorithm and use it to pick start and endpoints. 
I have been thinking about having some way to reset a single tile or region of tiles, this could be used instead of backtracking when encountering a contradiction, but I don't know if I have time for that.

Next, I'll go through the code, see if I need to write some more tests and if the current ones are actually still valid. I'll try and to have the validator working correctly and see if I'll have time to take a look at the contradiction problem.


[Hourly report](https://github.com/juhakaup/WFC_dungeon_gen/blob/master/documentation/hourly_report.md)