package net.nyllian.vhue.webservice;

import net.nyllian.vhue.model.*;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nyllian on 26/11/2017.
 *
 */
@Path("/api/{user}/groups")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GroupResource
{
    private final Logger LOG = LoggerFactory.getLogger(GroupResource.class);

    private Bridge bridge;

    @PathParam("user")
    private String username;

    public GroupResource(@Context Application application)
    {
        ResourceManager manager = (ResourceManager)application.getProperties().get("manager");
        bridge = (Bridge) manager.getResource("bridge");
    }

    @GET
    public Response getAllGroups(@Context HttpServletRequest request)
    {
        LOG.debug(String.format("%s (%s)", request.getRequestURI(), request.getMethod()));
        return Response.ok(bridge.getGroups()).build();
    }

    @POST
    public Response newGroup(@Context HttpServletRequest request)
    {
        LOG.debug(String.format("%s (%s)", request.getRequestURI(), request.getMethod()));

        try
        {
            String postData = IOUtils.toString(request.getInputStream(), Charset.forName("UTF-8"));
            LOG.info(String.format("Received: %s", postData));

            // The app does not recognize rooms without assigned devices
            String groupId = bridge.addGroup(Serializer.SerializeJson(postData, Group.class));

            // All lights that were assigned to this group must be removed from the DiscoveredLights
            Group newGroup = bridge.getGroup(groupId);
            for (String lightId : newGroup.getLightIds())
            {
                bridge.deleteDiscoveredLight(lightId);
            }
            bridge.writeConfig();

            String retval = String.format("[ {\"success\" : {\"id\" : \"%s\"}} ]", groupId);
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
    public Response deleteAllGroups(@Context HttpServletRequest request)
    {
        LOG.debug(String.format("%s (%s)", request.getRequestURI(), request.getMethod()));
        // TODO: Delete all Groups?
        return Response.ok().build();
    }

    @GET
    @Path("/{id}")
    public Response getGroup(@Context HttpServletRequest request, @PathParam("id") String id)
    {
        LOG.debug(String.format("%s (%s)", request.getRequestURI(), request.getMethod()));
        return Response.ok(bridge.getGroups().get(id)).build();
    }

    @PUT
    @Path("/{id}")
    @SuppressWarnings("unchecked")
    public Response editGroup(@Context HttpServletRequest request, @PathParam("id") String id)
    {
        LOG.debug(String.format("%s (%s)", request.getRequestURI(), request.getMethod()));

        try
        {
            String postData = IOUtils.toString(request.getInputStream(), Charset.forName("UTF-8"));
            LOG.info(String.format("Received: %s", postData));

            Group currentGroup = bridge.getGroup(id);
            if (postData.contains("lights"))
            {
                LOG.warn("Clearing all lights!");
                currentGroup.setLightIds(new String[]{});
            }
            Group updatedGroup = Serializer.UpdateObject(currentGroup, postData);
            bridge.getGroups().put(id, updatedGroup);

            // Construct the response message
            String retval = String.format("[ %s ]", HueUtils.getResponsePropertiesSuccess(postData, String.format("/groups/%s", id)));
            LOG.info(String.format("Responding: %s", retval));
            return Response.ok(retval).build();
        }
        catch (IOException iEx)
        {
            LOG.error("Unable to read POST data!", iEx);
            return Response.serverError().entity(iEx).build();
        }
    }

    @PUT
    @Path("/{id}/action")
    @SuppressWarnings("unchecked")
    public Response changeGroupState(@Context HttpServletRequest request, @PathParam("id") String id)
    {
        LOG.debug(String.format("%s (%s)", request.getRequestURI(), request.getMethod()));

        try
        {
            String postData = IOUtils.toString(request.getInputStream(), Charset.forName("UTF-8"));
            LOG.info(String.format("Received: %s", postData));

            if (id.equals("0"))
            {
                Map<String, Object> dataMap = Serializer.SerializeJson(postData, HashMap.class);
                if (dataMap.containsKey("scene"))
                {
                    // Get the scene
                    Scene groupScene = bridge.getScene(dataMap.get("scene").toString());
                    // Based on the lights of the scene, find the group
                    String groupKey = id;
                    sceneLightsLoop:
                    for (String sceneLightId : groupScene.getLightIds())
                    {
                        // Check all groups for respective lights
                        for (String gKey : bridge.getGroups().keySet())
                        {
                            if (Arrays.asList(bridge.getGroup(gKey).getLightIds()).contains(sceneLightId))
                            {
                                groupKey = gKey;
                                break sceneLightsLoop;
                            }

                        }
                    }

                    // TODO: Remove the actual lights from the group
                    // TODO: Always get the lights based on the lightId
                    for (String lKey : bridge.getGroup(groupKey).getLightIds())
                    {
                        // Dirty clone
                        String originalState = Serializer.SerializeJson(groupScene.getLightStates().get(lKey));
                        LightState clonedLightState = Serializer.SerializeJson(originalState, LightState.class);
                        bridge.getLight(lKey).setLightState(clonedLightState);
                    }

                    String retval = HueUtils.getResponseAttributesSuccess(postData, String.format("/groups/%s/action", id));
                    LOG.info(String.format("Responding: %s", retval));
                    return Response.ok(retval).build();
                }
                else
                {
                    // Change the groupAction of all groups
                    for (String groupKey : bridge.getGroups().keySet())
                    {
                        GroupAction currentGroupAction = bridge.getGroup(groupKey).getGroupAction();
                        GroupAction newGroupAction = Serializer.UpdateObject(currentGroupAction, postData);
                        bridge.getGroup(groupKey).setGroupAction(newGroupAction);
                    }
                }
            }
            else
            {
                GroupAction currentGroupAction = bridge.getGroup(id).getGroupAction();
                GroupAction newGroupAction = Serializer.UpdateObject(currentGroupAction, postData);
                bridge.getGroup(id).setGroupAction(newGroupAction);
            }

            // TODO: Split for 'all groups'
            // TODO: Check this code!!!

            // Construct response
            String retval = HueUtils.getResponseAttributesSuccess(postData, String.format("/groups/%s/action", id));
            /*

            Map<String, Object> dataMap = Serializer.SerializeJson(postData, Map.class);
            StringBuilder sb = new StringBuilder();
            for (String key : dataMap.keySet())
            {
                if (dataMap.get(key) instanceof String)
                {
                    String value = (String)dataMap.get(key);
                    sb.append(String.format("{\"address\": \"/groups/%s/action/%s\", \"value\":\"%s\"},", id, key, value));
                }
                else if (dataMap.get(key) instanceof Boolean)
                {
                    Boolean value = (Boolean)dataMap.get(key);
                    sb.append(String.format("{\"address\": \"/groups/%s/action/%s\", \"value\":%s},", id, key, value));
                }
                else if (dataMap.get(key) instanceof ArrayList)
                {
                    sb.append(String.format("{\"address\": \"/groups/%s/action/%s\", \"value\":%s},", id, key, dataMap.get(key)));

                    ArrayList<String> value = (ArrayList)dataMap.get(key);
                    String val = "";
                    for (String tmp : value)
                    {
                        val += String.format("\"%s\",", tmp);
                    }

                    sb.append(String.format("{\"address\": \"/groups/%s/action/%s\", \"value\":[%s]},", id, key, val.substring(0, val.length() - 1)));
                }
                else
                {
                    LOG.warn("Unknown object passed in editGroup()! ==> " + dataMap.get(key).getClass());
                }
            }
            */

            // String retval = String.format("[{ \"success\" : %s }]", sb.toString().substring(0, sb.toString().length()-1));
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
    public Response deleteGroup(@Context HttpServletRequest request, @PathParam("id") String id)
    {
        LOG.debug(String.format("%s (%s)", request.getRequestURI(), request.getMethod()));

        bridge.deleteGroup(id);
        bridge.writeConfig();

        // return Response.ok(String.format("[{\"success\" : \"/groups/%s\" deleted. }]", id)).build();
        return Response.ok(String.format("[{ \"success\" : \"/groups/%s deleted\" }]", id)).build();
    }

}
