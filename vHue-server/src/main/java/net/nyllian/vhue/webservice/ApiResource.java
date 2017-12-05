package net.nyllian.vhue.webservice;

import net.nyllian.vhue.model.Bridge;
import net.nyllian.vhue.model.UnauthorizedUser;
import net.nyllian.vhue.util.Randomizer;
import net.nyllian.vhue.server.ResourceManager;
import net.nyllian.vhue.util.Serializer;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

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
        bridge = manager.getBridge();
    }

    @POST
    @SuppressWarnings("unchecked")
    public Response newUser(@Context HttpServletRequest request)
    {
        LOG.debug(String.format("%s (%s)", request.getRequestURI(), request.getMethod()));

        try
        {
            String postData = IOUtils.toString(request.getInputStream(), Charset.forName("UTF-8"));
            LOG.info(String.format("Received: %s", postData));

            Map<String, Object> dataMap = Serializer.SerializeJson(postData, Map.class);

            // Wait 5 seconds to simulate a 'button press'
            Thread.sleep(1000);
            bridge.pressLinkButton();

            String newUserToken = Randomizer.generateUserToken();
            bridge.getBridgeConfig().addToWhitelist(newUserToken, dataMap.get("devicetype").toString());
            bridge.writeConfig();

            String retval = String.format("[{\"success\": {\"username\": \"%s\"}}]", newUserToken);
            LOG.info(String.format("Responding: %s", retval));
            return Response.ok(retval).build();

            /*
            // This is only applicable with the App
            // Not on a Philips TV
            if (dataMap.get("generateclientkey") instanceof Boolean)
            {
                boolean generateKey = (boolean)dataMap.get("generateclientkey");
                if (generateKey)
                {
                    String newUserToken = Randomizer.generateUserToken();
                    bridge.getBridgeConfig().addToWhitelist(newUserToken, dataMap.get("devicetype").toString());
                    bridge.writeConfig();

                    String retval = String.format("[{\"success\": {\"username\": \"%s\"}}]", newUserToken);
                    LOG.info(String.format("Responding: %s", retval));
                    return Response.ok(retval).build();
                }
                else
                {
                    return Response.ok(new UnauthorizedUser(request.getRequestURI())).build();
                }
            }
            else
            {
                return Response.ok(new UnauthorizedUser(request.getRequestURI())).build();
            }
            */
        }
        catch (IOException | InterruptedException ioEx)
        {
            LOG.error("Error occurred while creating newUser!", ioEx);
            return Response.serverError().entity(ioEx).build();
        }
    }

    @GET
    @Path("/config}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getConfig(@Context HttpServletRequest request)
    {

        LOG.debug(String.format("%s (%s)", request.getRequestURI(), request.getMethod()));

        try
        {
            return Response.ok(Serializer.SerializeJson(bridge.getBridgeConfig())).build();
        }
        catch (Exception ex)
        {
            LOG.error("Unable to serialize the object", ex);
            return Response.serverError().entity(ex).build();
        }
    }


    @GET
    @Path("/{default: .*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response unmapped(@Context HttpServletRequest request)
    {
        if (!request.getRequestURI().endsWith("favicon.ico"))
        {
            LOG.warn(String.format("Unmapped request received (%s): %s", request.getRemoteHost(), request.getRequestURL()));
        }

        return Response.status(404).build();
    }
}
