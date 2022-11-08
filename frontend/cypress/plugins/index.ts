const plugins: Cypress.PluginConfig = (on, _config) => {
    const logOptions = {
        printLogsToFile: "always",
        printLogsToConsole: "always",
        // outputRoot: _config.projectRoot + "/logs/",
        outputRoot: "/home/am/Documents",
        outputTarget: {
            "performance-logs.txt": "txt"
        }
    };

    require("cypress-terminal-report/src/installLogsPrinter")(on, logOptions);
}