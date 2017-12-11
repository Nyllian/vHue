package net.nyllian.vhue.webservice;

import net.nyllian.vhue.model.Bridge;
import net.nyllian.vhue.model.Light;
import net.nyllian.vhue.model.LightState;
import net.nyllian.vhue.model.Scene;
import net.nyllian.vhue.model.views.SceneView;
import net.nyllian.vhue.server.ResourceManager;
import net.nyllian.vhue.util.HueUtils;
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
@Path("/api/{user}/scenes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SceneResource
{
    private final Logger LOG = LoggerFactory.getLogger(SceneResource.class);

    private Bridge bridge;

    @PathParam("user")
    private String username;

    public SceneResource(@Context Application application)
    {
        ResourceManager manager = (ResourceManager)application.getProperties().get("manager");
        bridge = manager.getBridge();
    }

    @GET
    public Response getScenes(@Context HttpServletRequest request)
    {
        LOG.debug(String.format("%s (%s)", request.getRequestURI(), request.getMethod()));

        try
        {
            return Response.ok(
                    Serializer.SerializeJsonView(SceneView.AllProperties.class, bridge.getScenes())
                    // Serializer.SerializeJson(bridge.getScenes())
            ).build();
        }
        catch (Exception ex)
        {
            LOG.error("Unable to serialize the object", ex);
            return Response.status(Response.Status.PAYMENT_REQUIRED).entity(ex).build();
        }
    }

    @POST
    public Response newScene(@Context HttpServletRequest request)
    {
        LOG.debug(String.format("%s (%s)", request.getRequestURI(), request.getMethod()));

        try
        {
            String postData = IOUtils.toString(request.getInputStream(), Charset.forName("UTF-8"));
            LOG.info(String.format("Received: %s", postData));

            Scene newScene = Serializer.SerializeJson(postData, Scene.class);
            newScene.setOwner(username);
            // Get the respective lights
            for (String lid : newScene.getLightIds())
            {
                // Check if the lightIds is used in other scenes...
                for (String sKey : bridge.getScenes().keySet())
                {
                    // of this light, get the group
                    // of the found scenes, get the group
                    // if lightgroup != scenegroup; remove

                    // This will remove the light from the correct scene...
                    // TODO: Also take group into account
                    // Don't delete any lightStates if the scene belongs to the same group
                    // bridge.getScene(sKey).getLightStates().remove(lid);
                }

                // Light respectiveLight = bridge.getLight(lid);
                // Dirty clone
                // String lightStateJson = Serializer.SerializeJson(respectiveLight.getLightState());
                // LightState sceneLightState = Serializer.SerializeJson(lightStateJson, LightState.class);
                newScene.getLightStates().put(lid, new LightState());
            }
            String sceneId = bridge.addScene(newScene);
            bridge.writeConfig();

            String retval = String.format("[ {\"success\" : {\"id\" : \"%s\"}} ]", sceneId);
            LOG.info(String.format("Responding: %s", retval));
            return Response.ok(retval).build();
        }
        catch (IOException iEx)
        {
            LOG.error("Unable to read POST data!", iEx);
            return Response.status(Response.Status.PAYMENT_REQUIRED).entity(iEx).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getScene(@Context HttpServletRequest request, @PathParam("id") String id)
    {
        LOG.debug(String.format("%s (%s)", request.getRequestURI(), request.getMethod()));

        try
        {
            return Response.ok(
                    Serializer.SerializeJson(bridge.getScene(id))
            ).build();
        }
        catch (Exception ex)
        {
            LOG.error("Unable to serialize the object", ex);
            return Response.status(Response.Status.PAYMENT_REQUIRED).entity(ex).build();
        }
    }

    @PUT
    @Path("/{id}")
    @SuppressWarnings("unchecked")
    public Response editScene(@Context HttpServletRequest request, @PathParam("id") String id)
    {
        LOG.debug(String.format("%s (%s)", request.getRequestURI(), request.getMethod()));

        try
        {
            String postData = IOUtils.toString(request.getInputStream(), Charset.forName("UTF-8"));
            LOG.info(String.format("Received: %s", postData));

            Scene currentScene = bridge.getScene(id);
            Scene updatedScene = Serializer.UpdateObject(currentScene, postData);
            // It is possible that lights were added ==> add the corresponding lightStates
            // Get the respective lights
            for (String lid : updatedScene.getLightIds())
            {
                if (!updatedScene.getLightStates().containsKey(lid))
                {
                    Light respectiveLight = bridge.getLight(lid);
                    // Dirty clone
                    String lightStateJson = Serializer.SerializeJson(respectiveLight.getLightState());
                    LightState sceneLightState = Serializer.SerializeJson(lightStateJson, LightState.class);
                    updatedScene.getLightStates().put(lid, sceneLightState);
                }
            }

            bridge.getScenes().put(id, updatedScene);
            bridge.writeConfig();

            // Construct the response message
            String retval = HueUtils.getResponsePropertiesSuccess(postData, String.format("/scenes/%s", id));
            LOG.info(String.format("Responding: %s", retval));
            return Response.ok(retval).build();
        }
        catch (IOException iEx)
        {
            LOG.error("Unable to read POST data!", iEx);
            return Response.status(Response.Status.PAYMENT_REQUIRED).entity(iEx).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteScene(@Context HttpServletRequest request, @PathParam("id") String id)
    {
        LOG.debug(String.format("%s (%s)", request.getRequestURI(), request.getMethod()));
        bridge.deleteScene(id);
        bridge.writeConfig();

        String retval = String.format("[{ \"success\" : \"/scenes/%s deleted.\"}]", id);
        return Response.ok(retval).build();
    }

    @PUT
    @Path("/{id}/lightstates/{lid}")
    public Response changeLightState(@Context HttpServletRequest request, @PathParam("id") String id, @PathParam("lid") String lid)
    {
        LOG.debug(String.format("%s (%s)", request.getRequestURI(), request.getMethod()));

        try
        {
            String postData = IOUtils.toString(request.getInputStream(), Charset.forName("UTF-8"));
            LOG.info(String.format("Received: %s", postData));

            LightState sceneLightState = bridge.getScene(id).getLightStates().get(lid);
            if (sceneLightState == null)
            {
                sceneLightState = new LightState();
                // Dirty clone
                // sceneLightState = Serializer.SerializeJson(Serializer.SerializeJson(bridge.getLight(lid).getLightState()), LightState.class);
            }
            // Update the lightState
            LightState updatedSceneLightState = Serializer.UpdateObject(sceneLightState, postData);
            bridge.getLight(lid).setLightState(updatedSceneLightState);
            bridge.writeConfig();

            String retval = HueUtils.getResponsePropertiesSuccess(postData, String.format("/scenes/%s/lights/%s/state", id, lid));
            LOG.debug(String.format("Responding: %s", retval));
            return Response.ok(retval).build();
        }
        catch (IOException iEx)
        {
            LOG.error("Unable to read POST data!", iEx);
            return Response.status(Response.Status.PAYMENT_REQUIRED).entity(iEx).build();
        }
    }
}
