package com.github.kindrat.radagovuasdk.client.dto;

import lombok.Data;

import java.util.Date;

/**
 * Describes single publication of a document
 */
@Data
public class Publication {
    private String publisher;
    private Date date;
}
