package net.nyllian.vhue.model.views;

/**
 * Created by Nyllian on 7/12/2017.
 *
 */
public interface SceneView
{
    interface PictureOnly {}
    interface LockedOnly {}
    interface NameOnly {}
    interface OwnerOnly {}
    interface VersionOnly {}
    interface LastUpdatedOnly {}
    interface RecycleOnly {}
    interface AppDataOnly extends AppDataView.AllProperties {}
    interface LightStatesOnly extends LightStateView.AllProperties {}
    interface LightsOnly {}

    interface SceneOnlyProperties extends PictureOnly, LockedOnly, NameOnly, OwnerOnly, VersionOnly, LastUpdatedOnly, RecycleOnly, AppDataOnly, LightsOnly {}
    interface AllProperties extends SceneOnlyProperties, LightStatesOnly {}

    interface AppDataView
    {
        interface VersionOnly {}
        interface DataOnly {}

        interface AllProperties extends VersionOnly, DataOnly {}
    }
}
