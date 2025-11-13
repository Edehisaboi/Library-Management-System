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

public final class CatalogService {
    private final MediaRepository mediaRepo;
    private final InventoryRepository invRepo;

    public CatalogService(MediaRepository mediaRepo, InventoryRepository invRepo) {
        this.mediaRepo = Objects.requireNonNull(mediaRepo, "mediaRepo");
        this.invRepo = Objects.requireNonNull(invRepo, "invRepo");
    }

    public MediaItem addTitle(MediaItem item, int initialCopies) {
        Objects.requireNonNull(item, "item");
        Validation.require(initialCopies >= 0, "initialCopies must be >= 0");
        MediaItem saved = mediaRepo.save(item);
        for (int i = 0; i < initialCopies; i++) {
            invRepo.save(new Holding(saved));
        }
        return saved;
    }

    public Holding addCopy(UUID mediaId) {
        return mediaRepo.findById(mediaId)
                .map(Holding::new)
                .map(invRepo::save)
                .orElseThrow(() -> new NoSuchElementException("Media item not found: " + mediaId));
    }

    public List<MediaItem> search(Query q) {
        // Return all when query is null or has no criteria
        if (q == null || (q.title() == null && q.creator() == null && q.year() == null)) {
            return mediaRepo.findAll();
        }
        return mediaRepo.findAll().stream()
                .filter(m -> m.matches(q))
                .collect(Collectors.toList());
    }

    public List<Holding> copiesOf(UUID mediaId) {
        return invRepo.findByMediaId(mediaId);
    }

    public int availableCount(UUID mediaId) {
        return (int) invRepo.findByMediaId(mediaId).stream()
                .filter(h -> h.getStatus() == HoldingStatus.AVAILABLE)
                .count();
    }
}
