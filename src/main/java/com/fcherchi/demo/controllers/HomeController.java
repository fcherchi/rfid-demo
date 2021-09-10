package com.fcherchi.demo.controllers;

import com.fcherchi.demo.drivers.rfidreader.DTE820Reader;
import com.fcherchi.demo.readers.HeartbeatReport;
import com.fcherchi.demo.readers.RFIDReadersManager;
import com.fcherchi.demo.readers.ReadersStatusChecker;
import com.fcherchi.demo.readers.impl.RFIDReaderFactoryImpl;
import com.fcherchi.demo.readers.impl.RFIDReadersManagerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    private RFIDReaderFactoryImpl readerFactory;

    @Autowired
    private ReadersStatusChecker readersStatusChecker;

	@Autowired
    private RFIDReadersManager rfidReadersManager;



	/**
	 * Goes to the home template
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(Model model) {

//        Map<String, DTE820Reader> readers = readerFactory.getReaders();
//
//        model.addAttribute("readers", readers);
        return "home";
    }

    @RequestMapping(value = "/readers", method = RequestMethod.GET)
    public @ResponseBody Map<String, DTE820Reader> readers() {

        Map<String, DTE820Reader> readers = readerFactory.getReaders();

        return readers;
    }

    @RequestMapping(value = "/heartbeats", method = RequestMethod.GET)
    public @ResponseBody Map<String, HeartbeatReport> heartbeats() {

        Map<String, HeartbeatReport> hbs = readersStatusChecker.getReadersStatus();

        return hbs;
    }


    @RequestMapping("cableOff/{readerId}")
    public @ResponseBody String cableOff(@PathVariable String readerId) {
        //not part of the interface, just for the demo!
        ((RFIDReadersManagerImpl)rfidReadersManager).setCable(readerId, false);
        return "Off";
    }

    @RequestMapping("stop")
    public @ResponseBody String stop() {

        this.rfidReadersManager.stopAllReaders();
        return "Stopped";
    }
	
}
