package net.nyllian.vhue.webservice;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.text.SimpleDateFormat;

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
            ObjectMapper serializer = new ObjectMapper();
            serializer.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
            serializer.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);

            String response = serializer
                    // .writerWithDefaultPrettyPrinter()
                    .writerWithView(SceneView.SceneProperties.class)
                    .writeValueAsString(bridge.getScenes());

            LOG.warn("RSPONSE ==> \n" + response);
            // TODO: Exclude the scenes
            return Response.ok(
                    response
            ).build();
            /*
            return Response.ok(
                    Serializer.SerializeJson(bridge.getScenes())
            ).build();
            */
        }
        catch (Exception ex)
        {
            LOG.error("Unable to serialize the object", ex);
            return Response.serverError().entity(ex).build();
        }
    }

    @PUT
    public Response putScenes(@Context HttpServletRequest request)
    {
        LOG.debug(String.format("%s (%s)", request.getRequestURI(), request.getMethod()));

        try
        {
            String postData = IOUtils.toString(request.getInputStream(), Charset.forName("UTF-8"));
            LOG.info(String.format("Received: %s", postData));

            LOG.warn("Unimplemented feature in Scenes - putScenes");

            return Response.ok(bridge.getScenes()).build();
        }
        catch (IOException iEx)
        {
            LOG.error("Unable to read POST data!", iEx);
            return Response.serverError().entity(iEx).build();
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
                Light respectiveLight = bridge.getLight(lid);
                newScene.getLightStates().put(lid, respectiveLight.getLightState());
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
            return Response.serverError().entity(iEx).build();
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
            return Response.serverError().entity(ex).build();
        }
    }

    @POST
    @Path("/{id}")
    public Response unknownSceneFeature(@Context HttpServletRequest request, @PathParam("id") String id)
    {
        LOG.debug(String.format("%s (%s)", request.getRequestURI(), request.getMethod()));
        LOG.warn("unknownSceneFeature - Unknown feature - still needs implementation");
        return Response.ok(bridge.getScenes().get(id)).build();
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

            // Received: {"name":"1onsondergang","lights":["7","3"],"picture":"","appdata":{"version":1,"data":"HasOS_r01_d15"}}
            Scene currentScene = bridge.getScene(id);
            Scene updatedScene = Serializer.UpdateObject(currentScene, postData);
            bridge.getScenes().put(id, updatedScene);
            bridge.writeConfig();

            // Construct the response message
            String retval = HueUtils.getResponsePropertiesSuccess(postData, String.format("/scenes/%s", id));
            LOG.info(String.format("Responding: %s", retval));
            return Response.ok(retval).build();


/*
// TODO: This code works regarding lights
            Scene currentScene = bridge.getScene(id);
            if (postData.contains("lights"))
            {
                currentScene.getLights().clear();
            }
            Scene updatedScene = Serializer.UpdateObject(currentScene, postData);

            // Update the lights
            for (String l : updatedScene.getLightIds())
            {
                updatedScene.getLights().put(l, bridge.getLight(l));
            }
            bridge.getScenes().put(id, updatedScene);

            return Response.ok(bridge.getScenes().get(id)).build();
*/
        }
        catch (IOException iEx)
        {
            LOG.error("Unable to read POST data!", iEx);
            return Response.serverError().entity(iEx).build();
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
    public Response editSceneLights(@Context HttpServletRequest request, @PathParam("id") String id, @PathParam("lid") String lid)
    {
        LOG.debug(String.format("%s (%s)", request.getRequestURI(), request.getMethod()));

        try
        {
            String postData = IOUtils.toString(request.getInputStream(), Charset.forName("UTF-8"));
            LOG.info(String.format("Received: %s", postData));

            // TODO: Change the lightstate of the lights of the scene...
            // TODO: Go via the light.lightstate and not via the scene.lightstate...
            // bridge.getScene(id).getLights().get(lid).getLightState();

            // The lightstate under the Scene must be detached from the actual light
            LightState sceneLightState = bridge.getScene(id).getLightStates().get(lid);
            LightState updatedSceneLightState = Serializer.UpdateObject(sceneLightState, postData);
            bridge.getLight(lid).setLightState(updatedSceneLightState);
            bridge.writeConfig();

            String retval = HueUtils.getResponseAttributesSuccess(postData, String.format("/scenes/%s/lights/%s/state", id, lid));
            // String retval = String.format("[{ \"success\" : { \"/scenes/%s/lights/%s/state\" : \"Device state changed.\" }}]", id, lid);
            LOG.debug(String.format("Responding: %s", retval));
            return Response.ok(retval).build();
        }
        catch (IOException iEx)
        {
            LOG.error("Unable to read POST data!", iEx);
            return Response.serverError().entity(iEx).build();
        }
    }

    @POST
    @Path("/{id}/lightstates/{lid}")
    public Response editSceneLightsPost(@Context HttpServletRequest request, @PathParam("id") String id, @PathParam("lid") String lid)
    {
        LOG.debug(String.format("%s (%s)", request.getRequestURI(), request.getMethod()));

        try
        {
            String postData = IOUtils.toString(request.getInputStream(), Charset.forName("UTF-8"));
            LOG.info(String.format("Received: %s", postData));

            // TODO: Change the lightstate of the lights of the scene...
            // TODO: Go via the light.lightstate and not via the scene.lightstate...
            // bridge.getScene(id).getLights().get(lid).getLightState();

            // The lightstate under the Scene must be detached from the actual light
            LightState sceneLightState = bridge.getScene(id).getLightStates().get(lid);
            LightState updatedSceneLightState = Serializer.UpdateObject(sceneLightState, postData);
            bridge.getLight(lid).setLightState(updatedSceneLightState);
            bridge.writeConfig();

            String retval = HueUtils.getResponseAttributesSuccess(postData, String.format("/scenes/%s/lights/%s/state", id, lid));
            // String retval = String.format("[{ \"success\" : { \"/scenes/%s/lights/%s/state\" : \"Device state changed.\" }}]", id, lid);
            LOG.debug(String.format("Responding: %s", retval));
            return Response.ok(retval).build();
        }
        catch (IOException iEx)
        {
            LOG.error("Unable to read POST data!", iEx);
            return Response.serverError().entity(iEx).build();
        }
    }
}
