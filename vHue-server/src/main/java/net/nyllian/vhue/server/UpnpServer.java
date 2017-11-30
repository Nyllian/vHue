package net.nyllian.vhue.server;

import net.nyllian.vhue.util.UpnpConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Nyllian on 29/11/2017.
 * 
 */
public class UpnpServer implements Runnable
{
    private static final Logger LOG = LoggerFactory.getLogger(UpnpServer.class);

    private Map<String, Object> resourceMap;
    private MulticastSocket broadcastSocket;
    private InetSocketAddress broadcastSocketAddress;

    private AtomicBoolean active;

    public UpnpServer(Map<String, Object> resourceMap)
    {
        this.resourceMap = resourceMap;
        this.active = new AtomicBoolean(true);

        initialize();
    }

    public void initialize()
    {
        try
        {
            // Initialize socket for broadcast
            broadcastSocket = new MulticastSocket(UpnpConstants.BROADCAST_PORT);
            broadcastSocket.setSoTimeout(UpnpConstants.UPNP_NOTIFY_TIMEOUT);
            broadcastSocketAddress = new InetSocketAddress(UpnpConstants.BROADCAST_ADDR, UpnpConstants.BROADCAST_PORT);

            Enumeration<NetworkInterface> iFaces = NetworkInterface.getNetworkInterfaces();
            // Add network interfaces to the broadcast socket
            while (iFaces.hasMoreElements())
            {
                NetworkInterface iFace = iFaces.nextElement();
                Enumeration<InetAddress> iNetAddresses = iFace.getInetAddresses();
                String iNetName = iFace.getName();

                while (iNetAddresses.hasMoreElements())
                {
                    InetAddress iNetAddress = iNetAddresses.nextElement();

                    // Only ipv4 and not on loopback interface
                    if (iNetAddress instanceof Inet4Address && !iNetName.equalsIgnoreCase("lo"))
                    {
                        LOG.trace(String.format("Interface %s has address %s", iNetName, iNetAddress));
                        try
                        {
                            broadcastSocket.joinGroup(broadcastSocketAddress, iFace);
                            LOG.info(String.format("Added interface %s to broadcast group", iNetName));
                        }
                        catch (IOException ioEx)
                        {
                            LOG.warn(String.format("unable to add interface %s to broadcast group!", iNetName), ioEx);
                        }
                    }
                }
            }
        }
        catch (SocketException sEx)
        {
            LOG.error("Unknown exception occurred during upnpServer setup!", sEx);
        }
        catch (IOException ioEx)
        {
            LOG.error("Could not start the upnpServer...", ioEx);
        }
    }

    @Override
    public void run()
    {
        // Start broadcast loop
        Instant currentBroadcast, previousBroadcast;
        previousBroadcast = Instant.now();
        while (active.get())
        {
            byte[] packetBuf = new byte[1024];
            DatagramPacket receivedDataPacket = new DatagramPacket(packetBuf, packetBuf.length);
            try
            {
                broadcastSocket.receive(receivedDataPacket);
                if (isSSDPDiscovery(receivedDataPacket))
                {
                    try
                    {
                        sendUpnpResponse(receivedDataPacket.getAddress(), receivedDataPacket.getPort());
                    }
                    catch (IOException ioEx)
                    {
                        LOG.warn("Unable to respond to the upnp discovery request.", ioEx);
                    }
                }

                currentBroadcast = Instant.now();
                if (ChronoUnit.MILLIS.between(previousBroadcast, currentBroadcast) > UpnpConstants.UPNP_NOTIFY_TIMEOUT)
                {
                    sendUpnpNotify(broadcastSocketAddress.getAddress());
                    previousBroadcast = Instant.now();
                }
            }
            catch (SocketTimeoutException stEx)
            {
                // send a notify message
                LOG.info("BroadcastSocket entered in TimeOut. Sending notification message!");
                sendUpnpNotify(broadcastSocketAddress.getAddress());
            }
            catch (IOException ioEx)
            {
                LOG.error("UpnpServer encountered an error while reading socket.", ioEx);
            }
        }

        // Disconnect if bound
        if (!(broadcastSocket.isBound()) && !(broadcastSocket.isClosed()))
        {
            broadcastSocket.disconnect();
        }

        // Close id not already
        if (!broadcastSocket.isClosed())
        {
            broadcastSocket.close();
        }
    }

