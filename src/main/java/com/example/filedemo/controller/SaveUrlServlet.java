package com.example.filedemo.controller;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.RequestContext;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(urlPatterns = "/saveFile", loadOnStartup = 1)
public class SaveUrlServlet extends HttpServlet {

    Logger logger = LoggerFactory.getLogger(SaveUrlServlet.class);

    private static final long serialVersionUID = 1L;
    private static final String UPLOAD_DIRECTORY = "upload";
    private static final int MEMORY_THRESHOLD = 1024 * 1024 * 3;
    private static final int MAX_FILE_SIZE      = 1024 * 1024 * 40;
    private static final int MAX_REQUEST_SIZE   = 1024 * 1024 * 50;

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!ServletFileUpload.isMultipartContent(request)) {

            writer.write("RESPONSE: Form must has enctype=multipart/form-data.");

            response.setStatus(response.SC_BAD_REQUEST);
            writer.flush();
            return;
        }
        DiskFileItemFactory factory = new DiskFileItemFactory();

        factory.setSizeThreshold(MEMORY_THRESHOLD);

        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setFileSizeMax(MAX_FILE_SIZE);
        upload.setSizeMax(MAX_REQUEST_SIZE);
        String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIRECTORY;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        try {
            List<FileItem> formItems = (List<FileItem>) upload.parseRequest((RequestContext) request);
            System.out.println("Form Items Values   >>>   " + formItems);
            if (formItems != null && formItems.size() > 0) {

                for (FileItem item : formItems) {
                    System.out.println("Field Name:" + item.getFieldName());
                    if (!item.isFormField()) {
                        if (item.getFieldName().equalsIgnoreCase("content")) {
                            String fileName = new File(item.getName()).getName();
                            String filePath = uploadPath + "/" + fileName;
                            File storeFile = new File(filePath);
                            item.write(storeFile);
                            System.out.print("Got the file" +storeFile.getName());
                            logger.trace("Got the file" +storeFile.getName());
                            logger.debug("Got the file" +storeFile.getName());
                            logger.info("Got the file" +storeFile.getName());
                            logger.warn("Got the file" +storeFile.getName());
                            logger.error("Got the file" +storeFile.getName());
                        }
                    } else {
                        byte[] fieldVal;
                        if (item.getFieldName().equalsIgnoreCase("filename")) {
                            fieldVal = item.get();
                            String fileNameValue = new String(fieldVal);
                            System.out.println(item.getFieldName() + " value: " + fileNameValue);
                        } else if (item.getFieldName().equalsIgnoreCase("id")) {
                            fieldVal = item.get();
                            String idValue = new String(fieldVal);
                            System.out.println(item.getFieldName() + " value: " + idValue);
                        } else if (item.getFieldName().equalsIgnoreCase("format")) {
                            fieldVal = item.get();
                            String formatValue = new String(fieldVal);
                            System.out.println(item.getFieldName() + " value: " + formatValue);
                        }
                    }
                }
                response.setStatus(response.SC_OK);
                writer.write("Document Saved Succesfully !!!");
            }
        } catch (Exception ex) {
            response.setStatus(response.SC_BAD_REQUEST);

            writer.write("RESPONSE:Document save fails");
// For Custom message showing in zohoeditor

        }
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException,IOException {
        PrintWriter writer = response.getWriter();
        writer.write("Not Allowed");
        response.setStatus(response.SC_BAD_REQUEST);
    }


}
