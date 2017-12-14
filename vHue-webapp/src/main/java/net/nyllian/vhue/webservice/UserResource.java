package net.nyllian.vhue.webservice;

import net.nyllian.vhue.model.Bridge;
import net.nyllian.vhue.model.BridgeConfig;
import net.nyllian.vhue.model.IJSon;
import net.nyllian.vhue.model.UnauthorizedUser;
import net.nyllian.vhue.util.HueUtils;
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
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource
{
    private final Logger LOG = LoggerFactory.getLogger(UserResource.class);

    private Bridge bridge;

    @PathParam("user")
    private String username;

    public UserResource(@Context Application application)
    {
        ResourceManager manager = (ResourceManager)application.getProperties().get("manager");
        bridge = manager.getBridge();
    }

    @GET
    @SuppressWarnings("unchecked")
    public Response getUsername(@Context HttpServletRequest request)
    {
        LOG.debug(String.format("%s (%s)", request.getRequestURI(), request.getMethod()));

        try
        {
            if (username.equals("nouser"))
            {
                return Response.ok(
                        Serializer.SerializeJson(bridge.getBridgeConfig())
                ).build();
            }
            else
            {
                return Response.ok(
                        Serializer.SerializeJson(bridge)
                ).build();
            }
        }
        catch (Exception ex)
        {
            LOG.error("Unable to serialize the object", ex);
            return Response.status(Response.Status.SEE_OTHER).entity(ex).build();
        }
    }

    @GET
    @Path("/config")
    @SuppressWarnings("unchecked")
    public Response getUserConfig(@Context HttpServletRequest request)
    {
        LOG.debug(String.format("%s (%s)", request.getRequestURI(), request.getMethod()));

        try
        {
            if (bridge.getBridgeConfig().getWhiteList().containsKey(username))
            {
                return Response.ok(
                        Serializer.SerializeJson(bridge.getBridgeConfig())
                ).build();
            }
            else
            {
                return Response.ok(
                        Serializer.SerializeJson(bridge.getBridgeConfig())
                ).build();
            }
        }
        catch (Exception ex)
        {
            LOG.error("Unable to serialize the object", ex);
            return Response.status(Response.Status.SEE_OTHER).entity(ex).build();
        }
    }

    @PUT
    @Path("/config")
    @SuppressWarnings("unchecked")
    public Response editConfig(@Context HttpServletRequest request)
    {
        LOG.debug(String.format("%s (%s)", request.getRequestURI(), request.getMethod()));

        try
        {
            String postData = IOUtils.toString(request.getInputStream(), Charset.forName("UTF-8"));
            // Update the current bridge config
            BridgeConfig currentConfig = bridge.getBridgeConfig();
            BridgeConfig newConfig = Serializer.UpdateObject(currentConfig, postData);
            bridge.setBridgeConfig(newConfig);
            bridge.writeConfig();

            // Construct the response message
            // TODO: Check if this can be done with the HueUtils
            Map<String, String> dataMap = Serializer.SerializeJson(postData, Map.class);
            StringBuilder retVal = new StringBuilder();
            for (String key : dataMap.keySet())
            {
                retVal.append(String.format("\"/config/%s\" : \"%s\"", key.toLowerCase(), dataMap.get(key)));
            }

            return Response.ok(String.format("[{\"success\" : { %s }}]", retVal.toString())).build();
        }
        catch (IOException iEx)
        {
            LOG.error("Unable to read POST data!", iEx);
            return Response.status(Response.Status.SEE_OTHER).entity(iEx).build();
        }
    }

    @DELETE
    @Path("/config/whitelist/{delUser}")
    public Response deleteUser(@Context HttpServletRequest request, @PathParam("delUser") String delUser)
    {
        LOG.debug(String.format("%s (%s)", request.getRequestURI(), request.getMethod()));

        bridge.getBridgeConfig().getWhiteList().remove(delUser);
        String retVal = String.format("/config/whitelist/%s", delUser);
        return Response.ok(String.format("[{\"success\" : %s }]", retVal)).build();
    }

    @GET
    @Path("/{default: .*}")
    public Response unmapped(@Context HttpServletRequest request)
    {
        if (!request.getRequestURI().endsWith("favicon.ico"))
        {
            LOG.warn(String.format("Unmapped request received [%s]: %s (%s)", request.getRemoteHost(), request.getRequestURL(), request.getMethod()));

            return getUnauthorizedUser(request.getRequestURI());
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    private Response getUnauthorizedUser(String uri)
    {
        Map<String, IJSon> retMap = new HashMap<>();
        return Response.ok(retMap.put("error", new UnauthorizedUser(uri))).build();
    }
}
