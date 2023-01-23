
package com.cydeo.dto;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.NumberFormat;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "cad",
    "eur",
    "gbp",
    "inr",
    "jpy",
})
@Generated("jsonschema2pojo")
public class Usd {


    @JsonProperty("cad")
    private Double canadianDollar;
    @JsonProperty("eur")
    private Double euro;
    @JsonProperty("gbp")
    private Double britishPound;
    @JsonProperty("inr")
    private Double indianRupee;
    @JsonProperty("jpy")
    private Double japaneseYen;




}
