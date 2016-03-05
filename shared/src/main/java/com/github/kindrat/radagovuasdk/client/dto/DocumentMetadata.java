package com.github.kindrat.radagovuasdk.client.dto;

import lombok.*;

import java.util.List;

/**
 * Mostly links, describing different document data
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class DocumentMetadata {
    private DocumentCard documentCard;
    private String filesUri;
    private String historyUri;
    private String relatedDocsUri;
    @Singular
    private List<Publication> publications;
    @Singular
    private List<String> specifications;
    private String fullTextUri;
}
