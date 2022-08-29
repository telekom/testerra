const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const CopyWebpackPlugin = require('copy-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const DuplicatePackageCheckerPlugin = require('duplicate-package-checker-webpack-plugin');
const {AureliaPlugin} = require('aurelia-webpack-plugin');
const {BundleAnalyzerPlugin} = require('webpack-bundle-analyzer');
const {CleanWebpackPlugin} = require('clean-webpack-plugin');
const {IgnorePlugin} = require('webpack');
const webpack = require('webpack');

// config helpers:
const ensureArray = (config) => config && (Array.isArray(config) ? config : [config]) || [];
const when = (condition, config, negativeConfig) =>
    condition ? ensureArray(config) : ensureArray(negativeConfig);

// primary config:
const title = 'Report NG';
const srcDir = path.resolve(__dirname, 'src');
const nodeModulesDir = path.resolve(__dirname, 'node_modules');
const baseUrl = '';

module.exports = ({production} = {}, {analyze, tests, hmr, port, host} = {}) => {

    const outDir = (production?path.resolve(__dirname, "../src/main/resources/report-ng"):path.resolve(__dirname, "dist"));

    return {
        resolve: {
            extensions: ['.ts', '.js'],
            modules: [srcDir, 'node_modules'],

            alias: {
                // https://github.com/aurelia/dialog/issues/387
                // Uncomment next line if you had trouble to run aurelia-dialog on IE11
                // 'aurelia-dialog': path.resolve(__dirname, 'node_modules/aurelia-dialog/dist/umd/aurelia-dialog.js'),

                // https://github.com/aurelia/binding/issues/702
                // Enforce single aurelia-binding, to avoid v1/v2 duplication due to
                // out-of-date dependencies on 3rd party aurelia plugins
                'aurelia-binding': path.resolve(__dirname, 'node_modules/aurelia-binding')
            }
        },
        entry: {
            app: [
                // Uncomment next line if you need to support IE11
                // 'promise-polyfill/src/polyfill',
                'aurelia-bootstrapper'
            ],
        },
        mode: production ? 'production' : 'development',
        output: {
            path: outDir,
            publicPath: baseUrl,
            filename: production ? '[name].[chunkhash].bundle.js' : '[name].[hash].bundle.js',
            sourceMapFilename: production ? '[name].[chunkhash].bundle.map' : '[name].[hash].bundle.map',
            chunkFilename: production ? '[name].[chunkhash].chunk.js' : '[name].[hash].chunk.js'
        },
        optimization: {
            runtimeChunk: true,  // separates the runtime chunk, required for long term cacheability
            // moduleIds is the replacement for HashedModuleIdsPlugin and NamedModulesPlugin deprecated in https://github.com/webpack/webpack/releases/tag/v4.16.0
            // changes module id's to use hashes be based on the relative path of the module, required for long term cacheability
            moduleIds: 'deterministic',
            // Use splitChunks to breakdown the App/Aurelia bundle down into smaller chunks
            // https://webpack.js.org/plugins/split-chunks-plugin/
            splitChunks: {
                hidePathInfo: true, // prevents the path from being used in the filename when using maxSize
                chunks: "all",
                // sizes are compared against source before minification
                //maxSize: 200000, // splits chunks if bigger than 200k, adjust as required (maxSize added in webpack v4.15)
                cacheGroups: {
                    "vis-timeline": {
                        test: /[\\/]node_modules[\\/](vis-timeline)[\\/]/,
                        priority: 20,
                        minSize:0,
                    },
                    "vis-network": {
                        test: /[\\/]node_modules[\\/](vis-network)[\\/]/,
                        priority: 21,
                        minSize:0,
                    },
                    apexcharts: {
                        test: /[\\/]node_modules[\\/](apexcharts)[\\/]/,
                        priority: 22,
                        minSize:0,
                    },
                    // proto: {
                    //     test: /[\\/]src[\\/]services[\\/](report-model).+/,
                    //     priority: 20,
                    //     minSize:0,
                    // },
                    default: false, // Disable the built-in groups default & vendors (vendors is redefined below)
                    // This is the HTTP/1.1 optimised cacheGroup configuration
                    vendors: { // picks up everything from node_modules as long as the sum of node modules is larger than minSize
                        test: /[\\/]node_modules[\\/]/,
                        priority: -100,
                        enforce: true, // causes maxInitialRequests to be ignored, minSize still respected if specified in cacheGroup
                        minSize: 50000
                    },
                }
            },
        },
        performance: {
            hints: false
        },
        devServer: {
            static: {
                directory: outDir
            },
            headers: {},

            // serve index.html for all 404 (required for push-state)
            historyApiFallback: true,
            hot: hmr,
            port: port,
            host: host
        },
        devtool: production ? false : 'eval-source-map',
        module: {
            rules: [
                {
                    test: /\.html$/i,
                    loader: 'html-loader',
                    options: {
                        minimize: true,
                    }
                },
                {
                    test: /\.ts$/,
                    loader: "ts-loader",
                    options: {
                        allowTsInNodeModules: true
                    }
                },
                /**
                 * Import images from source code
                 * @see https://stackoverflow.com/questions/43638454/webpack-typescript-image-import
                 */
                {
                    test: /\.(jpg|png)$/,
                    use: {
                        loader: 'file-loader'
                    },
                },
                /**
                 * Import font files from source with url-loader
                 * @see https://stackoverflow.com/questions/68922912/reacterror-you-may-need-an-appropriate-loader-to-handle-this-file-type
                 */
                {
                    test: /\.(woff|woff2|ttf|eot)$/,
                    use: {
                        loader: 'url-loader'
                    },
                },
                {
                    test: /\.css$/,
                    issuer: /\.ts?$/i,
                    use: [
                        {loader: MiniCssExtractPlugin.loader},
                        "css-loader"
                    ],
                },
                {
                    test: /\.scss$/,
                    issuer: /\.ts?$/i,
                    use: [
                        {loader: MiniCssExtractPlugin.loader},
                        {loader: 'css-loader'},
                        {
                            loader: 'sass-loader', options: {
                                //implementation: require('sass'),
                                sassOptions: {
                                    includePaths: [nodeModulesDir]
                                }
                            }
                        },
                    ],
                },
            ]
        },
        plugins: [
            ...when(!tests, new DuplicatePackageCheckerPlugin()),
            new AureliaPlugin(),
            ...when(production,  new webpack.NormalModuleReplacementPlugin(/config-dev/gi, (resource) => {
                resource.request = resource.request.replace(/config-dev/, 'config-prod');
            })),
            new HtmlWebpackPlugin({
                template: 'index.ejs',
                metadata: {
                    // available in index.ejs //
                    title, baseUrl
                }
            }),
            new MiniCssExtractPlugin({ // updated to match the naming conventions for the js files
                filename: production ? 'css/[name].[contenthash].bundle.css' : 'css/[name].[hash].bundle.css',
                chunkFilename: production ? 'css/[name].[contenthash].chunk.css' : 'css/[name].[hash].chunk.css'
            }),
            /**
             * Optimized moment
             * @see https://github.com/jmblog/how-to-optimize-momentjs-with-webpack
             */
            new IgnorePlugin({
                resourceRegExp: /^\.\/locale$/,
                contextRegExp: /moment$/
            }),
            ...when(!tests, new CopyWebpackPlugin({
                patterns: [
                    {
                        from: 'static',
                        to: outDir,
                    }
                ],
            })),
            ...when(analyze, new BundleAnalyzerPlugin()),
            /**
             * Note that the usage of following plugin cleans the webpack output directory before build.
             * In case you want to generate any file in the output path as a part of pre-build step, this plugin will likely
             * remove those before the webpack build. In that case consider disabling the plugin, and instead use something like
             * `del` (https://www.npmjs.com/package/del), or `rimraf` (https://www.npmjs.com/package/rimraf).
             */
            new CleanWebpackPlugin()
        ]
    };
}
