package net.nyllian.vhue.model;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import net.nyllian.vhue.util.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Nyllian on 23/11/2017.
 *
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
// @JsonPropertyOrder({"datastoreversion", "mac", "name", "bridgeid", "swversion", "factorynew", "apiversion", "modelid"})
public class BridgeConfig implements IJSon
{
    @JsonProperty("ipaddress")
    private String ipAddress;
    @JsonProperty("gateway")
    private String gateway;
    @JsonProperty("netmask")
    private String netmask;
    @JsonProperty("proxyaddress")
    private String proxyAddress;
    @JsonProperty("proxyport")
    private int proxyPort;
    @JsonProperty("dhcp")
    private boolean dhcp;
    @JsonProperty("linkbutton")
    private boolean linkButton;
    @JsonProperty("portalservices")
    private boolean portalServices;
    @JsonProperty("zigbeechannel")
    private int zigbeeChannel;
    @JsonProperty("utc")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date utc = new Date();
    @JsonProperty("localtime")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date localtime = new Date();
    @JsonProperty("timezone")
    private String timezone = "Brussels/Europe";

    @JsonProperty("mac")
    private String macAddress;
    @JsonProperty("name")
    private String name;
    @JsonProperty("bridgeid")
    private String bridgeId;
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

    @JsonProperty("whitelist")
    private Map<String, Whitelist> whiteList = new LinkedHashMap<>();
    @JsonProperty("swupdate")
    private Map<String, Boolean> swupdate = new LinkedHashMap<>();

    @JsonIgnore
    private Boolean switchUpdate;

    public String getIpAddress()
    {
        return ipAddress;
    }

    public BridgeConfig setIpAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
        return this;
    }

    public String getGateway()
    {
        return gateway;
    }

    public BridgeConfig setGateway(String gateway)
    {
        this.gateway = gateway;
        return this;
    }

    public String getNetmask()
    {
        return netmask;
    }

    public BridgeConfig setNetmask(String netmask)
    {
        this.netmask = netmask;
        return this;
    }

    public String getProxyAddress()
    {
        return proxyAddress;
    }

    public BridgeConfig setProxyAddress(String proxyAddress)
    {
        this.proxyAddress = proxyAddress;
        return this;
    }

    public int getProxyPort()
    {
        return proxyPort;
    }

    public BridgeConfig setProxyPort(int proxyPort)
    {
        this.proxyPort = proxyPort;
        return this;
    }

    public boolean isDhcp()
    {
        return dhcp;
    }

    public BridgeConfig setDhcp(boolean dhcp)
    {
        this.dhcp = dhcp;
        return this;
    }

    public boolean isLinkButton()
    {
        return linkButton;
    }

    public BridgeConfig setLinkButton(boolean linkButton)
    {
        this.linkButton = linkButton;
        return this;
    }

    public boolean isPortalServices()
    {
        return portalServices;
    }

    public BridgeConfig setPortalServices(boolean portalServices)
    {
        this.portalServices = portalServices;
        return this;
    }

    public int getZigbeeChannel()
    {
        return zigbeeChannel;
    }

    public BridgeConfig setZigbeeChannel(int zigbeeChannel)
    {
        this.zigbeeChannel = zigbeeChannel;
        return this;
    }

    public Date getUtc()
    {
        return utc;
    }

    public BridgeConfig setUtc(Date utc)
    {
        this.utc = utc;
        return this;
    }

    public Date getLocaltime()
    {
        return localtime;
    }

    public BridgeConfig setLocaltime(Date localtime)
    {
        this.localtime = localtime;
        return this;
    }

    public String getTimezone()
    {
        return timezone;
    }

    public BridgeConfig setTimezone(String timezone)
    {
        this.timezone = timezone;
        return this;
    }

    public Map<String, Whitelist> getWhiteList()
    {
        return whiteList;
    }

    public BridgeConfig setWhiteList(Map<String, Whitelist> whiteList)
    {
        this.whiteList = whiteList;
        return this;
    }

    public BridgeConfig addToWhitelist(String key, String name)
    {
        whiteList.put(key, new Whitelist().setName(name));
        return this;
    }

    public boolean isSwitchUpdate()
    {
        return swupdate.get("checkforupdate");
    }

    public BridgeConfig setSwitchUpdate(boolean switchUpdate)
    {
        swupdate.put("checkforupdate", switchUpdate);
        return this;
    }

    public String getMacAddress()
    {
        return macAddress;
    }

    public BridgeConfig setMacAddress(String macAddress)
    {
        this.macAddress = macAddress;
        return this;
    }

    public String getName()
    {
        return name;
    }

    public BridgeConfig setName(String name)
    {
        this.name = name;
        return this;
    }

    public String getBridgeId()
    {
        return bridgeId;
    }

    public BridgeConfig setBridgeId(String bridgeId)
    {
        this.bridgeId = bridgeId;
        return this;
    }

    public String getModelId()
    {
        return modelId;
    }

    public BridgeConfig setModelId(String modelId)
    {
        this.modelId = modelId;
        return this;
    }

    public boolean isFactoryNew()
    {
        return factoryNew;
    }

    public BridgeConfig setFactoryNew(boolean factoryNew)
    {
        this.factoryNew = factoryNew;
        return this;
    }

    public int getDatastoreVersion()
    {
        return datastoreVersion;
    }

    public BridgeConfig setDatastoreVersion(int datastoreVersion)
    {
        this.datastoreVersion = datastoreVersion;
        return this;
    }

    public String getSwitchVersion()
    {
        return switchVersion;
    }

    public BridgeConfig setSwitchVersion(String switchVersion)
    {
        this.switchVersion = switchVersion;
        return this;
    }

    public String getApiVersion()
    {
        return apiVersion;
    }

    public BridgeConfig setApiVersion(String apiVersion)
    {
        this.apiVersion = apiVersion;
        return this;
    }
}
