package com.github.kindrat.radagovuasdk.client;

import com.github.kindrat.radagovuasdk.client.dto.DocumentEntry;
import com.github.kindrat.radagovuasdk.client.dto.DocumentCard;
import com.github.kindrat.radagovuasdk.client.dto.HistoryEntry;
import com.github.kindrat.radagovuasdk.client.dto.ListItem;

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
     * Get list of entry-related files
     * @param categoryEntry item metadata
     * @return list of items, describing item files
     */
    List<ListItem> listEntryFiles(DocumentEntry categoryEntry);

    /**
     * Get document lifecycle history
     * @param categoryEntry item metadata
     * @return list of history entries
     */
    List<HistoryEntry> listEntryHistory(DocumentEntry categoryEntry);
}