    private boolean isSSDPDiscovery(DatagramPacket packet)
    {
        String data = new String(packet.getData(), 0, packet.getLength());
        if ((data.length() > 0) && (data.startsWith(UpnpConstants.MSEARCH) && (data.contains(UpnpConstants.SSDP_DISCOVER))))
        {
            LOG.info("SSDP M-SEARCH packet received from {}:{}", packet.getAddress().getHostAddress(), packet.getPort());
            return true;
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    private void sendUpnpNotify(InetAddress socketAddress)
    {
        Map<String, String> tplMap = (Map<String, String>) resourceMap.get("tplMap");
        String notifyData = String.format(UpnpConstants.NOTIFY_TEMPLATE, UpnpConstants.BROADCAST_ADDR, UpnpConstants.BROADCAST_PORT,
                tplMap.get("URLBase"), tplMap.get("bridgeId"), tplMap.get("UDN"), tplMap.get("UDN")
        );

        LOG.trace("sendUpnpNotify:\n{}", notifyData);
        DatagramPacket notifyPacket = new DatagramPacket(notifyData.getBytes(), notifyData.length(), socketAddress, UpnpConstants.BROADCAST_PORT);
        try
        {
            broadcastSocket.send(notifyPacket);
        }
        catch (IOException ioEx)
        {
            LOG.warn("Failed to send an upnp notification packet.", ioEx);
        }

    }

    @SuppressWarnings("unchecked")
    private void sendUpnpResponse(InetAddress socketAddress, int port) throws IOException
    {
        Map<String, String> tplMap = (Map<String, String>) resourceMap.get("tplMap");

        // Send response 1
        String responseData = String.format(UpnpConstants.RESPONSE_TEMPLATE1, UpnpConstants.BROADCAST_ADDR, UpnpConstants.BROADCAST_PORT,
                tplMap.get("URLBase"), tplMap.get("bridgeId"), tplMap.get("UDN")
        );
        LOG.trace("sendUpnpResponse (1):\n{}", responseData);
        broadcastSocket.send(new DatagramPacket(responseData.getBytes(), responseData.length(), socketAddress, port));

        // Send response 2
        responseData = String.format(UpnpConstants.RESPONSE_TEMPLATE2, UpnpConstants.BROADCAST_ADDR, UpnpConstants.BROADCAST_PORT,
                tplMap.get("URLBase"), tplMap.get("bridgeId"), tplMap.get("UDN"), tplMap.get("UDN")
        );
        LOG.trace("sendUpnpResponse (2):\n{}", responseData);
        broadcastSocket.send(new DatagramPacket(responseData.getBytes(), responseData.length(), socketAddress, port));

        // Send response3
        responseData = String.format(UpnpConstants.RESPONSE_TEMPLATE3, UpnpConstants.BROADCAST_ADDR, UpnpConstants.BROADCAST_PORT,
                tplMap.get("URLBase"), tplMap.get("bridgeId"), tplMap.get("UDN")
        );
        LOG.trace("sendUpnpResponse (3):\n{}", responseData);
        broadcastSocket.send(new DatagramPacket(responseData.getBytes(), responseData.length(), socketAddress, port));
    }

    public void stopServer()
    {
        active.set(false);
    }


/*


    protected void sendUpnpResponse(InetAddress requester, int sourcePort) throws IOException {
        String discoveryResponse = null;
        discoveryResponse = String.format(responseTemplate1, Configuration.UPNP_MULTICAST_ADDRESS, Configuration.UPNP_DISCOVERY_PORT, responseAddress, httpServerPort, bridgeId, bridgeSNUUID);
        if(traceupnp) {
            LOG.info("Traceupnp: send upnp discovery template 1 with response address: " + responseAddress + ":" + httpServerPort + " to address: " + requester + ":" + sourcePort);
        }
        else
            LOG.debug("sendUpnpResponse to address: " + requester + ":" + sourcePort + " with discovery responseTemplate1 is <<<" + discoveryResponse + ">>>");
        sendUDPResponse(discoveryResponse.getBytes(), requester, sourcePort);

        discoveryResponse = String.format(responseTemplate2, Configuration.UPNP_MULTICAST_ADDRESS, Configuration.UPNP_DISCOVERY_PORT, responseAddress, httpServerPort, bridgeId, bridgeSNUUID, bridgeSNUUID);
        if(traceupnp) {
            LOG.info("Traceupnp: send upnp discovery template 2 with response address: " + responseAddress + ":" + httpServerPort + " to address: " + requester + ":" + sourcePort);
        }
        else
            LOG.debug("sendUpnpResponse to address: " + requester + ":" + sourcePort + " discovery responseTemplate2 is <<<" + discoveryResponse + ">>>");
        sendUDPResponse(discoveryResponse.getBytes(), requester, sourcePort);

        discoveryResponse = String.format(responseTemplate3, Configuration.UPNP_MULTICAST_ADDRESS, Configuration.UPNP_DISCOVERY_PORT, responseAddress, httpServerPort, bridgeId, bridgeSNUUID);
        if(traceupnp) {
            LOG.info("Traceupnp: send upnp discovery template 3 with response address: " + responseAddress + ":" + httpServerPort + " to address: " + requester + ":" + sourcePort);
        }
        else
            LOG.debug("sendUpnpResponse to address: " + requester + ":" + sourcePort + " discovery responseTemplate3 is <<<" + discoveryResponse + ">>>");
        sendUDPResponse(discoveryResponse.getBytes(), requester, sourcePort);
    }

    private void sendUDPResponse(byte[] udpMessage, InetAddress requester, int sourcePort) throws IOException {
        LOG.debug("Sending response string: <<<" + new String(udpMessage) + ">>>");
        if(upnpMulticastSocket == null)
            throw new IOException("Socket not initialized");
        DatagramPacket response = new DatagramPacket(udpMessage, udpMessage.length, requester, sourcePort);
        upnpMulticastSocket.send(response);
    }
 */



    /*
    public UpnpServer(BridgeSettingsDescriptor theSettings, BridgeControlDescriptor theControl, UDPDatagramSender aUdpDatagramSender) throws IOException
    {
        super();
        upnpMulticastSocket = null;
        httpServerPort = Integer.valueOf(theSettings.getServerPort());
        responseAddress = theSettings.getUpnpConfigAddress();
        strict = theSettings.isUpnpStrict();
        traceupnp = theSettings.isTraceupnp();
        useUpnpIface = theSettings.isUseupnpiface();
        bridgeControl = theControl;
        aHueConfig = HuePublicConfig.createConfig("temp", responseAddress, HueConstants.HUB_VERSION, theSettings.getHubmac());
        bridgeId = aHueConfig.getBridgeid();
        bridgeSNUUID = aHueConfig.getSNUUIDFromMac();
        try {
            upnpMulticastSocket  = new MulticastSocket(Configuration.UPNP_DISCOVERY_PORT);
        } catch(IOException e){
            LOG.error("Upnp Discovery Port is in use, or restricted by admin (try running with sudo or admin privs): " + Configuration.UPNP_DISCOVERY_PORT + " with message: " + e.getMessage());
            throw(e);
        }

    }

    public boolean startListening(){
// TODO: Next here

        upnpMulticastSocket.close();
        if (bridgeControl.isReinit())
            LOG.info("UPNP Discovery Listener - ended, restart found");
        if (bridgeControl.isStop())
            LOG.info("UPNP Discovery Listener - ended, stop found");
        if (!bridgeControl.isStop() && !bridgeControl.isReinit()) {
            LOG.info("UPNP Discovery Listener - ended, error found");
            return false;
        }
        return bridgeControl.isReinit();
    }
*/

    /**
     * ssdp discovery packet detection
     */
/*
    protected boolean isSSDPDiscovery(DatagramPacket packet){
        //Only respond to discover request for strict upnp form
        String packetString = new String(packet.getData(), 0, packet.getLength());
        if(packetString != null && packetString.startsWith("M-SEARCH * HTTP/1.1") && packetString.contains("\"ssdp:discover\"")){
            if(strict && (packetString.contains("ST: urn:schemas-upnp-org:device:basic:1") || packetString.contains("ST: upnp:rootdevice") || packetString.contains("ST: ssdp:all")))
            {
                if(traceupnp) {
                    LOG.info("Traceupnp: SSDP M-SEARCH packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort());
                }
                else
                    LOG.debug("SSDP M-SEARCH packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + ", body: <<<" + packetString + ">>>");
                return true;
            }
            else if (!strict)
            {
                if(traceupnp) {
                    LOG.info("Traceupnp: SSDP M-SEARCH packet (!strict) from " + packet.getAddress().getHostAddress() + ":" + packet.getPort());
                }
                else
                    LOG.debug("SSDP M-SEARCH packet (!strict) from " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + ", body: <<<" + packetString + ">>>");
                return true;
            }
        }
        else {
//			LOG.debug("isSSDPDiscovery found message to not be valid - strict: " + strict);
//			LOG.debug("SSDP packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + ", body: " + packetString);
        }
        return false;
    }

*/
}
