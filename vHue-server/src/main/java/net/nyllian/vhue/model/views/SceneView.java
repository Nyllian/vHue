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
    interface AppDataOnly {}
    interface LightScenesOnly {}
    interface LightsOnly {}

    interface SceneProperties extends PictureOnly, LockedOnly, NameOnly, OwnerOnly, VersionOnly, LastUpdatedOnly, RecycleOnly, AppDataOnly, LightsOnly {}
    interface AllProperties extends SceneProperties, LightScenesOnly {}
}
