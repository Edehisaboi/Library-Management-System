package services;

import domain.Query;
import domain.inventory.Holding;
import domain.inventory.HoldingStatus;
import domain.media.MediaItem;
import infra.ConsoleView;
import repo.*;
import util.Validation;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for managing the library catalog.
 * Handles adding titles, adding copies, and searching.
 */
public final class CatalogService {
    private final MediaRepository mediaRepo;
    private final InventoryRepository invRepo;

    /**
     * Creates a new CatalogService.
     * 
     * @param mediaRepo repository for media titles
     * @param invRepo   repository for physical copies
     */
    public CatalogService(MediaRepository mediaRepo, InventoryRepository invRepo) {
        this.mediaRepo = Objects.requireNonNull(mediaRepo, "mediaRepo");
        this.invRepo = Objects.requireNonNull(invRepo, "invRepo");
    }

    /**
     * Interactive search helper that prompts for a query, searches, and allows
     * selecting an item.
     * returns the selected item (or empty if cancelled/no results).
     *
     * @param view the console view to use for I/O
     * @return Optional containing the selected MediaItem, or empty if none selected
     */
    public Optional<MediaItem> searchAndSelect(ConsoleView view) {
        String query = view.promptString("Enter search query (title/creator or blank for all)", true);
        Query q = new Query(query, query, null);
        List<MediaItem> results = search(q);

        if (results.isEmpty()) {
            view.showMessage("No items found.");
            view.pause();
            return Optional.empty();
        }

        while (true) {
            view.showMessage("\nFound " + results.size() + " items:");
            for (int i = 0; i < results.size(); i++) {
                MediaItem item = results.get(i);
                int available = availableCount(item.getId());
                view.showMessage((i + 1) + ". " + item.toString() + " | Available: " + available);
            }

            view.showMessage("0. Back");
            int choice = view.promptInt("Select an item (or 0 to cancel)", 0, results.size());

            if (choice == 0) {
                return Optional.empty();
            }

            // Return the selected item
            return Optional.of(results.get(choice - 1));
        }
    }

    /**
     * Adds a new media title to the catalog and creates initial copies.
     *
     * @param item          the media item to add
     * @param initialCopies number of physical copies to create immediately
     * @return the saved media item
     */
    public MediaItem addTitle(MediaItem item, int initialCopies) {
        Objects.requireNonNull(item, "item");
        Validation.require(initialCopies >= 0, "initialCopies must be >= 0");
        MediaItem saved = mediaRepo.save(item);
        for (int i = 0; i < initialCopies; i++) {
            invRepo.save(new Holding(saved));
        }
        return saved;
    }

    /**
     * Adds physical copies of an existing media title.
     *
     * @param mediaId the ID of the media item
     * @param count   number of copies to add
     * @throws NoSuchElementException if the media item does not exist
     */
    public void addCopies(UUID mediaId, int count) {
        Validation.require(count > 0, "count must be > 0");
        MediaItem item = mediaRepo.findById(mediaId)
                .orElseThrow(() -> new NoSuchElementException("Media item not found: " + mediaId));

        for (int i = 0; i < count; i++) {
            invRepo.save(new Holding(item));
        }
    }

    /**
     * Searches the catalog for items matching the query.
     *
     * @param q the search criteria
     * @return list of matching media items
     */
    public List<MediaItem> search(Query q) {
        // Return all when query is null or has no criteria
        if (q == null || (q.title() == null && q.creator() == null && q.year() == null)) {
            return mediaRepo.findAll();
        }
        return mediaRepo.findAll().stream()
                .filter(m -> m.matches(q))
                .collect(Collectors.toList());
    }

    /**
     * Finds all physical copies of a specific media title.
     * 
     * @param mediaId the media item ID
     * @return list of holdings
     */
    public List<Holding> copiesOf(UUID mediaId) {
        return invRepo.findByMediaId(mediaId);
    }

    /**
     * Counts how many copies of a title are currently available to borrow.
     * 
     * @param mediaId the media item ID
     * @return count of available copies
     */
    public int availableCount(UUID mediaId) {
        return (int) invRepo.findByMediaId(mediaId).stream()
                .filter(h -> h.getStatus() == HoldingStatus.AVAILABLE)
                .count();
    }

    /**
     * Retrieves all media items in the catalog.
     * 
     * @return list of all items
     */
    public List<MediaItem> findAll() {
        return mediaRepo.findAll();
    }

    /**
     * Marks a specific holding as lost.
     * 
     * @param holdingId the holding ID
     */
    public void markLost(UUID holdingId) {
        Holding h = invRepo.findById(holdingId)
                .orElseThrow(() -> new NoSuchElementException("Holding not found: " + holdingId));
        h.markLost();
        invRepo.save(h);
    }

    /**
     * Marks a specific holding as damaged.
     * 
     * @param holdingId the holding ID
     */
    public void markDamaged(UUID holdingId) {
        Holding h = invRepo.findById(holdingId)
                .orElseThrow(() -> new NoSuchElementException("Holding not found: " + holdingId));
        h.markDamaged();
        invRepo.save(h);
    }
}
