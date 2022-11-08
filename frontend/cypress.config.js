const {defineConfig} = require("cypress");

module.exports = defineConfig({
  video: false,
  e2e: {
    setupNodeEvents(on, config) {
      require("cypress-terminal-report/src/installLogsPrinter")(on, {
        printLogsToFile: "always",
        printLogsToConsole: "always",
        outputRoot: config.projectRoot + "/logs/",
        outputTarget: {
          [`Performance-Log-${new Date().toISOString()}.txt`]: "txt"
        },
      });
    },
  },
});
