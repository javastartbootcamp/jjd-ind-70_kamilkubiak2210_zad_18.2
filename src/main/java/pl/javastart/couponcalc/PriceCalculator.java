package pl.javastart.couponcalc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public class PriceCalculator {
    private static final double HUNDRED_PERCENT = 100;
    private static final int ROUNDING_SCALE = 2;
    private static final double ONE_COUPON = 1;

    public double calculatePrice(List<Product> products, List<Coupon> coupons) {
        BigDecimal decimal = new BigDecimal(0);
        if (products == null) { //brak produktów
            return decimal.doubleValue();
        }
        if (coupons == null) { //brak kuponów
            for (Product product : products) {
                decimal = decimal.add(BigDecimal.valueOf(product.getPrice()).setScale(ROUNDING_SCALE, RoundingMode.HALF_UP));
            }
        } else {
            List<Product> productListWithCategory = getListOfProductsWithCategory(products, coupons);
            List<Product> productListWithoutCategory = compareProductLists(products, productListWithCategory);
            if (coupons.size() == ONE_COUPON) { //jeden kupon
                decimal = priceForAllProductsWithOneCoupon(products, coupons, decimal, productListWithCategory, productListWithoutCategory);
            } else if (coupons.size() > ONE_COUPON) { //wiele kuponów
                if (productListWithCategory.size() != 0) {
                    Optional<Category> categoryWithTheBiggestDiscount = getCategoryWithTheBiggestDiscount(productListWithCategory, coupons);
                    if (categoryWithTheBiggestDiscount.isPresent()) {
                        decimal = getPriceForAllProductsWithTheBiggestCoupon(coupons, decimal, productListWithCategory,
                                productListWithoutCategory, categoryWithTheBiggestDiscount.get());
                    }
                }
            }
        }
        return decimal.doubleValue();
    }

    private Optional<Category> getCategoryWithTheBiggestDiscount(List<Product> productListWithCategory, List<Coupon> coupons) {
        Map<Category, Double> couponsMapBeforeDiscount = new HashMap<>();
        for (Product product : productListWithCategory) {
            if (!couponsMapBeforeDiscount.containsKey(product.getCategory())) {
                couponsMapBeforeDiscount.put(product.getCategory(), 0.);
            }
            if (couponsMapBeforeDiscount.containsKey(product.getCategory())) {
                couponsMapBeforeDiscount.replace(product.getCategory(), couponsMapBeforeDiscount.get(product.getCategory()) + product.getPrice());
            }
        }

        HashMap<Category, Double> couponsValueAfterDiscount = new HashMap<>();
        HashMap<Category, Double> copyOfCouponsBeforeDiscount = new HashMap<>(couponsMapBeforeDiscount);
        for (Coupon coupon : coupons) {
            Map<Category, Double> collect = copyOfCouponsBeforeDiscount.entrySet().stream()
                    .filter(entry -> entry.getKey().equals(coupon.getCategory()))
                    .peek(entry -> entry.setValue(entry.getValue() - (entry.getValue() * (HUNDRED_PERCENT - coupon.getDiscountValueInPercents())
                            / HUNDRED_PERCENT))).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            couponsValueAfterDiscount.putAll(collect);
        }
        return couponsValueAfterDiscount.entrySet().stream().max(Comparator.comparingDouble(Map.Entry::getValue)).map(Map.Entry::getKey);
    }

    private BigDecimal getPriceForAllProductsWithTheBiggestCoupon(List<Coupon> coupons, BigDecimal decimal, List<Product> productListWithCategory,
                                                                  List<Product> productListWithoutCategory, Category categoryWithTheBiggestDiscount) {
        for (Product product : productListWithCategory) {
            if (product.getCategory().equals(categoryWithTheBiggestDiscount)) {
                Optional<Coupon> optionalCoupon = coupons.stream().filter(coupon -> coupon.getCategory().equals(categoryWithTheBiggestDiscount)).findFirst();
                if (optionalCoupon.isPresent()) {
                    decimal = decimal.add(BigDecimal.valueOf(product.getPrice() *
                            (HUNDRED_PERCENT - optionalCoupon.get().getDiscountValueInPercents())
                            / HUNDRED_PERCENT).setScale(ROUNDING_SCALE, RoundingMode.HALF_UP));
                }
            } else {
                decimal = decimal.add(BigDecimal.valueOf(product.getPrice()));
            }
        }
        for (Product product : productListWithoutCategory) {
            decimal = decimal.add(BigDecimal.valueOf(product.getPrice()));
        }
        return decimal;
    }

    private BigDecimal priceForAllProductsWithOneCoupon(List<Product> products, List<Coupon> coupons, BigDecimal decimal,
                                                        List<Product> productListWithCategory, List<Product> productListWithoutCategory) {
        Coupon coupon = coupons.get(0);
        if (productListWithoutCategory.size() == products.size()) {
            for (Product product : products) {
                decimal = getDiscount(coupon, decimal, product);
            }
        } else {
            decimal = getDiscountAndReturnPrice(coupon, decimal, productListWithCategory);
        }
        if (productListWithoutCategory.size() != 0 && productListWithoutCategory.size() != products.size()) {
            for (Product product : productListWithoutCategory) {
                decimal = decimal.add(BigDecimal.valueOf(product.getPrice()).setScale(ROUNDING_SCALE, RoundingMode.HALF_UP));
            }
        }
        return decimal;
    }

    private BigDecimal getDiscountAndReturnPrice(Coupon coupon, BigDecimal decimal, List<Product> productListWithCategory) {
        if (productListWithCategory.size() != 0) {
            for (Product product : productListWithCategory) {
                decimal = getDiscount(coupon, decimal, product);
            }
        }
        return decimal;
    }

    private static BigDecimal getDiscount(Coupon coupon, BigDecimal decimal, Product product) {
        double discountPrice = product.getPrice() * (HUNDRED_PERCENT - coupon.getDiscountValueInPercents()) / HUNDRED_PERCENT;
        decimal = decimal.add(BigDecimal.valueOf(discountPrice).setScale(ROUNDING_SCALE, RoundingMode.HALF_UP));
        return decimal;
    }

    private List<Product> getListOfProductsWithCategory(List<Product> products, List<Coupon> coupons) {
        List<Product> productsList = new LinkedList<>();
        for (Product product : products) {
            for (Coupon coupon : coupons) {
                if (product.getCategory().equals(coupon.getCategory())) {
                    productsList.add(product);
                }
            }
        }
        return productsList;
    }

    private List<Product> compareProductLists(List<Product> products, List<Product> categoryProducts) {
        return products.stream().filter(product -> !categoryProducts.contains(product)).toList();
    }
}