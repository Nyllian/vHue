package net.nyllian.vhue.webservice;

import net.nyllian.vhue.model.Bridge;
import net.nyllian.vhue.util.Randomizer;
import net.nyllian.vhue.util.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Nyllian on 16/11/2017.
 *
 */
@Path("/api")
@Singleton
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ApiResource
{
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private Bridge bridge;

    public ApiResource(@Context Application application)
    {
        ResourceManager manager = (ResourceManager)application.getProperties().get("manager");
        bridge = (Bridge) manager.getResource("bridge");
    }

    @POST
    public Response newUser(@Context HttpServletRequest request)
    {
        String newUserToken = Randomizer.generateUserToken();
        bridge.getBridgeConfig().addToWhitelist(newUserToken, request.getRemoteHost());
        bridge.writeConfig();

        return Response.ok(String.format("[{\"success\": {\"username\": \"%1s\"}}]", newUserToken)).build();
    }

    @GET
    @Path("/{default: .*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response unmapped(@Context HttpServletRequest request)
    {
        if (!request.getRequestURI().endsWith("favicon.ico"))
        {
            LOG.warn(String.format("Unmapped request received (%1s): %2s", request.getRemoteHost(), request.getRequestURL()));
        }

        return Response.status(404).build();
    }
}
