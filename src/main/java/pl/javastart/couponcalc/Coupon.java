package pl.javastart.couponcalc;

public class Coupon {

    private final Category category;
    private final int discountValueInPercents;

    public Coupon(Category category, int discountValueInPercents) {
        this.category = category;
        this.discountValueInPercents = discountValueInPercents;
    }

    public Category getCategory() {
        return category;
    }

    public int getDiscountValueInPercents() {
        return discountValueInPercents;
    }

    @Override
    public String toString() {
        return "Coupon{" +
                "category=" + category +
                ", discountValueInPercents=" + discountValueInPercents +
                '}';
    }
}