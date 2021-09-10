package com.fcherchi.demo.controllers;

import com.fcherchi.demo.readers.RFIDReadersManager;
import com.fcherchi.demo.readers.ReadersStatusChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {

    @Autowired
    private RFIDReadersManager rfidReadersManager;

    @Autowired
    private ReadersStatusChecker readerStatusChecker;


    @RequestMapping("ping")
    public @ResponseBody String ping() {
        return "pong";
    }



}
