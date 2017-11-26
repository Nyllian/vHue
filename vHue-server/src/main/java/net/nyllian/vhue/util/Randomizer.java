package net.nyllian.vhue.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Created by Nyllian on 15/11/2017.
 */
public class Randomizer
{
    private static final Logger LOG = LoggerFactory.getLogger(Randomizer.class);

    public static String generateUniqueId()
    {
        // "uniqueid": "1a2b3c465"
        LOG.trace("Generating uniqueId...");
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String retVal = uuid.substring(uuid.length() -10).toLowerCase();
        LOG.info(String.format("UniqueId generated = %1s", retVal));

        return retVal;
    }

    public static String generateSerialNumber()
    {
        // serial_number = AF96344E5EF0 // 0017880ae670
        LOG.trace("Generating serialNumber...");
        String uuid = UUID.randomUUID().toString();
        String retVal = uuid.substring(uuid.length() -12).toLowerCase();
        LOG.info(String.format("SerialNumber generated = %1s", retVal));

        return retVal;
    }

    public static String generateUuid()
    {
        // udb = MYUUID // uuid:2f402f80-da50-11e1-9b23-0017880ae670
        LOG.trace("Generating UUID...");
        String retVal = UUID.randomUUID().toString().toLowerCase();
        LOG.info(String.format("UUID generated = %1s", retVal));

        return retVal;
    }

    public static String generateBridgeId()
    {
        // hue-bridgeid = E4A471FFFE09B036
        // udb = MYUUID // uuid:2f402f80-da50-11e1-9b23-0017880ae670
        LOG.trace("Generating bridgeId...");
        String tmpVal = UUID.randomUUID().toString().replace("-", "");
        String retVal = tmpVal.substring(tmpVal.length() -16).toLowerCase();
        LOG.info(String.format("bridgeId generated = %1s", retVal));

        return retVal;
    }

    public static String generateUserToken()
    {
        // a7161538be80d40b3de98dece6e91f904dc96171
        LOG.trace("Generating userToken...");
        String tmpVal = UUID.randomUUID().toString();
        tmpVal += UUID.randomUUID().toString();
        String retVal = tmpVal.replace("-", "").substring(tmpVal.length() -40).toLowerCase();
        LOG.info(String.format("userToken generated = %1s", retVal));

        return retVal;
    }
}
