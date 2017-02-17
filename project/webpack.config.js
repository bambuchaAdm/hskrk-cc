var path = require('path');

module.exports = {
    entry: './src/main/typescript/index.ts',
    output: {
        filename: 'bundle.js',
        path: path.resolve(__dirname, '..' , 'target', 'public')
    }
};