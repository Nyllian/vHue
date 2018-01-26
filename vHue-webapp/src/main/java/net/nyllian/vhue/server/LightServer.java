package net.nyllian.vhue.server;

import net.nyllian.vhue.model.Bridge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Nyllian on 14/12/2017.
 *
 */
public class LightServer implements Runnable
{
    private static final Logger LOG = LoggerFactory.getLogger(LightServer.class);
    private Bridge bridge;

    public LightServer(Bridge bridge)
    {
        // TODO: Here we will specify which data that must be sent to the light and how
        this.bridge = bridge;
    }

    @Override
    public void run()
    {

    }
}
