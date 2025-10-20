package books;

import java.util.Optional;

public final class BookSearchSpec {
    public final Optional<String> titleLike;
    public final Optional<String> authorLike;
    public final Optional<Category> category;
    public final Optional<String> isbn;

    private BookSearchSpec(
            Optional<String> titleLike,
            Optional<String> authorLike,
            Optional<Category> category,
            Optional<String> isbn
    ) {
        this.titleLike = titleLike;
        this.authorLike = authorLike;
        this.category = category;
        this.isbn = isbn;
    }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private String titleLike, authorLike, isbn;
        private Category category;

        public Builder titleLike(String s) { this.titleLike = s; return this; }
        public Builder authorLike(String s) { this.authorLike = s; return this; }
        public Builder category(Category c) { this.category = c; return this; }
        public Builder isbn(String s) { this.isbn = s; return this; }

        public BookSearchSpec build() {
            return new BookSearchSpec(
                Optional.ofNullable(titleLike),
                Optional.ofNullable(authorLike),
                Optional.ofNullable(category),
                Optional.ofNullable(isbn)
            );
        }
    }
}
