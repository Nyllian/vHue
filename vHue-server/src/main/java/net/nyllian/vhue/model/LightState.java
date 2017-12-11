package net.nyllian.vhue.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonView;
import net.nyllian.vhue.model.views.LightStateView;

/**
 * Created by Nyllian on 20/11/2017.
 *
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
@JsonPropertyOrder({"alert", "bri", "colormode", "ct", "effect", "hue", "on", "reachable", "sat", "xy"})
public class LightState implements IJSon
{
    @JsonView(LightStateView.AlertOnly.class)
    @JsonProperty
    private String alert = "select";
    @JsonView(LightStateView.BrightnessOnly.class)
    @JsonProperty("bri")
    private int brightness = 18;
    @JsonView(LightStateView.ColorModeOnly.class)
    @JsonProperty("colormode")
    private String colorMode = "xy";
    @JsonView(LightStateView.ColorTempOnly.class)
    @JsonProperty("ct")
    private int colorTemp = 461;
    @JsonView(LightStateView.EffectOnly.class)
    @JsonProperty("effect")
    private String effect = "none";
    @JsonView(LightStateView.HueOnly.class)
    @JsonProperty("hue")
    private int hue = 0;
    @JsonView(LightStateView.OnOnly.class)
    @JsonProperty("on")
    private boolean on = false;
    @JsonView(LightStateView.ReachableOnly.class)
    @JsonProperty("reachable")
    private boolean reachable = false;
    @JsonView(LightStateView.SaturationOnly.class)
    @JsonProperty("sat")
    private int saturation = 0;
    @JsonView(LightStateView.XYOnly.class)
    @JsonProperty("xy")
    private double[] xy = new double[] { 0.310669, 0.323961 };
    @JsonView(LightStateView.TransitionTimeOnly.class)
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
