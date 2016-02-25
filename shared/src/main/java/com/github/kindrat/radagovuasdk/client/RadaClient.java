package com.github.kindrat.radagovuasdk.client;

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
    List<ListItem> listCategory(ListItem category);

    /**
     * Get single document by its metadata
     * @param categoryEntry item metadata
     * @return document as a string (including html tags)
     */
    String getEntryContent(ListItem categoryEntry);
}
