package net.nyllian.vhue;

import net.nyllian.vhue.model.*;
import net.nyllian.vhue.util.Serializer;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Nyllian on 22/11/2017.
 *
 */
public class BridgeTest
{
    public static void main(String[] args) throws Exception
    {
        Device device = new DeviceConfig()
                .setIpAddress(Inet4Address.getLocalHost().getHostAddress())
                .setGateway(Inet4Address.getLocalHost().getHostAddress())
                .setNetmask("255.255.255.0")
                .setProxyAddress("none")
                .setProxyPort(0)
                .setDhcp(true)
                .setLinkButton(true)
                .setPortalServices(true)
                .setZigbeeChannel(15)
                .setUtc(new Date())
                .setLocaltime(new Date())
                .setTimezone("Brussels/Europe")
                .setSwitchUpdate(false)
                .addToWhitelist("132465789123456789123456798", "Samsung vHue client")
                // Device Properties
                .setDatastoreVersion(1)
                .setMacAddress("12:34:56:78:90:AB")
                .setName("vHue")
                .setId("1234657890123456")
                .setSwitchVersion("0000000001")
                .setFactoryNew(false)
                .setApiVersion("0.1.0")
                .setModelId("vHUE-001");

        DeviceConfig deviceConfig = (DeviceConfig) device;

        Map<String, Light> lights = new LinkedHashMap<>();
        // lights.put("3", createLight());
        // lights.put("1", createLight());

        Bridge bridge = new Bridge()
                .setDeviceConfig(deviceConfig);
                // .setLights(lights);

        System.out.println(Serializer.SerializeJson(bridge));
    }

    private static Light createLight()
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
                .setXy(new double[]{ 0.310669, 0.323961});

        Light light1 = new Light()
                .setModelId("LCT001")
                .setName("MiLight rgb WS2812B")
                .setSwitchVersion("66009461")
                .setType("Extended color light")
                .setUniqueId("1a2b3c465");

        light1.setState(lightState1);

        return light1;
    }
}
