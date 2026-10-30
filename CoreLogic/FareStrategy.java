import java.math.BigDecimal;

public interface FareStrategy {
    public BigDecimal getFare(Ticket ticket, BigDecimal inputFare);
}

