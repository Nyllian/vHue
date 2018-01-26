package net.nyllian.vhue.webservice;

import net.nyllian.vhue.model.Bridge;
import net.nyllian.vhue.model.Capabilities;
import net.nyllian.vhue.server.util.ResourceManager;
import net.nyllian.vhue.util.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Nyllian on 30/11/2017.
 *
 */
@Path("/api/{username}/capabilities")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CapabilityResource
{
    private final Logger LOG = LoggerFactory.getLogger(CapabilityResource.class);

    private Bridge bridge;
    private Capabilities capabilities;

    @PathParam("username")
    private String username;

    public CapabilityResource(@Context Application application)
    {
        ResourceManager manager = (ResourceManager)application.getProperties().get(ResourceManager.MANAGER);
        bridge = manager.getBridge();
        capabilities = manager.getCapabilities();
    }

    @GET
    public Response getCapabilities(@Context HttpServletRequest request)
    {
        LOG.debug(String.format("%s (%s)", request.getRequestURI(), request.getMethod()));

        try
        {
            return Response.ok(
                    Serializer.SerializeJson(capabilities)
            ).build();
        }
        catch (Exception ex)
        {
            LOG.error("Unable to serialize the object", ex);
            return Response.status(Response.Status.SEE_OTHER).entity(ex).build();
        }
    }

    @GET
    @Path("/lights")
    public Response getAvailableLights(@Context HttpServletRequest request)
    {
        LOG.debug(String.format("%s (%s)", request.getRequestURI(), request.getMethod()));

        try
        {
            return Response.ok(
                    Serializer.SerializeJson(capabilities.getAvailableLights())
            ).build();
        }
        catch (Exception ex)
        {
            LOG.error("Unable to serialize the object", ex);
            return Response.status(Response.Status.SEE_OTHER).entity(ex).build();
        }
    }

    @GET
    @Path("/timezones")
    public Response getTimezones(@Context HttpServletRequest request)
    {
        LOG.debug(String.format("%s (%s)", request.getRequestURI(), request.getMethod()));

        try
        {
            return Response.ok(
                    Serializer.SerializeJson(capabilities.getTimezones())
            ).build();
        }
        catch (Exception ex)
        {
            LOG.error("Unable to serialize the object", ex);
            return Response.status(Response.Status.SEE_OTHER).entity(ex).build();
        }
    }
}
