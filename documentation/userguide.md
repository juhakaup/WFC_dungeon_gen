# WFC dungeon generator - user manual

## Running the program

### Running precompiled jar package
The precompiled *.jar* file can be run from the command line with the following command:
```
  java -jar WFC_dungeon_gen
```

### Compiling and running the program from source
The program uses gradle for building and package management.

If you have gradle installed on your system, you can use the command *gradle* directly, if you do not have gradle installed, substitute the command *gradle* with *./gradlew* for the following commands.

To run the program, use the following command:
```
  gradle run
```

To building a jar package containing all the dependencies, use the following command:
```
  gradle shadowJar
```

## User interface overview
![User interface](https://github.com/juhakaup/WFC_dungeon_gen/blob/master/documentation/UI.PNG)

The user interface consists of three parts. 

* On top there is a command strip, containing the controls for the program.
  * *Load tileset* -button is used for loadig tilesets, for changing the appearance and settings of the dungeon.
  * *Generate* -button, generates a new dungeon.
  * *Clear* -button, removes all tiles from the map, this is useful when you want to use the step-button.
  * *Step* -button, generates the map, step by step, displaying the result after each click of the button.
  * *Width* and *Depth* -fields are used to determine the size of the generated map.
  * *Font size* controls the font size.
  * *Split tiles* separates the individual tiles.

* In the middle there is a text output window. The generated map is displayed in here. You can edit the output, but this has no affect on the program.

* At the bottom there is a notification area. Here you can see possible errors and some stats about the program.