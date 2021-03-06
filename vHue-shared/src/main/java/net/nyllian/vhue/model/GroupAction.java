package net.nyllian.vhue.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Created by Nyllian on 20/11/2017.
 *
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
@JsonPropertyOrder({"alert", "bri", "colormode", "ct", "effect", "hue", "on", "reachable", "sat", "xy"})
public class GroupAction implements IJSon
{
    @JsonProperty
    private String alert = "select";
    @JsonProperty("bri")
    private int brightness = 20;
    @JsonProperty("colormode")
    private String colorMode = "ct";
    @JsonProperty("ct")
    private int colorTemp = 461;
    @JsonProperty
    private String effect = "none";
    @JsonProperty
    private int hue = 0;
    @JsonProperty
    private boolean on = false;
    @JsonProperty("sat")
    private int saturation = 0;
    @JsonProperty
    private double[] xy = new double[] { 0.310669, 0.323961 };

    public String getAlert()
    {
        return alert;
    }

    public GroupAction setAlert(String alert)
    {
        this.alert = alert;
        return this;
    }

    public int getBrightness()
    {
        return brightness;
    }

    public GroupAction setBrightness(int brightness)
    {
        this.brightness = brightness;
        return this;
    }

    public String getColorMode()
    {
        return colorMode;
    }

    public GroupAction setColorMode(String colorMode)
    {
        this.colorMode = colorMode;
        return this;
    }

    public int getColorTemp()
    {
        return colorTemp;
    }

    public GroupAction setColorTemp(int colorTemp)
    {
        this.colorTemp = colorTemp;
        return this;
    }

    public String getEffect()
    {
        return effect;
    }

    public GroupAction setEffect(String effect)
    {
        this.effect = effect;
        return this;
    }

    public int getHue()
    {
        return hue;
    }

    public GroupAction setHue(int hue)
    {
        this.hue = hue;
        return this;
    }

    public boolean isOn()
    {
        return on;
    }

    public GroupAction setOn(boolean on)
    {
        this.on = on;
        return this;
    }

    public int getSaturation()
    {
        return saturation;
    }

    public GroupAction setSaturation(int saturation)
    {
        this.saturation = saturation;
        return this;
    }

    public double[] getXy()
    {
        return xy;
    }

    public GroupAction setXy(double[] xy)
    {
        this.xy = xy;
        return this;
    }
}
