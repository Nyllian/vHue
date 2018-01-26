package net.nyllian.vhue.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import net.nyllian.vhue.util.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by Nyllian on 22/11/2017.
 *
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
@JsonPropertyOrder({"lights", "groups", "schedules", "rules", "resourcelinks", "sensors", "config", "scenes"})
public class Bridge implements IJSon
{
    @JsonIgnore
    private static final Logger LOG = LoggerFactory.getLogger(BridgeConfig.class);
    @JsonIgnore
    public static String configStoreLocation = "./conf/bridgeconfig.json";

    @JsonProperty("config")
    private BridgeConfig bridgeConfig;
    @JsonProperty("lights")
    private Map<String, Light> lights = new LinkedHashMap<>();
    @JsonProperty("groups")
    private Map<String, Group> groups = new LinkedHashMap<>();
    @JsonProperty("schedules")
    private Map<String, Schedule> schedules = new LinkedHashMap<>();
    @JsonProperty("rules")
    private Map<String, Rule> rules = new LinkedHashMap<>();
    @JsonProperty("sensors")
    private Map<String, Sensor> sensors = new LinkedHashMap<>();
    @JsonProperty("resourceLinks")
    private Map<String, ResourceLink> resourceLinks = new LinkedHashMap<>();
    @JsonProperty("scenes")
    private Map<String, Scene> scenes = new LinkedHashMap<>();

    // @JsonIgnore
    // private Map<String, Capabilities> capabilities = new TreeMap<>();
    // private Capabilities capabilities = new Capabilities(this);
    @JsonIgnore
    private Map<String, Object> discoveredLights = new TreeMap<>();

    /**
     * Write the bridge config
     */
    public void writeConfig()
    {
        try
        {
            FileWriter fw = new FileWriter(configStoreLocation);
            fw.write(Serializer.SerializeJson(this));
            fw.close();
        }
        catch (JsonProcessingException jEx)
        {
            LOG.error("Unknown exception while serializing object", jEx);
        }
        catch (IOException ioEx)
        {
            LOG.error("Error while reading the config file.", ioEx);
        }
    }

    /**
     * Simulate a link button press event
     */
    public void pressLinkButton()
    {
        Runnable press = () -> {
            try
            {
                if (bridgeConfig.isLinkButton())
                {
                    Thread.sleep(10000);
                    bridgeConfig.setLinkButton(false);
                }
            }
            catch (Exception ex)
            {
                LOG.error("Link button could not be reset!", ex);
            }
        };

        new Thread(press).start();
    }

    public void discoverLights()
    {
        // TODO: Search for new lights...
        // TODO: Depending on the discovered lights, check ifi already added or not
        // TODO: Should it be possible to add 2 lights onto multiple rooms / apps???

        addDiscoveredLight(new DiscoveredLight().setName("NewDummyLight"));

    }

    public Map<String, Object> getDiscoveredLights()
    {
        return discoveredLights;
    }

    public void addDiscoveredLight(DiscoveredLight light)
    {
        discoveredLights.put("lastscan", new Date());

        // 3 || 2 & 3
        Set<String> keys = new HashSet<>(discoveredLights.keySet());
        keys.addAll(lights.keySet());
        String id = "1";
        if (!keys.contains(keys.size() + ""))
        {
            id = String.format("%s", keys.size());
        }
        else
        {
            String lastId = (String) keys.toArray()[keys.size()-1];
            id = Integer.toString(Integer.valueOf(lastId)+1);
        }

        LOG.info("addDiscoveredLight ==> " + id);
        discoveredLights.put(String.format("%s", id), light);

        Light discoveredLight = new Light();
        discoveredLight.getLightState().setReachable(true);
        discoveredLight.getLightState().setColorMode("xy");
        // Add direct to the list to keep the 'id' synced
        lights.put(id, discoveredLight);
    }

    public void deleteDiscoveredLight(String id)
    {
        discoveredLights.remove(id);
    }

    public Light getLight(String id)
    {
        return lights.get(id);
    }

    public String addLight(Light light)
    {
        // exclude '0' index
        String newId = String.format("%s", lights.size()+1);
        lights.put(newId, light);

        return newId;
    }

    public void deleteLight(String id)
    {
        deleteDiscoveredLight(id);
        lights.remove(id);
    }

    public Group getGroup(String id)
    {
        return groups.get(id);
    }

    public String addGroup(Group group)
    {
        String newId = String.format("%s", groups.size()+1);
        LOG.info("Creating new group with id: " + newId);
        groups.put(newId, group);

        return newId;
    }

    public void deleteGroup(String id)
    {
        groups.remove(id);
    }

    public Scene getScene(String id)
    {
        return scenes.get(id);
    }

    public String addScene(Scene scene)
    {
        String newId = String.format("%s", scenes.size()+1);
        scenes.put(newId, scene);

        return newId;
    }

    public void deleteScene(String id)
    {
        scenes.remove(id);
    }

    public Sensor getSensor(String id)
    {
        return sensors.get(id);
    }

    public void addSensor(Sensor sensor)
    {
        String newId = String.format("%s", sensors.size()+1);
        sensors.put(newId, sensor);
    }

    public void deleteSensor(String id)
    {
        sensors.remove(id);
    }

    public void addSchedule(Schedule schedule)
    {
        String newId = String.format("%s", schedules.size()+1);
        schedules.put(newId, schedule);
    }

    public void addRule(Rule rule)
    {
        String newId = String.format("%s", rules.size()+1);
        rules.put(newId, rule);
    }

    public void addResourceLink(ResourceLink resourceLink)
    {
        String newId = String.format("%s", resourceLinks.size()+1);
        resourceLinks.put(newId, resourceLink);
    }
/*
    public void addCapability(Capabilities capability)
    {
        String newId = String.format("%s", capabilities.size()+1);
        capabilities.put(newId, capability);
    }
*/
    public BridgeConfig getBridgeConfig()
    {
        return bridgeConfig;
    }

    public Map<String, Light> getLights()
    {
        return lights;
    }

    public Bridge setLights(Map<String, Light> lights)
    {
        this.lights = lights;
        return this;
    }

    public Map<String, Group> getGroups()
    {
        return groups;
    }

    public Bridge setGroups(Map<String, Group> groups)
    {
        this.groups = groups;
        return this;
    }

    public Map<String, Schedule> getSchedules()
    {
        return schedules;
    }

    public Bridge setSchedules(Map<String, Schedule> schedules)
    {
        this.schedules = schedules;
        return this;
    }

    public Map<String, Rule> getRules()
    {
        return rules;
    }

    public Bridge setRules(Map<String, Rule> rules)
    {
        this.rules = rules;
        return this;
    }

    public Map<String, Sensor> getSensors()
    {
        return sensors;
    }

    public Bridge setSensors(Map<String, Sensor> sensors)
    {
        this.sensors = sensors;
        return this;
    }

    public Map<String, ResourceLink> getResourceLinks()
    {
        return resourceLinks;
    }

    public Bridge setResourceLinks(Map<String, ResourceLink> resourceLinks)
    {
        this.resourceLinks = resourceLinks;
        return this;
    }

    public Map<String, Scene> getScenes()
    {
        return scenes;
    }

    public Bridge setScenes(Map<String, Scene> scenes)
    {
        this.scenes = scenes;
        return this;
    }

    public Bridge setBridgeConfig(BridgeConfig bridgeConfig)
    {
        this.bridgeConfig = bridgeConfig;
        return this;
    }

}
