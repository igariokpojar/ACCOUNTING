package com.cydeo;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
@Getter
@Setter
public class EmailContext {
    private String from;
    private String to;
    private String subject;
    private String email;
    private String attachment;
    private String fromDisplayName;
    private String emailLanguage;
    private String displayName;
    private String templateLocation;
    private Map<String, Object> context;

}
