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
I tested map initialization and solving separately. 
As defined in the definition, the time complexity is O(n). The initilization stays very liner throughout the test range. 
The map generation times start to increase after 40x40 tile map. In the end, the processing time has more than doubled. This is with a map consisting of 25 000 objects, so this could be just memory issue as the tiles are selected in somewhat random order.
```
Testing map initialization
10000 10 x 10 maps initialized in   3721.450 ms. That is 37.215 ms per 10000 tiles.
10000 14 x 14 maps initialized in   6243.960 ms. That is 31.857 ms per 10000 tiles.
10000 20 x 20 maps initialized in  12364.161 ms. That is 30.910 ms per 10000 tiles.
10000 28 x 28 maps initialized in  24683.180 ms. That is 31.484 ms per 10000 tiles.
10000 40 x 40 maps initialized in  50473.834 ms. That is 31.546 ms per 10000 tiles.
10000 56 x 56 maps initialized in  98112.444 ms. That is 31.286 ms per 10000 tiles.
10000 80 x 80 maps initialized in 200098.538 ms. That is 31.265 ms per 10000 tiles.
10000 113x113 maps initialized in 408395.023 ms. That is 31.983 ms per 10000 tiles.
10000 160x160 maps initialized in 832023.981 ms. That is 32.501 ms per 10000 tiles.
```

```
Testing map solving after initialization
10000 10 x 10 maps generated in   10871.143 ms. That is 108.711 ms per 10000 tiles.
10000 14 x 14 maps generated in   20285.014 ms. That is 103.495 ms per 10000 tiles.
10000 20 x 20 maps generated in   41326.250 ms. That is 103.316 ms per 10000 tiles.
10000 28 x 28 maps generated in   82711.568 ms. That is 105.499 ms per 10000 tiles.
10000 40 x 40 maps generated in  173547.537 ms. That is 108.467 ms per 10000 tiles.
10000 56 x 56 maps generated in  361229.495 ms. That is 115.188 ms per 10000 tiles.
10000 80 x 80 maps generated in  839961.269 ms. That is 131.244 ms per 10000 tiles.
10000 113x113 maps generated in 2158729.754 ms. That is 169.060 ms per 10000 tiles.
10000 160x160 maps generated in 6252852.711 ms. That is 244.252 ms per 10000 tiles.

```
## Testing

### Dungeon validity

### Unit testing
[Documented here](https://github.com/juhakaup/WFC_dungeon_gen/blob/master/documentation/testing.md)

## Improvements
There is probably a lot of optimization that could be done. I think that having the tile as a separate class is a bit of an overkill, the same amount of information could be stored in a few arrays. It would be interesting to convert the boolean arrays used to store the available tiles to bitSets, the memory savings these days is neglegtable, but maybe the set operations would be faster.

## References
### Wave function collapse algorithm
[github.com/mxgmn/WaveFunctionCollapse](https://github.com/mxgmn/WaveFunctionCollapse)

### Random number generation
[Xorshift](https://en.wikipedia.org/wiki/Xorshift)

### Other
[font by Wouter van Oortmerssen](http://strlen.com/square/)