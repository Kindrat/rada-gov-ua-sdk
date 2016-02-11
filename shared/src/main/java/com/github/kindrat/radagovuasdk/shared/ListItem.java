package com.github.kindrat.radagovuasdk.shared;

import lombok.Value;

import java.net.URI;

@Value
public class ListItem {
    private final String title;
    private final String type;
    private final URI uri;
}
