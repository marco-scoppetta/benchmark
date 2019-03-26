import Vue from 'vue';
import Router from 'vue-router';
import Home from './views/Home.vue';

Vue.use(Router);

export default new Router({
  routes: [
    { path: '/', redirect: '/overview' },
    {
      path: '/',
      name: 'home',
      component: Home,
      children: [
        {
          path: 'overview',
          component: () => import(/* webpackChunkName: "overview" */ './views/overview/Overview.vue'),
        },
        {
          path: 'executions',
          component: () => import(/* webpackChunkName: "executions" */ './views/executions/Executions.vue'),
        },
        {
          path: 'inspect/:executionId',
          component: () => import(/* webpackChunkName: "inspect" */ './views/inspect/Inspect.vue'),
        },
      ],
    },
    // {
    //   path: "/about",
    //   name: "about",
    //   // route level code-splitting
    //   // this generates a separate chunk (about.[hash].js) for this route
    //   // which is lazy-loaded when the route is visited.
    //   component: () =>
    //     import(/* webpackChunkName: "about" */ "./views/About.vue")
    // }
  ],
});
