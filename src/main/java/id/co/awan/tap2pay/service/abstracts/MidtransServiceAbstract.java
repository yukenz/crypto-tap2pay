package id.co.awan.tap2pay.service.abstracts;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;

public abstract class MidtransServiceAbstract {

    @Value("${midtrans.url.transaction}")
    protected String transactionUrl;

    @Value("${midtrans.server-key}")
    protected String serverKey;

    // TODO: Logic untuk AUTH_STRING: Base64Encode("YourServerKey"+":")

    public abstract String midtransBasicAuthorization();

    // TODO: Logic untuk manajemen orderId
    public abstract String generateOrderId(String ownerAddress);

    // TODO: Logic untuk menyimpan token
    public abstract String saveToken(String ownerAddress, String midtransTransactionToken);

    public JsonNode generateTransactionRequest(
            String orderId,
            Integer grossAmount,
            Boolean secure,
            String firstName,
            String lastName,
            String email,
            String phone
    ) {

        /*
          {
            "transaction_details": {
              "order_id": "YOUR-ORDERID-123456",
              "gross_amount": 10000
            },
            "credit_card": {
              "secure": true
            },
            "customer_details": {
              "first_name": "budi",
              "last_name": "pratama",
              "email": "budi.pra@example.com",
              "phone": "08111222333"
            }
          }
          */

        ObjectNode transactionDetails = JsonNodeFactory.instance.objectNode();
        transactionDetails.put("order_id", orderId);
        transactionDetails.put("gross_amount", grossAmount);

        ObjectNode creditCard = JsonNodeFactory.instance.objectNode();
        creditCard.put("secure", secure);

        ObjectNode customerDetails = JsonNodeFactory.instance.objectNode();
        customerDetails.put("first_name", firstName);
        customerDetails.put("last_name", lastName);
        customerDetails.put("email", email);
        customerDetails.put("phone", phone);


        ObjectNode requestObject = JsonNodeFactory.instance.objectNode();
        requestObject.set("transaction_details", transactionDetails);
        requestObject.set("credit_card", creditCard);
        requestObject.set("customer_details", customerDetails);

        return requestObject;
    }

}
