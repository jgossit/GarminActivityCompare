package jgossit.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jgossit.GarminActivityCompare;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class GarminActivityCompareServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	private static final int MAX_SIZE = 512000; // 500kb
	private static final String REDIRECT_PAGE = "garminactivitycompare.html";
	private static final String ACCEPT_EXTENSION = ".gpx";
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		boolean isMultipart = ServletFileUpload.isMultipartContent(req);
		if (!isMultipart)
			resp.sendRedirect(REDIRECT_PAGE);
		
		DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
		fileItemFactory.setSizeThreshold(MAX_SIZE);
		ServletFileUpload upload = new ServletFileUpload(fileItemFactory);
		upload.setSizeMax(MAX_SIZE);
		upload.setFileSizeMax(MAX_SIZE);
		try
		{
			List<FileItem> items = upload.parseRequest(req);
			if (items.size() != 2)
				resp.sendRedirect(REDIRECT_PAGE);
			
			BufferedReader[] activityBufferedReaders = new BufferedReader[2];
			String title = null;
			for (int i=0;i<2;i++)
			{
				if (!items.get(i).getName().endsWith(ACCEPT_EXTENSION))
					resp.sendRedirect(REDIRECT_PAGE);
				
				InputStream inputStream = items.get(i).getInputStream();
				activityBufferedReaders[i] = new BufferedReader(new InputStreamReader(inputStream));
				if (i == 0)
				{
					File outputFile = new File(items.get(i).getName());
					title = outputFile.getName().substring(0,outputFile.getName().length() - ACCEPT_EXTENSION.length());
				}
			}
			
			GarminActivityCompare garminActivityCompare = new GarminActivityCompare(resp.getWriter(), title, activityBufferedReaders);
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