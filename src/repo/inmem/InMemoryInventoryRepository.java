package repo.inmem;

import domain.inventory.Holding;
import repo.InventoryRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory implementation of InventoryRepository.
 * Stores holdings in a HashMap.
 */
public final class InMemoryInventoryRepository implements InventoryRepository {
    private final Map<UUID, Holding> store = new HashMap<>();
    private final Map<UUID, List<UUID>> byMedia = new HashMap<>();

    @Override
    public Holding save(Holding h) {
        store.put(h.getId(), h);
        List<UUID> ids = byMedia.computeIfAbsent(h.getItem().getId(), k -> new ArrayList<>());
        if (!ids.contains(h.getId())) {
            ids.add(h.getId());
        }
        return h;
    }

    @Override
    public Optional<Holding> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Holding> findByMediaId(UUID mediaId) {
        List<UUID> ids = byMedia.getOrDefault(mediaId, List.of());
        return ids.stream().map(store::get).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
