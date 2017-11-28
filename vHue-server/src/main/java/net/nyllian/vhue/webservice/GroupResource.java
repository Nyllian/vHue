package net.nyllian.vhue.webservice;

import net.nyllian.vhue.model.Bridge;
import net.nyllian.vhue.model.Group;
import net.nyllian.vhue.model.GroupAction;
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
import java.util.ArrayList;
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
        LOG.debug(String.format("%1s (%2s)", request.getRequestURI(), request.getMethod()));
        return Response.ok(bridge.getGroups()).build();
    }

    @POST
    public Response newGroup(@Context HttpServletRequest request)
    {
        LOG.debug(String.format("%1s (%2s)", request.getRequestURI(), request.getMethod()));

        try
        {
            String postData = IOUtils.toString(request.getInputStream(), Charset.forName("UTF-8"));
            LOG.info(String.format("Received: %1s", postData));

            // The app does not recognize rooms without assigned devices
            String groupId = bridge.addGroup(Serializer.SerializeJson(postData, Group.class));

            // All lights that were assigned to this group must be removed from the DiscoveredLights
            Group newGroup = bridge.getGroup(groupId);
            for (String lightId : newGroup.getLightIds())
            {
                bridge.deleteDiscoveredLight(lightId);
            }
            bridge.writeConfig();

            String retval = String.format("[ {\"success\" : {\"id\" : \"%1s\"}} ]", groupId);
            LOG.info(String.format("Responding: %1s", retval));
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
        LOG.debug(String.format("%1s (%2s)", request.getRequestURI(), request.getMethod()));
        // TODO: Delete all Groups?
        return Response.ok().build();
    }

    @GET
    @Path("/{id}")
    public Response getGroup(@Context HttpServletRequest request, @PathParam("id") String id)
    {
        LOG.debug(String.format("%1s (%2s)", request.getRequestURI(), request.getMethod()));
        return Response.ok(bridge.getGroups().get(id)).build();
    }

    @PUT
    @Path("/{id}")
    @SuppressWarnings("unchecked")
    public Response editGroup(@Context HttpServletRequest request, @PathParam("id") String id)
    {
        LOG.debug(String.format("%1s (%2s)", request.getRequestURI(), request.getMethod()));

        try
        {
            String postData = IOUtils.toString(request.getInputStream(), Charset.forName("UTF-8"));
            LOG.info(String.format("Received: %1s", postData));

            Group currentGroup = bridge.getGroup(id);
            if (postData.contains("lights"))
            {
                LOG.warn("Clearing all lights!");
                currentGroup.getLights().clear();
            }
            Group updatedGroup = Serializer.UpdateObject(currentGroup, postData);
            // Update the lights
            for (String l : updatedGroup.getLightIds())
            {
                LOG.warn("Adding light " + l);
                updatedGroup.getLights().put(l, bridge.getLight(l));
            }
            bridge.getGroups().put(id, updatedGroup);

            // Construct the response message
            Map<String, Object> dataMap = Serializer.SerializeJson(postData, Map.class);
            StringBuilder sb = new StringBuilder();
            for (String key : dataMap.keySet())
            {
                if (dataMap.get(key) instanceof String)
                {
                    sb.append(String.format("{\"/groups/%1s/%2s\":\"%3s\"},", id, key, dataMap.get(key)));
                }
                else if (dataMap.get(key) instanceof ArrayList)
                {
                    // Assumption: The lights are processed here
                    ArrayList<String> tmpArray = (ArrayList)dataMap.get(key);
                    String val = "";
                    LOG.info("tmpArray.size() ==> " + tmpArray.size());
                    if (tmpArray.size() > 0)
                    {
                        for (String tmp : tmpArray)
                        {
                            val += String.format("\"%1s\",", tmp);
                        }

                        sb.append(String.format("{\"/groups/%1s/%2s\": [%3s]},", id, key, val.substring(0, val.length() - 1)));
                    }
                    else
                    {
                        sb.append(String.format("{\"/groups/%1s/%2s\": [%3s]},", id, key, val));
                    }
                }
                else
                {
                    LOG.warn("Unknown object passed in editGroup()! ==> " + dataMap.get(key).getClass());
                }
            }

            String retval = String.format("[{ \"success\" : %1s }]", sb.toString().substring(0, sb.toString().length() - 1));
            LOG.info(String.format("Responding: %1s", retval));
            return Response.ok(retval).build();
        }
        catch (IOException iEx)
        {
            LOG.error("Unable to read POST data from lights!", iEx);
            return Response.serverError().entity(iEx).build();
        }
    }

    @PUT
    @Path("/{id}/action")
    @SuppressWarnings("unchecked")
    public Response changeGroupState(@Context HttpServletRequest request, @PathParam("id") String id)
    {
        LOG.debug(String.format("%1s (%2s)", request.getRequestURI(), request.getMethod()));

        try
        {
            String postData = IOUtils.toString(request.getInputStream(), Charset.forName("UTF-8"));
            LOG.info(String.format("Received: %1s", postData));

            if (id.equals("0"))
            {
                Map<String, Object> dataMap = Serializer.SerializeJson(postData, HashMap.class);
                if (dataMap.containsKey("scene"))
                {
                    // TODO: If the data is a scene, retrieve the scene lights (id)
                    // TODO: Compare the light_ids with the groups
                    // TODO: accordingly, change the group state???

                    LOG.warn("This feature still has to be developed!");
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

            Map<String, Object> dataMap = Serializer.SerializeJson(postData, Map.class);
            StringBuilder sb = new StringBuilder();
            for (String key : dataMap.keySet())
            {
                if (dataMap.get(key) instanceof String)
                {
                    String value = (String)dataMap.get(key);
                    sb.append(String.format("{\"address\": \"/groups/%1s/action/%2s\", \"value\":\"%3s\"},", id, key, value));
                }
                else if (dataMap.get(key) instanceof Boolean)
                {
                    Boolean value = (Boolean)dataMap.get(key);
                    sb.append(String.format("{\"address\": \"/groups/%1s/action/%2s\", \"value\":%3s},", id, key, value));
                }
                else if (dataMap.get(key) instanceof ArrayList)
                {
                    sb.append(String.format("{\"address\": \"/groups/%1s/action/%2s\", \"value\":%3s},", id, key, dataMap.get(key)));

                    ArrayList<String> value = (ArrayList)dataMap.get(key);
                    String val = "";
                    for (String tmp : value)
                    {
                        val += String.format("\"%1s\",", tmp);
                    }

                    sb.append(String.format("{\"address\": \"/groups/%1s/action/%2s\", \"value\":[%3s]},", id, key, val.substring(0, val.length() - 1)));
                }
                else
                {
                    LOG.warn("Unknown object passed in editGroup()! ==> " + dataMap.get(key).getClass());
                }
            }

            String retval = String.format("[{ \"success\" : %1s }]", sb.toString().substring(0, sb.toString().length()-1));
            LOG.info(String.format("Responding: %1s", retval));
            return Response.ok(retval).build();
        }
        catch (IOException iEx)
        {
            LOG.error("Unable to read POST data from groups!", iEx);
            return Response.serverError().entity(iEx).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteGroup(@Context HttpServletRequest request, @PathParam("id") String id)
    {
        LOG.debug(String.format("%1s (%2s)", request.getRequestURI(), request.getMethod()));

        bridge.deleteGroup(id);
        bridge.writeConfig();

        // return Response.ok(String.format("[{\"success\" : \"/groups/%1s\" deleted. }]", id)).build();
        return Response.ok(String.format("[{ \"success\" : \"/groups/%1s deleted\" }]", id)).build();
    }

}
