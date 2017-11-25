package net.nyllian.vhue.webservice;

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
public class ApiResource
{
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Context
    private Application application;

    @POST
    public Response xx()
    {
        String retval = "[{\"success\": {\"username\": \"a7161538be80d40b3de98dece6e91f904dc96170\"}}]";
        return Response.ok(retval).build();
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


    /*
    @GET
    @Path("/{user}")
    public Response getUsername(@PathParam("user") String username)
    {
        LOG.trace(String.format("(getUsername) Request received from user %1s", username));

        // This is the button pressed method
        // Whitelist the given user

        // Check authentication!

        String retval = "[{\"success\": {\"username\": \"a7161538be80d40b3de98dece6e91f904dc96170\"}}]";

        return Response.ok(retval).build();
    }
    */

    /*
    @GET
    @Path("/{user}/config")
    @SuppressWarnings("unchecked")
    public Response getUserConfig(@PathParam("user") String username)
    {
        LOG.trace(String.format("(getUserConfig) Request received from user %1s", username));

        // Check authentication!
        String retval = "{}";

        // check if username = nouser
        if (username.equals("nouser"))
        {
            // Whitelist the user
            LOG.info(String.format("Creating new userId: %1s", "a7161538be80d40b3de98dece6e91f904dc96170"));
        }

        if (username.equals("a7161538be80d40b3de98dece6e91f904dc96170"))
        {
            Map<String, String> tplMap = (Map<String, String>)application.getProperties().get("tplMap");
            tplMap.get("bridgeId");

            retval = "{" +
                        "\"datastoreversion\": 59, " +
                        "\"mac\": \"e4:a4:71:09:b0:36\", " +
                        "\"name\": \"Philips hue\", " +
                        "\"bridgeid\": " +
                        "\"E4A471FFFE09B036\", " +
                        "\"swversion\": \"1709131301\", " +
                        "\"factorynew\": false, " +
                        "\"apiversion\": \"1.19.0\", " +
                        "\"modelid\": \"BSB002\"" +
                    "}";
        }

        /*
        // Return the config
        String
        */
        /*
            {
                "datastoreversion": 59,
                "mac": "e4:a4:71:09:b0:36",
                "name": "Philips hue",
                "bridgeid": "E4A471FFFE09B036",
                "swversion": "1709131301",
                "factorynew": false,
                "apiversion": "1.19.0",
                "modelid": "BSB002"
            }

        return Response.ok(retval).build();
    }
    */

    /*
192.168.254.120 - - [20/Nov/2017 19:04:17] "POST /api/ HTTP/1.1" 200 -
in post method
/api/
{"devicetype":"Hue 2#Samsung SM-G925F"}
[
    {
        "success": {
            "username": "a7161538be80d40b3de98dece6e91f904dc96170"
        }
    }
]

192.168.254.120 - - [20/Nov/2017 19:06:29] "GET /api/nouser/config HTTP/1.1" 200 -
192.168.254.120 - - [20/Nov/2017 19:06:29] "GET /api/a7161538be80d40b3de98dece6e91f904dc96170 HTTP/1.1" 200 -
192.168.254.120 - - [20/Nov/2017 19:06:29] "GET /api/a7161538be80d40b3de98dece6e91f904dc96170/config HTTP/1.1" 200 -
192.168.254.120 - - [20/Nov/2017 19:06:30] "GET /api/a7161538be80d40b3de98dece6e91f904dc96170 HTTP/1.1" 200 -
Sending M Search response

     */
/*
    @GET
    @Path("/{default: .*}")
    public Response unmapped(@Context HttpServletRequest request)
    {
        if (!request.getRequestURI().endsWith("favicon.ico"))
        {
            LOG.warn(String.format("Unmapped request received (%1s): %2s", request.getRemoteHost(), request.getRequestURL()));
        }
        // LOG.warn(String.format("Unmapped request received (%1s): %2s", request.getRemoteHost(), request.getRequestURI()));

        return Response.status(404).build();
    }

    /*
    // /api/nouser/config
    // reply:
        {"datastoreversion": 59, "mac": "e4:a4:71:09:b0:36", "name": "Philips hue", "bridgeid": "E4A471FFFE09B036", "swversion": "1709131301", "factorynew": false, "apiversion": "1.19.0", "modelid": "BSB002"}
     */
}
