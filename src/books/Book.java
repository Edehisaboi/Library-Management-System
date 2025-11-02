package books;

import java.util.*;


public final class Book {
    private final UUID id;
    private final String title;
    private final List<String> authors;
    private final String publisher;
    private final int publicationYear;
    private final String isbn;
    private final Set<Category> categories;

    private Book(
            String title,
            List<String> authors,
            String publisher,
            int publicationYear,
            String isbn,
            Set<Category> categories
    ) {
        this.id = UUID.randomUUID();
        this.title = Objects.requireNonNull(title, "Title cannot be null!");
        this.authors = List.copyOf(Objects.requireNonNull(authors, "Authors cannot be null!"));
        this.publisher = Objects.requireNonNull(publisher, "Publisher cannot be null!");
        this.publicationYear = publicationYear;
        this.isbn = isbn;
        this.categories = Set.copyOf(Objects.requireNonNull(categories, "Categories cannot be null!"));
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public String getPublisher() {
        return publisher;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public String getIsbn() {
        return isbn;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String title;
        private final List<String> authors = new ArrayList<>();
        private String publisher;
        private int publicationYear;
        private String isbn;
        private final Set<Category> categories = new HashSet<>();

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder addAuthor(String author) {
            if (author != null && !author.trim().isEmpty()) {
                this.authors.add(author.trim());
            }
            return this;
        }

        public Builder publisher(String publisher) {
            this.publisher = publisher;
            return this;
        }

        public Builder publicationYear(int publicationYear) {
            this.publicationYear = publicationYear;
            return this;
        }

        public Builder isbn(String isbn) {
            this.isbn = isbn;
            return this;
        }

        public Builder addCategory(Category category) {
            if (category != null) {
                this.categories.add(category);
            }
            return this;
        }

        public Book build() {
            return new Book(title, authors, publisher, publicationYear, isbn, categories);
        }
    }

    @Override
    public String toString() {
        return "Book Details:" +
               "\nID: " + id +
               "\nTitle: " + title +
               "\nAuthors: " + String.join(", ", authors) +
               "\nPublisher: " + publisher +
               "\nPublication Year: " + publicationYear +
               "\nISBN: " + isbn +
               "\nCategories: " + categories;
    }
}
