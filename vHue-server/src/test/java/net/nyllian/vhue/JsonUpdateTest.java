package net.nyllian.vhue;

import net.nyllian.vhue.model.Light;
import net.nyllian.vhue.model.LightState;
import net.nyllian.vhue.util.Serializer;

/**
 * Created by Nyllian on 26/11/2017.
 *
 */
public class JsonUpdateTest
{
    public static void main(String[] args) throws Exception
    {
        Light l = new Light();
        l.getLightState().setOn(true);
//        System.err.println(Serializer.SerializeJson(l));

        String jsonData = "{\"bri\":90,\"xy\":[0.624511,0.264368]}";

        LightState newLightState = Serializer.UpdateObject(l.getLightState(), jsonData);
        Light updatedLight = l.setLightState(newLightState);
        System.out.println(Serializer.SerializeJson(updatedLight));
    }
}
