package com.example.filedemo.controller;

public class ZohoResponse {

    private String session_delete_url;
    private String save_url;
    private String session_id;
    private String document_delete_url;
    private String document_id;
    private String editor_url;
    private String document_url;

    public String getSession_delete_url() {
        return session_delete_url;
    }

    public void setSession_delete_url(String session_delete_url) {
        this.session_delete_url = session_delete_url;
    }

    public String getSave_url() {
        return save_url;
    }

    public void setSave_url(String save_url) {
        this.save_url = save_url;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public String getDocument_delete_url() {
        return document_delete_url;
    }

    public void setDocument_delete_url(String document_delete_url) {
        this.document_delete_url = document_delete_url;
    }

    public String getDocument_id() {
        return document_id;
    }

    public void setDocument_id(String document_id) {
        this.document_id = document_id;
    }

    public String getEditor_url() {
        return editor_url;
    }

    public void setEditor_url(String editor_url) {
        this.editor_url = editor_url;
    }

    public String getDocument_url() {
        return document_url;
    }

    public void setDocument_url(String document_url) {
        this.document_url = document_url;
    }
}
