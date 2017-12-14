package net.nyllian.vhue.model;

import com.fasterxml.jackson.annotation.*;

import java.util.Date;

/**
 * Created by Nyllian on 26/11/2017.
 *
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
@JsonPropertyOrder("{last use date, create date, name}")
public class Whitelist implements IJSon
{
    @JsonProperty("last use date")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date lastUseDate = new Date();
    @JsonProperty("create date")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date createDate = new Date();
    @JsonProperty("name")
    private String name;

    @JsonCreator
    public Whitelist()
    {
        // Default Constructor for Json
    }

    public Date getLastUseDate()
    {
        return lastUseDate;
    }

    public Whitelist setLastUseDate(Date lastUseDate)
    {
        this.lastUseDate = lastUseDate;
        return this;
    }

    public Date getCreateDate()
    {
        return createDate;
    }

    public Whitelist setCreateDate(Date createDate)
    {
        this.createDate = createDate;
        return this;
    }

    public String getName()
    {
        return name;
    }

    public Whitelist setName(String name)
    {
        this.name = name;
        return this;
    }
}

