package com.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController { 
  
    @Value("${my.website.name}") 
    private String myWebsiteName;
    
    @Value("${my.additional.prop}") 
    private String myAdditionalProp; 
  
    @RequestMapping(path = "/", method = RequestMethod.GET) 
    public String welcome() { 
        System.out.println(myAdditionalProp);
        return "Welcome to " + myWebsiteName; 
    } 
  
} 
