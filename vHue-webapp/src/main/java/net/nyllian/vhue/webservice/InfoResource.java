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
import java.util.*;

/**
 * Created by Nyllian on 27/11/2017.
 *
 */
@Path("/api/{username}/info")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InfoResource
{
    private final Logger LOG = LoggerFactory.getLogger(InfoResource.class);

    @PathParam("username")
    private String username;

    private Bridge bridge;

    public InfoResource(@Context Application application)
    {
        ResourceManager manager = (ResourceManager)application.getProperties().get("manager");
        bridge = manager.getBridge();
    }

    @GET
    @Path("/timezones")
    public Response getTimeZones(@Context HttpServletRequest request)
    {
        try
        {
            return Response.ok(
                    Serializer.SerializeJson(Arrays.asList(TimeZone.getAvailableIDs()))
            ).build();
        }
        catch (Exception ex)
        {
            LOG.error("Unable to serialize the object", ex);
            return Response.status(Response.Status.SEE_OTHER).entity(ex).build();
        }
    }


}
