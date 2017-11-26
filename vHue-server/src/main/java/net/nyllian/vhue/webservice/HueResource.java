package net.nyllian.vhue.webservice;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import net.nyllian.vhue.util.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Nyllian on 16/11/2017.
 *
 */
@Path("/")
@Singleton
public class HueResource
{
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private StringBuilder template = new StringBuilder();
    private ResourceManager manager;

    public HueResource(@Context Application application)
    {
        manager = (ResourceManager)application.getProperties().get("manager");
    }

    @GET
    @Path("/description.xml")
    @Produces(MediaType.APPLICATION_XML)
    public Response getDescription()
    {
        if (template.length() == 0)
        {
            InitializeTemplate();
        }

        return Response.ok(template.toString())
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @DELETE
    public Response cleanBridge(@Context HttpServletRequest request)
    {
        LOG.debug(String.format("%1s (%2s)", request.getRequestURI(), request.getMethod()));
        // Clean entire bridge...
        return Response.ok().build();
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

    /**
     * Initialize the template
     */
    @SuppressWarnings("unchecked")
    private void InitializeTemplate()
    {
        try
        {
            Configuration config = new Configuration(Configuration.VERSION_2_3_27);
            config.setDefaultEncoding("UTF-8");
            config.setLocale(Locale.US);
            config.setClassForTemplateLoading(this.getClass(), "/ftl/");
            Template tpl = config.getTemplate("description.xml");

            Map<String, String> tplMap = (Map<String, String>) manager.getResource("tplMap");

            StringWriter templateStringWriter = new StringWriter();
            tpl.process(tplMap, templateStringWriter);
            template.append(templateStringWriter);
        }
        catch (IOException | TemplateException ioEx)
        {
            LOG.error("The description template could not be loaded!", ioEx);
        }
    }
}
