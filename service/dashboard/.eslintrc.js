module.exports = {
  root: true,
  env: {
    node: true,
  },
  extends: ['airbnb-base', 'plugin:vue/essential'],
  rules: {
    'no-console': process.env.NODE_ENV === 'production' ? 'error' : 'off',
    'no-debugger': process.env.NODE_ENV === 'production' ? 'error' : 'off',
    'prefer-destructuring': ['error', { object: true, array: false }],
    'max-len': ["error", { "ignoreComments": true, "code": 150 }],
    'no-use-before-define': 0,
    'import/no-unresolved': 0
  },
  parserOptions: {
    parser: 'babel-eslint',
  },
};
