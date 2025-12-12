package domain;

import java.util.Locale;

/**
 * Represents a search query for the catalog.
 * Stores normalized criteria for title, creator, or year.
 *
 * @param title   text to match in title (partial)
 * @param creator text to match in creators (partial)
 * @param year    exact year to match
 */
public record Query(String title, String creator, Integer year) {
    /**
     * Compact constructor that normalizes input strings.
     * Trims and converts to lowercase.
     * 
     * @param title   raw title query
     * @param creator raw creator query
     * @param year    raw year query
     */
    public Query {
        title = normalize(title);
        creator = normalize(creator);
    }

    private static String normalize(String s) {
        if (s == null)
            return null;
        String t = s.trim().toLowerCase(Locale.ROOT);
        return t.isEmpty() ? null : t;
    }

    /**
     * Creates a query searching only by title.
     * 
     * @param title title text
     * @return a new Query object
     */
    public static Query byTitle(String title) {
        return new Query(title, null, null);
    }

    /**
     * Creates a query searching only by creator.
     * 
     * @param creator creator text
     * @return a new Query object
     */
    public static Query byCreator(String creator) {
        return new Query(null, creator, null);
    }

    /**
     * Creates a query searching only by year.
     * 
     * @param year exact year
     * @return a new Query object
     */
    public static Query byYear(int year) {
        return new Query(null, null, year);
    }
}
