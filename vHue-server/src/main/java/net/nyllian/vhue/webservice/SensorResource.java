package net.nyllian.vhue.webservice;

import net.nyllian.vhue.model.Bridge;
import net.nyllian.vhue.model.Sensor;
import net.nyllian.vhue.util.ResourceManager;
import net.nyllian.vhue.util.Serializer;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by Nyllian on 26/11/2017.
 *
 */
@Path("/api/{user}/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource
{
    private final Logger LOG = LoggerFactory.getLogger(SensorResource.class);

    private Bridge bridge;

    @PathParam("user")
    private String username;

    public SensorResource(@Context Application application)
    {
        ResourceManager manager = (ResourceManager)application.getProperties().get("manager");
        bridge = (Bridge) manager.getResource("bridge");
    }

    @GET
    public Response getSensors(@Context HttpServletRequest request)
    {
        LOG.debug(String.format("%s (%s)", request.getRequestURI(), request.getMethod()));
        return Response.ok(bridge.getSensors()).build();
    }

    @POST
    public Response newSensor(@Context HttpServletRequest request)
    {
        LOG.debug(String.format("%s (%s)", request.getRequestURI(), request.getMethod()));

        try
        {
            String postData = IOUtils.toString(request.getInputStream(), Charset.forName("UTF-8"));
            LOG.warn("ZZZZZ => " + postData);

            Sensor newSensor = Serializer.SerializeJson(postData, Sensor.class);
            bridge.addSensor(newSensor);
            bridge.writeConfig();
        }
        catch (IOException iEx)
        {
            LOG.error("Unable to read POST data!", iEx);
        }

        return Response.ok(bridge.getSensors()).build();
    }

    @GET
    @Path("/{id}")
    public Response getSensor(@Context HttpServletRequest request, @PathParam("id") String id)
    {
        LOG.debug(String.format("%s (%s)", request.getRequestURI(), request.getMethod()));
        return Response.ok(bridge.getSensors().get(id)).build();
    }

}
