package net.nyllian.vhue.server;

import net.nyllian.vhue.model.Bridge;
import net.nyllian.vhue.model.Capabilities;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Nyllian on 25/11/2017.
 *
 */
public class ResourceManager<T>
{
    public static final String MANAGER = "manager";
    public static final String RESOURCE_BRIDGE = "bridge";
    public static final String RESOURCE_CAPABILITIES = "capabilities";

    private Map<String, T> resourceMap;

    public ResourceManager()
    {
        this.resourceMap = new LinkedHashMap<>();
    }

    public Map<String, T> getResourceMap()
    {
        return resourceMap;
    }

    public void addResource(String key, T val)
    {
        this.resourceMap.put(key, val);
    }

    public T getResource(String key)
    {
        return resourceMap.get(key);
    }

    public Bridge getBridge()
    {
        return (Bridge) resourceMap.get(RESOURCE_BRIDGE);
    }

    public Capabilities getCapabilities()
    {
        return (Capabilities) resourceMap.get(RESOURCE_CAPABILITIES);
    }
}
