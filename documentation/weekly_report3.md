# Weekly report 3

I got the basic propagation algorithm to work quite quickly. Then spend quite alot of time debugging, as it seemed to behave a bit strangely. This was propbably due to bad data. After re-writing the algorithm, fixing my data and going back to my original implementation, I'm pretty sure it works. Some datasets can cause the algorithm to reach an unresolvable state. At the moment this is not handled in any way, implementing some sort of reset should be fairly straightforwad. Some sort of backtracking system could work as well, but would be more complicated to implement, and I have not had the algorithm reach this state with the larger tile set.

Loading data fron JSON is also now implemented. Decided to write a simple gui for the program with javafx. At the moment it is just an output window with text, but I will implement some controls next week.

Next week I'll start implementing my own version of priority queue, fix and refactor the existing code, continue work on the ui and think about post processing and validating the generated dungeon.

[Hourly report](https://github.com/juhakaup/WFC_dungeon_gen/blob/master/documentation/hourly_report.md)
