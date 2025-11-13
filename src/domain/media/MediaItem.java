package domain.media;

import domain.Category;
import domain.Query;
import util.Validation;

import java.util.UUID;
import java.util.List;
import java.util.Set;
import java.util.Locale;
import java.util.Objects;

public abstract class MediaItem {
    private final UUID id;
    private final String title;
    private final List<String> creators;
    private final int year;
    private final Set<Category> categories;

    protected MediaItem(String title, List<String> creators, int year, Set<Category> categories) {
        this.id = UUID.randomUUID();
        this.title = Validation.nonBlank(title, "title");
        this.creators = List.copyOf(Validation.nonNull(creators, "creators"));
        Validation.require(year > 0, "year must be greater than 0");
        this.year = year;
        this.categories = Set.copyOf(Validation.nonNull(categories, "categories"));
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getCreators() {
        return creators;
    }

    public int getYear() {
        return year;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public boolean matches(Query q) {
        if (q == null) {
            return true;
        }

        // Title/creator text: OR semantics when both are provided
        boolean hasTitle = q.title() != null && !q.title().isBlank();
        boolean hasCreator = q.creator() != null && !q.creator().isBlank();

        boolean textMatches;
        if (!hasTitle && !hasCreator) {
            textMatches = true;
        } else {
            boolean titleMatches = false;
            boolean creatorMatches = false;

            if (hasTitle) {
                String t = title.toLowerCase(Locale.ROOT);
                titleMatches = t.contains(q.title());
            }
            if (hasCreator) {
                String c = q.creator();
                creatorMatches = creators.stream()
                        .map(s -> s.toLowerCase(Locale.ROOT))
                        .anyMatch(s -> s.contains(c));
            }
            textMatches = titleMatches || creatorMatches;
        }

        // Year: AND with text
        boolean yearMatches = q.year() == null || Objects.equals(year, q.year());

        return textMatches && yearMatches;
    }

    public abstract String details();

    @Override
    public String toString() {
        String by = creators.isEmpty() ? "" : " by " + String.join(", ", creators);
        return getClass().getSimpleName() + ": " + title + by + " (" + year + ") ";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof MediaItem that))
            return false;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
