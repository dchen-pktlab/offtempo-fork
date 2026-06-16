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
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent req) {
        if (!req.toolSource().isFromTool(ToolType.INTRUDER) || !mainPanel.isCaptureEnabled()) {
            return RequestToBeSentAction.continueWith(req);
        }
        String selected = mainPanel.getSelectedType();

        String pool = req.hasHeader("X-OffTempo-Pool")
            ? req.headerValue("X-OffTempo-Pool")
            : selected;

        if ("A".equals(pool)) {
            existingRequestMap.put(req.messageId(), new HttpRequestWithTimestamp(req.messageId(), System.currentTimeMillis()));
        }
        else {
            nonExistingRequestMap.put(req.messageId(), new HttpRequestWithTimestamp(req.messageId(), System.currentTimeMillis()));
        }

        if (req.hasHeader("X-OffTempo-Pool")) {
            return RequestToBeSentAction.continueWith(
                req.withRemovedHeader("X-OffTempo-Pool")
            );
        }
        return RequestToBeSentAction.continueWith(req);

        // if (requestToBeSent.toolSource().isFromTool(ToolType.INTRUDER) && mainPanel.isCaptureEnabled()) {
        //     String selected = mainPanel.getSelectedType();
        //     if ("Pool A".equals(selected)) {
        //         existingRequestMap.put(requestToBeSent.messageId(), new HttpRequestWithTimestamp(requestToBeSent.messageId(), System.currentTimeMillis()));
        //     } else {
        //         nonExistingRequestMap.put(requestToBeSent.messageId(), new HttpRequestWithTimestamp(requestToBeSent.messageId(), System.currentTimeMillis()));
        //     }
        // }
        // return RequestToBeSentAction.continueWith(requestToBeSent);
    }

    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived responseReceived) {
        if (responseReceived.toolSource().isFromTool(ToolType.INTRUDER) && mainPanel.isCaptureEnabled()) {
            int messageId = responseReceived.messageId();
            HttpRequestWithTimestamp request = existingRequestMap.remove(messageId);
            if (request != null) {
                mainPanel.addTiming("Pool A", request.getBurpMessageId(), System.currentTimeMillis() - request.getSendTimestamp());
            } else {
                request = nonExistingRequestMap.remove(messageId);
                if (request != null) {
                    mainPanel.addTiming("Pool B", request.getBurpMessageId(), System.currentTimeMillis() - request.getSendTimestamp());
                }
            }
        }
        return ResponseReceivedAction.continueWith(responseReceived);
    }
}
