package burp;

import com.google.gson.annotations.Since;
import lombok.*;
import lombok.experimental.Accessors;

@Builder
@ToString
@Accessors(fluent=true)
@Getter
public class Settings {

    // use this field to track settings version. new settings should be
    // annotated with @Since(NEW_VERSION)
    @Setter(AccessLevel.NONE)
    private final float version = 0;

    @Since(0)
    @Builder.Default
    private final boolean proxySigningEnabled = true;

    @Since(0)
    @Builder.Default
    private final boolean useSuiteScope = true;

    @Since(0)
    @Builder.Default
    private final boolean repeaterSigningEnabled = true;

    @Since(0)
    @Builder.Default
    private final String scopePathPrefix = "";

    @Since(0)
    private final String signingKeyId;

    @Since(0)
    private final String signingSecret;

    @Since(0)
    private final String extensionVersion;
}
