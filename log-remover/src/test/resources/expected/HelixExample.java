class Foo {
    static void addOrUpdateWorkflowJobUserContentMap(final HelixPropertyStore<ZNRecord> propertyStore, String workflowJobResource, final Map<String, String> contentToAddOrUpdate) {
        if (workflowJobResource == null) {
            throw new IllegalArgumentException("workflowJobResource must be not null when adding workflow / job user content");
        }
        String path = Joiner.on("/").join(TaskConstants.REBALANCER_CONTEXT_ROOT, workflowJobResource, USER_CONTENT_NODE);
    }
}