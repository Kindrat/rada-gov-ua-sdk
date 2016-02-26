package com.github.kindrat.radagovuasdk.client.dto;

import lombok.Data;
import lombok.Singular;

import java.util.Date;
import java.util.EnumSet;

@Data
public class DocumentCard {
    @Singular
    private EnumSet<Type> types;
    private Publisher publisher;
    private Date date;
    private String regNumber;
    private State state;
    private String uid;
}
