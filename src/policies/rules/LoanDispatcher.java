package policies.rules;

import domain.inventory.Holding;
import domain.media.MediaItem;
import domain.user.Member;
import policies.LoanRule;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Dispatcher that delegates loan decisions to specific rules based on the
 * MediaItem type.
 * Implements the Strategy Pattern using a map of media types to rules.
 */
public class LoanDispatcher implements LoanRule {
    private final Map<Class<? extends MediaItem>, LoanRule> strategies = new HashMap<>();

    /**
     * Creates a new LoanDispatcher.
     */
    public LoanDispatcher() {
    }

    /**
     * Registers a specific rule for a media type.
     * 
     * @param type the media class (e.g. Book.class)
     * @param rule the rule implementation to use
     */
    public void register(Class<? extends MediaItem> type, LoanRule rule) {
        strategies.put(type, rule);
    }

    /**
     * Gets the appropriate rule for a given holding.
     * 
     * @param holding the holding to get the rule for
     * @return the rule for the holding
     * @throws IllegalStateException if no rule is found for the media type
     */
    private LoanRule getRule(Holding holding) {
        MediaItem item = holding.getItem();
        LoanRule rule = strategies.get(item.getClass());
        if (rule == null) {
            throw new IllegalStateException(
                    "No loan rule registered for media type: " + item.getClass().getSimpleName());
        }
        return rule;
    }

    @Override
    public boolean canLoan(Member member, Holding holding) {
        return getRule(holding).canLoan(member, holding);
    }

    @Override
    public LocalDate dueDate(Member member, Holding holding, LocalDate now) {
        return getRule(holding).dueDate(member, holding, now);
    }
}
