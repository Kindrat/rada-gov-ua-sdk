package com.github.kindrat.radagovuasdk.client;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.github.kindrat.radagovuasdk.util.TestUtils.print;
import static org.assertj.core.api.Assertions.assertThat;

public class ApacheRadaClientIT {
    private static final ClientConfiguration config = new ClientConfiguration();
    private ApacheRadaClient client;

    @Before
    public void setUp() throws Exception {
        client = new ApacheRadaClient(config);
    }

    @Test
    public void getAllCategories() throws Exception {
        List<ListItem> categories = client.listAllCategories();
        assertThat(categories).isNotNull().isNotEmpty();
    }

    @Test
    public void getCategoryContent() throws Exception {
        client.listAllCategories().forEach(listItem -> {
            List<ListItem> categoryContent = client.listCategory(listItem);
            assertThat(categoryContent).isNotNull().isNotEmpty();
            print(listItem.getTitle(), categoryContent);
        });
    }
}
