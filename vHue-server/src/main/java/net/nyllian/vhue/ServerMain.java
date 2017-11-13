package net.nyllian.vhue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Nyllian on 13/11/2017.
 *
 */
public class ServerMain
{
    private static final Logger LOG = LoggerFactory.getLogger(ServerMain.class);

    public static void main(String[] args)
    {
        LOG.trace("Starting application...");
        LOG.debug("Starting application...");
        LOG.info("Starting application...");
        LOG.warn("Starting application...");
        LOG.error("Starting application...");
    }
}
