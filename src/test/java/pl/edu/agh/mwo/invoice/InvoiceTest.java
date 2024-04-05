package pl.edu.agh.mwo.invoice;

import java.math.BigDecimal;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pl.edu.agh.mwo.invoice.Invoice;
import pl.edu.agh.mwo.invoice.product.*;

public class InvoiceTest {
    private Invoice invoice;

    @Before
    public void createEmptyInvoiceForTheTest() {
        invoice = new Invoice();
    }

    @Test
    public void testEmptyInvoiceHasEmptySubtotal() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testEmptyInvoiceHasEmptyTaxAmount() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getTaxTotal()));
    }

    @Test
    public void testEmptyInvoiceHasEmptyTotal() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceSubtotalWithTwoDifferentProducts() {
        Product onions = new TaxFreeProduct("Warzywa", new BigDecimal("10"));
        Product apples = new TaxFreeProduct("Owoce", new BigDecimal("10"));
        invoice.addProduct(onions);
        invoice.addProduct(apples);
        Assert.assertThat(new BigDecimal("20"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceSubtotalWithManySameProducts() {
        Product onions = new TaxFreeProduct("Warzywa", BigDecimal.valueOf(10));
        invoice.addProduct(onions, 100);
        Assert.assertThat(new BigDecimal("1000"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceHasTheSameSubtotalAndTotalIfTaxIsZero() {
        Product taxFreeProduct = new TaxFreeProduct("Warzywa", new BigDecimal("199.99"));
        invoice.addProduct(taxFreeProduct);
        Assert.assertThat(invoice.getNetTotal(), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceHasProperSubtotalForManyProducts() {
        invoice.addProduct(new TaxFreeProduct("Owoce", new BigDecimal("200")));
        invoice.addProduct(new DairyProduct("Maslanka", new BigDecimal("100")));
        invoice.addProduct(new OtherProduct("Wino", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("310"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceHasProperTaxValueForManyProduct() {
        // tax: 0
        invoice.addProduct(new TaxFreeProduct("Pampersy", new BigDecimal("200")));
        // tax: 8
        invoice.addProduct(new DairyProduct("Kefir", new BigDecimal("100")));
        // tax: 2.30
        invoice.addProduct(new OtherProduct("Piwko", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("10.30"), Matchers.comparesEqualTo(invoice.getTaxTotal()));
    }

    @Test
    public void testInvoiceHasProperTotalValueForManyProduct() {
        // price with tax: 200
        invoice.addProduct(new TaxFreeProduct("Maskotki", new BigDecimal("200")));
        // price with tax: 108
        invoice.addProduct(new DairyProduct("Maslo", new BigDecimal("100")));
        // price with tax: 12.30
        invoice.addProduct(new OtherProduct("Chipsy", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("320.30"), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceHasPropoerSubtotalWithQuantityMoreThanOne() {
        // 2x kubek - price: 10
        invoice.addProduct(new TaxFreeProduct("Kubek", new BigDecimal("5")), 2);
        // 3x kozi serek - price: 30
        invoice.addProduct(new DairyProduct("Kozi Serek", new BigDecimal("10")), 3);
        // 1000x pinezka - price: 10
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
        Assert.assertThat(new BigDecimal("50"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceHasPropoerTotalWithQuantityMoreThanOne() {
        // 2x chleb - price with tax: 10
        invoice.addProduct(new TaxFreeProduct("Chleb", new BigDecimal("5")), 2);
        // 3x chedar - price with tax: 32.40
        invoice.addProduct(new DairyProduct("Chedar", new BigDecimal("10")), 3);
        // 1000x pinezka - price with tax: 12.30
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
        Assert.assertThat(new BigDecimal("54.70"), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvoiceWithZeroQuantity() {
        invoice.addProduct(new TaxFreeProduct("Tablet", new BigDecimal("1678")), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvoiceWithNegativeQuantity() {
        invoice.addProduct(new DairyProduct("Zsiadle mleko", new BigDecimal("5.55")), -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddingNullProduct() {
        invoice.addProduct(null);
    }

    @Test
    public void testInvoiceHasNumberGreaterThan0() {
        int number = invoice.getNumber();
        Assert.assertThat(number, Matchers.greaterThan(0));
    }

    @Test
    public void testTwoInvoicesHaveDifferentNumbers() {
        int number1 = new Invoice().getNumber();
        int number2 = new Invoice().getNumber();
        Assert.assertNotEquals(number1, number2);
    }

    @Test
    public void testInvoiceDoesNotChangeItsNumber() {
        Assert.assertEquals(invoice.getNumber(), invoice.getNumber());
    }

    @Test
    public void testTheFirstInvoiceNumberIsLowerThanTheSecond() {
        int number1 = new Invoice().getNumber();
        int number2 = new Invoice().getNumber();
        Assert.assertThat(number1, Matchers.lessThan(number2));
    }

    @Test
    public void testProductList() {
        Product product1 = new TaxFreeProduct("Warzywa", new BigDecimal("12"));
        Product product2 = new TaxFreeProduct("Owoce", new BigDecimal("10"));
        int quantity1 = 2;
        int quantity2 = 1;
        invoice.addProduct(product1, quantity1);
        invoice.addProduct(product2, quantity2);

        String expectedProductList = "Numer faktury: " + invoice.getNumber() + "\n" +
                "Warzywa- ilość: 2 szt., cena: 24 PLN\n" +
                "Owoce- ilość: 1 szt., cena: 10 PLN\n" +
                "Liczba pozycji: 2";

        Assert.assertEquals(expectedProductList, invoice.getProductList());
    }

    @Test
    public void testDoubleProducts() {
        Product product1 = new TaxFreeProduct("Warzywa", new BigDecimal("12"));
        Product product2 = new TaxFreeProduct("Owoce", new BigDecimal("10"));
        Product product3 = new TaxFreeProduct("Warzywa", new BigDecimal("12"));
        int quantity1 = 2;
        int quantity2 = 1;
        int quantity3 = 1;
        invoice.addProduct(product1, quantity1);
        invoice.addProduct(product2, quantity2);
        invoice.addProduct(product3, quantity3);

        String expectedProductList = "Numer faktury: " + invoice.getNumber() + "\n" +
                "Warzywa- ilość: 3 szt., cena: 36 PLN\n" +
                "Owoce- ilość: 1 szt., cena: 10 PLN\n" +
                "Liczba pozycji: 2";

        Assert.assertEquals(expectedProductList, invoice.getProductList());
    }

    @Test
    public void testBottleOfWine() {
        ProduktyAkcyzowe bottleOfWine = new ProduktyAkcyzowe("Butelka wina", new BigDecimal("20.50"), new BigDecimal("0.23"));

        invoice.addProduct(bottleOfWine);

        String expectedProductList = "Numer faktury: " + invoice.getNumber() + "\n" +
                "Butelka wina- ilość: 1 szt., cena: 30.7750 PLN\n" +
                "Liczba pozycji: 1";

        Assert.assertEquals(expectedProductList, invoice.getProductList());
    }

    @Test
    public void testFuelCanister() {
        ProduktyAkcyzowe FuelCanister = new ProduktyAkcyzowe("Kanister paliwa", new BigDecimal("50"), new BigDecimal("0.23"));

        invoice.addProduct(FuelCanister);

        String expectedProductList = "Numer faktury: " + invoice.getNumber() + "\n" +
                "Kanister paliwa- ilość: 1 szt., cena: 67.06 PLN\n" +
                "Liczba pozycji: 1";

        Assert.assertEquals(expectedProductList, invoice.getProductList());
    }

    @Test
    public void testFakturka() {
        Product product1 = new TaxFreeProduct("Warzywa", new BigDecimal("12"));
        ProduktyAkcyzowe bottleOfWine = new ProduktyAkcyzowe("Butelka wina", new BigDecimal("20.50"), new BigDecimal("0.23"));
        ProduktyAkcyzowe FuelCanister = new ProduktyAkcyzowe("Kanister paliwa", new BigDecimal("50"), new BigDecimal("0.23"));

        int quantity1 = 1;
        int quantity2 = 1;
        int quantity3 = 2;
        invoice.addProduct(product1, quantity1);
        invoice.addProduct(bottleOfWine, quantity2);
        invoice.addProduct(FuelCanister, quantity3);

        String expectedProductList = "Numer faktury: " + invoice.getNumber() + "\n" +
                "Warzywa- ilość: 1 szt., cena: 12 PLN\n" +
                "Butelka wina- ilość: 1 szt., cena: 30.7750 PLN\n" +
                "Kanister paliwa- ilość: 2 szt., cena: 134.12 PLN\n" +
                "Liczba pozycji: 3";

        Assert.assertEquals(expectedProductList, invoice.getProductList());
    }
}