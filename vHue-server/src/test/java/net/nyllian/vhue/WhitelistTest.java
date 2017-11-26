package net.nyllian.vhue;

import net.nyllian.vhue.model.Bridge;
import net.nyllian.vhue.model.BridgeConfig;
import net.nyllian.vhue.util.Serializer;

/**
 * Created by Nyllian on 26/11/2017.
 *
 */
public class WhitelistTest
{
    public static void main(String args[]) throws Exception
    {
        BridgeConfig bc = new BridgeConfig();
        bc.addToWhitelist("123", "Test");

        String jsonData = Serializer.SerializeJson((bc));
        BridgeConfig bc2 = Serializer.SerializeJson(jsonData, BridgeConfig.class);
    }
}
