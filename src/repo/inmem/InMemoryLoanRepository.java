package repo.inmem;

import domain.loan.Loan;
import repo.LoanRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory implementation of LoanRepository.
 * Stores loans in a HashMap.
 */
public final class InMemoryLoanRepository implements LoanRepository {
    private final Map<UUID, Loan> store = new HashMap<>();

    @Override
    public Loan save(Loan loan) {
        store.put(loan.getId(), loan);
        return loan;
    }

    @Override
    public void update(Loan loan) {
        if (!store.containsKey(loan.getId())) {
            throw new NoSuchElementException("Loan not found: " + loan.getId());
        }
        store.put(loan.getId(), loan);
    }

    @Override
    public Optional<Loan> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Loan> findActiveByMemberId(UUID memberId) {
        return store.values().stream()
                .filter(l -> l.getBorrower().getId().equals(memberId))
                .filter(l -> !l.isReturned())
                .collect(Collectors.toList());
    }

    @Override
    public List<Loan> findOverdue(LocalDate today) {
        return store.values().stream()
                .filter(l -> !l.isReturned() && l.getDueOn().isBefore(today))
                .collect(Collectors.toList());
    }
}
