package com.iolite.kpcamera.attendance;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HellowController {
	
	@GetMapping("/hello")
	public String sayHellow() {
		return "hello";
	}

}
