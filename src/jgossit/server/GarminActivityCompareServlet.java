package jgossit.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;

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
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		boolean isMultipart = ServletFileUpload.isMultipartContent(req);
		if (!isMultipart)
			resp.sendRedirect(REDIRECT_PAGE);
		
		ServletFileUpload upload = new ServletFileUpload();
		try
		{
			@SuppressWarnings("unchecked")
			ArrayList<String>[] activityContents = new ArrayList[2];
			String title = null;
			FileItemIterator iterator = upload.getItemIterator(req);
			int count = 0;
			while (iterator.hasNext())
			{
				FileItemStream item = iterator.next();
				if (!item.getName().endsWith(ACCEPT_EXTENSION))
					resp.sendRedirect(REDIRECT_PAGE);
				
		        if (title == null)
		        {
		        	File uploadFile = new File(item.getName());
		        	title = uploadFile.getName().substring(0,uploadFile.getName().length() - ACCEPT_EXTENSION.length());
		        }
		        InputStream inputStream = item.openStream();
		        ArrayList<String> activityContent = new ArrayList<String>();
		        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		        String line = null;
		        while ((line = br.readLine()) != null)
		        	activityContent.add(line);
		        activityContents[count++] = activityContent;
		        br.close();
			}
			
			GarminActivityCompare garminActivityCompare = new GarminActivityCompare(resp.getWriter(), title, activityContents);
			garminActivityCompare.go();
		}
		catch (FileUploadException e)
		{
			resp.sendRedirect(REDIRECT_PAGE);
		}
		catch (ParseException e)
		{
			resp.sendRedirect(REDIRECT_PAGE);
		}
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		doPost(req, resp);
	}
}