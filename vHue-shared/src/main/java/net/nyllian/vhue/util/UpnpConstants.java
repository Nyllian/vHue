package net.nyllian.vhue.util;

/**
 * Created by Nyllian on 30/11/2017.
 *
 */
public class UpnpConstants
{
    public static final String BROADCAST_ADDR = "239.255.255.250";
    public static final int BROADCAST_PORT = 1900;
    public static final int UPNP_NOTIFY_TIMEOUT = 7000;

    public static final String MSEARCH = "M-SEARCH * HTTP/1.1";
    public static final String SSDP_DISCOVER = "\"ssdp:discover\"";
    public static final String ST_UPNP_BASIC = "ST: urn:schemas-upnp-org:device:basic:1";
    public static final String ST_UPNP_ROOT = "ST: upnp:rootdevice";
    public static final String ST_SSDP_ALL = "ST: ssdp:all";

    public static final String RESPONSE_TEMPLATE1 = "HTTP/1.1 200 OK\r\n" +
            "HOST: %s:%s\r\n" +
            "EXT:\r\n" +
            "CACHE-CONTROL: max-age=100\r\n" +
            "LOCATION: %s/description.xml\r\n" +
            "SERVER: Linux/3.14.0 UPnP/1.0 IpBridge/" + HueUtils.API_VERSION + "\r\n" +
            "hue-bridgeid: %s\r\n" +
            "ST: upnp:rootdevice\r\n" +
            "USN: uuid:%s::upnp:rootdevice\r\n\r\n";
    public static final String RESPONSE_TEMPLATE2 = "HTTP/1.1 200 OK\r\n" +
            "HOST: %s:%s\r\n" +
            "EXT:\r\n" +
            "CACHE-CONTROL: max-age=100\r\n" +
            "LOCATION: %s/description.xml\r\n" +
            "SERVER: Linux/3.14.0 UPnP/1.0 IpBridge/" + HueUtils.API_VERSION + "\r\n" +
            "hue-bridgeid: %s\r\n" +
            "ST: uuid:%s\r\n" +
            "USN: uuid:%s\r\n\r\n";
    public static final String RESPONSE_TEMPLATE3 = "HTTP/1.1 200 OK\r\n" +
            "HOST: %s:%s\r\n" +
            "EXT:\r\n" +
            "CACHE-CONTROL: max-age=100\r\n" +
            "LOCATION: %s/description.xml\r\n" +
            "SERVER: Linux/3.14.0 UPnP/1.0 IpBridge/" + HueUtils.API_VERSION + "\r\n" +
            "hue-bridgeid: %s\r\n" +
            "ST: urn:schemas-upnp-org:device:basic:1\r\n" +
            "USN: uuid:%s\r\n\r\n";

    public static final String NOTIFY_TEMPLATE = "NOTIFY * HTTP/1.1\r\n" +
            "HOST: %s:%s\r\n" +
            "CACHE-CONTROL: max-age=100\r\n" +
            "LOCATION: %s/description.xml\r\n" +
            "SERVER: Linux/3.14.0 UPnP/1.0 IpBridge/" + HueUtils.API_VERSION + "\r\n" +
            "NTS: ssdp:alive\r\n" +
            "hue-bridgeid: %s\r\n" +
            "NT: uuid:%s\r\n" +
            "USN: uuid:%s\r\n\r\n";
}
