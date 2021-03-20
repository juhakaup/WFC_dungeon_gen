# Project definition

The aim of this project is to create a dungeon generator using wave function collapse algorithm.
 
The wave function collapse -algorithm (WFC) generates tiled patterns from a set of adjacency rules. These rules can be pre-determined (simple tiled model), or generated from a source (overlapping model).
As I'm interested in procedural content generation, I would like to imlement both models. This may turn out be a bit too much work for this course, so I'll start with the simpler model.
The generated map may need some validating and post processing, so at least the simpler model could be extended with a pathfinding algoritm to ensure playable dungeon.

### Algorithms and data-structures
The original implementation of the Wave function collapse algoritm mostly stores data in arrays and array-lists. I think some frequency data is stored in a map, but maybe I can come up with some other implementation. 
The order of iterating the tiles is based on the number of possible outcomes for each tile, so a heap could be used for optimizing this process.

### Time and space complexity
The algoritm iterates through each tile once. Each iteration can affect a number of other tiles reducing the possible outcomes for these tiles. Since the number of possible outcomes is fixed and each tile is iterated through once, the time complexity is linear O(n).
The space complexity for the algoritm is probably still within some multiple of n, so O(n).

### Input/Output
Input for the simpler model is a JSON file containing the adjacency rulse and the tile set in ascii. The output can be printed to the command line or saved as text.
Input for the overlapping model is a text file and the output is the same as for the simpler model.

### References
https://github.com/mxgmn/WaveFunctionCollapse

### Language
The program will be written in Java and documented in english.

### Adminastrive stuff
Opinto-ohjelma: tietojenkäsittelytieteen kandidaatti.
