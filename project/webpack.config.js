var path = require('path');
var ExtractTextPlugin = require('extract-text-webpack-plugin');

module.exports = {
    entry: './src/main/typescript/index.ts',
    output: {
        filename: 'bundle.js',
        path: path.resolve(__dirname, '..' , 'target', 'public')
    },
    module: {
        rules: [{
            test: /\.css$/,
            use: ExtractTextPlugin.extract({
                fallback: "style-loader",
                use: "css-loader"
            })
        }]
    },
    plugins: [
        new ExtractTextPlugin('css/styles.css'),
    ]
};