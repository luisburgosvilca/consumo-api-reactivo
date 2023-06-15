package com.tipo.cambio.controller;

import java.math.BigDecimal;
import java.sql.Date;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.tipo.cambio.model.CurrencyConvertion;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/currency-convertion")
public class CurrencyConvertionController {
	
	private static final String API_KEY = "3lnJwIFWCY5EZvN1i1fwj5qEM5uiKGpf";
	private static final String API_URL = "https://api.apilayer.com/fixer/convert";
	
	private WebClient webClient = null;
	   
	
   public CurrencyConvertionController(WebClient.Builder webClientBuilder) {
       this.webClient = webClientBuilder.build();
   }	
   
   @GetMapping
   public Mono<CurrencyConvertion> convertCurrency(@RequestParam String from,
                                                   @RequestParam String to,
                                                   @RequestParam BigDecimal amount) {
       return webClient.get()
               .uri(API_URL + "?from={from}&to={to}&amount={amount}&apikey={apikey}", from, to, amount, API_KEY)
               .retrieve()
               .bodyToMono(JsonNode.class)
               .map(jsonNode -> {
                   BigDecimal convertedAmount = jsonNode.get("result").decimalValue();
                   BigDecimal rate  = jsonNode.findPath("rate").decimalValue();
                   String date = jsonNode.findPath("date").textValue();
                   BigDecimal timestamp = jsonNode.findPath("timestamp").decimalValue();
                   CurrencyConvertion conversion = new CurrencyConvertion();
                   conversion.setFromCurrency(from);
                   conversion.setToCurrency(to);
                   conversion.setAmount(amount);
                   conversion.setConversionRate(rate);
                   conversion.setDate(date);
                   conversion.setTimestamp(timestamp);
                   conversion.setConvertedAmount(convertedAmount);
                   return conversion;
               });
   }   

}
