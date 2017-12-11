package net.nyllian.vhue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
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
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("1", new AtomicBoolean(false));
        map.put("2", new AtomicBoolean(true));

        if (map.containsKey("3"))
        {
            System.out.println("contains");
        }
        else
        {
            System.out.println("not");
        }
    }
}
