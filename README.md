GarminActivityCompare
=====================

Garmin Activity Compare is a java application/java servlet designed to compare two Garmin activities visually over a Google Maps route.

This looks similar to the 'player' on Garmin Connect, however that is only capable of displaying a single activity while the 'compare' function on Garmin Connect only compares a table of lap times.

By providing two Garmin activities of an identical (or close to) route/distance, you can get a visual comparison of where the two activities are at the same point in time, the distance each has travelled (keeping in mind the devices may vary slightly in distance recording) and the gap between them. The pace, as an average of up to the last 10 seconds, for both activities is also displayed.

There are controls for<br>
- Play/Pause and Stop (return to the start) of the playback<br>
- Slider showing the Activity progress, which can also be used to scan through/check individual points<br>
- Slider to control the playback speed

There is also a Gap over Time chart, to display how the gap between the two grew/shrink over the course of the activity, and a Pace Difference chart to show the pace differences at the same distance (part) of the course.

The typical use case is for comparing two runners competing in the same race or for a single runner competing the same race/route on multiple occasions.


Usage
=====

Application:

Compile:<br>
&nbsp;&nbsp;<i>javac jgossit\GarminActivityCompare.java</i><br>
and run:<br>
&nbsp;&nbsp;<i>java jgossit.GarminActivityCompare</i>

Or download the jar file from \bin and execute (Java 6+):<br>
&nbsp;&nbsp;<i>java -jar GarminActivityCompare.jar</i>
  
Then follow the prompts to provide<br>
1) The directory containing the Garmin Activity (.gpx) files<br>
2) The numbers of the two Garmin Activity files

The order of the two files is not important, the slower time will be (A) and it's Acitivity Name and Date will be used for the output filename, and the faster time (B).


Servlet:

The war directory contains a WEB-INF/web.xml for deploying the servlet to your app server and the ui files for obtaining the activity information to pass to the servlet and displaying the response.<br><br>
The ui allows either the exported activity .gpx files to be uploaded, or the activity numbers to be specified and the file content downloaded remotely using the garmin connection REST api - this requires that the activities privacy setting is set to 'Everyone'.<br><br>
Additionally, the activity numbers can be specified as RESTful parameters to the servlet as /garminactivitycompare/111111111/222222222, where /garminactivitycompare is the servlet URL and 111111111 and 222222222 are the 9-digit activity numbers.


Demo
====

Available here <a target="_blank" href="http://jgossit.appspot.com/garminactivitycompare">jgossit.appspot.com/garminactivitycompare</a>
<p align="center" >
  <img src="https://raw.github.com/jgossit/GarminActivityCompare/master/example/web form.png">
</p>


Output
======

A single HTML file containing everything necessary will be output to the directory containing the Garmin Activity files with the name '&lt;Activity A Name&gt; - &lt;Activity A Date&gt;.html'

An example is available in \example or by clicking an image below. (Note: you will need to save the raw file content locally to a .html file and then open it)
<p align="center" >
  <a href="https://raw.github.com/jgossit/GarminActivityCompare/master/example/2013-04-28_Sri_Chinmoy_Princes_Park_10km.html"><img src="https://raw.github.com/jgossit/GarminActivityCompare/master/example/2013-04-28_Sri_Chinmoy_Princes_Park_10km.png"></a>
  <br>At the start of the event
</p>
<br>
<p align="center" >
  <a href="https://raw.github.com/jgossit/GarminActivityCompare/master/example/2013-04-28_Sri_Chinmoy_Princes_Park_10km.html"><img src="https://raw.github.com/jgossit/GarminActivityCompare/master/example/2013-04-28_Sri_Chinmoy_Princes_Park_10km-2.png"></a>
  <br>Showing the Gap over Time chart
</p>
<br>
<p align="center" >
  <a href="https://raw.github.com/jgossit/GarminActivityCompare/master/example/2013-04-28_Sri_Chinmoy_Princes_Park_10km.html"><img src="https://raw.github.com/jgossit/GarminActivityCompare/master/example/2013-04-28_Sri_Chinmoy_Princes_Park_10km-3.png"></a>
  <br>Showing the Pace Difference chart
</p>


Requirements
============

Application:

Java 6+ to compile or run the jar.<br>
Firefox and Chrome play the output well but Internet Explorer (8) seems to run it extremely slowly.


Servlet:

Your App server of choice to deploy the servlet<br>
Apache Commons FileUpload<br>
Apache Commons IO


Credits
=======

Map markers are from http://www.benjaminkeen.com/google-maps-coloured-markers/<br>
And converted to Base64 data with http://websemantics.co.uk/online_tools/image_to_data_uri_convertor/


License
=======
See the LICENSE file
