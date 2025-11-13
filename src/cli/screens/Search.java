package cli.screens;

import cli.Screen;
import domain.Query;
import domain.media.MediaItem;
import services.CatalogService;
import util.KeyboardReader;

import java.util.List;

public class Search implements Screen {
    private final KeyboardReader kbr = new KeyboardReader();
    private final CatalogService catalogService;

    public Search(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @Override
    public void display() {
        String query = kbr.getString("Enter search query (title/creator)");

        Query spec = new Query(query, query, null);
        List<MediaItem> items = catalogService.search(spec);

        if (items.isEmpty()) {
            System.out.println("No items found.");
            return;
        }

        for (MediaItem item : items) {
            System.out.println("*****************************************");
            System.out.println(item.toString());
        }
    }
}
