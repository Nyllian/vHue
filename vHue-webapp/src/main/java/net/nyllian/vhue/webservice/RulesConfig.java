package net.nyllian.vhue.webservice;

import net.nyllian.vhue.model.Bridge;
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
 * Created by Nyllian on 1/12/2017.
 *
 */
@Path("/api/{user}/rules")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RulesConfig
{
    private final Logger LOG = LoggerFactory.getLogger(RulesConfig.class);

    private Bridge bridge;

    @PathParam("user")
    private String username;

    public RulesConfig(@Context Application application)
    {
        ResourceManager manager = (ResourceManager)application.getProperties().get("manager");
        bridge = manager.getBridge();
    }

    @GET
    public Response getAllRules(@Context HttpServletRequest request)
    {
        LOG.debug(String.format("%s (%s)", request.getRequestURI(), request.getMethod()));

        try
        {
            return Response.ok(
                    Serializer.SerializeJson(bridge.getRules())
            ).build();
        }
        catch (Exception ex)
        {
            LOG.error("Unable to serialize the object", ex);
            return Response.status(Response.Status.SEE_OTHER).entity(ex).build();
        }
    }


}
