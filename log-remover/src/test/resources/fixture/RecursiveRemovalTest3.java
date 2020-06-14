class Foo {

    @Override
    public void loadCase3(final String domain, final DataSource datasource) {
        boolean existingData;
        try {
            existingData = !confParamOps.list(domain).isEmpty();
        } catch (Exception e) {
            LOG.error("[{}] Could not access Keymaster", domain, e);
        }

        if (existingData)
            LOG.info("[{}] Data found in Keymaster, leaving untouched", domain);
    }
}