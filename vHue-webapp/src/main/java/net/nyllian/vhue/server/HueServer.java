package net.nyllian.vhue.server;

import net.nyllian.vhue.model.Bridge;
import net.nyllian.vhue.model.BridgeConfig;
import net.nyllian.vhue.model.Capabilities;
import net.nyllian.vhue.util.HueUtils;
import net.nyllian.vhue.util.Randomizer;
import net.nyllian.vhue.util.ResourceManager;
import net.nyllian.vhue.util.Serializer;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Nyllian on 16/11/2017.
 *
 */
public class HueServer extends ResourceConfig
{
    private static final Logger LOG = LoggerFactory.getLogger(HueServer.class);

    private Properties properties = new Properties();
    private ResourceManager<Object> manager;

    public HueServer()
    {
        manager = new ResourceManager<>();

        try
        {
            initServerConfig();
            initTemplateMap();
            initResources();
            initBridgeConfig();
            initBridgeCapabilities();
            // startSsdpServer();
            startUpnpServer();
            // startHueLightServer();
        }
        catch (Exception ex)
        {
            LOG.error("Error occurred during starting of the server! Exiting now!", ex);
            System.exit(1);
        }

        Map<String, Object> resourceMap = new HashMap<>();
        resourceMap.put("manager", manager);
        setProperties(resourceMap);
    }

    private void startSsdpServer()
    {
        LOG.info("Starting SSDP Server...");

        Thread ssdpThread = new Thread(new SSDPServer(manager.getResourceMap()));
        ssdpThread.start();

        LOG.info("SSDP server started successfully!");
    }

    private void startUpnpServer()
    {
        LOG.info("Starting Upnp Server...");

        Thread upnpThread = new Thread(new UpnpServer(manager.getResourceMap()));
        upnpThread.start();

        LOG.info("Upnp server started successfully!");
    }

    /**
     * Initialize the server configuration
     * When no configurations was found, save the configuration
     */
    private void initServerConfig() throws IOException
    {
        String fileLocation = "./conf/.properties";
        File configFile = new File(fileLocation);

        try
        {
            boolean writeConfig = false;

            if (!configFile.exists())
            {
                LOG.warn(String.format("Properties file could not be found on location %s", configFile.getAbsolutePath()));
                if (configFile.createNewFile())
                {
                    LOG.info(String.format("New properties file created [%s]", configFile.getAbsolutePath()));
                }
                else
                {
                    throw new IOException(String.format("New properties file could not be created [%s]", configFile.getAbsolutePath()));
                }
            }

            // Read the properties file
            FileInputStream fis = new FileInputStream(configFile);
            properties.load(fis);
            fis.close();

            // Always set these properties
            String urlBase = String.format("http://%s:%d", HueUtils.getListeningAddress().getHostAddress(), 80);
            // String urlBase = String.format("http://%s:%d", "192.168.254.64", 80);


            LOG.trace(String.format("Setting property of URLBase to %s", urlBase));
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
            LOG.trace(String.format("Property serialNumber = %s", serialNumber));
            LOG.trace("Getting property of UDN...");
            String udn = properties.getProperty("UDN");
            if (udn == null)
            {
                udn = Randomizer.generateUuid();
                properties.setProperty("UDN", udn);
                writeConfig = true;
            }
            LOG.trace(String.format("Property UDN = %s", udn));
            LOG.trace("Getting property of bridgeId...");
            String bridgeId = properties.getProperty("bridgeId");
            LOG.trace("property of bridgeId ==> " + bridgeId);
            if (bridgeId == null)
            {
                bridgeId = Randomizer.generateBridgeId();
                properties.setProperty("bridgeId", bridgeId);
                writeConfig = true;
            }
            LOG.trace(String.format("Property bridgeId = %s", bridgeId));

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

    private void initBridgeConfig() throws IOException
    {
        File configFile = new File(Bridge.configStoreLocation);
        Bridge bridge = new Bridge();

        try
        {
            if (!configFile.exists())
            {
                LOG.warn(String.format("Config file could not be found on location %s", configFile.getAbsolutePath()));
                if (configFile.createNewFile())
                {
                    LOG.info(String.format("New config file created [%s]", configFile.getAbsolutePath()));
                }
                else
                {
                    throw new IOException(String.format("New properties file could not be created [%s]", configFile.getAbsolutePath()));
                }
            }
        }
        catch (IOException ioEx)
        {
            LOG.error("Error while reading the config file.", ioEx);
            throw ioEx;
        }

        try
        {
            FileInputStream fis = new FileInputStream(configFile);
            byte[] jsonData = new byte[(int) configFile.length()];
            int bytesRead = fis.read(jsonData);
            fis.close();

            LOG.debug(String.format("Number of bytes read: %1d", bytesRead));
            bridge = Serializer.SerializeJson(new String(jsonData), Bridge.class);
            // sync the properties with the bridge
            bridge.getBridgeConfig().setBridgeId(properties.get("bridgeId").toString());
            bridge.getBridgeConfig().setIpAddress(manager.getResource("ipAddress").toString());
            bridge.getBridgeConfig().setGateway(manager.getResource("ipAddress").toString());
            bridge.getBridgeConfig().setMacAddress(manager.getResource("macAddress").toString());
            manager.addResource(ResourceManager.RESOURCE_BRIDGE, bridge);
        }
        catch (IOException ioEx)
        {
            LOG.warn("The bridgeconfig file could not be read or is empty!", ioEx);

            // Create a new Bridge
            BridgeConfig bridgeConfig = new BridgeConfig()
                    .setIpAddress(HueUtils.getListeningAddress().getHostAddress())
                    .setGateway(HueUtils.getListeningAddress().getHostAddress())
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
                    .setMacAddress(manager.getResource("macAddress").toString())
                    .setName("vHue")
                    .setBridgeId("1234657890123456")
                    .setFactoryNew(false)
                    .setDatastoreVersion(1)
                    .setSwitchVersion(HueUtils.HUB_VERSION)
                    .setApiVersion(HueUtils.API_VERSION)
                    .setModelId(HueUtils.MODEL_ID);

            bridge.setBridgeConfig(bridgeConfig);
            manager.addResource(ResourceManager.RESOURCE_BRIDGE, bridge);

            bridge.writeConfig();
        }
    }

    private void initBridgeCapabilities()
    {
        manager.addResource(ResourceManager.RESOURCE_CAPABILITIES, new Capabilities(manager.getBridge()));
    }

    private void initResources() throws UnknownHostException, SocketException
    {
        // Get the macAddress
        byte[] mac = NetworkInterface.getByInetAddress(Inet4Address.getLocalHost()).getHardwareAddress();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? ":" : ""));
        }
        manager.addResource("macAddress", sb.toString());

        // Get the ipAddress
        manager.addResource("ipAddress", HueUtils.getListeningAddress().getHostAddress());
    }

    /**
     * Initialize the Template Map object
     */
    private void initTemplateMap()
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

        manager.addResource("tplMap", tplMap);
    }
}
