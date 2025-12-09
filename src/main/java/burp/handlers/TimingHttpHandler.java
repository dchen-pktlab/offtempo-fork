package burp.handlers;

import burp.api.montoya.core.ToolType;
import burp.api.montoya.http.handler.*;
import burp.api.montoya.http.message.*;
import burp.model.HttpRequestWithTimestamp;
import burp.ui.MainPanel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TimingHttpHandler implements HttpHandler {

    private final MainPanel mainPanel;
    private final Map<Integer, HttpRequestWithTimestamp> existingRequestMap = new ConcurrentHashMap<>();
    private final Map<Integer, HttpRequestWithTimestamp> nonExistingRequestMap = new ConcurrentHashMap<>();

    public TimingHttpHandler(MainPanel mainPanel) {
        this.mainPanel = mainPanel;
    }

    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent requestToBeSent) {
        if (requestToBeSent.toolSource().isFromTool(ToolType.INTRUDER) && mainPanel.isCaptureEnabled()) {
            String selected = mainPanel.getSelectedType();
            if ("Existing resource".equals(selected)) {
                existingRequestMap.put(requestToBeSent.messageId(), new HttpRequestWithTimestamp(requestToBeSent.messageId(), System.currentTimeMillis()));
            } else {
                nonExistingRequestMap.put(requestToBeSent.messageId(), new HttpRequestWithTimestamp(requestToBeSent.messageId(), System.currentTimeMillis()));
            }
        }
        return RequestToBeSentAction.continueWith(requestToBeSent);
    }

    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived responseReceived) {
        if (responseReceived.toolSource().isFromTool(ToolType.INTRUDER) && mainPanel.isCaptureEnabled()) {
            String selected = mainPanel.getSelectedType();
            HttpRequestWithTimestamp r;
            if ("Existing resource".equals(selected)) {
                r = existingRequestMap.get(responseReceived.messageId());
            } else {
                r = nonExistingRequestMap.get(responseReceived.messageId());
            }
            if (r != null) {
                long elapsed = System.currentTimeMillis() - r.getSendTimestamp();
                mainPanel.addTiming(selected, r.getBurpMessageId(), elapsed);
            }
        }
        return ResponseReceivedAction.continueWith(responseReceived);
    }
}
