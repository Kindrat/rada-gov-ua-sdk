package com.github.kindrat.radagovuasdk.client;

import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ApacheRadaClientIT {
    private ApacheRadaClient client;

    @Test
    public void getAllCategories() throws Exception {
        List<ListItem> categories = client.listAllCategories();
        assertThat(categories).isNotNull().isNotEmpty();
    }
}
