package net.nyllian.vhue.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;

/**
 * Created by Nyllian on 29/11/2017.
 *
 */
public class HueUtils
{
    private static final Logger LOG = LoggerFactory.getLogger(HueUtils.class);

    public final static String HUB_VERSION = "1709131301";
    public final static String API_VERSION = "1.20.0";
    public final static String MODEL_ID = "vHUE002";

    public static InetAddress getListeningAddress()
    {
        List<InetAddress> availableAddresses = new ArrayList<>();

        try
        {
            Enumeration<NetworkInterface> iFaces = NetworkInterface.getNetworkInterfaces();
            while (iFaces.hasMoreElements())
            {
                NetworkInterface iFace = iFaces.nextElement();
                LOG.trace("Checking interface {}...", iFace.getName());

                Enumeration<InetAddress> inetAddresses = iFace.getInetAddresses();
                while ((inetAddresses.hasMoreElements()))
                {
                    InetAddress inetAddress = inetAddresses.nextElement();

                    // Only ipv4
                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress() && inetAddress.isReachable(50))
                    {
                        LOG.debug(String.format("Interface %s has address %s", iFace.getName(), inetAddress.getHostAddress()));
                        availableAddresses.add(inetAddress);
                    }
                }
            }

            for (InetAddress inetAddress : availableAddresses)
            {
                if (inetAddress.getHostAddress().startsWith("192.168.") && !inetAddress.getHostAddress().endsWith(".1"))
                {
                    LOG.info("Found listening address {}", inetAddress.getHostAddress());
                    return inetAddress;
                }
            }

            return InetAddress.getLocalHost();
        }
        catch (IOException sEx)
        {
            LOG.error("Unable to retrieve the listening address of the server!", sEx);
        }

        return null;
    }

    public static String getResponsePropertiesSuccess(String postData, String uri) throws IOException
    {
        // Construct the response message
        String retval = successNodeValue(postData, uri);
        return String.format("[ %s ]", retval.substring(0, retval.length()-1));
    }

    @SuppressWarnings("unchecked")
    private static String successNodeValue(String postData, String uri) throws IOException
    {
        Map<String, Object> dataMap = Serializer.SerializeJson(postData, Map.class);
        StringBuilder sb = new StringBuilder();
        for (String key : dataMap.keySet())
        {
            if (dataMap.get(key) instanceof List)
            {
                ArrayList<String> value = (ArrayList) dataMap.get(key);
                String val = "";
                for (Object tmp : value)
                {
                    if (tmp instanceof String)
                    {
                        val += String.format("\"%s\",", tmp);
                    }
                    else
                    {
                        val += String.format("%s,", tmp);
                    }
                }

                if (val.length() > 0)
                {
                    sb.append(String.format("{\"success\": {\"%s/%s\" : [%s] }},", uri, key, val.substring(0, val.length() - 1)));
                }
                else
                {
                    sb.append(String.format("{\"success\": {\"%s/%s\" : [%s] }},", uri, key, val));
                }
            }
            else if (dataMap.get(key) instanceof Map)
            {
                String tmp = Serializer.SerializeJson(dataMap.get(key));
                sb.append(successNodeValue(tmp, uri + "/appdata"));
            }
            else if (dataMap.get(key) instanceof String)
            {
                sb.append(String.format("{\"success\": {\"%s/%s\" : \"%s\" }},", uri, key, dataMap.get(key)));
            }
            else
            {
                sb.append(String.format("{\"success\": {\"%s/%s\" : %s }},", uri, key, dataMap.get(key)));
            }
        }

        return sb.toString();
    }

    public static String getResponseAttributesSuccess(String postData, String uri) throws IOException
    {
        String retval = successAddressValue(postData, uri);
        return String.format("[ %s ]", retval.substring(0, retval.length() - 1));
    }

    @SuppressWarnings("unchecked")
    private static String successAddressValue(String postData, String uri) throws IOException
    {
        Map<String, Object> dataMap = Serializer.SerializeJson(postData, Map.class);
        StringBuilder sb = new StringBuilder();
        for (String key : dataMap.keySet())
        {
            sb.append("{\"success\": ");

            if (dataMap.get(key) instanceof ArrayList)
            {
                ArrayList<String> value = (ArrayList)dataMap.get(key);
                String val = "";
                for (Object tmp : value)
                {
                    if (tmp instanceof String)
                    {
                        val += String.format("\"%s\",", tmp);
                    }
                    else
                    {
                        val += String.format("%s,", tmp);
                    }
                }

                sb.append(String.format("{\"address\": \"%s/%s\", \"value\":[%s]},", uri, key, val.substring(0, val.length() - 1)));
            }
            else if (dataMap.get(key) instanceof Map)
            {
                String tmp = Serializer.SerializeJson(dataMap.get(key));
                sb.append(successAddressValue(tmp, uri + "/appdata"));
            }
            else if (dataMap.get(key) instanceof String)
            {
                String value = (String)dataMap.get(key);
                sb.append(String.format("{\"address\": \"%s/%s\", \"value\":\"%s\"},", uri, key, value));
            }
            else
            {
                sb.append(String.format("{\"address\": \"%s/%s\", \"value\":%s},", uri, key, dataMap.get(key)));
            }
        }
        return sb.toString();
    }

}
