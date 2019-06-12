package com.aws.logs.metricfilter;

import com.amazonaws.cloudformation.proxy.AmazonWebServicesClientProxy;
import com.amazonaws.cloudformation.proxy.Logger;
import com.amazonaws.cloudformation.proxy.ProgressEvent;
import com.amazonaws.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.DeleteMetricFilterRequest;
import software.amazon.awssdk.services.cloudwatchlogs.model.ResourceNotFoundException;

import static com.aws.logs.metricfilter.ResourceModelExtensions.getPrimaryIdentifier;

public class DeleteHandler extends BaseHandler<CallbackContext> {

    private AmazonWebServicesClientProxy proxy;
    private CloudWatchLogsClient client;
    private Logger logger;

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger) {

        this.proxy = proxy;
        this.client = ClientBuilder.getClient();
        this.logger = logger;

        return deleteMetricFilter(proxy, request);
    }

    private ProgressEvent<ResourceModel, CallbackContext> deleteMetricFilter(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request) {

        ResourceModel model = request.getDesiredResourceState();

        try {
            final DeleteMetricFilterRequest deleteMetricFilterRequest = DeleteMetricFilterRequest.builder()
                .filterName(model.getFilterName())
                .logGroupName(model.getLogGroupName())
                .build();
            proxy.injectCredentialsAndInvokeV2(deleteMetricFilterRequest, this.client::deleteMetricFilter);
            logger.log(String.format("%s [%s] deleted successfully",
                ResourceModel.TYPE_NAME, getPrimaryIdentifier(model).toString()));
        } catch (ResourceNotFoundException e) {
            // already gone, can treat as success
            logger.log(String.format("%s [%s] is already deleted",
                ResourceModel.TYPE_NAME, getPrimaryIdentifier(model).toString()));
        }

        return ProgressEvent.defaultSuccessHandler(null);
    }
}