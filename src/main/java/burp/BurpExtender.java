package burp;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.UserInterface;
import burp.api.montoya.extension.ExtensionUnloadingHandler;
import burp.api.montoya.BurpExtension;
import burp.handlers.TimingHttpHandler;
import burp.logic.PlotService;
import burp.logic.StatsService;
import burp.ui.MainPanel;

public class BurpExtender implements BurpExtension, ExtensionUnloadingHandler {

    private MontoyaApi api;
    private MainPanel mainPanel;
    private TimingHttpHandler httpHandler;
    private final String extensionName = "Timer";

    @Override
    public void initialize(MontoyaApi api) {
        this.api = api;
        api.extension().setName(extensionName);
        StatsService statsService = new StatsService();
        PlotService plotService = new PlotService();

        mainPanel = new MainPanel(statsService, plotService);

        httpHandler = new TimingHttpHandler(mainPanel);

        javax.swing.SwingUtilities.invokeLater(() -> {
            UserInterface ui = api.userInterface();
            ui.registerSuiteTab(extensionName, mainPanel.getRootPanel());
            api.http().registerHttpHandler(httpHandler);
            api.extension().registerUnloadingHandler(BurpExtender.this);
        });
    }

    @Override
    public void extensionUnloaded() {

     }
}