package jgossit.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jgossit.GarminActivityCompare;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class GarminActivityCompareServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	private static final String REDIRECT_PAGE = "garminactivitycompare.html";
	private static final String ACCEPT_EXTENSION = ".gpx";
	private static final Pattern activityUrlPattern = Pattern.compile(".*garminactivitycompare/(\\d+)/(\\d+).*");
	private static final Pattern activityUrlNamedPattern = Pattern.compile(".*garminactivitycompare/(\\d+)/(\\d+)/(.*)/(.*)");
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		@SuppressWarnings("unchecked")
		ArrayList<String>[] activityContents = new ArrayList[2];
		String[] activityLabels = { null, null };
		StringBuffer title = new StringBuffer();
		String redirectUrl = req.getRequestURI().replaceFirst("garminactivitycompare.*", REDIRECT_PAGE);
		
		Matcher matcher = activityUrlPattern.matcher(req.getRequestURI());
		Matcher namedMatcher = activityUrlNamedPattern.matcher(req.getRequestURI());
		if (matcher.matches() || namedMatcher.matches())
		{
			String activityStr = null;
			try
			{
				for (int i=0;i<2;i++)
				{
					activityStr = matcher.matches() ? matcher.group(i+1) : namedMatcher.group(i+1);
					if (activityStr.length() != 9)
					{
						resp.sendRedirect(redirectUrl + "?message=" + URLEncoder.encode("Invalid Activity # format, it should be 9 digits long","ASCII"));
						return;
					}
					int activity = Integer.parseInt(activityStr);
					try
					{
						activityContents[i] = readActivity(activity, title);
					}
					catch (Exception e)
					{
						resp.sendRedirect(redirectUrl + "?message=" + URLEncoder.encode("Error accessing Activity #" + activity + ", it's privacy may not be set to 'Everyone'","ASCII"));
						return;
					}
				}
				if (namedMatcher.matches())
				{
					activityLabels[0] = namedMatcher.group(3);
					activityLabels[1] = namedMatcher.group(4);
					if (activityLabels[1].indexOf("/") != -1)
						activityLabels[1] = activityLabels[1].substring(0, activityLabels[1].indexOf("/"));
				}
			}
			catch (NumberFormatException e)
			{
				resp.sendRedirect(redirectUrl + "?message=" + URLEncoder.encode("Invalid Activity # format '" + activityStr + "'","ASCII"));
				return;
			}
		}
		else
		{
			boolean isMultipart = ServletFileUpload.isMultipartContent(req);
			if (!isMultipart)
			{
				resp.sendRedirect(redirectUrl);
				return;
			}
			
			ServletFileUpload upload = new ServletFileUpload();
			try
			{
				FileItemIterator iterator = upload.getItemIterator(req);
				int activityCount = 0;
				int activityNameCount = 0;
				while (iterator.hasNext())
				{
					FileItemStream item = iterator.next();
					if (!item.isFormField()) // file input
					{
						if (item.getName().equals("")) // no activity file provided
							continue;
						else if (!item.getName().toLowerCase().endsWith(ACCEPT_EXTENSION))
						{
							resp.sendRedirect(redirectUrl + "?message=" + URLEncoder.encode("Invalid Activity file extension, only .gpx files are accepted","ASCII"));
							return;
						}
						
				        InputStream inputStream = item.openStream();
				        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
				        activityContents[activityCount++] = readActivity(br, title);
					}
					else
					{
						InputStream inputStream = item.openStream();
						BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
						String activityStr = br.readLine();
						if (item.getFieldName().endsWith("name")) // activity name
						{
							if (activityStr != null && activityStr.length() > 0)
								activityLabels[activityNameCount++] = activityStr;
						}
						else // activity number
						{
							if (activityStr != null && activityStr.length() == 9) // activity numbers are 9 digits
							{
								int activity = Integer.parseInt(activityStr);
								try
								{
									activityContents[activityCount++] = readActivity(activity, title);
								}
								catch (Exception e)
								{
									resp.sendRedirect(redirectUrl + "?message=" + URLEncoder.encode("Error accessing Activity #" + activity + ", it's privacy may not be set to 'Everyone'","ASCII"));
									return;
								}
							}
						}
						br.close();
					}
				}
				if (activityCount != 2)
				{
					resp.sendRedirect(redirectUrl + "?message=" + URLEncoder.encode("Two Acitivity files or #'s are required, but only " + activityCount + " was provided","ASCII"));
					return;
				}
			}
			catch (FileUploadException e)
			{
				resp.sendRedirect(redirectUrl + "?message=" + URLEncoder.encode("Error in file upload - " + e.getMessage(),"ASCII"));
				return;
			}
			
		}

		try
		{
			GarminActivityCompare garminActivityCompare = new GarminActivityCompare(resp.getWriter(), title.toString(), activityContents, activityLabels);
			garminActivityCompare.go();
		}
		catch (ParseException e)
		{
			resp.sendRedirect(redirectUrl + "?message=" + URLEncoder.encode("Error parsing Activity - " + e.getMessage(),"ASCII"));
			return;
		}
	}
	
	private ArrayList<String> readActivity(int activityNumber, StringBuffer title) throws Exception
	{
		String address = "http://connect.garmin.com/activity/proxy/activity-service-1.1/gpx/activity/" + activityNumber + "?full=true";
		URL url = new URL(address);
		HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
		urlConnection.setUseCaches(false);
		urlConnection.setDefaultUseCaches(false);
		urlConnection.addRequestProperty("Cache-Control", "no-cache,max-age=0");
		urlConnection.addRequestProperty("Pragma", "no-cache");
		if (urlConnection.getResponseCode() != 200)
		{
			throw new Exception("Response code " + urlConnection.getResponseCode());
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
		ArrayList<String> activityContent = readActivity(br, title);
		urlConnection.disconnect();
		return activityContent;
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		doPost(req, resp);
	}
	
	private ArrayList<String> readActivity(BufferedReader br, StringBuffer title)
	{
		ArrayList<String> activityContent = new ArrayList<String>();
		String line = null;
        try
        {
			while ((line = br.readLine()) != null)
			{
				if (title.length() == 0 && line.contains("<name>"))
					title.append(line.substring(line.indexOf("<name>")+6, line.indexOf("</name>")));
				activityContent.add(line);
			}
			br.close();
		}
        catch (IOException e)
        {
		}
        return activityContent;
	}
}