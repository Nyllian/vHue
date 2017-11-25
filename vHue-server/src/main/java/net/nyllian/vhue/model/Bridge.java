package net.nyllian.vhue.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Nyllian on 22/11/2017.
 *
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
@JsonPropertyOrder({"lights", "groups", "schedules", "rules", "resourcelinks", "sensors", "config", "scenes"})
public class Bridge implements IJSon
{
    @JsonProperty("config")
    private DeviceConfig deviceConfig;

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


    // addLight
    // addGroup
    // addSchedule
    // addRule
    // addResourceLink???
    // addSensor
    // addScene


    public DeviceConfig getDeviceConfig()
    {
        return deviceConfig;
    }

    public Bridge setDeviceConfig(DeviceConfig deviceConfig)
    {
        this.deviceConfig = deviceConfig;
        return this;
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
}
