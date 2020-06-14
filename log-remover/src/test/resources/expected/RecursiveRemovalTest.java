class Foo {

    @Override
    public void load(final String domain, final DataSource datasource) {
        boolean existingData;
        try {
            existingData = !confParamOps.list(domain).isEmpty();
        } catch (Exception e) {
            existingData = true;
        }

        if (existingData) {
        } else {
            try {
                InputStream contentJSON = ApplicationContextProvider.getBeanFactory().
                        getBean(domain + "KeymasterConfParamsJSON", InputStream.class);
                loadDefaultContent(domain, contentJSON);
            } catch (Exception e) {
            }
        }
    }
}