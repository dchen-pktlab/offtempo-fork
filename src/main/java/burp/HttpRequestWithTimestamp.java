package burp;

import lombok.AllArgsConstructor;

import burp.api.montoya.http.message.requests.HttpRequest;

@AllArgsConstructor
public class HttpRequestWithTimestamp {
    final int burpMessageId;
    final long sendTimestamp;
}
