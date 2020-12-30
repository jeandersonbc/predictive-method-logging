class Foo {
    static void addOrUpdateWorkflowJobUserContentMap(final HelixPropertyStore<ZNRecord> propertyStore, String workflowJobResource, final Map<String, String> contentToAddOrUpdate) {
        if (workflowJobResource == null) {
            throw new IllegalArgumentException("workflowJobResource must be not null when adding workflow / job user content");
        }
        String path = Joiner.on("/").join(TaskConstants.REBALANCER_CONTEXT_ROOT, workflowJobResource, USER_CONTENT_NODE);

        if (!propertyStore.update(path, new DataUpdater<ZNRecord>() {
            @Override
            public ZNRecord update(ZNRecord znRecord) {
                if (znRecord == null) {
                    // This indicates that somehow the UserContentStore ZNode is missing
                    // This should not happen, but if it is missing, create one
                    znRecord = new ZNRecord(new ZNRecord(TaskUtil.USER_CONTENT_NODE));
                }
                znRecord.getSimpleFields().putAll(contentToAddOrUpdate);
                return znRecord;
            }
        }, AccessOption.PERSISTENT)) {
            LOG.error("Failed to update the UserContentStore for {}", workflowJobResource);
        }
    }
}