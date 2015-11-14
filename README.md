# GraphMaker
Simple graphical application for generating graphs.

## Author
Minas Mina  
Contact: [minasm1990 [at] gmail.com](mailto:minasm1990@gmail.com)  

![Alt text](/screenshots/f(x)=x^2.png?raw=true "Example plot")

### What can it do?
 - Import text files which contain 2D plot data
 - Generate a graph, which can be exported as an image
 - Supports multiple lines/curves in the same graph

### How to run
Open the **dist** folder and run GraphMaker-*version*.jar

### Importing a file
Graph maker can import text files with 2D coordinates, as comma separated values (CSV).

##### Example #1 (Single line)
Let's say we want a graph with the values *(0, 0)*, *(1, 1)*, *(2, 4)* and *(3, 9)*.  
Open your favourite spreadsheet application (Calc, Excel can be used):

|   | A  | B |
| :---: |:-:| :-:|
| **1** | 0 | 0 |
| **2** | 1 | 1 |
| **3** | 2 | 4 |
| **4** | 3 | 9 |

The first column has the values of the X axis whereas the second has the values of the Y axis.

Save the file and choose CSV format (.csv). Then press the *import* button inside *Graph Maker* and locate it.

##### Example #2 (Multiple lines)
*Graph Maker* supports multiple lines/curves in the same graph.  
Let's say we want the following lines:  
**Line A:** *(0, 0)*, *(1, 1)*  
**Line B:** *(1, 2)*, *(1, 3)*

How we do this? The solution is not that complex:  

|       | A | B | C |
|  :-:  |:-:|:-: |:-: |
| **1** | 0 | 0 | *empty cell* |
| **2** | 1 | 1 | 2 |
| **3** | 1 | *empty cell* | 3 |

The values of the columns **A** and **B** define the **first line**.  
The values of the columns **A** and **C** define the **second line**.

Save the file in .csv format and import into *Graph Maker*.

## Changelog
##### v0.1.4
* The default background color of the exported image is now white (previously light gray)
* The default symbol type is now *no symbol* instead of *filled circle*
