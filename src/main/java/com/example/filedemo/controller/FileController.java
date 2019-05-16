package com.example.filedemo.controller;

import com.example.filedemo.payload.UploadFileResponse;
import com.example.filedemo.service.FileStorageService;
import org.apache.batik.gvt.renderer.ImageRenderer;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
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
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.io.*;
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

    int b=0;

    @RequestMapping("/saveToH2")
    public String process(){
        repository.save(new Customer("Jack", "Smith"));
        repository.save(new Customer("Adam", "Johnson"));
        repository.save(new Customer("Kim", "Smith"));
        repository.save(new Customer("David", "Williams"));
        repository.save(new Customer("Peter", "Davis"));
        return "Done";
    }

    @GetMapping("/save")
    public String process1() throws TranscoderException {

        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        File ff = new File("C:\\Users\\ponnl\\Desktop\\svgstyle.css");

        try {
            fileInputStream = new FileInputStream("C:\\Users\\ponnl\\Desktop\\USStates.svg");
            fileOutputStream = new FileOutputStream("C:\\Users\\ponnl\\Desktop\\test\\Freesample.jpg");
            TranscoderInput input = new TranscoderInput(fileInputStream);
            //TranscoderInput input1 = new TranscoderInput(new FileInputStream("C:\\Users\\ponnl\\Desktop\\svgstyle.css"));
            TranscoderOutput output = new TranscoderOutput(fileOutputStream);
            JPEGTranscoder t = new JPEGTranscoder();

            // Set the transcoding hints.
            t.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, new Float(.8));
           //t.addTranscodingHint(JPEGTranscoder.KEY_USER_STYLESHEET_URI, ff.toURI().toString());
            t.transcode(input, output);
        } catch (IOException e) {

        } finally { //  finally blocks are guaranteed to be executed
            // close() can throw an IOException too, so we got to wrap that too
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                // handle an exception, or often we just ignore it
            }
        }
            return "OK";
    }
    @PostMapping("/save2")
    private byte[] renderPng(@RequestParam String type, @RequestParam Float quality) {

        try {
            byte[] svgBytes = IOUtils.toByteArray(new FileInputStream("C:\\Users\\ponnl\\Desktop\\USStates.svg"));
            TranscoderInput transcoderInput = new TranscoderInput(new ByteArrayInputStream(svgBytes));
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            TranscoderOutput transcoderOutput = new TranscoderOutput(output);

            Transcoder transcoder = null;
            if(type.equalsIgnoreCase("png")) {
                transcoder = new PNGTranscoder()
                {
                    @Override
                    protected ImageRenderer createRenderer()
                    {
                        ImageRenderer r = super.createRenderer();

                        RenderingHints rh = r.getRenderingHints();

                        rh.add(new RenderingHints(RenderingHints.KEY_ALPHA_INTERPOLATION,
                                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY));
                        rh.add(new RenderingHints(RenderingHints.KEY_INTERPOLATION,
                                RenderingHints.VALUE_INTERPOLATION_BICUBIC));

                        rh.add(new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON));

                        rh.add(new RenderingHints(RenderingHints.KEY_COLOR_RENDERING,
                                RenderingHints.VALUE_COLOR_RENDER_QUALITY));
                        rh.add(new RenderingHints(RenderingHints.KEY_DITHERING,
                                RenderingHints.VALUE_DITHER_DISABLE));

                        rh.add(new RenderingHints(RenderingHints.KEY_RENDERING,
                                RenderingHints.VALUE_RENDER_QUALITY));

                        rh.add(new RenderingHints(RenderingHints.KEY_STROKE_CONTROL,
                                RenderingHints.VALUE_STROKE_PURE));

                        rh.add(new RenderingHints(RenderingHints.KEY_FRACTIONALMETRICS,
                                RenderingHints.VALUE_FRACTIONALMETRICS_ON));
                        rh.add(new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING,
                                RenderingHints.VALUE_TEXT_ANTIALIAS_OFF));

                        r.setRenderingHints(rh);

                        return r;
                    }
                };
                transcoder.addTranscodingHint(PNGTranscoder.KEY_BACKGROUND_COLOR, Color.WHITE);
            } else {
                transcoder = new JPEGTranscoder();

                Float jpegQuality = new Float(quality);
                // KEY_WIDTH - seems to pick it up just fine from the SVG charts.  Set to 560 otherwise.
                // KEY_QUALITY 0-1.0 with 1.0 being No Loss.  Value must be of type Float.
                transcoder.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, jpegQuality);
            }

            // NOTE: for linux you need Java 1.4.1+ AND the headless environment (e.g. export JAVA_OPTS='-Djava.awt.headless=true').
            try {
                transcoder.transcode(transcoderInput, transcoderOutput);
            }
            catch(Exception e) {
                logger.error("SVG To Raster response transcode exception", e);
                if(output != null) {
                    output.close();
                }
                throw( new RuntimeException("SVG To Raster Filter Response Stream Exception", e) );
            }

            if(output != null) {
                output.flush();
                output.close();
            }

            transcoderInput  = null;
            transcoderOutput = null;
            transcoder = null;
            if(type.equalsIgnoreCase("jpg")){
                FileUtils.writeByteArrayToFile(new File("C:\\Users\\ponnl\\Desktop\\USStates1"+System.currentTimeMillis()+".jpg"), output.toByteArray());
            }else{
                FileUtils.writeByteArrayToFile(new File("C:\\Users\\ponnl\\Desktop\\USStates1"+System.currentTimeMillis()+".png"), output.toByteArray());
            }

            return output.toByteArray();
        } catch (Exception exc) {
            logger.error("Error in rendering png method", exc);
        }
        return new byte[0];
    }


    @PostMapping("/uploadFile")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = null;
        try {
           fileName  = fileStorageService.storeFileToBOX(file);
        }catch (Exception e){

        }

        /*String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();*/

        return new UploadFileResponse(file.getOriginalFilename(), fileName,
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

    @PostMapping("/editFileDecoded/{fileName}/{type}/{id}")
    public ZohoResponse editFileWithBase(@RequestParam("filedecoded") String fileBase,  @PathVariable("fileName") String fileName,  @PathVariable("type") String type,  @PathVariable("id") String id) throws IOException {

        BASE64Decoder decoder = new BASE64Decoder();
        byte[] decodedBytes = decoder.decodeBuffer(fileBase);
        File convFile = new File(fileName + ".docx");

        FileOutputStream fop = null;

        ZohoResponse zohoResponse = callZoho(convFile, fileName, type, id);

        try {
            fop = new FileOutputStream(convFile);

            fop.write(decodedBytes);

            zohoResponse = callZoho(convFile, fileName, type, id);
        } catch (Exception e) {

        } finally {
            fop.close();
        }
        return zohoResponse;

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
        map.add("callback_settings", "{\"save_format\":\"docx\",\"save_url\":\"https://immense-island-67360.herokuapp.com/saveFile\",\"context_info\":" + id + " }");
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
