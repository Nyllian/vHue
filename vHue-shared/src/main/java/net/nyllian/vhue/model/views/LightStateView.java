package net.nyllian.vhue.model.views;

/**
 * Created by Nyllian on 8/12/2017.
 *
 */
public interface LightStateView
{
    interface AlertOnly {}
    interface BrightnessOnly {}
    interface ColorModeOnly {}
    interface ColorTempOnly {}
    interface EffectOnly {}
    interface HueOnly {}
    interface OnOnly {}
    interface ReachableOnly {}
    interface SaturationOnly {}
    interface XYOnly {}
    interface TransitionTimeOnly {}

    interface AllProperties extends AlertOnly, BrightnessOnly, ColorModeOnly, ColorTempOnly, EffectOnly, HueOnly, OnOnly, ReachableOnly, SaturationOnly, XYOnly, TransitionTimeOnly {}
}
