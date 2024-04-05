package pl.edu.agh.mwo.invoice;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import pl.edu.agh.mwo.invoice.product.Product;

public class Invoice {
    private Map<Product, Integer> products = new HashMap<>();
    private static int nextNumber = 0;
    private final int number = ++nextNumber;

    public void addProduct(Product product) {
        addProduct(product, 1);
    }

    public void addProduct(Product product, Integer quantity) {
        if (product == null || quantity <= 0) {
            throw new IllegalArgumentException();
        }

        products.merge(product, quantity, Integer::sum);
    }

    public BigDecimal getNetTotal() {
        BigDecimal totalNet = BigDecimal.ZERO;
        for (Product product : products.keySet()) {
            BigDecimal quantity = new BigDecimal(products.get(product));
            totalNet = totalNet.add(product.getPrice().multiply(quantity));
        }
        return totalNet;
    }

    public BigDecimal getTaxTotal() {
        return getGrossTotal().subtract(getNetTotal());
    }

    public BigDecimal getGrossTotal() {
        BigDecimal totalGross = BigDecimal.ZERO;
        for (Product product : products.keySet()) {
            BigDecimal quantity = new BigDecimal(products.get(product));
            totalGross = totalGross.add(product.getPriceWithTax().multiply(quantity));
        }
        return totalGross;
    }

    public int getNumber() {
        return number;
    }

    public String getProductList() {
        StringBuilder productList = new StringBuilder();
        productList.append("Numer faktury: ").append(getNumber()).append("\n");

        Map<String, BigDecimal> productNameToTotalPrice = new HashMap<>();
        for (Map.Entry<Product, Integer> entry : products.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(quantity));
            String productName = product.getName();

            productNameToTotalPrice.put(productName, totalPrice);
        }

        for (Map.Entry<String, BigDecimal> entry : productNameToTotalPrice.entrySet()) {
            String productName = entry.getKey();
            BigDecimal totalPrice = entry.getValue();
            productList.append(productName).append("- ilość: ").append(getProductQuantity(productName)).append(" szt., cena: ").append(totalPrice).append(" PLN\n");
        }

        productList.append("Liczba pozycji: ").append(products.size());
        return productList.toString();
    }

    private int getProductQuantity(String productName) {
        int quantity = 0;
        for (Map.Entry<Product, Integer> entry : products.entrySet()) {
            Product product = entry.getKey();
            if (product.getName().equals(productName)) {
                quantity += entry.getValue();
            }
        }
        return quantity;
    }
}

