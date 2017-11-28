package net.nyllian.vhue.webservice;

import net.nyllian.vhue.model.Bridge;
import net.nyllian.vhue.util.ResourceManager;
import net.nyllian.vhue.util.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Nyllian on 27/11/2017.
 *
 */
@Path("/api/{username}/info")
@Singleton
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InfoResource
{
    private final Logger LOG = LoggerFactory.getLogger(InfoResource.class);

    private Bridge bridge;
    private List<String> timezones = new ArrayList<>();

    public InfoResource(@Context Application application)
    {
        ResourceManager manager = (ResourceManager)application.getProperties().get("manager");
        bridge = (Bridge) manager.getResource("bridge");

        for (String tz : TimeZone.getAvailableIDs())
        {
            if (tz.toLowerCase().startsWith("europe"))
            {
                timezones.add(tz);
            }
        }
    }

    @GET
    @Path("/timezones")
    public Response getTimeZones(@Context HttpServletRequest request)
    {
        try
        {
            return Response.ok(Serializer.SerializeJson(timezones)).build();
        }
        catch (Exception ex)
        {
            return Response.serverError().entity(ex).build();
        }
    }


}
