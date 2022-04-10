package net.vino9.vinobank;

import io.opentelemetry.contrib.awsxray.AwsXrayIdGenerator;
import io.opentelemetry.sdk.trace.IdGenerator;
import io.quarkus.arc.Unremovable;
import javax.inject.Singleton;
import javax.ws.rs.Produces;

@Singleton
@Unremovable
public class CustomConfiguration {

    /**
     * Creates a custom IdGenerator for OpenTelemetry
     */
    @Produces
    @Singleton
    @Unremovable
    public IdGenerator idGenerator() {
        return AwsXrayIdGenerator.getInstance();
    }
}