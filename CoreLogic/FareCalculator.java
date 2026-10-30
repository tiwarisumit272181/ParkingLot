import java.math.BigDecimal;
import java.util.List;

public class FareCalculator {
    private final List<FareStrategy> strategies;
    FareCalculator(List<FareStrategy> strategies) {
        this.strategies = strategies;
    }
    public BigDecimal calculate(Ticket ticket) {
        BigDecimal fare=BigDecimal.ZERO;
        for (FareStrategy strategy : strategies) {
            fare=fare.add(strategy.getFare(ticket,fare));
        }
        return fare;
    }
}
