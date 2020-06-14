class Foo {

    @Override
    public void loadCase2(final String domain, final DataSource datasource) {
        boolean existingData;
        try {
            existingData = !confParamOps.list(domain).isEmpty();
        } catch (Exception e) {
        }
    }

}