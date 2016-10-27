package edu.sjsu.cmpe277.org.sjsumap;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by pnedunuri on 10/26/16.
 */

public class RESTHelper {
    public static String fetchData(String url, String query)
    {
        String charset = "UTF-8";

        try
        {
            HttpURLConnection httpConnection = (HttpURLConnection) new URL(url + "?" + query).openConnection();
            httpConnection.setRequestProperty("Accept-Charset", charset);

            InputStream response = httpConnection.getInputStream();

            httpConnection.disconnect();

            return parseResponseData(response);
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    private static String parseResponseData(InputStream response)
    {
        StringBuffer buffer = new StringBuffer();

        try
        {
            int i = 0;
            while ((i = response.read()) != -1)
            {
                buffer.append((char) i);
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return buffer.toString();
    }
}