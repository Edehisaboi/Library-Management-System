package domain.media;

import domain.Category;
import domain.Query;
import util.Validation;

import java.util.UUID;
import java.util.List;
import java.util.Set;
import java.util.Locale;
import java.util.Objects;

/**
 * Abstract base class representing a media item in the library catalog.
 * Stores common metadata like title, creators, and year.
 */
public abstract class MediaItem {
    private final UUID id;
    private final String title;
    private final List<String> creators;
    private final int year;
    private final Set<Category> categories;

    /**
     * Initializes a new MediaItem.
     *
     * @param title      the title of the item
     * @param creators   list of authors, artists, or directors
     * @param year       release year
     * @param categories set of categories the item belongs to
     */
    protected MediaItem(String title, List<String> creators, int year, Set<Category> categories) {
        this.id = UUID.randomUUID();
        this.title = Validation.nonBlank(title, "title");
        this.creators = List.copyOf(Validation.nonNull(creators, "creators"));
        Validation.require(year > 0, "year must be greater than 0");
        this.year = year;
        this.categories = Set.copyOf(Validation.nonNull(categories, "categories"));
    }

    /**
     * Gets the unique ID of the media item.
     * 
     * @return the UUID
     */
    public UUID getId() {
        return id;
    }

    /**
     * Gets the title of the media item.
     * 
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the list of creators (authors, artists, etc.).
     * 
     * @return an immutable list of creators
     */
    public List<String> getCreators() {
        return creators;
    }

    /**
     * Gets the release year.
     * 
     * @return the year
     */
    public int getYear() {
        return year;
    }

    /**
     * Gets the set of categories associated with this item.
     * 
     * @return an immutable set of categories
     */
    public Set<Category> getCategories() {
        return categories;
    }

    /**
     * Checks if this item matches the given search query.
     * Matches against title and creators (case-insensitive partial match)
     * and year (exact match).
     *
     * @param q the search query object
     * @return true if it matches, false otherwise
     */
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

    /**
     * Returns a string with specific details about the item (e.g. ISBN for books).
     * 
     * @return detail string
     */
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
