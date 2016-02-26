package com.github.kindrat.radagovuasdk.client;

import com.github.kindrat.radagovuasdk.client.dto.*;

import java.util.List;

public interface RadaClient {
    /**
     * Get a list of all categories of documents on rada.gov.ua
     * @return list of items, including human readable title and category uri
     */
    List<ListItem> listAllCategories();

    /**
     * Get a list of single category items
     * @param category category (including title and uri)
     * @return list of items, including human readable title and category uri
     */
    List<DocumentEntry> listCategory(ListItem category);

    /**
     * Get single document by its metadata
     * @param categoryEntry item metadata
     * @return document as a string (including html tags)
     */
    String getEntryContent(DocumentEntry categoryEntry);

    /**
     * Get single document metadata
     * @param categoryEntry item metadata
     * @return metadata
     */
    DocumentCard getEntryCard(DocumentEntry categoryEntry);

    /**
     * Each document has a list (almost each) of other documents related to a same topic. Since this list is located
     * at a separate page under document page, there is a separate method to retrieve this link
     * @param documentEntry item metadata
     * @return link to related docs
     */
    RelatedDocsLink getLinkToRelatedDocs(DocumentEntry documentEntry);

    /**
     * Get list of entry-related files
     * @param relatedDocsLink link to a page with related docs list
     * @return list of items, describing item files
     */
    List<DocumentEntry> listEntryFiles(RelatedDocsLink relatedDocsLink);

    /**
     * Get document lifecycle history
     * @param categoryEntry item metadata
     * @return list of history entries
     */
    List<HistoryEntry> listEntryHistory(DocumentEntry categoryEntry);
}
