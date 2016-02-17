package com.github.kindrat.radagovuasdk.client;

import com.google.common.base.Charsets;
import org.apache.http.client.utils.URIBuilder;

import java.util.function.Supplier;

import static java.util.Arrays.stream;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class UriSupplier implements Supplier<String> {
    private static final String SLASH = "/";

    private final URIBuilder builder;
    private final StringBuilder stringBuilder;

    public UriSupplier(String host, String... parts) {
        builder = new URIBuilder();
        builder.setCharset(Charsets.UTF_8);
        builder.setScheme("http");
        builder.setHost(host);

        stringBuilder = new StringBuilder(100);
        stream(parts).forEach(this::addPart);
    }

    private UriSupplier addPart(String part) {
        if (isNotEmpty(part)) {
            if (!part.startsWith(SLASH)) {
                stringBuilder.append(SLASH);
            }
            stringBuilder.append(part);
            if (part.endsWith(SLASH)) {
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            }
        }
        return this;
    }

    @Override
    public String get() {
        builder.setPath(stringBuilder.toString());
        return builder.toString();
    }
}
