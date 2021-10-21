package com.lulu.bootstrap.kafka.api;

import lombok.Data;

@Data
public class MessageRequest {

    private int id;
    private String name;
    private String description;
    private Color[] colors;

    @Data
    public static class Color{
        private int id;
        private String name;
    }

}
