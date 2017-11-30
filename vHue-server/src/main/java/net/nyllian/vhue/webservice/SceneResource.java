package net.nyllian.vhue.webservice;

import net.nyllian.vhue.model.Bridge;
import net.nyllian.vhue.model.Light;
import net.nyllian.vhue.model.Scene;
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
        bridge = (Bridge) manager.getResource("bridge");
    }

    @GET
    public Response getScenes(@Context HttpServletRequest request)
    {
        LOG.debug(String.format("%s (%s)", request.getRequestURI(), request.getMethod()));

        return Response.ok(bridge.getScenes()).build();
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
        return Response.ok(bridge.getScenes().get(id)).build();
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
    public Response editScene(@Context HttpServletRequest request, @PathParam("id") String id)
    {
        LOG.debug(String.format("%s (%s)", request.getRequestURI(), request.getMethod()));

        try
        {
            String postData = IOUtils.toString(request.getInputStream(), Charset.forName("UTF-8"));
            LOG.info(String.format("Received: %s", postData));

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
        }
        catch (IOException iEx)
        {
            LOG.error("Unable to read POST data from groups!", iEx);
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

            return Response.ok(bridge.getScenes().get(id)).build();
        }
        catch (IOException iEx)
        {
            LOG.error("Unable to read POST data from groups!", iEx);
            return Response.serverError().entity(iEx).build();
        }
    }
}
