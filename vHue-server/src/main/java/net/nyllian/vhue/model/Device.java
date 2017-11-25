package net.nyllian.vhue.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Created by Nyllian on 22/11/2017.
 *
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
@JsonPropertyOrder({"datastoreversion", "mac", "name", "bridgeid", "swversion", "factorynew", "apiversion", "modelid"})
public class Device implements IJSon
{
    @JsonProperty("mac")
    private String macAddress;
    @JsonProperty("name")
    private String name;
    @JsonProperty("bridgeid")
    private String id;
    @JsonProperty("modelid")
    private String modelId;
    @JsonProperty("factorynew")
    private boolean factoryNew;
    @JsonProperty("datastoreversion")
    private int datastoreVersion = 59;
    @JsonProperty("swversion")
    private String switchVersion = "1709131301";
    @JsonProperty("apiversion")
    private String apiVersion = "1.19.0";

    public String getMacAddress()
    {
        return macAddress;
    }

    public Device setMacAddress(String macAddress)
    {
        this.macAddress = macAddress;
        return this;
    }

    public String getName()
    {
        return name;
    }

    public Device setName(String name)
    {
        this.name = name;
        return this;
    }

    public String getId()
    {
        return id;
    }

    public Device setId(String id)
    {
        this.id = id;
        return this;
    }

    public String getModelId()
    {
        return modelId;
    }

    public Device setModelId(String modelId)
    {
        this.modelId = modelId;
        return this;
    }

    public boolean isFactoryNew()
    {
        return factoryNew;
    }

    public Device setFactoryNew(boolean factoryNew)
    {
        this.factoryNew = factoryNew;
        return this;
    }

    public int getDatastoreVersion()
    {
        return datastoreVersion;
    }

    public Device setDatastoreVersion(int datastoreVersion)
    {
        this.datastoreVersion = datastoreVersion;
        return this;
    }

    public String getSwitchVersion()
    {
        return switchVersion;
    }

    public Device setSwitchVersion(String switchVersion)
    {
        this.switchVersion = switchVersion;
        return this;
    }

    public String getApiVersion()
    {
        return apiVersion;
    }

    public Device setApiVersion(String apiVersion)
    {
        this.apiVersion = apiVersion;
        return this;
    }
}
