package net.nyllian.vhue.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Date;

/**
 * Created by Nyllian on 20/11/2017.
 *
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
@JsonPropertyOrder({"all_on", "any_on", "bri", "lastupdated", "on"})
public class GroupState implements IJSon
{
    @JsonProperty
    private boolean on = false;
    @JsonProperty("all_on")
    private boolean allOn = false;
    @JsonProperty("any_on")
    private boolean anyOn = false;
    @JsonProperty
    private int brightness = 0;
    @JsonProperty("lastupdated")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date lastUpdated = new Date();

    public boolean isOn()
    {
        return on;
    }

    public GroupState setOn(boolean on)
    {
        this.on = on;
        return this;
    }

    public boolean isAllOn()
    {
        return allOn;
    }

    public GroupState setAllOn(boolean allOn)
    {
        this.allOn = allOn;
        return this;
    }

    public boolean isAnyOn()
    {
        return anyOn;
    }

    public GroupState setAnyOn(boolean anyOn)
    {
        this.anyOn = anyOn;
        return this;
    }

    public int getBrightness()
    {
        return brightness;
    }

    public GroupState setBrightness(int brightness)
    {
        this.brightness = brightness;
        return this;
    }

    public Date getLastUpdated()
    {
        return lastUpdated;
    }

    public GroupState setLastUpdated(Date lastUpdated)
    {
        this.lastUpdated = lastUpdated;
        return this;
    }
}
