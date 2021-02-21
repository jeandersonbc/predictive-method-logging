class Foo {

    // This lambda expression is ignored as it does not belong to a method body
    private static final F<Long> a = new F<>(() -> {
        try {
            return X.w();
        } catch (Exception e) {
            log.warn("oh no", e);
        }
    });

    void foo(int a) {
        logger.info("Hello");
        doThat(() -> {
            logger.warn("Test 123");
        });
    }
    // This lambda expression is ignored as it does not belong to a method body
    private static final F<Long> a = new F<>(() -> {
        try {
            return X.w();
        } catch (Exception e) {
            log.warn("oh no", e);
        }
    });

}