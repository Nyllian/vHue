package net.nyllian.vhue.webservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.nyllian.vhue.model.*;
import net.nyllian.vhue.util.ResourceManager;
import net.nyllian.vhue.util.Serializer;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by Nyllian on 22/11/2017.
 *
 */
@Path("/api/{user}")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource
{
    private final Logger LOG = LoggerFactory.getLogger(UserResource.class);

    private ResourceManager manager;
    private Bridge bridge;

    @PathParam("user")
    private String username;

    public UserResource(@Context Application application)
    {
        manager = (ResourceManager)application.getProperties().get("manager");
        bridge = (Bridge) manager.getResource("bridge");
    }

    @GET
    @SuppressWarnings("unchecked")
    public Response getUsername(@Context HttpServletRequest request)
    {
        LOG.debug(String.format("%1s (%2s)", request.getRequestURI(), request.getMethod()));

        try
        {
            if (username.equals("nouser"))
            {
                return Response.ok(Serializer.SerializeJson(bridge)).build();
            }
            else
            {
                return Response.ok(Serializer.SerializeJson(bridge)).build();
            }
        }
        catch (JsonProcessingException jEx)
        {
            LOG.error("Unable to serialize the object", jEx);
            return Response.status(400).entity(jEx).build();
        }
    }

    @POST
    public Response newForm(@Context HttpServletRequest request)
    {
        LOG.debug(String.format("%1s (%2s)", request.getRequestURI(), request.getMethod()));

        try
        {
            String postData = IOUtils.toString(request.getInputStream(), Charset.forName("UTF-8"));
            LOG.warn("NewForm => " + postData);
        }
        catch (IOException iEx)
        {
            LOG.error("Unable to read POST data!", iEx);
        }

        return Response.ok().build();
    }

    @POST
    @Path("/config")
    public Response newConfig(@Context HttpServletRequest request)
    {
        LOG.debug(String.format("%1s (%2s)", request.getRequestURI(), request.getMethod()));

        try
        {
            String postData = IOUtils.toString(request.getInputStream(), Charset.forName("UTF-8"));
            LOG.warn("NewConfig => " + postData);
        }
        catch (IOException iEx)
        {
            LOG.error("Unable to read POST data!", iEx);
        }

        return Response.ok().build();
    }

    @GET
    @Path("/config")
    @SuppressWarnings("unchecked")
    public Response getUserConfig(@Context HttpServletRequest request)
    {
        LOG.debug(String.format("%1s (%2s)", request.getRequestURI(), request.getMethod()));

        try
        {
            if (username.equals("nouser"))
            {
                return getUsername(request);
            }
            else
            {
                // Check authentication
                String retval = "{\"proxyport\": 0, \"UTC\": \"2017-11-22T21:53:40\", \"factorynew\": false, \"swupdate\": {\"checkforupdate\": true}, \"whitelist\": {\"a7161538be80d40b3de98dece6e91f904dc96170\": {\"last use date\": \"2017-11-22T19:30:34\", \"create date\": \"2017-11-22T19:30:34\", \"name\": \"Hue 2#Samsung SM-G925F\"}, \"243b9a9757506e2e54dfbc10fc0e699f6f3d128a\": {\"last use date\": \"2017-11-11T11:14:35\", \"create date\": \"2017-11-11T11:14:35\", \"name\": \"55PUS6432/12\"}}, \"apiversion\": \"1.19.0\", \"zigbeechannel\": 15, \"linkbutton\": true, \"bridgeid\": \"AF9634FFFE4E5EF0\", \"timezone\": \"Europe/Brussels\", \"ipaddress\": \"192.168.0.129\", \"gateway\": \"192.168.0.129\", \"modelid\": \"BSB002\", \"portalservices\": true, \"name\": \"VM-Hue\", \"swversion\": \"1709131301\", \"proxyaddress\": \"none\", \"netmask\": \"255.255.255.0\", \"mac\": \"af:96:34:4e:5e:f0\", \"datastoreversion\": 59, \"dhcp\": false, \"localtime\": \"2017-11-22T22:53:40\"}";

                return Response.ok(bridge.getBridgeConfig()).build();
            }
        }
        catch (Exception ex) // (JsonProcessingException jEx)
        {
            LOG.error("Unable to serialize the object", ex);
            return Response.status(400).entity(ex).build();
        }
    }
    @GET
    @Path("/{default: .*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response unmapped(@Context HttpServletRequest request)
    {
        if (!request.getRequestURI().endsWith("favicon.ico"))
        {
            LOG.warn(String.format("Unmapped request received [%1s]: %2s (%3s)", request.getRemoteHost(), request.getRequestURL(), request.getMethod()));

            return getUnauthorizedUser(request.getRequestURI());
        }

        return Response.status(404).build();
    }

    private Response getUnauthorizedUser(String uri)
    {
        Map<String, IJSon> retMap = new HashMap<>();
        return Response.ok(retMap.put("error", new UnauthorizedUser(uri))).build();
    }
}
