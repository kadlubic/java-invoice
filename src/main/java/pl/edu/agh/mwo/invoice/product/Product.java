package pl.edu.agh.mwo.invoice.product;

import java.math.BigDecimal;
import java.util.Objects;

public abstract class Product {
    private final String name;
    private final BigDecimal price;
    private final BigDecimal taxPercent;
    private int quantity = 0;

    protected Product(String name, BigDecimal price, BigDecimal tax) {
        if (name == null || name.isEmpty() || price == null || tax == null || tax.compareTo(BigDecimal.ZERO) < 0
                || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Nieprawidłowe parametry produktu");
        }
        this.name = name;
        this.price = price;
        this.taxPercent = tax;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Ujemna ilość produktu");
        }
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getTaxPercent() {
        return taxPercent;
    }

    public BigDecimal getPriceWithTax() {
        BigDecimal taxAmount = price.multiply(taxPercent);
        return price.add(taxAmount);
    }


    public BigDecimal getTax() {
        BigDecimal taxAmount = price.multiply(taxPercent);
        return taxAmount.multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal getTotalPriceWithTax() {
        return getPriceWithTax().multiply(BigDecimal.valueOf(quantity));
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Product other = (Product) obj;
        return Objects.equals(name, other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
