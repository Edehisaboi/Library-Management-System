package books;

import java.util.UUID;
import java.time.LocalDate;

public record ActiveBorrow(
        UUID bookId,
        UUID copyId,
        UUID memberId,
        LocalDate borrowedDate,
        LocalDate dueDate
) {
}
