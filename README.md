GarminActivityCompare
=====================

Garmin Activity Compare is a java application designed to compare two Garmin activities visually over a Google Maps route.

This looks similar to the 'player' on Garmin Connect, however that is only capable of displaying a single activity while the 'compare' function on Garmin Connect only compares a table of lap times.

By providing two Garmin activities of an identical (or close to) route/distance, you can get a visual comparison of where the two activities are at the same point in time, the distance each has travelled (keeping in mind the devices may vary slightly in distance recording) and the gap between them. The pace, as an average of up to the last 10 seconds, for both activities is also displayed.

The typical use case is for comparing two runners competing in the same race or for a single runner competing the same race/route on multiple occasions.


Usage
=====

Compile:<br>
&nbsp;&nbsp;<i>javac jgossit\GarminActivityCompare.java</i><br>
and run:<br>
&nbsp;&nbsp;<i>java jgossit.GarminActivityCompare</i>

Or download the jar file from \bin and execute:<br>
&nbsp;&nbsp;<i>java -jar GarminActivityCompare.jar</i>
  
Then follow the prompts to provide<br>
1) The directory containing the Garmin Activity (.gpx) files<br>
2) The numbers of the two Garmin Activity files

The order of the two files is not important, the slower time will be (A) and it's Acitivity Name and Date will be used for the output filename, and the faster time (B).


Output
==============

A single HTML file containing everything necessary will be output to the directory containing the Garmin Activity files with the name '&lt;Activity A Name&gt; - &lt;Activity A Date&gt;.html'

An example is available in \example or by clicking the link below. (Note: you will need to save the raw file content locally to a .html file and then open it)
<p align="center" >
  <a href="https://raw.github.com/jgossit/GarminActivityCompare/master/example/2013-04-28_Sri_Chinmoy_Princes_Park_10km.html"><img src="https://raw.github.com/jgossit/GarminActivityCompare/master/example/2013-04-28_Sri_Chinmoy_Princes_Park_10km.png"></a>
</p>
