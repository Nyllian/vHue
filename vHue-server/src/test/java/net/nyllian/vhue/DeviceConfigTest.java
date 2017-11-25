package net.nyllian.vhue;

import net.nyllian.vhue.model.DeviceConfig;
import net.nyllian.vhue.util.Serializer;

/**
 * Created by Nyllian on 23/11/2017.
 */
public class DeviceConfigTest
{
    public static void main(String[] args) throws Exception
    {
        new DeviceConfigTest();
    }

    public DeviceConfigTest() throws Exception
    {
        System.out.println(
                Serializer.SerializeJson(
                        new DeviceConfig()
                                .setSwitchUpdate(false)
                                .addToWhitelist("12345678901234567890", "vHue-Test")
                                .addToWhitelist("98798798798798798799", "vHue-Test2")
                )
        );

    }
}
