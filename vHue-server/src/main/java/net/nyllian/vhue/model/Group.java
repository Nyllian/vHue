package net.nyllian.vhue.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Nyllian on 20/11/2017.
 *
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
@JsonPropertyOrder({"action", "class", "lights", "name", "state", "type"})
public class Group implements IJSon
{
    @JsonProperty
    private String name = "newGroup";
    @JsonProperty
    private String type = "Room";
    @JsonProperty("class")
    private String groupClass = "Living room";
    @JsonProperty("lights")
    private String[] lightIds = new String[]{};
    @JsonProperty("action")
    private GroupAction groupAction = new GroupAction();
    @JsonProperty("state")
    private GroupState groupState = new GroupState();

    @JsonIgnore
    private Map<String, Light> lights = new LinkedHashMap<>();

    public String getName()
    {
        return name;
    }

    public Group setName(String name)
    {
        this.name = name;
        return this;
    }

    public String getType()
    {
        return type;
    }

    public Group setType(String type)
    {
        this.type = type;
        return this;
    }

    public String getGroupClass()
    {
        return groupClass;
    }

    public Group setGroupClass(String groupClass)
    {
        this.groupClass = groupClass;
        return this;
    }

    public String[] getLightIds()
    {
        return lightIds;
    }

    public Group setLightIds(String[] lightIds)
    {
        this.lightIds = lightIds;
        return this;
    }

    public Map<String, Light> getLights()
    {
        return lights;
    }

    public Group setLights(Map<String, Light> lights)
    {
        this.lights = lights;
        return this;
    }

    public GroupAction getGroupAction()
    {
        return groupAction;
    }

    public Group setGroupAction(GroupAction groupAction)
    {
        this.groupAction = groupAction;
        return this;
    }

    public GroupState getGroupState()
    {
        return groupState;
    }

    public Group setGroupState(GroupState groupState)
    {
        this.groupState = groupState;
        return this;
    }
}
