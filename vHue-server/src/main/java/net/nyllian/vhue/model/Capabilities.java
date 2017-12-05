package net.nyllian.vhue.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by Nyllian on 5/12/2017.
 *
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
// @JsonPropertyOrder({"lights", "groups", "schedules", "rules", "resourcelinks", "sensors", "config", "scenes"})
public class Capabilities implements IJSon
{
    private static final Logger LOG = LoggerFactory.getLogger(Capabilities.class);
    private static final int MAX = 1024;

    private Bridge bridge;

    @JsonProperty("lights")
    private Map<String, Integer> lights = new HashMap<>();
    private int sensors;
    private int groups;
    private int scenes;
    private int rules;
    @JsonProperty("schedules")
    private Map<String, Schedule> schedules;
    @JsonProperty("resourcelinks")
    private Map<String, ResourceLink> resourcelinks;
    @JsonProperty("whitelists")
    private Map<String, Whitelist> whitelists;
    @JsonProperty("timezones")
    private Map<String, List<String>> timezones = new HashMap<>();

    @JsonCreator
    public Capabilities()
    {
        // Default constructor for Json
    }

    /*
    // Initializer for Json
    public Capabilities(@Context Application application)
    {
        ResourceManager manager = (ResourceManager)application.getProperties().get("manager");
        bridge = (Bridge) manager.getResource("bridge");

        lights = 0;

        // rules = bridge.getRules();
        schedules = bridge.getSchedules();
        resourcelinks = bridge.getResourceLinks();
        whitelists = bridge.getBridgeConfig().getWhiteList();
        timezones.put("values", Arrays.asList(TimeZone.getAvailableIDs()));
    }
    */

    public Capabilities(Bridge bridge)
    {
        this.bridge = bridge;

        lights.put("available", (MAX - bridge.getLights().size()));
        timezones.put("values", Arrays.asList(TimeZone.getAvailableIDs()));
    }

    public Map<String, Integer> getAvailableLights()
    {
        int cachedSize = (MAX - bridge.getLights().size());

        if (lights.get("available") != cachedSize)
        {
            // Update the size
            lights.put("available", cachedSize);
        }

        return lights;
    }

    public Map<String, List<String>> getTimezones()
    {
        return timezones;
    }

    /*
{
"lights":{
  "available": 10,
},
"sensors":{
  "availble": 60,
  "clip": {
      "available": 60,
  },
  "zll": {
      "available": 60,
  },
  "zgp": {
      "available": 60
  }
},
"groups": {...},
"scenes": {
      "available": 100,

      "lightstates": {
          "available": 1500
      }
"rules": {...},
"schedules": {...},
"resourcelinks": {...},
"whitelists": {...}
"timezones": {
      "values":[
         "Africa/Abidjan",
         "Africa/Accra",
         (...)
         "Pacific/Wallis",
         "US/Pacific-New" }
 }
}
 */
}
