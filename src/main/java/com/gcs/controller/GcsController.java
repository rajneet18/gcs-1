package com.gcs.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gcs.service.GoogleStorageClientAdapter;

@Controller
public class GcsController {

	@Autowired
	private GoogleStorageClientAdapter managerURLService;
	

	@GetMapping("/test")
	public @ResponseBody String getAllUserData() {
		return "success !!!";
	}

	@RequestMapping("home")
	public String welcome(Map<String, Object> model) {
		return "index";
	}
	
	@RequestMapping("generateurl")
	public @ResponseBody String generateurl(@RequestParam String objectName ) {
		try {
			return managerURLService.generateSignedURL(objectName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "<b style='color:red'>SERVER ERROR</b>";
		
	}

}