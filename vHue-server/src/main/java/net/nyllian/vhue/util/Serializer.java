package net.nyllian.vhue.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.nyllian.vhue.model.IJSon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Created by Nyllian on 22/11/2017.
 *
 */
public class Serializer
{
    private static final Logger LOG = LoggerFactory.getLogger(Serializer.class);

    public static String SerializeJson(IJSon jsonObject) throws JsonProcessingException
    {
        return SerializeJson((Object)jsonObject);
    }

    public static String SerializeJson(Object jsonObject) throws JsonProcessingException
    {
        ObjectMapper serializer = new ObjectMapper();
        serializer.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));

        String serializedString = serializer
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(jsonObject);

        LOG.trace(String.format("Serializing object: \n%1s", serializedString));
        return serializedString;
    }

    public static <T> T SerializeJson(String jsonString, Class<T> jsonClass) throws IOException
    {
        ObjectMapper serializer = new ObjectMapper();
        serializer.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH24:mm:ss"));
        serializer.enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);

        LOG.trace(String.format("Serializing string to %1s", jsonClass.getTypeName()));
        return serializer.readValue(jsonString, jsonClass);
    }

    public static <T> T UpdateObject(T jsonObj, String jsonData) throws IOException
    {
        ObjectMapper serializer = new ObjectMapper();
        serializer.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH24:mm:ss"));
        return serializer.readerForUpdating(jsonObj).readValue(jsonData);
    }
}
