package net.nyllian.vhue.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Nyllian on 21/11/2017.
 *
 */
public class SSDPServer implements Runnable
{
    private static final Logger LOG = LoggerFactory.getLogger(SSDPServer.class);
    private static final String mSearch = "M-SEARCH * HTTP/1.1";

    private AtomicBoolean sendSsdp = new AtomicBoolean(true);
    private Map<String, Object> resourceMap;

    public SSDPServer(Map<String, Object> resourceMap)
    {
        this.resourceMap = resourceMap;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void run()
    {
        while (sendSsdp.get())
        {
            AtomicBoolean ssdpMatch = new AtomicBoolean(false);
            try
            {
                int ssdpPort = 1900;
                InetAddress ssdpAddress = InetAddress.getByName("239.255.255.250");
                MulticastSocket ssdpSocket = new MulticastSocket(ssdpPort);
                ssdpSocket.joinGroup(ssdpAddress);
                LOG.debug("SSDP Protocol service is running!");
                LOG.trace(String.format("Listening for SSDP messages on %s:%2d", Inet4Address.getLocalHost().getHostAddress(), ssdpPort));
                while (!ssdpMatch.get())
                {
                    // Retrieve the request package
                    byte[] request = new byte[1024];
                    DatagramPacket requestPacket = new DatagramPacket(request, request.length);
                    ssdpSocket.receive(requestPacket);
                    InetAddress responseAddress = requestPacket.getAddress();
                    String requestData = new String(requestPacket.getData());

                    // LOG.trace(String.format("SSDP packet received from %s!\n", responseAddress.getHostAddress()) + requestData);
                    ssdpMatch.set(requestData.regionMatches(0, mSearch, 0, mSearch.length()));

                    // Send the response
                    if (ssdpMatch.get() && requestData.contains("\"ssdp:discover\"") && (!Inet4Address.getLocalHost().getHostAddress().equals(responseAddress.getHostAddress())))
                    {
                        LOG.debug("host: " + Inet4Address.getLocalHost().getHostAddress() + " -- client: " + responseAddress.getHostAddress());
                        Map<String, String> tplMap = (Map<String, String>) resourceMap.get("tplMap");

                        byte[] responseData = ("NOTIFY * HTTP/1.1\r\n" +
                                "HOST: 239.255.255.250:1900\r\n" +
                                "CACHE-CONTROL: max-age=100\r\n" +
                                String.format("LOCATION: %s/description.xml", tplMap.get("URLBase")) + "\r\n" +
                                "SERVER: Linux/3.14.0 UPnP/1.0 IpBridge/1.20.0\r\n" +
                                "NTS: ssdp:alive\r\n" +
                                String.format("HUE-BRIDGEID: %s", tplMap.get("bridgeId")) + "\r\n" +
                                String.format("ST: uuid:%s", tplMap.get("UDN"))
                        ).getBytes();

                        DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length, responseAddress, ssdpPort);
                        LOG.trace(String.format("Responding to SSDP request from %s!\n", responseAddress.getHostAddress()) + new String(responseData));
                        ssdpSocket.send(responsePacket);

                        ssdpMatch.set(false);
                    }


                    /*
Message from client (Chrome!!!)
--------------------------------
                    M-SEARCH * HTTP/1.1
                    HOST: 239.255.255.250:1900
                    MAN: "ssdp:discover"
                    MX: 1
                    ST: urn:dial-multiscreen-org:service:dial:1
                    USER-AGENT: Google Chrome/62.0.3202.94 Windows

--------------------------------
Message to client (3 replies???)
--------------------------------
                byte[] sendData = (
                    "NOTIFY * HTTP/1.1\r\n" +
                    "HOST: 239.255.255.250:1900\r\n" +
                    "CACHE-CONTROL: max-age=100\r\n" +
                    "LOCATION: http://192.168.43.96:80/description.xml\r\n" +
                    "SERVER: Linux/3.14.0 UPnP/1.0 IpBridge/1.20.0\r\n" +
                    "NTS: ssdp:alive\r\n" +
                    "hue-bridgeid: E4A471FFFE09B036").getBytes();

********************************
                    hue-bridgeid: E4A471FFFE09B036
                    ST: upnp:rootdevice
                    USN: uuid:2f402f80-da50-11e1-9b23-e4a47109b036::upnp:rootdevice

                    ST: upnp:rootdevice
                    ST: urn:schemas-upnp-org:device:basic:1
                    ST: uuid:2f402f80-da50-11e1-9b23-e4a47109b036


                     */


                }


/*
                // byte[] sendData = ("M-SEARCH * HTTP/1.1\nHost: 239.255.255.250:1900\nMan: \"ssdp:discover\"\nST: roku:ecp\n").getBytes();
                byte[] sendData = (
                    "NOTIFY * HTTP/1.1\r\n" +
                    "HOST: 239.255.255.250:1900\r\n" +
                    "CACHE-CONTROL: max-age=100\r\n" +
                    "LOCATION: http://192.168.43.96:80/description.xml\r\n" +
                    "SERVER: Linux/3.14.0 UPnP/1.0 IpBridge/1.20.0\r\n" +
                    "NTS: ssdp:alive\r\n" +
                    "hue-bridgeid: E4A471FFFE09B036").getBytes();

                byte[] receiveData = new byte[1024];

                // String mSearch = "M-SEARCH * HTTP/1.1\nHost: 239.255.255.250:1900\nMan: \"ssdp:discover\"\nST: roku:ecp\n";
                // sendData = mSearch.getBytes();

                // Broadcast the M-SEARCH packet
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("239.255.255.250"), 1900);
                // DatagramSocket clientSocket = new DatagramSocket();
                MulticastSocket ssdpSocket = new MulticastSocket(1900);
                clientSocket.setSoTimeout(5000);
                clientSocket.setBroadcast(true);

                clientSocket.setTrafficClass(TrafficClass);
                LOG.trace("Sending SSDP M-SEARCH packet...");
                clientSocket.send(sendPacket);

                // Receive the replies from the M-SEARCH
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);
                LOG.trace("SSDP M-SEARCH packet received!");

                String response = new String(receiveData);
                // String response = new String(receivePacket.getData());
                LOG.debug("Received response: " + response);
*/
            }
            catch (SocketTimeoutException stEx)
            {
                LOG.trace("SocketTimeout occurred! Restarting...");
            }
            catch (UnknownHostException uhEx)
            {
                LOG.error("Unknown broadcast address specified!", uhEx);
            }
            catch (IOException ioEx)
            {
                LOG.error("Unable to broadcast SSDP Protocol!", ioEx);
            }
            catch (Exception ex)
            {
                LOG.error("Unhandled exception occurred!", ex);
                LOG.warn("Restarting SSDPServer...");
            }

            /*
            try
            {
                Thread.sleep(5000);
            }
            catch (InterruptedException iEx)
            {
                LOG.error("SSDP Thread could not be halted!", iEx);
            }
            */
        }
    }

    public void stopSsdpService()
    {
        sendSsdp.set(false);
    }

    /*
start ssdp broadcast
------------ MESSAGE -------------

NOTIFY * HTTP/1.1
HOST: 239.255.255.250:1900
  CACHE-CONTROL: max-age=100
  LOCATION: http://192.168.43.96:80/description.xml
  SERVER: Linux/3.14.0 UPnP/1.0 IpBridge/1.20.0
  NTS: ssdp:alive
  hue-bridgeid: E4A471FFFE09B036

------------ MESSAGE -------------
--------- CUSTOM MESSAGE ----------
{0: {'usn': 'uuid:2f402f80-da50-11e1-9b23-e4a47109b036::upnp:rootdevice', 'nt': 'upnp:rootdevice'}, 1: {'usn': 'uuid:2f402f80-da50-11e1Starting httpd...-9b23-e4a4
7109b036', 'nt': 'uuid:2f402f80-da50-11e1-9b23-e4a47109b036'}, 2: {'usn': 'uuid:2f402f80-da50-11e1-9b23-e4a47109b036', 'nt': 'urn:schemas-upnp-org:device:basic:1'}}
--------- CUSTOM MESSAGE ----------
     */
}
