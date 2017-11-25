package net.nyllian.vhue.webservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.nyllian.vhue.model.Bridge;
import net.nyllian.vhue.model.Device;
import net.nyllian.vhue.model.DeviceConfig;
import net.nyllian.vhue.model.Light;
import net.nyllian.vhue.util.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Nyllian on 22/11/2017.
 *
 */
@Path("/api/{user}")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource
{
    private final Logger LOG = LoggerFactory.getLogger(UserResource.class);

    @Context
    private Application application;

    @PathParam("user")
    private String username;

    @GET
    @SuppressWarnings("unchecked")
    public Response getUsername(@Context HttpServletRequest request)
    {
        LOG.trace(request.getRequestURI());

        try
        {
            // String retval = "{}";
            if (username.equals("nouser"))
            {
                // Whitelist the user
                return newDevice();
            }
            else
            {
                return existingDevice();
            }
        }
        catch (UnknownHostException | SocketException uEx)
        {
            LOG.error("Unable to process the request!", uEx);
            return Response.status(400).entity(uEx).build();
        }
        catch (JsonProcessingException jEx)
        {
            LOG.error("Unable to serialize the object", jEx);
            return Response.status(400).entity(jEx).build();
        }
    }

    @GET
    @Path("/config")
    @SuppressWarnings("unchecked")
    public Response getUserConfig(@Context HttpServletRequest request)
    {
        LOG.trace(request.getRequestURI());

        try
        {
            if (username.equals("nouser"))
            {
                return newDevice();
            }
            else
            {
                // Check authentication
                String retval = "{\"proxyport\": 0, \"UTC\": \"2017-11-22T21:53:40\", \"factorynew\": false, \"swupdate\": {\"checkforupdate\": true}, \"whitelist\": {\"a7161538be80d40b3de98dece6e91f904dc96170\": {\"last use date\": \"2017-11-22T19:30:34\", \"create date\": \"2017-11-22T19:30:34\", \"name\": \"Hue 2#Samsung SM-G925F\"}, \"243b9a9757506e2e54dfbc10fc0e699f6f3d128a\": {\"last use date\": \"2017-11-11T11:14:35\", \"create date\": \"2017-11-11T11:14:35\", \"name\": \"55PUS6432/12\"}}, \"apiversion\": \"1.19.0\", \"zigbeechannel\": 15, \"linkbutton\": true, \"bridgeid\": \"AF9634FFFE4E5EF0\", \"timezone\": \"Europe/Brussels\", \"ipaddress\": \"192.168.0.129\", \"gateway\": \"192.168.0.129\", \"modelid\": \"BSB002\", \"portalservices\": true, \"name\": \"VM-Hue\", \"swversion\": \"1709131301\", \"proxyaddress\": \"none\", \"netmask\": \"255.255.255.0\", \"mac\": \"af:96:34:4e:5e:f0\", \"datastoreversion\": 59, \"dhcp\": false, \"localtime\": \"2017-11-22T22:53:40\"}";

                return Response.ok(retval).build();
            }
        }
        catch (UnknownHostException | SocketException uEx)
        {
            LOG.error("Unable to process the request!", uEx);
            return Response.status(400).entity(uEx).build();
        }
        catch (JsonProcessingException jEx)
        {
            LOG.error("Unable to serialize the object", jEx);
            return Response.status(400).entity(jEx).build();
        }
    }

    @GET
    @Path("/lights")
    public Response getLights(@Context HttpServletRequest request)
    {
        /* -- TODO
        {"1": {"name": "MiLight rgbw 123456789", "swversion": "66009461", "state": {"on": false, "xy": [0.0, 0.0], "alert": "none", "reachable": true, "bri": 200, "hue": 0, "colormode": "ct", "ct": 461, "effect": "none", "sat": 0}, "uniqueid": "1a2b3c457", "type": "Extended color light", "modelid": "LCT001"}}
         */

        return Response.ok("{ }").build();
    }

    @GET
    @Path("/groups")
    public Response getGroups(@Context HttpServletRequest request)
    {
        // groups

        return Response.ok("{ }").build();
    }

    @GET
    @Path("/sensors")
    public Response getSensors(@Context HttpServletRequest request)
    {
        return Response.ok("{ }").build();
    }

    @GET
    @Path("/{default: .*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response unmapped(@Context HttpServletRequest request)
    {
        if (!request.getRequestURI().endsWith("favicon.ico"))
        {
            LOG.warn(String.format("Unmapped request received (%1s): %2s", request.getRemoteHost(), request.getRequestURL()));
        }

        return Response.status(404).build();
    }

    public Response existingDevice() throws UnknownHostException, JsonProcessingException
    {
        Device device = new DeviceConfig()
                .setIpAddress(Inet4Address.getLocalHost().getHostAddress())
                .setGateway(Inet4Address.getLocalHost().getHostAddress())
                .setNetmask("255.255.255.0")
                .setProxyAddress("none")
                .setProxyPort(0)
                .setDhcp(true)
                .setLinkButton(true)
                .setPortalServices(true)
                .setZigbeeChannel(15)
                .setUtc(new Date())
                .setLocaltime(new Date())
                .setTimezone("Brussels/Europe")
                .setSwitchUpdate(false)
                .addToWhitelist("a7161538be80d40b3de98dece6e91f904dc96170", "vHue client") // TODO: These details should come from the client
                // Device Properties
                .setDatastoreVersion(1)
                .setMacAddress("12:34:56:78:90:AB")
                .setName("vHue")
                .setId("1234657890123456")
                .setSwitchVersion("0000000001")
                .setFactoryNew(false)
                .setApiVersion("0.1.0")
                .setModelId("vHUE-001");

        DeviceConfig deviceConfig = (DeviceConfig) device;
        Bridge bridge = new Bridge()
                .setDeviceConfig(deviceConfig);

        bridge.getLights().put("1", new Light());

        // lights?
        // groups?
        // schedules?
        // rules?
        // resourceLinks?
        // sensors?
        // Config....
        // Scenes?

        return Response.ok(Serializer.SerializeJson(bridge)).build();
    }

    @SuppressWarnings("unchecked")
    public Response newDevice() throws UnknownHostException, SocketException, JsonProcessingException
    {
        Map<String, String> tplMap = (Map<String, String>)application.getProperties().get("tplMap");
        byte[] mac = NetworkInterface.getByInetAddress(Inet4Address.getLocalHost()).getHardwareAddress();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? ":" : ""));
        }

        /*
        Device newDevice = new Device()
                .setDatastoreVersion(1)
                .setMacAddress(sb.toString())
                .setName("vHue")
                .setId(tplMap.get("bridgeId"))
                .setSwitchVersion("0000000001")
                .setFactoryNew(false)
                .setApiVersion("0.1.0")
                .setModelId("vHUE-001");
        */


        Device newDevice = new DeviceConfig()
                .setIpAddress(Inet4Address.getLocalHost().getHostAddress())
                .setGateway(Inet4Address.getLocalHost().getHostAddress())
                .setNetmask("255.255.255.0")
                .setProxyAddress("none")
                .setProxyPort(0)
                .setDhcp(true)
                .setLinkButton(true)
                .setPortalServices(true)
                .setZigbeeChannel(15)
                .setUtc(new Date())
                .setLocaltime(new Date())
                .setTimezone("Brussels/Europe")
                .setSwitchUpdate(false)
                .addToWhitelist("a7161538be80d40b3de98dece6e91f904dc96170", "vHue client") // TODO: These details should come from the client
                // Device Properties
                .setDatastoreVersion(1)
                .setMacAddress("12:34:56:78:90:AB")
                .setName("vHue")
                .setId("1234657890123456")
                .setSwitchVersion("0000000001")
                .setFactoryNew(false)
                .setApiVersion("0.1.0")
                .setModelId("vHUE-001");

        // TODO This should be the device retrieved from the bridge
        application.getProperties();
        //Map<String, Object> newProps = new LinkedHashMap<>(application.getProperties()).
        application.getProperties().put("device", newDevice);

        return Response.ok(Serializer.SerializeJson(newDevice)).build();
    }

}
