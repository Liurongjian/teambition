package com.org.rong.teambition.bean;

/**
 * Created by Rong on 2015/9/30.
 */
public class EmailMessage {
    private String content;
    private String from;
    private String subject;
    private String data;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
