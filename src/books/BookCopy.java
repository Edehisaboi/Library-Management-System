package books;

import java.util.Objects;
import java.util.UUID;

public class BookCopy {
    private final UUID copyId;
    private final UUID bookId; // references Book#getID()
    private Status status;

    public BookCopy(UUID bookId) {
        this.copyId = UUID.randomUUID();
        this.bookId = Objects.requireNonNull(bookId);
        this.status = Status.AVAILABLE;
    }

    public UUID getCopyId() { return copyId; }
    public UUID getBookId()  { return bookId; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = Objects.requireNonNull(status); }

    @Override
    public String toString() {
        return "BookCopy:" +
               "\nCopy ID: " + copyId +
               "\nBook ID: " + bookId +
               "\nStatus: " + status;
    }
}
