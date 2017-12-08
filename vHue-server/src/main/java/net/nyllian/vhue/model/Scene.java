package net.nyllian.vhue.model;

import com.fasterxml.jackson.annotation.*;
import net.nyllian.vhue.model.views.LightStateView;
import net.nyllian.vhue.model.views.SceneView;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Nyllian on 20/11/2017.
 *
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
public class Scene implements IJSon
{
    @JsonView(SceneView.PictureOnly.class)
    @JsonProperty("picture")
    private String picture = "";
    @JsonView(SceneView.LockedOnly.class)
    @JsonProperty("locked")
    private boolean locked = false;
    @JsonView(SceneView.NameOnly.class)
    @JsonProperty("name")
    private String name = "";
    @JsonView(SceneView.OwnerOnly.class)
    @JsonProperty("owner")
    private String owner = "";
    @JsonView(SceneView.VersionOnly.class)
    @JsonProperty("version")
    private int version = 2;
    @JsonView(SceneView.LastUpdatedOnly.class)
    @JsonProperty("lastupdated")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date lastUpdated = new Date();
    @JsonView(SceneView.RecycleOnly.class)
    @JsonProperty("recycle")
    private boolean recycle = true;
    @JsonView(SceneView.AppDataOnly.class)
    @JsonProperty("appdata")
    private AppData appData = new AppData();
    @JsonView(SceneView.LightStatesOnly.class)
    @JsonProperty("lightstates")
    private Map<String, LightState> lightStates = new LinkedHashMap<>();
    @JsonView(SceneView.LightsOnly.class)
    @JsonProperty("lights")
    private String[] lightIds = new String[]{};

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
    private class AppData implements IJSon
    {
        @JsonView(SceneView.AppDataView.VersionOnly.class)
        @JsonProperty("version")
        private int version = 1;
        @JsonView(SceneView.AppDataView.DataOnly.class)
        @JsonProperty("data")
        private String data = "GPm4Y_r01_d17";

        public AppData()
        {
            // Default constructor for Json
        }

        public int getVersion()
        {
            return version;
        }

        public AppData setVersion(int version)
        {
            this.version = version;
            return this;
        }

        public String getData()
        {
            return data;
        }

        public AppData setData(String data)
        {
            this.data = data;
            return this;
        }
    }

    public String getPicture()
    {
        return picture;
    }

    public Scene setPicture(String picture)
    {
        this.picture = picture;
        return this;
    }

    public boolean isLocked()
    {
        return locked;
    }

    public Scene setLocked(boolean locked)
    {
        this.locked = locked;
        return this;
    }

    public String getName()
    {
        return name;
    }

    public Scene setName(String name)
    {
        this.name = name;
        return this;
    }

    public String getOwner()
    {
        return owner;
    }

    public Scene setOwner(String owner)
    {
        this.owner = owner;
        return this;
    }

    public int getVersion()
    {
        return version;
    }

    public Scene setVersion(int version)
    {
        this.version = version;
        return this;
    }

    public Date getLastUpdated()
    {
        return lastUpdated;
    }

    public Scene setLastUpdated(Date lastUpdated)
    {
        this.lastUpdated = lastUpdated;
        return this;
    }

    public boolean isRecycle()
    {
        return recycle;
    }

    public Scene setRecycle(boolean recycle)
    {
        this.recycle = recycle;
        return this;
    }

    public AppData getAppData()
    {
        return appData;
    }

    public Scene setAppData(AppData appData)
    {
        this.appData = appData;
        return this;
    }

    public Map<String, LightState> getLightStates()
    {
        return lightStates;
    }

    public Scene setLightStates(Map<String, LightState> lightStates)
    {
        this.lightStates = lightStates;
        return this;
    }

    public String[] getLightIds()
    {
        return lightIds;
    }

    public Scene setLightIds(String[] lightIds)
    {
        this.lightIds = lightIds;
        return this;
    }
}
