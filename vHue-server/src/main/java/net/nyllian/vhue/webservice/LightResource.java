package net.nyllian.vhue.webservice;

import net.nyllian.vhue.model.Bridge;
import net.nyllian.vhue.model.DiscoveredLight;
import net.nyllian.vhue.model.Light;
import net.nyllian.vhue.model.LightState;
import net.nyllian.vhue.server.ResourceManager;
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
@Path("/api/{user}/lights")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LightResource
{
    private final Logger LOG = LoggerFactory.getLogger(LightResource.class);

    private Bridge bridge;

    @PathParam("user")
    private String username;

    public LightResource(@Context Application application)
    {
        ResourceManager manager = (ResourceManager)application.getProperties().get("manager");
        bridge = manager.getBridge();
    }

    @GET
    public Response getAllLights(@Context HttpServletRequest request)
    {
        LOG.debug(String.format("%s (%s)", request.getRequestURI(), request.getMethod()));

        try
        {
            return Response.ok(
                    Serializer.SerializeJson(bridge.getLights())
            ).build();
        }
        catch (Exception ex)
        {
            LOG.error("Unable to serialize the object", ex);
            return Response.serverError().entity(ex).build();
        }
    }

    @POST
    public Response searchNewLights(@Context HttpServletRequest request)
    {
        LOG.debug(String.format("%s (%s)", request.getRequestURI(), request.getMethod()));

        // TODO: Start a new LightScan
        LOG.warn("newLights size => " + bridge.getDiscoveredLights().size());
        if (bridge.getDiscoveredLights().size() < 5)
        {
            // Add a new dummy light
            bridge.addDiscoveredLight(new DiscoveredLight().setName("NewDummyLight"));
            // Loop over all the 'Discovered' lights and compare with the current
        }

        return Response.ok("[{ \"success\" : { \"/lights\" : \"Searching for new devices\"}}]").build();
    }

    @DELETE
    public Response deleteAllLights(@Context HttpServletRequest request)
    {
        LOG.debug(String.format("%s (%s)", request.getRequestURI(), request.getMethod()));
        // TODO: Delete all lights?
        return Response.ok().build();
    }

    @GET
    @Path("/{id}")
    public Response getLight(@Context HttpServletRequest request, @PathParam("id") String id)
    {
        LOG.debug(String.format("%s (%s)", request.getRequestURI(), request.getMethod()));

        try
        {
            if (id.equals("new"))
            {
                return Response.ok(
                        Serializer.SerializeJson(bridge.getDiscoveredLights())
                ).build();
            }
            else
            {
                return Response.ok(
                        Serializer.SerializeJson(bridge.getLights().get(id))
                ).build();
            }
        }
        catch (Exception ex)
        {
            LOG.error("Unable to serialize the object", ex);
            return Response.serverError().entity(ex).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response editLight(@Context HttpServletRequest request, @PathParam("id") String id)
    {
        LOG.debug(String.format("%s (%s)", request.getRequestURI(), request.getMethod()));

        try
        {
            String postData = IOUtils.toString(request.getInputStream(), Charset.forName("UTF-8"));
            LOG.info(String.format("Received: %s", postData));

            Light currentLight = bridge.getLight(id);
            Light updatedLight = Serializer.UpdateObject(currentLight, postData);
            bridge.getLights().put(id, updatedLight);
            bridge.writeConfig();

            String retval = String.format("[{ \"success\" : { \"/lights/%s\" : \"Device Edited\"}}]", id);
            LOG.info(String.format("Responding: %s", retval));
            return Response.ok(retval).build();
        }
        catch (IOException iEx)
        {
            LOG.error("Unable to read POST data!", iEx);
            return Response.serverError().entity(iEx).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteLight(@Context HttpServletRequest request, @PathParam("id") String id)
    {
        LOG.debug(String.format("%s (%s)", request.getRequestURI(), request.getMethod()));
        bridge.deleteLight(id);
        bridge.writeConfig();

        return Response.ok("[{ \"success\" : { \"/lights\" : \"Device removed\"}}]").build();
    }

    @PUT
    @Path("/{id}/state")
    public Response changeLightState(@Context HttpServletRequest request, @PathParam("id") String id)
    {
        LOG.debug(String.format("%s (%s)", request.getRequestURI(), request.getMethod()));

        try
        {
            String postData = IOUtils.toString(request.getInputStream(), Charset.forName("UTF-8"));
            LOG.info(String.format("Received: %s", postData));

            // TODO: When selecting a light...
            // Received: {"alert":"select"}
            // Received: {"alert":"1select"}
            // TODO: When un-selecting the light...
            // Received: {"alert":"none"}

            // TODO: add transitiontime
            // Received: {"bri":127,"transitiontime":5,"xy":[0.6457210183143616,0.2752789855003357]}

            // TODO: Put a timer to check if the lights must go out
            // TODO: When the TV turns off, no 'off' signal will be sent

            LightState currentLightState = bridge.getLight(id).getLightState();
            LightState newLightState = Serializer.UpdateObject(currentLightState, postData);
            bridge.getLight(id).setLightState(newLightState);
            // TODO: when the server powers down -- turn all devices in the off state -- otherwise, upon start all lights will turn on
            bridge.writeConfig();

            String retval = String.format("[{ \"success\" : { \"/lights/%s/state\" : \"Device state changed.\" }}]", id);
            LOG.info(String.format("Responding: %s", retval));
            return Response.ok(retval).build();
        }
        catch (IOException iEx)
        {
            LOG.error("Unable to read POST data!", iEx);
            return Response.serverError().entity(iEx).build();
        }
    }

}
