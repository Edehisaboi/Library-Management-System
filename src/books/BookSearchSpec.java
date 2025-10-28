package books;

import java.util.Optional;


public final class BookSearchSpec {
    private final String titleLike;
    private final String authorLike;
    private final Category category;
    private final String isbn;

    private BookSearchSpec(String titleLike, String authorLike, Category category, String isbn) {
        this.titleLike = titleLike;
        this.authorLike = authorLike;
        this.category = category;
        this.isbn = isbn;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static BookSearchSpec quick(String query) {
        String q = query == null ? "" : query.trim();
        if (q.isEmpty()) {
            return new BookSearchSpec(null, null, null, null);
        }

        return builder()
                .titleLike(q)
                .authorLike(q)
                .build();
    }

    public Optional<String> getTitleLike() {
        return Optional.ofNullable(titleLike);
    }

    public Optional<String> getAuthorLike() {
        return Optional.ofNullable(authorLike);
    }

    public Optional<Category> getCategory() {
        return Optional.ofNullable(category);
    }

    public Optional<String> getIsbn() {
        return Optional.ofNullable(isbn);
    }

    public static final class Builder {
        private String titleLike;
        private String authorLike;
        private String isbn;
        private Category category;

        public Builder titleLike(String s) {
            this.titleLike = isBlank(s) ? null : s;
            return this;
        }

        public Builder authorLike(String s) {
            this.authorLike = isBlank(s) ? null : s;
            return this;
        }

        public Builder category(Category c) {
            this.category = c;
            return this;
        }

        public Builder isbn(String s) {
            this.isbn = isBlank(s) ? null : s;
            return this;
        }

        public BookSearchSpec build() {
            return new BookSearchSpec(titleLike, authorLike, category, isbn);
        }

        private static boolean isBlank(String s) {
            return s == null || s.trim().isEmpty();
        }
    }
}
