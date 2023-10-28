package pl.javastart.couponcalc;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PriceCalculatorTest {

    @Test
    public void shouldReturnZeroForNoProducts() { // Zero produktów i zero kodów rabatowych
        // given
        PriceCalculator priceCalculator = new PriceCalculator();

        // when
        double result = priceCalculator.calculatePrice(null, null);

        // then
        assertThat(result).isEqualTo(0.);
    }

    @Test
    public void shouldReturnPriceForSingleProductAndNoCoupons() { // Brak kodów rabatowych

        // given
        PriceCalculator priceCalculator = new PriceCalculator();
        List<Product> products = new ArrayList<>();
        products.add(new Product("Masło", 6.0, Category.FOOD));
        products.add(new Product("Lampa", 6.0, Category.HOME));

        // when
        double result = priceCalculator.calculatePrice(products, null);

        // then
        assertThat(result).isEqualTo(12.0);
    }

    @Test
    public void shouldReturnPriceForSingleProductAndOneCoupon() { // Jeden produkt z jednym kuponem na jedzenie

        // given
        PriceCalculator priceCalculator = new PriceCalculator();
        List<Product> products = new ArrayList<>();
        products.add(new Product("Masło", 6.0, Category.FOOD));

        List<Coupon> coupons = new ArrayList<>();
        coupons.add(new Coupon(Category.FOOD, 20));

        // when
        double result = priceCalculator.calculatePrice(products, coupons);

        // then
        assertThat(result).isEqualTo(4.8);
    }

    @Test
    public void shouldReturnPriceForAllProductsAndNoCoupons() { // Jeśli nie ma żadnych kodów rabatowych to wynikiem powinna być suma cen produktów

        // given
        PriceCalculator priceCalculator = new PriceCalculator();
        List<Product> products = new ArrayList<>();
        products.add(new Product("Masło", 6.00, Category.FOOD));
        products.add(new Product("Baton", 6.00, Category.FOOD));
        products.add(new Product("Lampa", 10, Category.HOME));

        // when
        double result = priceCalculator.calculatePrice(products, null);

        // then
        assertThat(result).isEqualTo(22.0);
    }

    @Test
    public void shouldReturnPriceForAllProductsAndApplyOneCouponForProducts() { // Jeden kod rabatowy na jedzenie, produkty z różnymi kategoriami

        // given
        PriceCalculator priceCalculator = new PriceCalculator();
        List<Product> products = new ArrayList<>();
        products.add(new Product("Masło", 6.00, Category.FOOD));
        products.add(new Product("Baton", 6.00, Category.FOOD));
        products.add(new Product("Lampa", 10, Category.HOME));

        List<Coupon> coupons = new ArrayList<>();
        coupons.add(new Coupon(Category.FOOD, 20));

        // when
        double result = priceCalculator.calculatePrice(products, coupons);

        // then
        assertThat(result).isEqualTo(19.6);
    }

    @Test
    public void shouldReturnPriceForAllProductsWithoutCouponCategory() { //jeśli użytkownik podał kod rabatowy który nie jest przypisany do konkretnej
        // kategorii rabat zastosowany jest do wszystkich produktów - przykładowo
        // , mamy produkty na łączną kwotę 100zł i jeden kod rabatowy o wartości 10%, da nam to wynik 90zł

        // given
        PriceCalculator priceCalculator = new PriceCalculator();
        List<Product> products = new ArrayList<>();
        products.add(new Product("Masło", 6, Category.FOOD));
        products.add(new Product("Domek drewniany", 1_000, Category.HOME));

        List<Coupon> coupons = new ArrayList<>();
        coupons.add(new Coupon(null, 20));

        // when
        double result = priceCalculator.calculatePrice(products, coupons);

        // then
        assertThat(result).isEqualTo(804.8);
    }

    @Test
    public void shouldReturnPriceForAllProductsAndApplyTheBestDiscountCoupon() { //przykład 1: masło za 6zł i opony za 100zł, oraz kody rabatowe na
        // jedzenie o wartości 50% i na art. samochodowe 10% - wybrany zostanie kupon na art samochodowe ponieważ obniżka ot 10zł, a spożywczy dałby zniżkę 3zł.

        // given
        PriceCalculator priceCalculator = new PriceCalculator();
        List<Product> products = new ArrayList<>();
        products.add(new Product("Opony", 100, Category.CAR));
        products.add(new Product("Masło", 6, Category.FOOD));

        List<Coupon> coupons = new ArrayList<>();
        coupons.add(new Coupon(Category.CAR, 10));
        coupons.add(new Coupon(Category.FOOD, 50));

        // when
        double result = priceCalculator.calculatePrice(products, coupons);

        // then
        assertThat(result).isEqualTo(96);
    }
}