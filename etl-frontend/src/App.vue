<template>
  <v-app dark>
    <v-toolbar app fixed clipped-right>
      <v-spacer></v-spacer>
      <v-toolbar-items>
        <v-btn flat href='/main/process'>Home</v-btn>
        <v-btn flat href='/process/list'>Consumer</v-btn>
        <v-btn flat href='/metric/list'>Metric Consumer</v-btn>
        <v-btn flat href='/referential'>Referential</v-btn>
        <v-btn flat href="/configuration/list">Logstash Generator</v-btn>
        <v-btn flat href='/simulate/grok'>Grok Pattern Simulation</v-btn>
      </v-toolbar-items>
    </v-toolbar>
    <v-content>
          <router-view></router-view>
      </v-content>
    <v-footer app></v-footer>
  </v-app>
</template>

