package net.nyllian.vhue.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Created by Nyllian on 20/11/2017.
 *
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
@JsonPropertyOrder({"modelid", "name", "state", "swversion", "type", "uniqueid"})
public class Light implements IJSon
{
    @JsonProperty("modelid")
    private String modelId;
    @JsonProperty
    private String name;
    @JsonProperty("swversion")
    private String switchVersion;
    @JsonProperty
    private String type;
    @JsonProperty("uniqueid")
    private String uniqueId;
    @JsonProperty
    private LightState state;

    public String getModelId()
    {
        return modelId;
    }

    public Light setModelId(String modelId)
    {
        this.modelId = modelId;
        return this;
    }

    public String getName()
    {
        return name;
    }

    public Light setName(String name)
    {
        this.name = name;
        return this;
    }

    public String getSwitchVersion()
    {
        return switchVersion;
    }

    public Light setSwitchVersion(String version)
    {
        this.switchVersion = version;
        return this;
    }

    public String getType()
    {
        return type;
    }

    public Light setType(String type)
    {
        this.type = type;
        return this;
    }

    public String getUniqueId()
    {
        return uniqueId;
    }

    public Light setUniqueId(String uniqueId)
    {
        this.uniqueId = uniqueId;
        return this;
    }

    public LightState getState()
    {
        return state;
    }

    public Light setState(LightState state)
    {
        this.state = state;
        return this;
    }
}
