package com.github.kindrat.radagovuasdk.client.dto;

import lombok.*;

import java.util.Date;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class DocumentCard {
    @Singular
    private Set<Type> types;
    @Singular
    private Set<String> publishers;
    private Date date;
    private String regNumber;
    private State state;
    private String uid;
}
