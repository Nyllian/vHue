package net.nyllian.vhue.webservice;

import net.nyllian.vhue.model.Bridge;
import net.nyllian.vhue.model.DiscoveredLight;
import net.nyllian.vhue.model.Light;
import net.nyllian.vhue.model.LightState;
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
import java.util.HashMap;

/**
 * Created by Nyllian on 26/11/2017.
 *
 */
@Path("/api/{user}/lights")
@Produces(MediaType.APPLICATION_JSON)
public class LightResource
{
    private final Logger LOG = LoggerFactory.getLogger(LightResource.class);

    private Bridge bridge;

    @PathParam("user")
    private String username;

    public LightResource(@Context Application application)
    {
        ResourceManager manager = (ResourceManager)application.getProperties().get("manager");
        bridge = (Bridge) manager.getResource("bridge");
    }

    @GET
    public Response getAllLights(@Context HttpServletRequest request)
    {
        LOG.debug(String.format("%1s (%2s)", request.getRequestURI(), request.getMethod()));
        return Response.ok(bridge.getLights()).build();
    }

    @POST
    public Response searchNewLights(@Context HttpServletRequest request)
    {
        LOG.debug(String.format("%1s (%2s)", request.getRequestURI(), request.getMethod()));

        // TODO: Start a new LightScan
        LOG.warn("newLights size => " + bridge.getDiscoveredLights().size());
        if (bridge.getDiscoveredLights().size() < 3)
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
        LOG.debug(String.format("%1s (%2s)", request.getRequestURI(), request.getMethod()));
        // TODO: Delete all lights?
        return Response.ok().build();
    }

    @GET
    @Path("/{id}")
    public Response getLight(@Context HttpServletRequest request, @PathParam("id") String id)
    {
        LOG.debug(String.format("%1s (%2s)", request.getRequestURI(), request.getMethod()));

        try
        {
            if (id.equals("new"))
            {
                // Return list of new discovered lights
                return Response.ok(Serializer.SerializeJson(bridge.getDiscoveredLights())).build();
            }
        }
        catch (IOException iEx)
        {
            LOG.error("Unable to read POST data from groups!", iEx);
        }

        return Response.ok(bridge.getLights().get(id)).build();
    }

    @PUT
    @Path("/{id}")
    public Response editLight(@Context HttpServletRequest request, @PathParam("id") String id)
    {
        LOG.debug(String.format("%1s (%2s)", request.getRequestURI(), request.getMethod()));

        try
        {
            String postData = IOUtils.toString(request.getInputStream(), Charset.forName("UTF-8"));
            LOG.warn("SetLightAttributes ==>\n" + postData);

            Light currentLight = bridge.getLight(id);
            Light updatedLight = Serializer.UpdateObject(currentLight, postData);
            bridge.getLights().put(id, updatedLight);

            return Response.ok(String.format("[{ \"success\" : { \"/lights/%1s\" : \"Device Edited\"}}]", id)).build();
        }
        catch (IOException iEx)
        {
            LOG.error("Unable to read POST data from lights!", iEx);
            return Response.serverError().entity(iEx).build();
        }
    }

    @PUT
    @Path("/{id}/state")
    public Response changeLightState(@Context HttpServletRequest request, @PathParam("id") String id)
    {
        LOG.debug(String.format("%1s (%2s)", request.getRequestURI(), request.getMethod()));

        try
        {
            String postData = IOUtils.toString(request.getInputStream(), Charset.forName("UTF-8"));
            LightState currentLightState = bridge.getLight(id).getLightState();
            LightState newLightState = Serializer.UpdateObject(currentLightState, postData);
            bridge.getLight(id).setLightState(newLightState);

            return Response.ok(String.format("[{ \"success\" : { \"/lights/%1s/state\" : \"Device state changed.\" }}]", id)).build();
        }
        catch (IOException iEx)
        {
            LOG.error("Unable to read POST data from groups!", iEx);
            return Response.serverError().entity(iEx).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteLight(@Context HttpServletRequest request, @PathParam("id") String id)
    {
        LOG.debug(String.format("%1s (%2s)", request.getRequestURI(), request.getMethod()));
        bridge.deleteLight(id);
        bridge.writeConfig();
        return Response.ok("[{ \"success\" : { \"/lights\" : \"Device removed\"}}]").build();
    }

}