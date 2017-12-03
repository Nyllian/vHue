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
public class LightState implements IJSon
{
    @JsonProperty
    private String alert = "select";
    @JsonProperty("bri")
    private int brightness = 18;
    @JsonProperty("colormode")
    private String colorMode = "ct";
    @JsonProperty("ct")
    private int colorTemp = 461;
    @JsonProperty("effect")
    private String effect = "none";
    @JsonProperty("hue")
    private int hue = 0;
    @JsonProperty("on")
    private boolean on = false;
    @JsonProperty("reachable")
    private boolean reachable = false;
    @JsonProperty("sat")
    private int saturation = 0;
    @JsonProperty("xy")
    private double[] xy = new double[] { 0.310669, 0.323961 };
    @JsonProperty("transitiontime")
    private int transitionTime = 5;

    public String getAlert()
    {
        return alert;
    }

    public LightState setAlert(String alert)
    {
        this.alert = alert;
        return this;
    }

    public int getBrightness()
    {
        return brightness;
    }

    public LightState setBrightness(int brightness)
    {
        this.brightness = brightness;
        return this;
    }

    public String getColorMode()
    {
        return colorMode;
    }

    public LightState setColorMode(String colorMode)
    {
        this.colorMode = colorMode;
        return this;
    }

    public int getColorTemp()
    {
        return colorTemp;
    }

    public LightState setColorTemp(int colorTemp)
    {
        this.colorTemp = colorTemp;
        return this;
    }

    public String getEffect()
    {
        return effect;
    }

    public LightState setEffect(String effect)
    {
        this.effect = effect;
        return this;
    }

    public int getHue()
    {
        return hue;
    }

    public LightState setHue(int hue)
    {
        this.hue = hue;
        return this;
    }

    public boolean isOn()
    {
        return on;
    }

    public LightState setOn(boolean on)
    {
        this.on = on;
        return this;
    }

    public boolean isReachable()
    {
        return reachable;
    }

    public LightState setReachable(boolean reachable)
    {
        this.reachable = reachable;
        return this;
    }

    public int getSaturation()
    {
        return saturation;
    }

    public LightState setSaturation(int saturation)
    {
        this.saturation = saturation;
        return this;
    }

    public double[] getXy()
    {
        return xy;
    }

    public LightState setXy(double[] xy)
    {
        this.xy = xy;
        return this;
    }

    public int getTransitionTime()
    {
        return transitionTime;
    }

    public LightState setTransitionTime(int transitionTime)
    {
        this.transitionTime = transitionTime;
        return this;
    }
}
