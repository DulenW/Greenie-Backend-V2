package com.example.projectgreenie.dto;

import lombok.Data;

@Data
public class ShippingAddressDTO {
    private String fullName;
    private String phone;
    private String addressLine1;
    private String city;
    private String postalCode;
    private String country;
}