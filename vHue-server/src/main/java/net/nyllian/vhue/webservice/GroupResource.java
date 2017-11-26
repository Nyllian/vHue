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

/**
 * Created by Nyllian on 26/11/2017.
 *
 */
@Path("/api/{user}/groups")
@Produces(MediaType.APPLICATION_JSON)
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
            LOG.warn("newGroup ==> \n" + postData);

            // The app does not recognize rooms without assigned devices
            String groupId = bridge.addGroup(Serializer.SerializeJson(postData, Group.class));

            // All lights that were assigned to this group must be removed from the DiscoveredLights
            Group newGroup = bridge.getGroup(groupId);
            for (String lightId : newGroup.getLightIds())
            {
                bridge.deleteDiscoveredLight(lightId);
            }
            bridge.writeConfig();

            return Response.ok(String.format("[ {\"success\" : {\"id\" : \"%1s\"}} ]", groupId)).build();
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
    public Response editGroup(@Context HttpServletRequest request, @PathParam("id") String id)
    {
        LOG.debug(String.format("%1s (%2s)", request.getRequestURI(), request.getMethod()));

        try
        {
            String postData = IOUtils.toString(request.getInputStream(), Charset.forName("UTF-8"));
            LOG.warn("SetLightAttributes ==>\n" + postData);

            Group currentGroup = bridge.getGroup(id);
            Group updatedGroup = Serializer.UpdateObject(currentGroup, postData);
            bridge.getGroups().put(id, updatedGroup);

            return Response.ok(String.format("[{ \"success\" : { \"/groups/%1s\" : \"Group Edited\"}}]", id)).build();
        }
        catch (IOException iEx)
        {
            LOG.error("Unable to read POST data from lights!", iEx);
            return Response.serverError().entity(iEx).build();
        }
    }

    @PUT
    @Path("/{id}/action")
    public Response changeGroupState(@Context HttpServletRequest request, @PathParam("id") String id)
    {
        LOG.debug(String.format("%1s (%2s)", request.getRequestURI(), request.getMethod()));

        try
        {
            String postData = IOUtils.toString(request.getInputStream(), Charset.forName("UTF-8"));
            if (id.equals("0"))
            {
                // Change the groupAction of all groups
                for (String groupKey : bridge.getGroups().keySet())
                {
                    GroupAction currentGroupAction = bridge.getGroup(groupKey).getGroupAction();
                    GroupAction newGroupAction = Serializer.UpdateObject(currentGroupAction, postData);
                    bridge.getGroup(groupKey).setGroupAction(newGroupAction);
                }
            }
            else
            {
                GroupAction currentGroupAction = bridge.getGroup(id).getGroupAction();
                GroupAction newGroupAction = Serializer.UpdateObject(currentGroupAction, postData);
                bridge.getGroup(id).setGroupAction(newGroupAction);
            }

            return Response.ok(String.format("[{ \"success\" : { \"/groups/%1s/action\" : \"Group action changed.\" }}]", id)).build();
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
        return Response.ok("[{ \"success\" : { \"/groups\" : \"Group removed\"}}]").build();
    }

}
