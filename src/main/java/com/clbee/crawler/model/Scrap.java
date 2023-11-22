package com.clbee.crawler.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scrap {
    private List<String> urls;
    private Map<String, Object> meta;

    public Scrap() {
        urls = new ArrayList<>();
        meta = new HashMap<String,Object>();
    }
    public Scrap(List<String> urls, Map<String, Object> meta) {
        this.urls = urls;
        this.meta = meta;
    }
    public List<String> getUrls() {
        return urls;
    }
    public void setUrls(List<String> urls) {
        this.urls = urls;
    }
    public Map<String, Object> getMeta() {
        return meta;
    }
    public void setMeta(Map<String,Object> meta) {
        this.meta = meta;
    }
}