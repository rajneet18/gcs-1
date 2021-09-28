package com.gcs.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dto.FileDetail;
import com.gcs.service.ManageGcsSignedURLService;
import com.gcs.service.GcsUploadService;

@Controller
public class GcsController {

	@Autowired
	private ManageGcsSignedURLService managerURLService;

	@Autowired
	private GcsUploadService uploader;

	@GetMapping("/test")
	public @ResponseBody String getAllUserData() {
		return "success !!!";
	}

	@RequestMapping("home")
	public String welcome(Map<String, Object> model) {
		return "index";
	}

	@RequestMapping("getobject")
	public @ResponseBody List<String> getobject(@RequestParam String bucketname) {
		List<String> objs = uploader.getListOfObject(bucketname);
		return objs;
	}
	
	
	@RequestMapping("getbucket")
	public @ResponseBody List<String> getAvailableBucket() {
		List<String> objs = uploader.getAvailableBucket();
		return objs;
	}


	@RequestMapping("generateurl")
	public @ResponseBody String generateurl(@RequestParam String objectName,@RequestParam String bucketname) {
		try {
			System.out.println(bucketname);
			return managerURLService.generateSignedURL(objectName,bucketname);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "<b style='color:red'>SERVER ERROR</b>";

	}

	@RequestMapping(method = RequestMethod.POST, value = "uploaddoc")
	public @ResponseBody String uploadFile(@ModelAttribute FileDetail uplFile) {
		uploader.processRequest(uplFile);
		return "good";
	}
}