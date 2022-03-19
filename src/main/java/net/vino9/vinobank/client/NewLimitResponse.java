package net.vino9.vinobank.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

@JsonIgnoreProperties(value = {"action"})
public class NewLimitResponse {

    @JsonProperty("req_id")
    private String requestId;
    @JsonProperty("customer_id")
    private String customerId;
    @JsonProperty("req_amount")
    private double requestAmount;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public double getRequestAmount() {
        return requestAmount;
    }

    public void setRequestAmount(double requestAmount) {
        this.requestAmount = requestAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NewLimitResponse that = (NewLimitResponse) o;
        return Double.compare(that.requestAmount, requestAmount) == 0
            && Objects.equals(requestId, that.requestId) && Objects.equals(
            customerId, that.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId, customerId, requestAmount);
    }
}
