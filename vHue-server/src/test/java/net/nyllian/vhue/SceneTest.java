package net.nyllian.vhue;

import net.nyllian.vhue.model.LightState;
import net.nyllian.vhue.model.Scene;
import net.nyllian.vhue.model.views.SceneView;
import net.nyllian.vhue.util.HueUtils;
import net.nyllian.vhue.util.Serializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nyllian on 26/11/2017.
 *
 */
public class SceneTest
{
    public static void main(String[] args) throws Exception
    {
        new SceneTest();
        /*
        System.out.println(Serializer.SerializeJson(newScene));
        Scene scene = Serializer.SerializeJson(Serializer.SerializeJson(newScene), Scene.class);
        System.out.println("Scene (ser-des) ==> " + scene);

        // {"name":"Test","lights":["0"],"recycle":false,"appdata":{"version":1,"data":"IIsNw_r01"}}
        String jsonData = "{\"name\":\"Test\",\"lights\":[\"0\"],\"recycle\":false,\"appdata\":{\"version\":1,\"data\":\"IIsNw_r01\"}}";
        System.err.println(Serializer.SerializeJson(jsonData, Scene.class));
        */

    }

    public SceneTest() throws Exception
    {
        HashMap<String, LightState> map = new HashMap<>();
        map.put("1", new LightState());

        Map<String, Scene> scenes = new HashMap<>();
        Scene s1 = new Scene();
        s1.setLightStates(map);
        scenes.put("1", s1);
        scenes.put("2", new Scene().setName("Test2"));

        System.out.println(Serializer.SerializeJsonView(SceneView.SceneOnlyProperties.class, scenes.get("1")));
        System.out.println(HueUtils.getResponseAttributesSuccess(Serializer.SerializeJsonView(SceneView.LightStatesOnly.class, scenes.get("1").getLightStates().get("1")), "/scenes/1/lights/1/state"));
        System.out.println(HueUtils.getResponsePropertiesSuccess(Serializer.SerializeJsonView(SceneView.SceneOnlyProperties.class, scenes.get("1")), "/scenes/1"));
        // System.out.println(Serializer.SerializeJson(scenes));
    }
}
