package net.nyllian.vhue.test;

import net.nyllian.vhue.model.*;
import net.nyllian.vhue.util.Serializer;

/**
 * Created by Nyllian on 22/11/2017.
 *
 */
public class BridgeTest
{
    public static void main(String[] args) throws Exception
    {
        Bridge b = new Bridge();
        b.setBridgeConfig(new BridgeConfig());
        b.getBridgeConfig().addToWhitelist("8f2ce5f6591542cfa1ca7978a7eb9ef7", "Nyllian!!!");
        String jsonB = Serializer.SerializeJson(b);
        System.out.println(jsonB);
        Bridge b2 = Serializer.SerializeJson(jsonB, Bridge.class);

        System.err.println("Starting with jsonData...");
        String jsonData = "{\"lights\" : {\"1\" : {\"modelid\" : \"DUM001\",\"name\" : \"Dummy Light\",\"state\" : {\"alert\" : \"select\",\"bri\" : 18,\"colormode\" : \"ct\",\"ct\" : 461,\"effect\" : \"none\",\"hue\" : 0,\"on\" : false,\"reachable\" : true,\"sat\" : 0,\"xy\" : [ 0.310669, 0.323961 ]},\"swversion\" : \"66009461\",\"type\" : \"Dummy color Light\",\"uniqueid\" : \"37550654df\",\"manufacturername\" : \"Nyllian\"},\"2\" : {\"modelid\" : \"DUM001\",\"name\" : \"Dummy Light\",\"state\" : {\"alert\" : \"select\",\"bri\" : 18,\"colormode\" : \"ct\",\"ct\" : 461,\"effect\" : \"none\",\"hue\" : 0,\"on\" : false,\"reachable\" : true,\"sat\" : 0,\"xy\" : [ 0.310669, 0.323961 ]},\"swversion\" : \"66009461\",\"type\" : \"Dummy color Light\",\"uniqueid\" : \"831f4e6975\",\"manufacturername\" : \"Nyllian\"}},\"groups\" : {\"1\" : {\"action\" : {\"alert\" : \"select\",\"bri\" : 20,\"colormode\" : \"ct\",\"ct\" : 461,\"effect\" : \"none\",\"hue\" : 0,\"on\" : false,\"sat\" : 0,\"xy\" : [ 0.310669, 0.323961 ]},\"class\" : \"Living room\",\"lights\" : [ ],\"name\" : \"Woonkamer\",\"state\" : {\"all_on\" : false,\"any_on\" : false,\"lastupdated\" : \"2017-11-26T16:30:50\",\"on\" : false,\"brightness\" : 0},\"type\" : \"Room\"}},\"schedules\" : { },\"rules\" : { },\"sensors\" : { },\"config\" : {\"datastoreversion\" : 1,\"mac\" : \"F0:1F:AF:43:BC:AE\",\"name\" : \"vHue\",\"bridgeid\" : \"1234657890123456\",\"swversion\" : \"1709131301\",\"factorynew\" : false,\"apiversion\" : \"1.19.0\",\"modelid\" : \"vHUE-001\",\"ipaddress\" : \"192.168.0.14\",\"gateway\" : \"192.168.0.14\",\"netmask\" : \"255.255.255.0\",\"proxyaddress\" : \"none\",\"proxyport\" : 0,\"dhcp\" : true,\"linkbutton\" : true,\"portalservices\" : true,\"zigbeechannel\" : 1,\"utc\" : \"2017-11-26T16:29:40\",\"localtime\" : \"2017-11-26T16:29:40\",\"timezone\" : \"Brussels/Europe\",\"whitelist\" : {\"303a22f8e5e24b4bb9f81eebdc4de86a\" : {\"last use date\" : \"2017-11-26T16:29:41\",\"create date\" : \"2017-11-26T16:29:41\",\"name\" : \"192.168.0.248\"}},\"swupdate\" : {\"checkforupdate\" : false}},\"scenes\" : { },\"resourceLinks\" : { }}";
        Bridge bridge = Serializer.SerializeJson(jsonData, Bridge.class);

        System.out.println(bridge);
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

        light1.setLightState(lightState1);

        return light1;
    }
}
