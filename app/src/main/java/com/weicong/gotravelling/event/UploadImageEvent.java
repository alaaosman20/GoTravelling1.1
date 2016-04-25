package com.weicong.gotravelling.event;

import java.util.List;

public class UploadImageEvent {

    private List<String> mFilename;

    private List<String> mUrl;

    public UploadImageEvent(List<String> filename, List<String> url) {
        mFilename = filename;
        mUrl = url;
    }

    public List<String> getFilename() {
        return mFilename;
    }

    public List<String> getUrl() {
        return mUrl;
    }
}
