
package com.cydeo.dto;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "date",
    "usd"
})
@Generated("jsonschema2pojo")
public class Currencies {

    @JsonProperty("date")
    private String date;
    @JsonProperty("usd")
    private Usd usd;

    @JsonProperty("date")
    public String getDate() {
        return date;
    }

    @JsonProperty("date")
    public void setDate(String date) {
        this.date = date;
    }

    @JsonProperty("usd")
    public Usd getUsd() {
        return usd;
    }

    @JsonProperty("usd")
    public void setUsd(Usd usd) {
        this.usd = usd;
    }

}
