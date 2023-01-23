package com.cydeo.client;

import com.cydeo.annotation.ExecutionTime;
import com.cydeo.dto.Currencies;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(url = "https://cdn.jsdelivr.net/gh/fawazahmed0/currency-api@1/latest/currencies/usd.json",name = "Currency-Exchange-Client")
public interface CurrencyExchangeClient {
    @ExecutionTime
    @GetMapping()
    Currencies getExchangeRates();

}
