package com.example.filedemo;

import com.example.filedemo.controller.ZohoResponse;
import com.example.filedemo.property.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;

@SpringBootApplication
@EnableConfigurationProperties({
		FileStorageProperties.class
})
@ServletComponentScan
public class FileDemoApplication {

	public static void main(String[] args) throws FileNotFoundException {

		SpringApplication.run(FileDemoApplication.class, args);
//		RestTemplate restTemplate = new RestTemplate();
//		byte body[] = restTemplate.getForObject("https://immense-island-67360.herokuapp.com//download/1231", byte[].class);
//		try (FileOutputStream ff = new FileOutputStream(new File("uploads/des1.docx"))) {
//			ff.write(body);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}



	}
}
