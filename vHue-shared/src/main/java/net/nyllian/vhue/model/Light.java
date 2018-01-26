package net.nyllian.vhue.model;

import com.fasterxml.jackson.annotation.*;
import net.nyllian.vhue.model.views.LightView;
import net.nyllian.vhue.util.Randomizer;

/**
 * Created by Nyllian on 20/11/2017.
 *
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
@JsonPropertyOrder({"modelid", "name", "lightState", "swversion", "type", "uniqueid"})
public class Light implements IJSon
{
    @JsonView(LightView.ModelIdOnly.class)
    @JsonProperty("modelid")
    private String modelId = "BSB002";
    @JsonView(LightView.NameOnly.class)
    @JsonProperty("name")
    private String name = "Dummy Light";
    @JsonView(LightView.SwitchVersionOnly.class)
    @JsonProperty("swversion")
    private String switchVersion = "66009461";
    @JsonView(LightView.TypeOnly.class)
    @JsonProperty("type")
    private String type = "Dummy color Light";
    @JsonView(LightView.UniqueIdOnly.class)
    @JsonProperty("uniqueid")
    private String uniqueId = Randomizer.generateUniqueId();
    @JsonView(LightView.LightStateOnly.class)
    @JsonProperty("state")
    private LightState lightState = new LightState();
    @JsonView(LightView.ManufacturerNameOnly.class)
    @JsonProperty("manufacturername")
    private String manufacturerName = "Nyllian";

    private String ipAddress;

    public Light()
    {
        // Default constructor for Json
    }

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

    public LightState getLightState()
    {
        return lightState;
    }

    public Light setLightState(LightState lightState)
    {
        this.lightState = lightState;
        return this;
    }

    public String getManufacturerName()
    {
        return manufacturerName;
    }

    public Light setManufacturerName(String manufacturerName)
    {
        this.manufacturerName = manufacturerName;
        return this;
    }

    public String getIpAddress()
    {
        return ipAddress;
    }

    public Light setIpAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
        return this;
    }
}
