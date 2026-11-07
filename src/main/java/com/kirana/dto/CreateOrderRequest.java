package com.kirana.dto;

import com.kirana.entity.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class CreateOrderRequest {

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Please enter a valid 10-digit Indian mobile number")
    private String customerPhone;

    @NotBlank(message = "Village name is required")
    private String deliveryVillage;

    @NotBlank(message = "Street address or house number is required")
    private String streetArea;

    private String landmark;
    private String pincode = "518001";
    private String deliveryInstructions;
    private PaymentMethod paymentMethod = PaymentMethod.COD;
    private String couponCode;

    public CreateOrderRequest() {}

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getDeliveryVillage() { return deliveryVillage; }
    public void setDeliveryVillage(String deliveryVillage) { this.deliveryVillage = deliveryVillage; }

    public String getStreetArea() { return streetArea; }
    public void setStreetArea(String streetArea) { this.streetArea = streetArea; }

    public String getLandmark() { return landmark; }
    public void setLandmark(String landmark) { this.landmark = landmark; }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    public String getDeliveryInstructions() { return deliveryInstructions; }
    public void setDeliveryInstructions(String deliveryInstructions) { this.deliveryInstructions = deliveryInstructions; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }
}
