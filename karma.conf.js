const {createDefaultConfig} = require('@open-wc/testing-karma');
const merge = require('deepmerge');
module.exports = function (config) {
  var junitOutputDir = process.env.CIRCLE_TEST_REPORTS || "target/junit"

  config.set(merge(createDefaultConfig(config), {
    browsers: ['ChromeHeadless'],
    basePath: 'target',
    files: ['karma-test.js'],
    frameworks: ['cljs-test', 'mocha', 'chai'],
    client: {
        mocha: {ui: 'bdd'}
    },
    plugins: [
        'karma-cljs-test',
        'karma-mocha',
        'karma-chai',
        'karma-chrome-launcher',
        'karma-junit-reporter'
    ],
    colors: true,
    logLevel: config.LOG_INFO,
    client: {
      args: ['shadow.test.karma.init']
    },

    // the default configuration
    junitReporter: {
      outputDir: junitOutputDir + '/karma', // results will be saved as outputDir/browserName.xml
      outputFile: undefined, // if included, results will be saved as outputDir/browserName/outputFile
      suite: '' // suite will become the package name attribute in xml testsuite element
    }
  }))
}
