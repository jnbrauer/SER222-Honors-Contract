# Genetic-algorithm based scheduler
Program that schedules a set of tasks using a genetic algorithm. Created as part of an honors contract for SER222 at ASU.

## Description
This project uses a genetic algorithm to schedule a set of tasks provided by the user. In addition to specifying tasks,
the user can also provide a set of regularly scheduled reserved times, such as class time or sleeping time, in which
other tasks should not be scheduled.

The fitness of generated schedules is evaluated by several factors. Firstly, the algorithm will attempt to minimize time
overlaps both between tasks and between tasks and reserved times. The two categories are evaluated separately
and given different weights in the fitness function: currently, overlap between tasks is weighted more heavily than 
overlap between tasks and reserved times. Additionally, the fitness of a schedule also considers whether or not tasks
are completed in their proper order as defined by their priorities. 

## Usage
The algorithm can be run by executing `Main.java`. Currently, all tasks and reserved times are defined programmatically in 
Main. When run, the final schedule will be displayed in the console output. Additionally, a CSV file `output.csv` will be
generated containing information about each generation of the genetic algorithm: specifically, it will contain the average
fitness in each generation, the best fitness value of each generation, and the best schedule from each generation. An
example `output.csv` file is provided as well an `output.xslx` file showing the data from the output CSV graphed in Excel.

#### Command line run instructions:
```bash
> cd src
> javac com/jnbrauer/*.java com/jnbrauer/data/*.java com/jnbrauer/utils/*.java
> java com.jnbrauer.Main
```


Copyright (C) 2021 Jude Brauer. All rights reserved.