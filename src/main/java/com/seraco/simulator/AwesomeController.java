package com.seraco.simulator;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;

@RestController
public class AwesomeController {

    @RequestMapping("/awesome")
    public ArrayList thing() {
        ArrayList satelliteList = new ArrayList();
        satelliteList.add(new Satellite(1,2,3,4,5,6));
        satelliteList.add(new Satellite(2,3,4,5,6,7));
        return satelliteList;
    }

}