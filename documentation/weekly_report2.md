# Weekly report 2

Getting started seems to always take a bit of time, but after pondering about how to represent my hard coded data for too long I settled for a simple boolean array. Thought about using BitSet or just bits since most of the operations are just ANDing multiple arrays together.

I got the basic structure set up and some basic functionality made. 
At the moment the program picks the tiles from a heap and collapses them to the final outcome based on weight distribution of the tiles.
There is no propagation to neighbouring tiles yet, so the outcome is not wery useful.

Had a bit of trouble as I wanted to represent my tiles in certain utf-8 characters, but this messes up building with gradle somehow. 
The algorithm does not know about the graphical representation, but I still need to figure out some way to see the result. Atleast I can test it with netbeans, as it can handle the characters.

Next week I'll start to work on the propagation or the tiles, get the test coverage a bit better and maybe think about loading data from JSON.

[Hourly report](https://github.com/juhakaup/WFC_dungeon_gen/blob/master/documentation/hourly_report.md)
