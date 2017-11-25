package net.nyllian.vhue.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by Nyllian on 16/11/2017.
 *
 */

public class HueServerStarter implements ServletContextListener
{
    private static final Logger LOG = LoggerFactory.getLogger(HueServerStarter.class);

    @Override
    public void contextInitialized(ServletContextEvent sce)
    {

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce)
    {

    }
}
