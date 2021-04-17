# Weekly report 4

This week I implemented a version of the priority queue. Integrating it to the existing code went without a hassle, allthought it could have been simpler if I would have wrapped the default implementation in a custom class to begin with.
I re-structured the code a bit to get it to work with the ui better. I added a few controls to the ui, now you can generate a new dungeon with a click of a button, there is also a button for generating one tile at a time.

I have not done any work on implementing the validation of the dungeon, but I'll probably start just by setting entry and exit points and use a pathfinding algoritm to determine whether the dungeon needs post processing or not.

Next week I'll start the implementation of the pathfinding algoritm, generate some test data and set up performance tests.

[Hourly report](https://github.com/juhakaup/WFC_dungeon_gen/blob/master/documentation/hourly_report.md)