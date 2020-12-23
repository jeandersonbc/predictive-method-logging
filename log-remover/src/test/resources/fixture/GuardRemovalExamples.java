class Foo {

    private void setStoreType(String name, StoreType type) {
        if (storeType == null) {
            storeType = type;
        } else if (storeType != type) {
            log.warn(sm.getString("sslHostConfigCertificate.mismatch", name, sslHostConfig.getHostName(), type, this.storeType));
        }
    }

}