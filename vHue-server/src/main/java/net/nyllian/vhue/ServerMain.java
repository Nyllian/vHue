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

            String docBase = new File(".").getAbsolutePath();
            tomcatServer.setBaseDir(docBase);
            tomcatServer.setHostname("vHue");
            tomcatServer.getHost().setAutoDeploy(true);
            tomcatServer.getHost().setDeployOnStartup(true);
            Context ctx = tomcatServer.addWebapp("", docBase + "/vHue-webapp.war");
            ctx.setName("vHue");
            ctx.setDisplayName("vHue");

            Connector connector = new Connector();
            connector.setPort(port);
            tomcatServer.setConnector(connector);
            tomcatServer.start();
            tomcatServer.getServer().await();
        }
        catch (ServletException sEx)
        {
            LOG.error("Unable to start the tomcat server!", sEx);
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
