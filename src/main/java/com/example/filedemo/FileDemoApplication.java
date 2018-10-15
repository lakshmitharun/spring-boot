package com.example.filedemo;

import com.example.filedemo.property.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@EnableConfigurationProperties({
		FileStorageProperties.class
})
@ServletComponentScan
public class FileDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileDemoApplication.class, args);
	}
}
