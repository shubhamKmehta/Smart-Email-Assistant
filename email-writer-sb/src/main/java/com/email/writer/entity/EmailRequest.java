package com.email.writer.entity;

import lombok.Data;

@Data
public class EmailRequest {
    private String emailContent;
    private String tone;  // yaha tone ka mtlb haie kis trh ka reply chahiye...casual reply chahiye ,proffesinal reply etc.
}
