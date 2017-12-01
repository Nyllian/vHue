package net.nyllian.vhue.webservice;

import net.nyllian.vhue.model.Bridge;
import net.nyllian.vhue.util.ResourceManager;
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
@Path("/api/{user}/capabilities")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CapabilityResource
{
    private final Logger LOG = LoggerFactory.getLogger(CapabilityResource.class);

    private Bridge bridge;

    @PathParam("user")
    private String username;

    public CapabilityResource(@Context Application application)
    {
        ResourceManager manager = (ResourceManager)application.getProperties().get("manager");
        bridge = (Bridge) manager.getResource("bridge");
    }

    @GET
    public Response getCapabilities(@Context HttpServletRequest request)
    {
        LOG.debug(String.format("%s (%s)", request.getRequestURI(), request.getMethod()));
        return Response.ok(bridge.getCapabilities()).build();
    }
}