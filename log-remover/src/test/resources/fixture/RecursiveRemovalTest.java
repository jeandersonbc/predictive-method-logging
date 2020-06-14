class Foo {

    @Override
    public void load(final String domain, final DataSource datasource) {
        boolean existingData;
        try {
            existingData = !confParamOps.list(domain).isEmpty();
        } catch (Exception e) {
            LOG.error("[{}] Could not access Keymaster", domain, e);
            existingData = true;
        }

        if (existingData) {
            LOG.info("[{}] Data found in Keymaster, leaving untouched", domain);
        } else {
            LOG.info("[{}] Empty Keymaster found, loading default content", domain);

            try {
                InputStream contentJSON = ApplicationContextProvider.getBeanFactory().
                        getBean(domain + "KeymasterConfParamsJSON", InputStream.class);
                loadDefaultContent(domain, contentJSON);
            } catch (Exception e) {
                LOG.error("[{}] While loading default Keymaster content", domain, e);
            }
        }
    }
}