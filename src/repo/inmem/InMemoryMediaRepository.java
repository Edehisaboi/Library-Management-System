package repo.inmem;

import domain.media.MediaItem;
import repo.MediaRepository;

import java.util.*;

public final class InMemoryMediaRepository implements MediaRepository {
    private final Map<UUID, MediaItem> store = new HashMap<>();

    @Override
    public MediaItem save(MediaItem item) {
        store.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<MediaItem> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<MediaItem> findAll() {
        return new ArrayList<>(store.values());
    }
}


