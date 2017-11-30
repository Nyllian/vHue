package net.nyllian.vhue;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.servlet.ServletException;
import java.io.File;
import java.net.MalformedURLException;
import java.util.logging.LogManager;

/**
 * Created by Nyllian on 13/11/2017.
 *
 */
public class ServerMain
{
    private static final Logger LOG = LoggerFactory.getLogger(ServerMain.class);

    public static void main(String[] args) throws Exception
    {
        new ServerMain();
    }

    public ServerMain() throws LifecycleException
    {
        int port = 80;
        Tomcat tomcatServer = new Tomcat();

        try
        {
            LogManager.getLogManager().reset();
            SLF4JBridgeHandler.install();

            String webappDir = "src/main/web/";
            tomcatServer.setPort(port);

            Context ctx = tomcatServer.addWebapp("", new File(webappDir).getAbsolutePath());
            File configFile = new File(webappDir + "WEB-INF/web.xml");
            LOG.trace(String.format("Following config file is used for context: %s", configFile.getAbsolutePath()));
            try
            {
                ctx.setConfigFile(configFile.toURI().toURL());
            }
            catch (MalformedURLException mEx)
            {
                LOG.error("Unable to load the specified web.xml file!", mEx);
            }

            Connector connector = new Connector();
            connector.setPort(port);
            tomcatServer.setConnector(connector);
            tomcatServer.start();
            tomcatServer.getServer().await();
        }
        catch (ServletException sEx)
        {
            LOG.error("The current IP Address could not be retrieved!", sEx);
        }
        finally
        {
            if (tomcatServer.getServer().getStateName().equalsIgnoreCase("STARTED"))
            {
                tomcatServer.stop();
            }

            tomcatServer.getServer().getCatalinaBase().delete();
        }
    }
}
