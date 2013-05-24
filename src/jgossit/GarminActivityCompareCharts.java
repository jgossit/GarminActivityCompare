package jgossit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.codec.binary.Base64;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.DefaultXYDataset;

public class GarminActivityCompareCharts
{
	static String createGapsChart(Double[] gaps, int samplingIntervalSecs)
	{
		DefaultXYDataset dataset = new DefaultXYDataset();
		
		// + 1 for the start and 1 for end (if % samplingIntervalSecs != 0)
		Double size = (gaps.length % samplingIntervalSecs == 0 ? 1 : 2) + Math.floor(gaps.length / samplingIntervalSecs);
		double[][] data = new double[2][size.intValue()];
		double[][] control = new double[2][size.intValue()];
		
		int count = 0;
		for (int i=0;i<gaps.length;i++)
		{
			if (i == gaps.length-1 || i % samplingIntervalSecs == 0)
			{
				control[0][count] = (i+1)*1000 - (1000*60*60*10); // time in seconds, minus 10:00:00 where the chart wants to start time from
				data[0][count] = control[0][count];
				control[1][count] = 0;
				data[1][count++] = gaps[i];
			}
		}

		dataset.addSeries("Gaps", data);
		dataset.addSeries("Control", control);
		JFreeChart chart = ChartFactory.createTimeSeriesChart("Gap over Time", "Time", "Gap (metres)", dataset, false, false, false);
		
		int width=640;
        int height=480;                
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try
        {
			ChartUtilities.writeChartAsPNG(stream, chart, width, height);
			String base64Chart = Base64.encodeBase64String(stream.toByteArray());
	        return base64Chart;
		}
        catch (IOException e)
        {
		}
        return "";
	}
}