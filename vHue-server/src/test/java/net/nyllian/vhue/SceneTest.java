package net.nyllian.vhue;

import net.nyllian.vhue.model.Scene;
import net.nyllian.vhue.util.Serializer;

/**
 * Created by Nyllian on 26/11/2017.
 *
 */
public class SceneTest
{
    public static void main(String[] args) throws Exception
    {
        Scene newScene = new Scene();
        System.out.println(Serializer.SerializeJson(newScene));
        Scene scene = Serializer.SerializeJson(Serializer.SerializeJson(newScene), Scene.class);
        System.out.println("Scene (ser-des) ==> " + scene);

        // {"name":"Test","lights":["0"],"recycle":false,"appdata":{"version":1,"data":"IIsNw_r01"}}
        String jsonData = "{\"name\":\"Test\",\"lights\":[\"0\"],\"recycle\":false,\"appdata\":{\"version\":1,\"data\":\"IIsNw_r01\"}}";
        System.err.println(Serializer.SerializeJson(jsonData, Scene.class));


    }
}
