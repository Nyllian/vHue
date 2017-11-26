package net.nyllian.vhue.server;

import net.nyllian.vhue.model.Bridge;
import net.nyllian.vhue.model.BridgeConfig;
import net.nyllian.vhue.util.Randomizer;
import net.nyllian.vhue.util.ResourceManager;
import net.nyllian.vhue.util.Serializer;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
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
    //private Map<String, Object> resourceMap;
    private ResourceManager<Object> manager;

    public HueServer()
    {
        manager = new ResourceManager<>();

        try
        {
            InitializeServerConfig();
            InitializeTemplateMap();
            InitializeResources();
            InitializeBridgeConfig();
            startSsdpServer();
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
        // resourceMap.put("ssdp", ssdpThread);
    }

    /**
     * Initialize the server configuration
     * When no configurations was found, save the configuration
     */
    private void InitializeServerConfig() throws IOException
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
        File configFile = new File(Bridge.configStoreLocation);
        Bridge bridge = new Bridge();

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

        try
        {
            FileInputStream fis = new FileInputStream(configFile);
            byte[] jsonData = new byte[(int) configFile.length()];
            int bytesRead = fis.read(jsonData);
            fis.close();

            LOG.debug(String.format("Number of bytes read: %1d", bytesRead));
            bridge = Serializer.SerializeJson(new String(jsonData), Bridge.class);
            manager.addResource("bridge", bridge);
        }
        catch (IOException ioEx)
        {
            LOG.warn("The bridgeconfig file could not be read or is empty!", ioEx);

            // Create a new Bridge
            BridgeConfig bridgeConfig = new BridgeConfig()
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
                    .setMacAddress(manager.getResource("macAddress").toString())
                    .setName("vHue")
                    .setBridgeId("1234657890123456")
                    .setFactoryNew(false)
                    .setDatastoreVersion(1)
                    .setSwitchVersion("1709131301")
                    .setApiVersion("1.19.0")
                    .setModelId("vHUE-001");

            bridge.setBridgeConfig(bridgeConfig);
            manager.addResource("bridge", bridge);

            bridge.writeConfig();
        }
    }

    private void InitializeResources() throws UnknownHostException, SocketException
    {
        // Get the macAddress
        byte[] mac = NetworkInterface.getByInetAddress(Inet4Address.getLocalHost()).getHardwareAddress();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? ":" : ""));
        }
        manager.addResource("macAddress", sb.toString());

        // Get the ipAddress

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

        manager.addResource("tplMap", tplMap);
    }
}