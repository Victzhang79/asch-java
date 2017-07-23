package so.asch.sdk.impl;

import com.alibaba.fastjson.JSONObject;
import so.asch.sdk.ContentEncoding;
import so.asch.sdk.Misc;
import so.asch.sdk.codec.Decoding;
import so.asch.sdk.dbc.Argument;
import so.asch.sdk.transaction.TransactionInfo;

public class MiscService extends AschRESTService implements Misc {
    @Override
    public JSONObject getLoadStatus() {
        return get(AschServiceUrls.Misc.GET_LOAD_STATUS);
    }

    @Override
    public JSONObject getSyncStatus() {
        return get(AschServiceUrls.Misc.GET_SYNC_STATUS);
    }

    @Override
    public JSONObject storeData(String content, ContentEncoding encoding, int wait, String secret, String secondSecret) {
        try {
            Argument.notNullOrEmpty(content, "content");
            Argument.notNull(encoding, "encoding");
            Argument.require(Validation.isValidContent(content, encoding), "invalid content");
            Argument.require(Validation.isValidWait(wait), "invalid wait");
            Argument.require(Validation.isValidSecure(secret), "invalid secret");
            Argument.optional(secondSecret, Validation::isValidSecure, "invalid second secret");

            TransactionInfo transaction = getTransactionBuilder()
                    .buildStore(getContentBuffer(content, encoding), wait, secret, secondSecret);

            return broadcastTransaction(transaction);
        }
        catch (Exception ex){
            return fail(ex);
        }
    }

    private byte[] getContentBuffer(String content, ContentEncoding encoding){
        try {
            switch (encoding) {
                case Base64:
                    return Decoding.base64(content);
                case Hex:
                    return Decoding.hex(content);
                case Raw:
                    return content.getBytes();
                default:
                    return new byte[0];
            }
        }catch (Exception ex){
            return new byte[0];
        }
    }

    @Override
    public JSONObject getStoredData(String transactionId) {
        try {
            Argument.require(Validation.isValidTransactionId(transactionId), "invalid transactionId");

            JSONObject parameters = new JSONObject().fluentPut("id", transactionId);

            return get(AschServiceUrls.Misc.GET_STORED_DATA, parameters);
        }
        catch (Exception ex){
            return fail(ex);
        }
    }
}
