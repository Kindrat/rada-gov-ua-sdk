package com.github.kindrat.radagovuasdk.client.dto;

import lombok.Data;
import lombok.Singular;

import java.util.List;

/**
 * Mostly links, describing different document data
 */
@Data
public class DocumentMetadata {
    private String cardUri;
    private String filesUri;
    private String historyUri;
    private String relatedDocsUri;
    @Singular
    private List<Publication> publications;
    @Singular
    private List<String> specifications;
    private String fullTextUri;
}
