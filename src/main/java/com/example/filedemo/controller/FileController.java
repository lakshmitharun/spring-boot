package com.example.filedemo.controller;

import com.example.filedemo.payload.UploadFileResponse;
import com.example.filedemo.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    CustomerRepository repository;

    @Autowired
    private DBFileStorageService DBFileStorageService;

    @RequestMapping("/saveToH2")
    public String process(){
        repository.save(new Customer("Jack", "Smith"));
        repository.save(new Customer("Adam", "Johnson"));
        repository.save(new Customer("Kim", "Smith"));
        repository.save(new Customer("David", "Williams"));
        repository.save(new Customer("Peter", "Davis"));
        return "Done";
    }


    @PostMapping("/uploadFile")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = fileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();

        return new UploadFileResponse(fileName, fileDownloadUri,
                file.getContentType(), file.getSize());
    }

    @PostMapping("/uploadMultipleFiles")
    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        return Arrays.asList(files)
                .stream()
                .map(file -> uploadFile(file))
                .collect(Collectors.toList());
    }

    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileId) {
        // Load file from database
        DBFile dbFile = DBFileStorageService.getFile(fileId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + dbFile.getFileName() + "\"")
                .body(new ByteArrayResource(dbFile.getData()));
    }

    @PostMapping("/editFile/{fileName}/{type}/{id}")
    public ZohoResponse editFile(@RequestParam("file") MultipartFile files,  @PathVariable("fileName") String fileName,  @PathVariable("type") String type,  @PathVariable("id") String id) throws IOException {
        File convFile = new File(fileName + ".docx");
        convFile.createNewFile();
        FileOutputStream fos = null;
        ZohoResponse zohoResponse = null;
        try {
            fos = new FileOutputStream(convFile);

        fos.write(files.getBytes());

            zohoResponse = callZoho(convFile, fileName, type, id);
        } catch (Exception e) {

        } finally {
            fos.close();
        }
        return zohoResponse;

    }

    public ZohoResponse callZoho(File file, String fileName, String type, String id) {
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("document", new FileSystemResource(file));
        map.add("editor_settings", "{\"unit\":\"in\",\"language\":\"en\",\"view\":\"pageview\"}\n");
        map.add("permissions", "{\"document.export\":true,\"document.print\":true,\"document.edit\":true,\"review.changes.resolve\":false,\"review.comment\":true,\"collab.chat\":true }");
        map.add("callback_settings", "{\"save_format\":\"docx\",\"save_url\":\"https://example.com/zoho_save_callback\",\"context_info\":" + id + " }");
        map.add("document_info", "{\"document_name\":\"Legal  Document\",\"document_id\":1349}");
        map.add("user_info", "{\"user_id\":\"3001083\",\"display_name\":\"Mickel Jackson\"}");

        org.springframework.http.HttpHeaders headers = new HttpHeaders();
        //headers.setContentType(new MediaType("multipart", "form-data"));

        RestTemplate template = new RestTemplate();

        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<LinkedMultiValueMap<String, Object>>(map, headers);
        ResponseEntity<ZohoResponse> response = template.postForEntity("https://writer.zoho.com/v1/officeapi/document?apikey=49f1b6d07570d28cf729d4e2acdde0b5", requestEntity, ZohoResponse.class);
        ZohoResponse res = response.getBody();
        return res;
    }


}
