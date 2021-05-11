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

## Performance tests

Ideas on what to test.
* Dungeon generator itself with various tilesets and output sizes.
* Generator with post-processing.
* Just post-processing
* Performance with some standard library implementations.

## Dungeon validity
There is no actual integrity testing for the generated dungeon. The nature of the algoritm is to generate random dungeons that should be valid, if the code is working properly. The program does place the player and exit on the map, and tries to place them as far apart as possible. So there should always be a valid path from the player to the exit.