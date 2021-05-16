# Testing

Automated testing is implemented through GitHub Actions, this covers checkstyle, cove coverage and unit-tests, code coverage report is generated with Covecov.io.

## Unit testing
Unit tests are implemented using JUnit. Tests are separated to individual files by class, except for classes used for data access, that are tested in a single file. The user-interface is left out of the unit tests.

## Checkstyle
Checkstyle is used for static code analaysis of the project. The style settings are derived from previous university courses. Line length is set to 100 to aid the use of more descriptive naming for methods and variables.

## Code coverage
Test coverage is measured using JaCoCo. The user-interface is left out of the coverage report.
Testing coverage raport on codecov:
[![codecov](https://codecov.io/gh/juhakaup/WFC_dungeon_gen/branch/master/graph/badge.svg)](https://codecov.io/gh/juhakaup/WFC_dungeon_gen)

## Dungeon validity
The validity of the dungeon is tested by the Validator class. It places a starting point in a walkable tile near the center of the map and calculates the distances of each reachable tile from there. If it cannot place the staring point, or if the start or endpoints land on empty tile, the map is invalid. In a valid map, there should always be a path from the start to the endpoint. 
There is no test that the given data is able to produce a valid map, and for example validating the *dungeon_basic.JSON* is not possible with the the tile data only, it could be validated by checking the text output, but at the moment there is no such feature.
The map generation is unit tested with data that should be able to generate a valid map.