package com.kirana.controller;

import com.kirana.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/store")
public class StoreConfigController {

    @Value("${app.store.name:Anshu Kirana Store}")
    private String storeName;

    @Value("${app.store.tagline:తాజా సరుకులు మీ ఇంటి వద్దకే | Fresh Groceries at Your Village Doorstep}")
    private String tagline;

    @Value("${app.store.currency:₹}")
    private String currency;

    @Value("${app.store.country:India}")
    private String country;

    @Value("${app.store.support-phone:+917993542873}")
    private String supportPhone;

    @Value("${app.store.whatsapp-number:917993542873}")
    private String whatsappNumber;

    @Value("${app.store.free-delivery-threshold:299.00}")
    private BigDecimal freeDeliveryThreshold;

    @Value("${app.store.delivery-charge:20.00}")
    private BigDecimal deliveryCharge;

    @Value("${app.store.serviceable-villages:Repudi Village,Kummarikunta,Kothapalli,Nagareddy Gudam,Ananthapur Village,LIG Colony,Madhavaram}")
    private List<String> serviceableVillages;

    @GetMapping("/config")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStoreConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("name", storeName);
        config.put("tagline", tagline);
        config.put("currency", currency);
        config.put("country", country);
        config.put("supportPhone", supportPhone);
        config.put("whatsappNumber", whatsappNumber);
        config.put("freeDeliveryThreshold", freeDeliveryThreshold);
        config.put("deliveryCharge", deliveryCharge);
        config.put("serviceableVillages", serviceableVillages);
        return ResponseEntity.ok(ApiResponse.ok(config));
    }
}
