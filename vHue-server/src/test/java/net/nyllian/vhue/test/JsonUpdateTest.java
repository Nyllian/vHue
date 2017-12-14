package net.nyllian.vhue.test;

import net.nyllian.vhue.util.HueUtils;

/**
 * Created by Nyllian on 26/11/2017.
 *
 */
public class JsonUpdateTest
{
    public static void main(String[] args) throws Exception
    {
        String postData = "{\"name\":\"1onsondergang\",\"lights\":[\"7\",\"3\"],\"picture\":\"\",\"appdata\":{\"version\":1,\"data\":\"HasOS_r01_d15\"}}";
        String lightState = "{\"on\":false,\"bri\":18,\"xy\":[0.310669,0.323961]}";

        System.out.println(HueUtils.getResponsePropertiesSuccess(postData, "/scenes/1"));
        System.out.println(HueUtils.getResponseAttributesSuccess(lightState, "/lights/3/state"));
    }
}
