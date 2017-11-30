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
@Consumes(MediaType.APPLICATION_JSON)
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
        LOG.debug(String.format("%s (%s)", request.getRequestURI(), request.getMethod()));

        try
        {
            if (username.equals("nouser"))
            {
                return Response.ok().build();
                // return Response.ok(Serializer.SerializeJson(bridge)).build();
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
        LOG.debug(String.format("%s (%s)", request.getRequestURI(), request.getMethod()));

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
                return Response.ok(Serializer.SerializeJson(bridge.getBridgeConfig())).build();
            }
            else
            {

                return Response.ok(bridge.getBridgeConfig()).build();
                /*
                -- This seemed to work
                if (username.equals("nouser"))
                {

                    String newUserToken = Randomizer.generateUserToken();
                    String retval = String.format("[{\"success\": {\"username\": \"%s\"}}]", newUserToken);
                    LOG.info(String.format("Responding: %s", retval));
                    return Response.ok(retval).build();
                }
                else
                {
                    LOG.warn(String.format("Unauthorized user requesting access! (%s)", username));
                    return getUnauthorizedUser(request.getRequestURI());
                }
                */
            }

            /*
            if (username.equals("nouser"))
            {
                String newUserToken = Randomizer.generateUserToken();
                String retval = String.format("[{\"success\": {\"username\": \"%s\"}}]", newUserToken);
                LOG.info(String.format("Responding: %s", retval));
                return Response.ok(retval).build();
            }
            else
            {
                // TODO: Check authentication
                return Response.ok(bridge.getBridgeConfig()).build();
            }
            */
        }
        catch (Exception ex) // (JsonProcessingException jEx)
        {
            LOG.error("Unable to serialize the object", ex);
            return Response.status(400).entity(ex).build();
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
            Map<String, String> dataMap = Serializer.SerializeJson(postData, Map.class);
            StringBuilder retval = new StringBuilder();
            for (String key : dataMap.keySet())
            {
                retval.append(String.format("\"/config/%s\" : \"%s\"", key.toLowerCase(), dataMap.get(key)));
            }

            return Response.ok(String.format("[{\"success\" : { %s }}]", retval.toString())).build();
        }
        catch (IOException iEx)
        {
            LOG.error("Unable to read POST data!", iEx);
            return Response.serverError().entity(iEx).build();
        }
    }

    @POST
    @Path("/config")
    public Response newConfig(@Context HttpServletRequest request)
    {
        LOG.debug(String.format("%s (%s)", request.getRequestURI(), request.getMethod()));

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
    @Path("/{default: .*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response unmapped(@Context HttpServletRequest request)
    {
        if (!request.getRequestURI().endsWith("favicon.ico"))
        {
            LOG.warn(String.format("Unmapped request received [%s]: %s (%s)", request.getRemoteHost(), request.getRequestURL(), request.getMethod()));

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
