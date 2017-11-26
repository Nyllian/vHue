package net.nyllian.vhue.webservice;

import net.nyllian.vhue.model.Bridge;
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
        LOG.debug(String.format("%1s (%2s)", request.getRequestURI(), request.getMethod()));

        return Response.ok(bridge.getScenes()).build();
    }

    @POST
    public Response newScene(@Context HttpServletRequest request)
    {
        LOG.debug(String.format("%1s (%2s)", request.getRequestURI(), request.getMethod()));

        try
        {
            String postData = IOUtils.toString(request.getInputStream(), Charset.forName("UTF-8"));
            LOG.warn("YYYYY => " +postData);

            Scene newScene = Serializer.SerializeJson(postData, Scene.class);
            newScene.setOwner(username);
            String newId = bridge.addScene(newScene);
            bridge.writeConfig();

            return Response.ok(String.format("[ {\"success\" : {\"id\" : \"%1s\"} ]", newId)).build();
        }
        catch (IOException iEx)
        {
            LOG.error("Unable to read POST data!", iEx);
        }

        return Response.ok(bridge.getScenes()).build();
    }

    @GET
    @Path("/{id}")
    public Response getScene(@Context HttpServletRequest request, @PathParam("id") String id)
    {
        LOG.debug(String.format("%1s (%2s)", request.getRequestURI(), request.getMethod()));
        return Response.ok(bridge.getScenes().get(id)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteScene(@Context HttpServletRequest request, @PathParam("id") String id)
    {
        LOG.debug(String.format("%1s (%2s)", request.getRequestURI(), request.getMethod()));
        bridge.deleteScene(id);
        bridge.writeConfig();
        return Response.ok("[{ \"success\" : { \"/scenes\" : \"Scene removed\"}}]").build();
    }
}
