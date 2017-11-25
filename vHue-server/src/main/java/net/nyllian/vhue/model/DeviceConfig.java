package net.nyllian.vhue.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Nyllian on 23/11/2017.
 *
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
// @JsonPropertyOrder({"modelid", "name", "state", "swversion", "type", "uniqueid"})
public class DeviceConfig extends Device implements IJSon
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
    private Date utc;
    @JsonProperty("localtime")
    private Date localtime;
    @JsonProperty("timezone")
    private String timezone;
    @JsonProperty("whitelist")
    public Map<String, Whitelist> whiteList = new LinkedHashMap<>();
    @JsonProperty("swupdate")
    private Map<String, Boolean> swupdate = new LinkedHashMap<>();

    @JsonIgnore
    public Boolean switchUpdate;

    public String getIpAddress()
    {
        return ipAddress;
    }

    public DeviceConfig setIpAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
        return this;
    }

    public String getGateway()
    {
        return gateway;
    }

    public DeviceConfig setGateway(String gateway)
    {
        this.gateway = gateway;
        return this;
    }

    public String getNetmask()
    {
        return netmask;
    }

    public DeviceConfig setNetmask(String netmask)
    {
        this.netmask = netmask;
        return this;
    }

    public String getProxyAddress()
    {
        return proxyAddress;
    }

    public DeviceConfig setProxyAddress(String proxyAddress)
    {
        this.proxyAddress = proxyAddress;
        return this;
    }

    public int getProxyPort()
    {
        return proxyPort;
    }

    public DeviceConfig setProxyPort(int proxyPort)
    {
        this.proxyPort = proxyPort;
        return this;
    }

    public boolean isDhcp()
    {
        return dhcp;
    }

    public DeviceConfig setDhcp(boolean dhcp)
    {
        this.dhcp = dhcp;
        return this;
    }

    public boolean isLinkButton()
    {
        return linkButton;
    }

    public DeviceConfig setLinkButton(boolean linkButton)
    {
        this.linkButton = linkButton;
        return this;
    }

    public boolean isPortalServices()
    {
        return portalServices;
    }

    public DeviceConfig setPortalServices(boolean portalServices)
    {
        this.portalServices = portalServices;
        return this;
    }

    public int getZigbeeChannel()
    {
        return zigbeeChannel;
    }

    public DeviceConfig setZigbeeChannel(int zigbeeChannel)
    {
        this.zigbeeChannel = zigbeeChannel;
        return this;
    }

    public Date getUtc()
    {
        return utc;
    }

    public DeviceConfig setUtc(Date utc)
    {
        this.utc = utc;
        return this;
    }

    public Date getLocaltime()
    {
        return localtime;
    }

    public DeviceConfig setLocaltime(Date localtime)
    {
        this.localtime = localtime;
        return this;
    }

    public String getTimezone()
    {
        return timezone;
    }

    public DeviceConfig setTimezone(String timezone)
    {
        this.timezone = timezone;
        return this;
    }

    public Map<String, Whitelist> getWhiteList()
    {
        return whiteList;
    }

    public DeviceConfig setWhiteList(Map<String, Whitelist> whiteList)
    {
        this.whiteList = whiteList;
        return this;
    }

    public DeviceConfig addToWhitelist(String key, String name)
    {
        whiteList.put(key, new Whitelist().setName(name));
        return this;
    }

    public boolean isSwitchUpdate()
    {
        return switchUpdate;
    }

    public DeviceConfig setSwitchUpdate(boolean switchUpdate)
    {
        swupdate.put("checkforupdate", switchUpdate);
        return this;
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
    public class Whitelist implements IJSon
    {
        @JsonProperty("last use date")
        private Date lastUseDate = new Date();
        @JsonProperty("create date")
        private Date createDate = new Date();
        @JsonProperty("name")
        private String name;

        public Date getLastUseDate()
        {
            return lastUseDate;
        }

        public Whitelist setLastUseDate(Date lastUseDate)
        {
            this.lastUseDate = lastUseDate;
            return this;
        }

        public Date getCreateDate()
        {
            return createDate;
        }

        public Whitelist setCreateDate(Date createDate)
        {
            this.createDate = createDate;
            return this;
        }

        public String getName()
        {
            return name;
        }

        public Whitelist setName(String name)
        {
            this.name = name;
            return this;
        }
    }
}
