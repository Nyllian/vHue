package net.nyllian.vhue.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by Nyllian on 29/11/2017.
 *
 */
public class HueUtils
{
    private static final Logger LOG = LoggerFactory.getLogger(HueUtils.class);

    public final static String HUB_VERSION = "1709131301";
    public final static String API_VERSION = "1.19.0";
    public final static String MODEL_ID = "BSB002";

    public static InetAddress getListeningAddress()
    {
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
                    if (inetAddress instanceof Inet4Address)
                    {
                        LOG.debug(String.format("Interface %s has address %s", iFace.getName(), inetAddress.getHostAddress()));

                        if (inetAddress.getHostAddress().startsWith("192.168"))
                        {
                            LOG.info("Found listening address {}", inetAddress.getHostAddress());
                            return inetAddress;
                        }
                    }
                }
            }
        }
        catch (SocketException sEx)
        {
            LOG.error("Unable to retrieve the listening address of the server!", sEx);
        }

        return null;
    }
}
