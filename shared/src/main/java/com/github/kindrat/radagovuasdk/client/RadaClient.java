package com.github.kindrat.radagovuasdk.client;

import java.util.List;

public interface RadaClient {
    static final String CATEGORIES_URI = "laws/main/n/stru2";
    List<ListItem> listAllCategories();
    List<ListItem> listCategory(String category);
}
