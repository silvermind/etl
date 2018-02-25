import Vue from 'vue'
import App from './App'
import Vuetify from 'vuetify'
import Vuex from 'vuex'
import VueResource from 'vue-resource';

Vue.use(Vuex);
Vue.use(Vuetify);
Vue.use(VueResource);

import router from './service/router/Router'

window.bus = new Vue();

new Vue({
  el: '#app',
  router,
  render: h => h(App)
})
