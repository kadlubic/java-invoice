package pl.edu.agh.mwo.invoice.product;

import java.math.BigDecimal;

public class ProduktyAkcyzowe extends Product {
    public ProduktyAkcyzowe(String name, BigDecimal price, BigDecimal tax) {
        super(name, price.multiply(BigDecimal.ONE.add(new BigDecimal("0.23"))).add(new BigDecimal("5.56")), tax);
    }
}
