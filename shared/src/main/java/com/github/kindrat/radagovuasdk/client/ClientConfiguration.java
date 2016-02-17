package com.github.kindrat.radagovuasdk.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ClientConfiguration {
    private String baseUri = "zakon3.rada.gov.ua";
    private String rootMenu = "laws/main/n/stru2";
}
