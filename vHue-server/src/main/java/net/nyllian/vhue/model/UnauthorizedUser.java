package net.nyllian.vhue.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Created by Nyllian on 26/11/2017.
 *
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
@JsonPropertyOrder({"type", "description", "address"})
public class UnauthorizedUser implements IJSon
{
    @JsonProperty("type")
    private int type = 1;
    @JsonProperty("description")
    private String description = "unauthorized user";
    @JsonProperty("address")
    private String address;

    public UnauthorizedUser()
    {
        // Default constructor for JSON
    }

    public UnauthorizedUser(String description)
    {
        this.description = description;
    }

    public int getType()
    {
        return type;
    }

    public UnauthorizedUser setType(int type)
    {
        this.type = type;
        return this;
    }

    public String getDescription()
    {
        return description;
    }

    public UnauthorizedUser setDescription(String description)
    {
        this.description = description;
        return this;
    }

    public String getAddress()
    {
        return address;
    }

    public UnauthorizedUser setAddress(String address)
    {
        this.address = address;
        return this;
    }
}
