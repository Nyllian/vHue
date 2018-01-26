package net.nyllian.vhue.test;

import net.nyllian.vhue.model.Light;
import net.nyllian.vhue.model.views.LightView;
import net.nyllian.vhue.util.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Nyllian on 20/11/2017.
 *
 */
public class LightsTest
{
    private static final Logger LOG = LoggerFactory.getLogger(LightsTest.class);

    public static void main(String args[]) throws IOException
    {
        Light l1 = new Light();
        l1.setIpAddress("192.168.0.123");
        String s = Serializer.SerializeJsonView(LightView.LightProperties.class, l1);
        System.out.println(s);

        String s2 = Serializer.SerializeJson(l1);
        System.out.println(s2);
        Light l2 = Serializer.SerializeJson(s2, Light.class);

        System.out.println(l2.getIpAddress());

        Thread t = new Thread(new TestThread());
    }

}
class TestThread implements Runnable
{
    private AtomicBoolean running = new AtomicBoolean(true);

    @Override
    public void run()
    {
        while (running.get())
        {
            System.err.println("Send command to the specified light...");
        }
    }

    public void stopRunning()
    {
        running.set(false);
    }
}