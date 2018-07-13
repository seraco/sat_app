package com.seraco.simulator;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

// tag::code[]
@Controller
public class HomeController {

	@RequestMapping(value = "/")
	public String index() {

        System.out.println("I'm here!");

	    return "index";
	}

}
// end::code[]