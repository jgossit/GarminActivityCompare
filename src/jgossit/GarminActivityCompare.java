/**
 * @author Jason Gossit
 * @version 1.2, 02/06/13
 */
package jgossit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class GarminActivityCompare
{
	PrintWriter printWriter;
	String title;
	String gapsChartData = "";
	String paceDifferenceChartData = "";
	String activityDir = null;
	String[] activityFilenames = { "", "" };
	String[] activityNames = { "", "" };
	String[] activityLabels = { "A", "B" };
	String[] activityTimes = { "", "" };
	@SuppressWarnings("unchecked")
	ArrayList<String>[] activityContents = new ArrayList[2];
	SimpleDateFormat gpxTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
	SimpleDateFormat filenameTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
	{
		gpxTimeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		filenameTimeFormat.setTimeZone(TimeZone.getDefault());
	}
	// originals from http://www.benjaminkeen.com/google-maps-coloured-markers/
	// converted with http://websemantics.co.uk/online_tools/image_to_data_uri_convertor/
	String markerAImageData = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABQAAAAiCAYAAABfqvm9AAAABGdBTUEAAK/INwWK6QAAABl0RVh0U29mdHdhcmUAQWRvYmUgSW1hZ2VSZWFkeXHJZTwAAATcSURBVHjaYmTADeSB2AWI9YGYDyr2BYgvAfFeIL6LTRNAADFiEeMC4nIgzmRl5xYVktJm4BGQZWBgZGT4+uEJw9unVxh+//zyDig/C4jbgPgzsmaAAEI3UBGI13DzSxrpOxcxqJpG/ucTkmZgZmQAq/v7n+H/5/fPGe6eX814fncPw5d3j68ChUOA+AbMAIAAQjZQBYj3y2q5ybglLfvPzy/M+Oc/0JB/qDYyATErM9DvXz8y7F4Qz3D/wsZXQCFnIL4CkgcIIJiBnEB8VFbT3dA/bxMDEyMbw18G/ABoJsP//38Zts4KZbh3fj3IheZA/AkggJig8mXcAtKGHimL/2Mz7MiqAob1fQ4Mn98+gIuB1DAyMzO4Jcz/zyeipAHk1oLEAQIIZJEkEE8z927kV9J2Zvz9H9UwkCG75kYC6Ydgvpy2B1zuP1AtJzsHIxMbF8P9S5tVQeEPEEAgFzqwsHLJqpqGMfz+h+m1exc2gGl9p3yG68cXYMiD9CgbBv1n5xIUBYUlQACBDDQWktJk4AXG5r//mAZe3DuBQVHfn0HDMoHh1/ePDDfQDAXp4eIWZhSW1gVxdQACCGSgMDc/OGlggDePLzB8fvcQbJiIrAHQUnmwBeiACWgKj6AMiMkPEEDgSGFkYsQakxf3QTSzcwkwPL11gEFa3YHhzZOLYIswcggjxAyAAGIB4vdf3j0FJVoU8PPbB3j4behzxLDIOR7hdZDWL++fgJifAAIIZOCZ9y+vAwWeMXDxSzHAzL1/cQM4zMKrz4O9CwPbpgeALbIJ/QB2OchdXz+9+//mySUQ8wpAAIG8fPD3z6/P7p5fx8DCjHAFKEZBYYZsGAjAIgdkIdiLQBPuX97E+PPb+7egnAYQQCADnwLx7DM7Oxm+f/v4H+ZdcFJxLsAIKyWDAHCsg8MRqPrnz8//T29rBUmBwuAOQADBYoMHiE8q6gdo+WSu/f+PAUcsoWc/Job/O+ZEM946tewekGsKxO8AAgjmyV+gMu7DyxtB71/e5lMzCQbnAnwA5NU9i9IYbxxf+AbI9QJicL4ECCCkUGMAhcGBd8+uRALTEbuipj1GSYNs2Pm9kxnObG/9AeT6APFpmBxAADGjqX0OiuCnNw+GSKraMwiJKmDkHpBhb55e+b9jdgTjv7+/84FCa5HlAQKICYsDVgJDe8nBFbkMv379+I8tMI+sLWf88+vbflChgi4HEEBMOIKoFljUf7x8cAYjK5IfQOx7l7YxPLyyDcStwaYRIIBwGQgK4FkX901i+PHjO9yVwOgH5pKJIOYWID6GTSNAADHhicjJn97e//Lg8lZGULgxg8Lu+TWGx9f3gOVwaQIIIHwGPgbiTTdPLYHEHtCZd86uASanf6BqdBcuTQABxEQg7a55ducww9ev7xn+Ab0LdC1IbBM+DQABRMjAwz++vPv4+tEFhq9f3gCTy0Vw+YBPA0AAsRAwEJQLLr95ct6GEWj1398/X0BbDjgBQACxEJFlzwKTkM3f36DcyXAZVFrhUwwQQMQYePXjqzvAIusTAyHXgQBAABFj4MPP7x4xfPv0ElxMElIMEEDEGPjm68fnsHrnESHFAAFEjIGf//399R/YVACZ+ImQYoAAYiLCwI9A/A2piMMLAAKIhUgDn0EbVK8JKQYIMABhubHIul5+4wAAAABJRU5ErkJggg==";
	String markerBImageData = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABQAAAAiCAYAAABfqvm9AAAABGdBTUEAAK/INwWK6QAAABl0RVh0U29mdHdhcmUAQWRvYmUgSW1hZ2VSZWFkeXHJZTwAAATkSURBVHjaYmTADeSB2AWI9YGYDyr2BYgvAfFeIL6LTRNAADFiEeMC4nIgzmTnYhOV0hBlEJDmY2AEqvzw7DPD0+uvGH5+/fUOKD8LiNuA+DOyZoAAQjdQEYjX8IvzGjlnmTOYBGn/F5YUABrGDFb3///f/+9ffmI4v/Ea466pxxneP/10FSgcAsQ3YAYABBCygSpAvF/LUUkmaVbQf34hfsb/DH8Z/v7/i+YlJgZmRhaGrx+/MMzP3sBwcfvNV0BhZyC+ApIHCCCYgZxAfFTTUckwd1k0AzMbM8M/NIPQARMjUM3f/wyzElcynN96A+RCcyD+BBBAMAPrBSR5G2oOZPznFeFmBBl2fPlFMEYGwnICDGGtbgyc/BxgPjPQ0O+ffv5vcpjO+Pbhhx6gUClAADEBCUkgTnbOsmTgF+FjhLns7eMPDLePPWRQs5EHY1ldcYaL224wVBtOglsACg4uPi5GrwJbEDceFAcAAcQCJBzYOFllTQO0gd78g+E1nzJ7OFvNWoFhRtwqhidXXjDI6EiAxUB6DH00/69t2C367eMPZ4AAArnQWEJNhEFIip/hHxDiAxe23YR4XVYALgbSwy3EzSilJQbi6gAEEMiFwsDwA8YeM8gTGIZkijSj8ENbEGEIjyAgFJQCp31+gABiAUc1IyNOV3mX2cHZF7feZFhds4tBRlcC6H151OQENQMggEAGvv/w/DPDfwbsyQQ5DEHsIqUucOyjGvif4f2zTyDGJ4AAAoXhmRe33jC8f/4J6G1GvGH4/eMPUDJh4OJnR0rojAyf3n/5//TqSxD3CkAAgVx48Oe3X8/Ob7ku5ZxqyfDn/28UQ/r8FsHZoKQEApaR+kgJnIXh0vabjMAYfgvKaQABBHLhUyCevWPSEYZvn779hydiYEyqWqGGk4GXOkPhxjh4kgGBn19+/N/WdwjEXADEdwACCOZHHiA+qe+lrpW5MOL/f8Z/+P0OBcCc8n922hrG02uv3ANyTYH4HUAAMUPlfoHKuJe33wa9uPuGz9RPBxjM/wkYxsKwsGAj44kVl94AuV5A/AAkDhBAzEhqQGFw4Nn115HAFMCuaaMKTLJ/cRq2d9Zxhu19R36AIh+IT8PkAAKIGU3tcyC+f/PYgxBVKzkGMXkRoDv/YRj29PrL/7NT1jD+/fMvHyi0FlkeIICYsDhgJdCUJSsrtjP8/PHzP7aktLZhF+Ov77/3A5nT0OUAAogJRxDVAov6j4fmnwGW1axwQRYg+9LumwxX9twBcWuwaQQIIFwGggJ41r5ZJxm+f/8Od+U/oNP3zTwJYm4B4mPYNAIEEBOeiJz89tGHL1d23WYEJV5QYfrsxkuG6wfvgeVwaQIIIHwGPgbiTSdXX4JmMWaGc5uuMfz/9x8ksAuXJoAAYiKQdtfcOf6I4euHL+By7/Lu2yCxTfg0AAQQIQMPf/3w/eOjyy8Yvrz9Ai6pgWAbPg0AAcRCwEBQLrj85PILG1C0/Pn19wW05YATAAQQCxFZ9iwwCdn8/gXONZeB+Cs+xQABRIyBV1/de8fw/fNPBkKuAwGAACLGwIfvnnxk+Pwa7LDrhBQDBBAxBr758PIzsCAFJ+5HhBQDBBAxBn7+9/vf/38M4OzyiZBigABiIsLAj0D8DamIwwsAAoiFSAOfQRtUrwkpBggwAK1GpdpHAecTAAAAAElFTkSuQmCC";
	
	public GarminActivityCompare()
	{
		
	}
	
	public GarminActivityCompare(PrintWriter printWriter, String title,	ArrayList<String>[] activityContents)
	{
		this.printWriter = printWriter;
		this.title = title;
		this.activityContents = activityContents;
	}

	public GarminActivityCompare(PrintWriter printWriter, String title,	ArrayList<String>[] activityContents, String[] activityLabels)
	{
		this.printWriter = printWriter;
		this.title = title;
		this.activityContents = activityContents;
		if (activityLabels != null && activityLabels.length == 2 && activityLabels[0] != null && activityLabels[1] != null && activityLabels[0].length() > 0 && activityLabels[1].length() > 0)
		{
			this.activityLabels[0] = activityLabels[0] + " (A)";
			this.activityLabels[1] = activityLabels[1] + " (B)";
		}
	}

	public static void main(String[] args) throws IOException, ParseException
	{
		new GarminActivityCompare().promptForActivities();
	}
	
	@SuppressWarnings("unchecked")
	public void go() throws IOException, ParseException
	{
		if (activityDir != null)
			System.out.println("Creating comparison file...");
		ArrayList<Double>[] lats = new ArrayList[activityFilenames.length];
		ArrayList<Double>[] lons = new ArrayList[activityFilenames.length];
		ArrayList<Double>[] distancesTravelled = new ArrayList[activityFilenames.length];
		ArrayList<String> htmlContent = new ArrayList<String>();
		for (int i=0; i<activityFilenames.length; i++)
		{
			lats[i] = new ArrayList<Double>();
			lons[i] = new ArrayList<Double>();
			distancesTravelled[i] = new ArrayList<Double>();
			boolean reachedStart = false;
			Date prevTime = null;
			Double lat = null, prevLat = null;
			Double lon = null, prevLon = null;
			for (String line : activityContents[i])
			{
				if (line.contains("<trk>"))
					reachedStart = true;
				if (!reachedStart)
					continue;
				
				if (line.contains("<time>"))
				{
					String time = line.substring(line.indexOf("<time>")+6,line.indexOf("</time>"));
					Date thisTime = gpxTimeFormat.parse(time);
					if (prevTime != null)
					{
						long diff = thisTime.getTime()-prevTime.getTime();
						if (diff > 1000)
						{
							double latDiff = lat - prevLat;
							double latDiffIncr = latDiff / (diff / 1000);
							double lonDiff = lon - prevLon;
							double lonDiffIncr = lonDiff / (diff / 1000);
							for (int j=1;j<diff/1000;j++)
							{
								lats[i].add(prevLat + j*latDiffIncr);
								lons[i].add(prevLon + j*lonDiffIncr);
							}
						}
						lats[i].add(lat);
						lons[i].add(lon);
					}
					prevTime = thisTime;
				}
				else if (line.contains("<trkpt"))
				{
					if (lon != null)
					{
						prevLon = lon;
						prevLat = lat;
					}
					lon = Double.parseDouble(line.substring(line.indexOf("lon")+5, line.indexOf("lat")-2));
					lat = Double.parseDouble(line.substring(line.indexOf("lat")+5, line.indexOf(">")-1));
					if (prevTime == null)
					{
						lats[i].add(lat);
						lons[i].add(lon);
					}
				}
			}
		}
		
		int[] activityOrder = (lats[0].size() >= lats[1].size() ? new int[]{0,1} : new int[]{1,0}); // longest activity first
		File outputFile = null;
		if (activityDir != null)
		{
			outputFile = new File(activityDir + activityNames[activityOrder[0]] + " - " + activityTimes[activityOrder[0]] + ".html");
			if (outputFile.exists())
				outputFile.delete();
			printWriter = new PrintWriter(outputFile);
			title = outputFile.getName().substring(0,outputFile.getName().length()-5);
		}
		
		ArrayList<Double>[] paceAtIntervals = new ArrayList[activityOrder.length];
		for(int i=0;i<activityOrder.length;i++)
		{
			htmlContent.add("	  var lats" + (i+1) + " = [ ");
			for (int j=0;j<lats[activityOrder[i]].size();j++)
			{
				htmlContent.add(String.format("%s%s%s",
						lats[activityOrder[i]].get(j),
						j+1 < lats[activityOrder[i]].size() ? "," : "];\n",
						j > 0 && j % 10 == 0 ? "\n\t\t" : ""));
			}
			htmlContent.add("	  var lons" + (i+1) + " = [ ");
			for (int j=0;j<lons[activityOrder[i]].size();j++)
			{
				htmlContent.add(String.format("%s%s%s",
						lons[activityOrder[i]].get(j),
						j+1 < lons[activityOrder[i]].size() ? "," : "];\n",
						j > 0 && j % 10 == 0 ? "\n\t\t" : ""));
			}
			
			htmlContent.add("	  var distance" + (i+1) + " = [ ");
			double distance = 0;
			for (int j=1;j<lats[activityOrder[i]].size();j++)
			{
				distance += GarminActivityCompare.distanceBetween(lats[activityOrder[i]].get(j), lons[activityOrder[i]].get(j),
											lats[activityOrder[i]].get(j-1), lons[activityOrder[i]].get(j-1));
				distancesTravelled[activityOrder[i]].add(distance);
				htmlContent.add(String.format("%.3f%s%s",
						distance,
						j+1 < lats[activityOrder[i]].size() ? "," : "];\n",
						j % 10 == 0 ? "\n\t\t" : ""));
			}
			
			double nextInterval = 0.1; // 100 metres
			paceAtIntervals[i] = new ArrayList<Double>();
			htmlContent.add("	  var pace" + (i+1) + " = [ 0, ");
			for (int j=0;j<distancesTravelled[activityOrder[i]].size();j++)
			{
				Double distanceNow = distancesTravelled[activityOrder[i]].get(j);
				int timeFrame = j < 10 ? j+1 : 10;
				Double distanceBefore = j < 10 ? 0 : distancesTravelled[activityOrder[i]].get(j - timeFrame);
				Double pace = 3600 * ((distanceNow-distanceBefore)/timeFrame);
				
				htmlContent.add(String.format("%.1f%s%s",
						pace,
						j+1 < distancesTravelled[activityOrder[i]].size() ? "," : "];\n",
						j > 0 && j % 10 == 0 ? "\n\t\t" : ""));
				
				if (distanceNow >= nextInterval || j == distancesTravelled[activityOrder[i]].size()-1)
				{
					paceAtIntervals[i].add(pace);
					nextInterval += 0.1;
				}
			}
		}
		
		while (paceAtIntervals[0].size() != paceAtIntervals[1].size())
		{
			if (paceAtIntervals[0].size() < paceAtIntervals[1].size())
				paceAtIntervals[0].add(paceAtIntervals[0].get(paceAtIntervals[0].size()-1));
			else
				paceAtIntervals[1].add(paceAtIntervals[1].get(paceAtIntervals[1].size()-1));
		}
		double distanceInterval = 0.1; // 100 metres
		for (int i=0;i<paceAtIntervals[0].size();i++)
		{
			paceDifferenceChartData += String.format("['%.3f', %.1f, %s, %.1f, %s]",
					distanceInterval,
					paceAtIntervals[0].get(i),
					(paceAtIntervals[0].get(i) >= paceAtIntervals[1].get(i)),
					paceAtIntervals[1].get(i),
					(paceAtIntervals[1].get(i) >= paceAtIntervals[0].get(i)));
			distanceInterval += 0.1;
			if (i != paceAtIntervals[0].size()-1) // not at the end
				paceDifferenceChartData += ",\n\t";
		}
		
		// instead of comparing difference in A and B distance travelled at the same point in time, as the devices may vary,
		// find the closest point in B and work out how far it has travelled from there to it's current point 
		htmlContent.add("	  var gap = [ ");
		int lastTrkPoint = 0; // remember where the last point was to avoid starting all over again or matching the wrong lap if there are several laps
		int lookAhead = 0; // so it doesn't get tripped up on turnarounds/loops
		Double kmMarker = 1.0;
		for (int trkPointA=0; trkPointA<lats[activityOrder[0]].size(); trkPointA++)
		{
			double lat = lats[activityOrder[0]].get(trkPointA);
			double lon = lons[activityOrder[0]].get(trkPointA);
			double smallestDistance = 999999;
			int lookedAhead = 0;
			for (int trkPointB=lastTrkPoint; trkPointB<lons[activityOrder[1]].size(); trkPointB++)
			{
				double distance = GarminActivityCompare.distanceBetween(lat, lon, lats[activityOrder[1]].get(trkPointB), lons[activityOrder[1]].get(trkPointB));
				if (smallestDistance == 999999)
				{
					smallestDistance = distance;
					continue;
				}
				
				if (distance < smallestDistance)
				{
					smallestDistance = distance;
				}
				else if (lookedAhead < lookAhead)
				{
					lookedAhead++;
				}
				else
				{
					if (trkPointB-1 == lastTrkPoint) // matched same point as last time, look ahead further next time in case it's getting stuck
						lookAhead++;
					else
						lookAhead = 0;
					lastTrkPoint = trkPointB-1;
					break;
				}
			}
			
			// more trkPoint's in A than B = A took longer, B is probably at the finish line, use B's last trkPoint
			// (although sampling frequency could differ...)
			int currentTrkPointB = trkPointA >= distancesTravelled[activityOrder[1]].size() ? distancesTravelled[activityOrder[1]].size()-1 : trkPointA;
			int closestTrkPointB = lastTrkPoint == distancesTravelled[activityOrder[1]].size() ? lastTrkPoint-1 : lastTrkPoint;
			double distance = -1 * (distancesTravelled[activityOrder[1]].get(currentTrkPointB) - distancesTravelled[activityOrder[1]].get(closestTrkPointB));
			htmlContent.add(String.format("%.3f%s%s",
					distance,
					trkPointA+1 < lats[activityOrder[0]].size() ? "," : "];\n",
					trkPointA > 0 && trkPointA % 10 == 0 ? "\n\t\t" : ""));
			
			double distanceTravelledByA = trkPointA < distancesTravelled[activityOrder[0]].size() ? distancesTravelled[activityOrder[0]].get(trkPointA) : 0.0;
			boolean reachedKmMarker = distanceTravelledByA >= kmMarker;
			
			if (trkPointA == lats[activityOrder[0]].size()-1 || trkPointA % 10 == 0 || reachedKmMarker) // first,last and every tenth second
			{
				String minsSeconds = "'" + String.format("%.0f",Math.floor(trkPointA/60)) + ":" + String.format("%02d",trkPointA % 60) + "'";
				double distanceFormatted = Double.parseDouble(String.format("%.3f",distance));
				//String annotation = trkPointA % 300 == 0 ? minsSeconds : null;
				String annotation = null;
				if (reachedKmMarker)
				{
					annotation = "'" + kmMarker.intValue() + "km'"; 
					kmMarker += 1.0;
				}
				String annotationText = annotation == null ? null : "'Gap: " + distanceFormatted + "'";
				gapsChartData += "[" + minsSeconds + ", " + distanceFormatted + ", " + annotation + ", " + annotationText + "]";
				if (trkPointA != lats[activityOrder[0]].size()-1) // not at the end
					gapsChartData += ",\n\t";
			}

		}
		
		printHtml("garminactivitycompare_header.html",lats[activityOrder[0]].size());
		for (String htmlLine : htmlContent)
			printWriter.print(htmlLine);
		printHtml("garminactivitycompare_footer.html", 0);
				
		if (activityDir != null)
		{
			printWriter.close();
			System.out.format("Comparison file created at '%s'", outputFile.getAbsolutePath());
		}
	}
	
	private void promptForActivities() throws IOException, ParseException
	{
		BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
		while (activityDir == null)
		{
			System.out.println("Enter the directory containing the Garmin Activity (.gpx) files:");
			String activityDirInput = inputReader.readLine();
			File testActivityDir = new File(activityDirInput);
			if (!testActivityDir.exists())
			{
				System.err.println("'" + activityDirInput + "' does not exist");
				continue;
			}
			else if (!testActivityDir.isDirectory())
			{
				System.err.println("'" + activityDirInput + "' is not a directory");
				continue;
			}
			else
			{
				File[] activityFiles = testActivityDir.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						return name.toLowerCase().endsWith(".gpx");
					}
				});
				if (activityFiles.length < 2)
				{
					System.err.println("'" + activityDirInput + "' does not contain at least 2 activity files");
					continue;
				}
				
				String[] names = new String[activityFiles.length];
				String[] times = new String[activityFiles.length];
				activityDir = activityDirInput + File.separator;
				for (int i=0; i<activityFiles.length; i++)
				{
					String line = null;
					BufferedReader bufferedReader = new BufferedReader(new FileReader(activityFiles[i]));
					while ((line = bufferedReader.readLine()) != null)
					{
						if (line.contains("<time>"))
						{
							String time = line.substring(line.indexOf("<time>")+6,line.indexOf("</time>"));
							Date date = gpxTimeFormat.parse(time);
							times[i] = filenameTimeFormat.format(date);
						}
						if (line.contains("<name>"))
						{
							names[i] = line.substring(line.indexOf("<name>")+6, line.indexOf("</name>"));
							System.out.format("[%s]: %s - %s (%s)\n", i,
									activityFiles[i].getName(),
									times[i],
									names[i]);
							break;
						}
						else
							continue;
					}
					bufferedReader.close();
				}
				
				int[] activityNumbers = {0,1};
				while (true)
				{
					System.out.println("Enter the numbers of the activity files to compare: (default 0,1)");
					String activityNumbersInput = inputReader.readLine();
					if (activityNumbersInput.trim().equals(""))
						break;
					String[] activityNumbersArray = activityNumbersInput.split(",");
					try
					{
						if (activityNumbersArray.length != 2)
							throw new Exception();
						if (Integer.parseInt(activityNumbersArray[0]) >= activityFiles.length ||
							Integer.parseInt(activityNumbersArray[1]) >= activityFiles.length)
							throw new Exception();
						activityNumbers[0] = Integer.parseInt(activityNumbersArray[0]);
						activityNumbers[1] = Integer.parseInt(activityNumbersArray[1]);
						break;
					}
					catch(Exception e)
					{
						System.err.println("Invalid entry");
						continue;
					}
				}
				
				for (int i=0;i<activityNumbers.length;i++)
				{
					activityFilenames[i] = activityFiles[activityNumbers[i]].getName();
					activityNames[i] = names[activityNumbers[i]];
					activityTimes[i] = times[activityNumbers[i]];
					ArrayList<String> activityContent = new ArrayList<String>();
			        BufferedReader br = new BufferedReader(new FileReader(activityDir + activityFilenames[i]));
			        String line = null;
			        while ((line = br.readLine()) != null)
			        	activityContent.add(line);
			        activityContents[i] = activityContent;
			        br.close();
				}
			}
		}
		go();
	}
	
	private void printHtml(String filename, int numPoints)
	{
		FileReader fr = null;
		BufferedReader br = null;
		try
		{
			fr = new FileReader(filename);
			br = new BufferedReader(fr);
			String line = null;
			while ( (line = br.readLine()) != null)
			{
				line = line.replaceFirst("%TITLE%",title)
					.replaceFirst("%ACTIVITYA%",activityLabels[0])
					.replaceFirst("%ACTIVITYB%",activityLabels[1])
					.replaceFirst("%NUMPOINTS%",""+(numPoints-1))
					.replaceFirst("%GAPSCHARTDATA%",gapsChartData)
					.replaceFirst("%PACEDIFFCHARTDATA%",paceDifferenceChartData)
					.replaceFirst("%MARKERA%",markerAImageData)
					.replaceFirst("%MARKERB%",markerBImageData);
				printWriter.println(line);
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (br != null)
					br.close();
				if (fr != null)
					fr.close();
			}
			catch (IOException e)
			{
			}
		}
	}
	
	public static double distanceBetween(double lat1, double lng1, double lat2, double lng2)
	{
	    int earthRadius = 6371;
	    double dLat = Math.toRadians(lat2-lat1);
	    double dLng = Math.toRadians(lng2-lng1);
	    double sindLat = Math.sin(dLat / 2);
	    double sindLng = Math.sin(dLng / 2);
	    double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
	            * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double distance = earthRadius * c;

	    return distance;
	}
}