package net.nyllian.vhue.model.views;

/**
 * Created by Nyllian on 9/01/2018.
 *
 */
public interface LightView
{
    interface ModelIdOnly {}
    interface NameOnly {}
    interface SwitchVersionOnly {}
    interface SwitchTypeOnly {}
    interface TypeOnly {}
    interface UniqueIdOnly {}
    interface LightStateOnly extends LightStateView.AllProperties {}
    interface ManufacturerNameOnly {}

    interface LightProperties extends ModelIdOnly, NameOnly, SwitchVersionOnly, SwitchTypeOnly, TypeOnly, UniqueIdOnly, LightStateOnly, ManufacturerNameOnly {}

}
