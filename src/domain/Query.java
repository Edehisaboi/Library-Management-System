package domain;

import java.util.Locale;

public record Query(String title, String creator, Integer year) {
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

    public static Query byTitle(String title) {
        return new Query(title, null, null);
    }

    public static Query byCreator(String creator) {
        return new Query(null, creator, null);
    }

    public static Query byYear(int year) {
        return new Query(null, null, year);
    }
}
