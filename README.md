# CS 537 - Breweries

Nestor Tamares  
masc2566  
CS 537 - Programming for GIS  
Prof. Eckberg  
  
This project will show a map of San Diego with several different craft beer breweries as the points of interest. 
Upon clicking on one of the breweries,  a window will pop up containing information about that certain breweries   
such as location, types of beer that they carry, and the company motto/logo.

Additional Folders:
Breweries Photos folder contains pictures of the breweries, logos, area, etc.  
Data folder contains: .shp, .shx, .dbf, and .csv files.   
Icons folder contains custom icons used in the map.
Documentaion folder contains documentation files. To access documentation for esri/MOJO, go to the directory  
Documentation/MapObjects/javadoc and open up index.html.

To Run Using Windows Command Prompt:  
Change working directory to \CS-537-Breweries\src  
Type command "moj_compile Breweries.java" to compile  
Type command "moj_run Breweries ." to run  

Additional Notes:  
  - If you wish to download my files and run through Windows Command Prompt, you need to edit the moj_compile.bat  
    and moj_run.bat files. In moj_compile, you need to change lines 13 and 14 to where your JDK home and MOJO home  
	directories are, respectively. For moj_run, you need to change lines 13 and 19. In line 13, set your MOJO home  
	directory. In line 19, set the directory where your JRE is to run Java files.  
	
  - You also need to change the path of San Diego shape file. Change the String from  
	"D:\\Data\\Supervisor_Districts.shp" to the file location where you have it saved.  
	
  - Drink responsibly! Know your limits and call a taxi/Uber/Lyft/friend. The worst time and place to find out that 
    you're not invincible is drunk behind the wheel. 
	