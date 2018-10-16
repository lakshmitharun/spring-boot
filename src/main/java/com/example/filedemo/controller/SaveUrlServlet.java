package com.example.filedemo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.output.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@WebServlet(urlPatterns = "/saveFile", loadOnStartup = 1)
public class SaveUrlServlet extends HttpServlet {

    @Autowired
    private DBFileStorageService DBFileStorageService;

    Logger logger = LoggerFactory.getLogger(SaveUrlServlet.class);

    private static final long serialVersionUID = 1L;
    private static final int MEMORY_THRESHOLD = 1024 * 1024 * 3;
    private static final int MAX_FILE_SIZE      = 1024 * 1024 * 40;
    private static final int MAX_REQUEST_SIZE   = 1024 * 1024 * 50;
    private File file;

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
        File uploadDir = new File("uploads");
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        try {
            // Parse the request to get file items.
            List fileItems = null;
            try {
                fileItems = upload.parseRequest(request);
            } catch (FileUploadException e) {
                e.printStackTrace();
            }

            String uniquFile = null;

            // Process the uploaded file items
            Iterator i = fileItems.iterator();

            while (i.hasNext()) {
                FileItem fi = (FileItem) i.next();
                if (!fi.isFormField()) {
                    // Get the uploaded file parameters
                    String fieldName = fi.getFieldName();
                    String fileName = fi.getName();
                    String contentType = fi.getContentType();
                    boolean isInMemory = fi.isInMemory();
                    long sizeInBytes = fi.getSize();

                    // Write the file
                    file = new File("uploads"+"/"+fileName);
                    fi.write(file);
                    saveFile(file);
                }
                if(fi.getFieldName().equalsIgnoreCase("id")){
                    uniquFile = fi.getString();
                }
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }

    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException,IOException {
        PrintWriter writer = response.getWriter();
        writer.write("Not Allowed");
        response.setStatus(response.SC_BAD_REQUEST);
    }

    public void saveFile(File file) throws IOException {

        MultipartFile result = new MockMultipartFile(file.getName(), new FileInputStream(file));
        DBFileStorageService.storeFile(result);
    }
}
