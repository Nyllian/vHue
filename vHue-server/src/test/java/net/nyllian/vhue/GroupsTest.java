package net.nyllian.vhue;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.nyllian.vhue.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Nyllian on 20/11/2017.
 */
public class GroupsTest
{
    private static final Logger LOG = LoggerFactory.getLogger(GroupsTest.class);

    public static void main(String[] args) throws IOException
    {
        Group group1 = new Group();
        GroupAction groupAction1 = new GroupAction();
        groupAction1.setAlert("select");
        groupAction1.setBrightness(18);
        groupAction1.setColorMode("ct");
        groupAction1.setColorTemp(461);
        groupAction1.setEffect("none");
        groupAction1.setHue(0);
        groupAction1.setOn(false);
        groupAction1.setSaturation(0);
        groupAction1.setXy(new double[] { 0.310669, 0.323961 });
        group1.setGroupAction(groupAction1);
        group1.setGroupClass("Bedroom");

        Map<String, Light> lights = new LinkedHashMap<>();
        lights.put("1", createLight());

        // group1.setLights(new Light[] { createLight(), createLight() });
        String[] ids = lights.keySet().toArray(new String[lights.keySet().size()]);
        group1.setLightIds(ids);
        group1.setName("TV-Nyllian");
        GroupState groupState1 = new GroupState();
        groupState1.setAllOn(false);
        groupState1.setAnyOn(false);
        groupState1.setBrightness(18);
        groupState1.setLastUpdated(new Date());
        groupState1.setOn(false);
        group1.setGroupState(groupState1);
        group1.setType("Room");

        ObjectMapper serializer = new ObjectMapper();
        serializer.setDateFormat(new SimpleDateFormat("yyyy-MM-DD'T'HH24:mm:ss"));

        String serializedString = serializer
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(group1);
        LOG.warn(serializedString);
    }

    private static Light createLight()
    {
        LightState lightState1 = new LightState();
        lightState1.setAlert("select");
        lightState1.setBrightness(18);
        lightState1.setColorMode("ct");
        lightState1.setColorTemp(461);
        lightState1.setEffect("none");
        lightState1.setHue(0);
        lightState1.setOn(false);
        lightState1.setReachable(false);
        lightState1.setSaturation(0);
        lightState1.setXy(new double[]{ 0.310669, 0.323961});

        Light light1 = new Light();
        light1.setModelId("LCT001");
        light1.setName("MiLight rgb WS2812B");
        light1.setSwitchVersion("66009461");
        light1.setType("Extended color light");
        light1.setUniqueId("1a2b3c465");

        light1.setLightState(lightState1);

        return light1;
    }
}
