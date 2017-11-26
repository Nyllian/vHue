package net.nyllian.vhue.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Nyllian on 25/11/2017.
 *
 */
public class ResourceManager<T>
{
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
}
