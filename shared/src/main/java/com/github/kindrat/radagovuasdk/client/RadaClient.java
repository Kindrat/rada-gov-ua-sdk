package com.github.kindrat.radagovuasdk.client;

import java.util.List;

public interface RadaClient {
    List<ListItem> listAllCategories();
    List<ListItem> listCategory(ListItem category);
}
