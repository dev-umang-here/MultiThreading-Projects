package com.microservice.orderservice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

    private Long customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
}