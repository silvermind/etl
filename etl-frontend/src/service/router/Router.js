import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)


let router = new VueRouter({
    mode: 'history',
    routes: [
      {
        path: '/main/process',
        component: require('../../components/main/Home.vue'),
        name: 'main'
      },
      {
        path: '/configuration/list',
        component: require('../../components/configuration/list/List.vue'),
        name: 'listView'
      },
      {
        path: '/configuration/add',
        component: require('../../components/configuration/create/CreateConf.vue'),
        name: 'createName'
      },
      {
        path: '/configuration/edit',
        component: require('../../components/configuration/create/CreateConf.vue'),
        name: 'editedit'
      },
      {
        path: '/grok/view',
        component: require('../../components/grok/View.vue'),
        name: 'grok'
      },
      {
        path: '/generate/logstash',
        component: require('../../components/configuration/generate/Logstash.vue'),
        name: 'generatelogstash'
      },
      {
        path: '/process/list',
        component: require('../../components/process/list/List.vue'),
        name: 'processlist'
      },
      {
        path: '/process/add/name',
        component: require('../../components/process/add/Name.vue'),
        name: 'processaddname'
      },
      {
        path: '/process/add/input',
        component: require('../../components/process/add/Input.vue'),
        name: 'processaddinput'
      },
      {
        path: '/process/add/parser',
        component: require('../../components/process/add/Parser.vue'),
        name: 'processaddparser'
      },
      {
        path: '/process/add/transformation',
        component: require('../../components/process/add/Transformation.vue'),
        name: 'processaddtransformation'
      },
      {
        path: '/process/add/output',
        component: require('../../components/process/add/Output.vue'),
        name: 'processaddoutput'
      },
      {
        path: '/process/add/validation',
        component: require('../../components/process/add/Validation.vue'),
        name: 'processaddvalidation'
      },
      {
        path: '/process/add/process',
        component: require('../../components/process/add/Process.vue'),
        name: 'processaddprocess'
      },
      {
        path: '/process/edit/validation',
        component: require('../../components/process/edit/Validation.vue'),
        name: 'processeditvalidation'
      },
      {
        path: '/process/add/filter',
        component: require('../../components/process/add/Filter.vue'),
        name: 'processaddfilter'
      },
      {
        path: '/process/edit/filter',
        component: require('../../components/process/edit/Filter.vue'),
        name: 'processeditfilter'
      },
      {
        path: '/process/edit/parser',
        component: require('../../components/process/edit/Parser.vue'),
        name: 'processeditparser'
      },
      {
        path: '/process/edit/transformation',
        component: require('../../components/process/edit/Transformation.vue'),
        name: 'processedittransformation'
      },
      {
        path: '/process/action/view',
        component: require('../../components/process/action/Action.vue'),
        name: 'processactionview'
      },
      {
        path: '/metric/list',
        component: require('../../components/metric/list/List.vue'),
        name: 'metriclist'
      },
      {
        path: '/metric/add/name',
        component: require('../../components/metric/add/Name.vue'),
        name: 'metricaddname'
      },
      {
        path: '/simulate/view',
        component: require('../../components/simulate/Simulate.vue'),
        name: 'simulateview'
      },
      {
        path: '/simulate/grok',
        component: require('../../components/simulate/Grok.vue'),
        name: 'simulateviewgrok'
      },
      {
        path: '/referential',
        component: require('../../components/referential/Referential.vue'),
        name: 'referentialview'
      },
      {
        path: '/referential/action',
        component: require('../../components/referential/Action.vue'),
        name: 'referentialactionview'
      },
      {
        path: '*',
        redirect: '/main/process'
      }
    ]
  }
)

export default router
