package net.nyllian.vhue.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Created by Nyllian on 26/11/2017.
 *
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
@JsonPropertyOrder({"name"})
public class DiscoveredLight implements IJSon
{
    @JsonProperty("name")
    private String name;

    public String getName()
    {
        return name;
    }

    public DiscoveredLight setName(String name)
    {
        this.name = name;
        return this;
    }
}
