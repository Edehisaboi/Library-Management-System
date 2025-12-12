package services;

import domain.Query;
import domain.inventory.Holding;
import domain.inventory.HoldingStatus;
import domain.media.MediaItem;
import repo.InventoryRepository;
import repo.MediaRepository;
import util.Validation;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
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
     * Adds a single physical copy of an existing media title.
     *
     * @param mediaId the ID of the media item
     * @return the newly created Holding
     * @throws NoSuchElementException if the media item does not exist
     */
    public Holding addCopy(UUID mediaId) {
        return mediaRepo.findById(mediaId)
                .map(Holding::new)
                .map(invRepo::save)
                .orElseThrow(() -> new NoSuchElementException("Media item not found: " + mediaId));
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
        invRepo.update(h);
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
        invRepo.update(h);
    }
}
