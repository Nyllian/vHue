package net.nyllian.vhue;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.nyllian.vhue.model.Light;
import net.nyllian.vhue.model.LightState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * Created by Nyllian on 20/11/2017.
 */
public class LightsTest
{
    private static final Logger LOG = LoggerFactory.getLogger(LightsTest.class);

    public static void main(String args[]) throws IOException
    {
        LightState lightState1 = new LightState()
                .setAlert("select")
                .setBrightness(18)
                .setColorMode("ct")
                .setColorTemp(461)
                .setEffect("none")
                .setHue(0)
                .setOn(false)
                .setReachable(false)
                .setSaturation(0)
                .setXy(new double[]{
                        0.310669,
                        0.323961
                });

        Light light1 = new Light()
                .setModelId("LCT001")
                .setName("MiLight rgb WS2812B")
                .setSwitchVersion("66009461")
                .setType("Extended color light")
                .setUniqueId("1a2b3c465")
                .setState(lightState1);

        ObjectMapper serializer = new ObjectMapper();
        String serializedString = serializer.writeValueAsString(light1);
        LOG.warn(serializedString);

        Light light1a = serializer.readValue(serializedString, Light.class);
        LOG.warn(light1a.getSwitchVersion());

        LOG.trace("---------------------------------------------------------------------");
        Map<String, Light> lights = new LinkedHashMap<>();
        lights.put("3", light1);
        lights.put("1", light1a);
        LOG.info("\n\"lights\" : " + serializer.writerWithDefaultPrettyPrinter().writeValueAsString(lights));
    }
}
