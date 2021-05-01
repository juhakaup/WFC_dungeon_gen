# Implementation

## About the algorithm
This project implements the simple tiled model of the wave funtion collapse -algorithm.
The way the algoritm works is that you have a map consisting of tiles, a set of possible tiles, and a set of adjacency rules of how the tiles can be placed next to each other.
In the beginning, all the tiles are initialized to have the abitily to become any tile of the used tileset. 
Then a random tile is selected and collapsed into one of the tiles in the tileset. 
After this the change in the collapsed tile is propagated into its neighbouring tiles, possibly reducing the set of tiles those tiles could turn into based on the adjacency rules. 
After that, if a neighbouring tile was affected by the propagation, its change is propagated furher into its neighbours and so on until there are no more changes.
When the map has settled, and there are no more changes, another tile is chosen to be collapsed and the propagation cycle begins again.
This is repeated until all the tiles are set to some state, or until a contradiction occurs.
A contradiction means that a tile has no suitable tiles to turn into based on its neighbours. At the moment this is dealt with resetting the whole map and beginning whole cycle again.

## Project structure
![Structure diagram](https://github.com/juhakaup/WFC_dungeon_gen/blob/master/documentation/structure.png)

The actual wave function collapse algorithm is implemented in the Solver class. The solver is created in the ui class with parameters from the ui. The ui also handles loading of the data with through a separate dao-class. 
When an instance of the solver is created, it gets an instance of a TileSet class, this contains the adjacency rules, number of tiles and table of weights for tile distribution. It then creates a 2d array of Tile-objects. The tile object contain information about the tiles they can turn into, if they are collapsed or not and a function to change the possible outcomes of the tile. The tiles are also placed in a priority queue. The queue deques the tiles based on their set of possible outcomes, with the tiles with fewer outcomes being dequed first.

## Time and space coplexity

## Testing
### Performance

### Dungeon validity

### Unit testing
[Documented here](https://github.com/juhakaup/WFC_dungeon_gen/blob/master/documentation/testing.md)

## Improvements

## References
### Wave function collapse algorithm
[github.com/mxgmn/WaveFunctionCollapse](https://github.com/mxgmn/WaveFunctionCollapse)

### Random number generation
[Xorshift](https://en.wikipedia.org/wiki/Xorshift)

### Other
[font by Wouter van Oortmerssen](http://strlen.com/square/)