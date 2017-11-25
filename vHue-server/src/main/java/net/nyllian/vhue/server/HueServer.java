package net.nyllian.vhue.server;

import net.nyllian.vhue.model.Device;
import net.nyllian.vhue.model.DeviceConfig;
import net.nyllian.vhue.util.Randomizer;
import net.nyllian.vhue.util.Serializer;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Inet4Address;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Nyllian on 16/11/2017.
 *
 */
// @ApplicationPath("/")
public class HueServer extends ResourceConfig
{
    private static final Logger LOG = LoggerFactory.getLogger(HueServer.class);

    private Properties properties = new Properties();
    private Map<String, Object> resourceMap;

    public HueServer()
    {
        resourceMap = new HashMap<>();

        try
        {
            InitializeServer();
            InitializeTemplateMap();
            InitializeBridgeConfig();
            startSsdpServer();
        }
        catch (Exception ex)
        {
            LOG.error("Error occurred during starting of the server! Exiting now!", ex);
            System.exit(1);
        }

        setProperties(resourceMap);
    }

    private void startSsdpServer()
    {
        LOG.info("Starting SSDP Server...");

        Thread ssdpThread = new Thread(new SSDPServer(resourceMap));
        ssdpThread.start();

        LOG.info("SSDP server started successfully!");
        // resourceMap.put("ssdp", ssdpThread);
    }

    /**
     * Initialize the server configuration
     * When no configurations was found, save the configuration
     */
    private void InitializeServer() throws IOException
    {
        String fileLocation = "./conf/.properties";
        File configFile = new File(fileLocation);

        try
        {
            boolean writeConfig = false;

            if (!configFile.exists())
            {
                LOG.warn(String.format("Properties file could not be found on location %1s", configFile.getAbsolutePath()));
                if (configFile.createNewFile())
                {
                    LOG.info(String.format("New properties file created [%1s]", configFile.getAbsolutePath()));
                }
                else
                {
                    throw new IOException(String.format("New properties file could not be created [%1s]", configFile.getAbsolutePath()));
                }
            }

            // Read the properties file
            FileInputStream fis = new FileInputStream(configFile);
            properties.load(fis);
            fis.close();

            // Always set these properties
            String urlBase = String.format("http://%1s:%2d", Inet4Address.getLocalHost().getHostAddress(), 80);

            LOG.trace(String.format("Setting property of URLBase to %1s", urlBase));
            properties.setProperty("URLBase", urlBase);

            // Always get these properties
            LOG.trace("Getting property of serialNumber...");
            String serialNumber = properties.getProperty("serialNumber");
            if (serialNumber == null)
            {
                serialNumber = Randomizer.generateSerialNumber();
                properties.setProperty("serialNumber", serialNumber);
                writeConfig = true;
            }
            LOG.trace(String.format("Property serialNumber = %1s", serialNumber));
            LOG.trace("Getting property of UDN...");
            String udn = properties.getProperty("UDN");
            if (udn == null)
            {
                udn = Randomizer.generateUuid();
                properties.setProperty("UDN", udn);
                writeConfig = true;
            }
            LOG.trace(String.format("Property UDN = %1s", udn));
            LOG.trace("Getting property of bridgeId...");
            String bridgeId = properties.getProperty("bridgeId");
            LOG.trace("property of bridgeId ==> " + bridgeId);
            if (bridgeId == null)
            {
                bridgeId = Randomizer.generateBridgeId();
                properties.setProperty("bridgeId", bridgeId);
                writeConfig = true;
            }
            LOG.trace(String.format("Property bridgeId = %1s", bridgeId));

            // Save the configurations
            if (properties != null && writeConfig)
            {
                properties.store(new FileWriter(configFile), null);
            }
        }
        catch (IOException ioEx)
        {
            LOG.error("Error while reading the config file.", ioEx);
            throw ioEx;
        }
    }

    private void InitializeBridgeConfig() throws IOException
    {
        String fileLocation = "./conf/bridgeconfig.json";
        File configFile = new File(fileLocation);

        try
        {
            if (!configFile.exists())
            {
                LOG.warn(String.format("Config file could not be found on location %1s", configFile.getAbsolutePath()));
                if (configFile.createNewFile())
                {
                    LOG.info(String.format("New config file created [%1s]", configFile.getAbsolutePath()));
                }
                else
                {
                    throw new IOException(String.format("New properties file could not be created [%1s]", configFile.getAbsolutePath()));
                }
            }
        }
        catch (IOException ioEx)
        {
            LOG.error("Error while reading the config file.", ioEx);
            throw ioEx;
        }

        Device devConfig;

        try
        {
            FileInputStream fis = new FileInputStream(configFile);
            byte[] jsonData = new byte[(int) configFile.length()];
            int bytesRead = fis.read(jsonData);
            fis.close();

            LOG.debug(String.format("Number of bytes read: %1d", bytesRead));
            devConfig = Serializer.SerializeJson(new String(jsonData), DeviceConfig.class);
        }
        catch (IOException ioEx)
        {
            LOG.warn("Error while reading the config file, creating new bridge.");

            // Create new deviceConfig
            devConfig = new DeviceConfig()
                    .setIpAddress(Inet4Address.getLocalHost().getHostAddress())
                    .setGateway(Inet4Address.getLocalHost().getHostAddress())
                    .setNetmask("255.255.255.0")
                    .setProxyAddress("none")
                    .setProxyPort(0)
                    .setDhcp(true)
                    .setLinkButton(true)
                    .setPortalServices(true)
                    .setZigbeeChannel(1)
                    .setUtc(new Date())
                    .setLocaltime(new Date())
                    .setTimezone("Brussels/Europe")
                    .setSwitchUpdate(false)
                    .addToWhitelist("a7161538be80d40b3de98dece6e91f904dc96170", "vHue client") // TODO: These details should come from the client
                    // Device Properties
                    .setMacAddress("12:34:56:78:90:AB")
                    .setName("vHue")
                    .setId("1234657890123456")
                    .setFactoryNew(false)
                            // .setDatastoreVersion(1)
                            // .setSwitchVersion("0000000001")
                            // .setApiVersion("0.1.0")
                    .setModelId("vHUE-001");

        }

        // TODO: This should be the bridge
        resourceMap.put("device", devConfig);

        FileWriter fw = new FileWriter(configFile);
        fw.write(Serializer.SerializeJson(devConfig));
        fw.close();
    }

    /**
     * Initialize the Template Map object
     */
    private void InitializeTemplateMap()
    {
        Map<String, String> tplMap = new HashMap<>();
        tplMap.putAll(
                properties.entrySet().stream().collect(
                        Collectors.toMap(
                                e -> e.getKey().toString(),
                                e -> e.getValue().toString()
                        )
                )
        );

        resourceMap.put("tplMap", tplMap);
    }
}
