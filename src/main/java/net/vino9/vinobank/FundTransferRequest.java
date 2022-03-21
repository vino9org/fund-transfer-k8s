package net.vino9.vinobank;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class FundTransferRequest {

    @JsonProperty("customer_id")
    String customerId;
    @JsonProperty("account_id")
    String accountId;
    @JsonProperty("credit_account_id")
    String creditAccountId;
    double amount;
    String currency;
    @JsonProperty("transaction_date")
    String transactionDate;
    String memo;
    @JsonProperty("limits_req_id")
    String limitsRequestId;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getCreditAccountId() {
        return creditAccountId;
    }

    public void setCreditAccountId(String creditAccountId) {
        this.creditAccountId = creditAccountId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getLimitsRequestId() {
        return limitsRequestId;
    }

    public void setLimitsRequestId(String limitsRequestId) {
        this.limitsRequestId = limitsRequestId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FundTransferRequest that = (FundTransferRequest) o;
        return Objects.equals(customerId, that.customerId) && Objects.equals(
            accountId, that.accountId) && Objects.equals(creditAccountId,
            that.creditAccountId) && Objects.equals(amount, that.amount)
            && Objects.equals(currency, that.currency) && Objects.equals(
            transactionDate, that.transactionDate) && Objects.equals(memo, that.memo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, accountId, creditAccountId, amount, currency,
            transactionDate,
            memo);
    }
}
