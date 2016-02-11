package com.github.kindrat.radagovuasdk.shared;

import java.util.List;

public interface RadaClient {
    List<ListItem> listAllCategories();
    List<ListItem> listCategory(String category);
}
