package net.vino9.vinobank.client;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.AmazonWebServiceClient;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.DefaultRequest;
import com.amazonaws.RequestConfig;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.http.ExecutionContext;
import com.amazonaws.http.HttpMethodName;
import com.amazonaws.http.JsonErrorResponseHandler;
import com.amazonaws.http.JsonResponseHandler;
import com.amazonaws.internal.AmazonWebServiceRequestAdapter;
import com.amazonaws.internal.auth.DefaultSignerProvider;
import com.amazonaws.protocol.json.JsonOperationMetadata;
import com.amazonaws.protocol.json.SdkStructuredPlainJsonFactory;
import com.amazonaws.transform.JsonErrorUnmarshaller;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@ApplicationScoped
public class LimitsApiClient extends AmazonWebServiceClient {

    private static final Logger logger = Logger.getLogger(LimitsApiClient.class);

    private static final String API_GATEWAY_SERVICE_NAME = "execute-api";

    private AWSCredentialsProvider credentialProvider;
    private final JsonResponseHandler<ApiGatewayResponse> responseHandler;
    private final JsonErrorResponseHandler errorResponseHandler;
    private final AWS4Signer signer;
    private ObjectMapper objectMapper = new ObjectMapper();
    private URI endpoint;

    @ConfigProperty(name = "api.limits-api.base-url")
    String limitsApiUrl;

    @ConfigProperty(name = "api.limits-api.aws-region")
    String awsRegion;

    public LimitsApiClient() {
        super(new ClientConfiguration());

        final JsonOperationMetadata metadata = new JsonOperationMetadata().withHasStreamingSuccessResponse(
            false).withPayloadJson(false);
        final Unmarshaller<ApiGatewayResponse, JsonUnmarshallerContext> responseUnmarshaller = in -> new ApiGatewayResponse(
            in.getHttpResponse());
        this.responseHandler = SdkStructuredPlainJsonFactory.SDK_JSON_FACTORY.createResponseHandler(
            metadata, responseUnmarshaller);

        JsonErrorUnmarshaller defaultErrorUnmarshaller = new JsonErrorUnmarshaller(
            ApiGatewayException.class, null) {
            @Override
            public AmazonServiceException unmarshall(JsonNode jsonContent) throws Exception {
                return new ApiGatewayException(jsonContent.toString());
            }
        };

        this.errorResponseHandler = SdkStructuredPlainJsonFactory.SDK_JSON_FACTORY.createErrorResponseHandler(
            Collections.singletonList(defaultErrorUnmarshaller), null);

        this.signer = new AWS4Signer();
        this.signer.setServiceName(API_GATEWAY_SERVICE_NAME);

    }


    @PostConstruct
    void initialize() throws URISyntaxException {

        Config appConfig = ConfigProvider.getConfig();

        AWSCredentials credentials = DefaultAWSCredentialsProviderChain.getInstance()
            .getCredentials();

        this.credentialProvider = new AWSStaticCredentialsProvider(credentials);
        this.endpoint = new URI(limitsApiUrl);
        this.signer.setRegionName(awsRegion);
    }

    public NewLimitResponse newRequest(String customerId, double requestAmount) {
        String resourcePath = String.format("customers/%s/limits", customerId);
        String requestBody = String.format("{ \"req_amount\": %.2f }", requestAmount);
        logger.infof("requesting  for customer %s with request bod=%s", customerId, requestBody);

        final ExecutionContext executionContext = createExecutionContext();
        DefaultRequest request = prepareRequest(HttpMethodName.POST, resourcePath,
            new ByteArrayInputStream(requestBody.getBytes()));
        RequestConfig requestConfig = new AmazonWebServiceRequestAdapter(
            request.getOriginalRequest());

        ApiGatewayResponse response = this.client.execute(request, responseHandler,
            errorResponseHandler, executionContext, requestConfig).getAwsResponse();
        int responseCode = response.getHttpResponse().getStatusCode();
        if (responseCode == 201) {
            try {
                return this.objectMapper.readValue(response.getBody(), NewLimitResponse.class);
            } catch (JsonProcessingException e) {
                logger.info(e);
            }
        }

        logger.infof("request failed with status code %d, body=%s", responseCode,
            response.getBody());
        throw new ApiGatewayException(
            String.format("new request returned=%s", response.getBody()));
    }

    public void deleteRequest(String customerId, String requestId) {
        String resourcePath = String.format("customers/%s/limits/%s", customerId, requestId);
        logger.infof("deleting request %s for customer %s", requestId, customerId);

        final ExecutionContext executionContext = createExecutionContext();
        DefaultRequest request = prepareRequest(HttpMethodName.DELETE, resourcePath, null);
        RequestConfig requestConfig = new AmazonWebServiceRequestAdapter(
            request.getOriginalRequest());

        ApiGatewayResponse response = this.client.execute(request, responseHandler,
            errorResponseHandler, executionContext, requestConfig).getAwsResponse();
        int responseCode = response.getHttpResponse().getStatusCode();
        if (responseCode == 200) {
            return;
        }

        logger.infof("delete request %s for customer %s failed with status code %d, body=%s",
            requestId, customerId, responseCode,
            response.getBody());
        throw new ApiGatewayException(
            String.format("delete request returned=%s", response.getBody()));
    }

    private DefaultRequest prepareRequest(HttpMethodName method, String resourcePath,
        InputStream content) {
        DefaultRequest request = new DefaultRequest(API_GATEWAY_SERVICE_NAME);
        request.setHttpMethod(method);
        request.setContent(content);
        request.setEndpoint(this.endpoint);
        request.setResourcePath(resourcePath);
        request.setHeaders(Collections.singletonMap("Content-type", "application/json"));
        return request;
    }

    private ExecutionContext createExecutionContext() {
        final ExecutionContext executionContext = ExecutionContext.builder().withSignerProvider(
            new DefaultSignerProvider(this, signer)).build();
        executionContext.setCredentialsProvider(credentialProvider);
        return executionContext;
    }
}


