<template>
 <v-container fluid grid-list-md>
    <v-layout row wrap>
       <v-flex xs12 sm6 md4>
          <v-btn color="primary" v-on:click.native="newConfig">New Config</v-btn>
       </v-flex>
    </v-layout row wrap>
    <v-layout row wrap>
      <v-flex xs12 sm12 md12>
          <v-data-table v-bind:headers="headers" :items="listConfig" hide-actions dark >
            <template slot="items" slot-scope="props">
                <td>
                      <v-btn color="warning" small v-on:click.native="editConfig(props.item.idConfiguration)">Edit</v-btn>
                      <v-btn color="success" small v-on:click.native="generate(props.item.idConfiguration)">Logstash</v-btn>
                      <v-btn color="success" small v-if="props.item.statusConfig == 'INIT' || props.item.statusConfig == 'DISABLE'" v-on:click.native="active(props.item.idConfiguration)">Active</v-btn>
                      <v-btn color="pink darken-2" small v-if="props.item.statusConfig == 'ACTIVE'" v-on:click.native="deactive(props.item.idConfiguration)">DeActive</v-btn>
                      <v-btn color="error" small v-if="props.item.statusConfig == 'ERROR'" v-on:click.native="active(props.item.idConfiguration)">ERROR</v-btn>
                      <v-btn color="red" small v-on:click.native="deleteConfig(props.item.idConfiguration)">delete</v-btn>
                </td>
                <td class="text-xs-center">{{props.item.name}}</td>
                <td class="text-xs-center">
                  <v-layout row>
                      <v-flex class="pa-0 ma-0" xs12 sm12 md12 v-for="inputitem in props.item.input">
                         <v-chip color="purple lighten-2" small>{{inputitem.typeInput}}</v-chip>
                      </v-flex>
                  </v-layout>
                </td>
                <td class="text-xs-center">
                  <v-chip color="deep-orange lighten-3" small>{{props.item.output.topic}}</v-chip>
                </td>
            </template>
          </v-data-table>
        </v-flex>
    </v-layout row wrap>
    <v-layout row wrap>
      <v-flex xs12 sm12 md12 >
        <v-alert v-model="viewError" xs12 sm12 md12  color="error" icon="warning" value="true" dismissible>
             {{ msgError }}
        </v-alert>
       </v-flex>
     </v-layout row wrap>
 </v-container fluid grid-list-md>
</template>


<script>
  export default{
    data () {
         return {
           listConfig: [],
           input: {},
           uiCreate: '',
           msgError: '',
           viewError: false,
           selectedToCheckBox : false,
           headers: [
             { text: 'Action',align: 'center',value: '', width: '8%'},
             { text: 'Name',align: 'center',value: 'name', width: '8%'},
             { text: 'Input', align: 'center',value: 'input' },
             { text: 'Output', align: 'center',value: 'output' }
           ]
         }
    },
    mounted() {
       this.$http.get('/configuration/findAll').then(response => {
            this.listConfig=response.data;
         }, response => {
           this.viewError=true;
           this.msgError = "Error during call service";
         });
    },
    methods: {
        active(idConfigSelect){
          this.$http.get('/configuration/activeConfiguration', {params: {idConfiguration:idConfigSelect}}).then(response => {
              this.listConfig=response.data;
           }, response => {
             this.viewError=true;
             this.msgError = "Error during call service";
           });
        },
        deactive(idConfigSelect){
          this.$http.get('/configuration/deactiveConfiguration', {params: {idConfiguration:idConfigSelect}}).then(response => {
              this.listConfig=response.data;
           }, response => {
             this.viewError=true;
             this.msgError = "Error during call service";
           });
        },
        deleteConfig(idConfigSelect){
           this.$http.get('/configuration/deleteConfiguration', {params: {idConfiguration:idConfigSelect}}).then(response => {
              this.listConfig=response.data;
           }, response => {
             this.viewError=true;
             this.msgError = "Error during call service";
           });
        },
        generate(idConfigSelect){
          this.$router.push('/generate/logstash?idConfiguration='+idConfigSelect);
        },
        removeConfig(idConfigSelect){
          console.log('removeConfig', idConfigSelect);
        },
        editConfig(idConfigSelect){
          this.$router.push('/configuration/edit?idConfiguration='+idConfigSelect);
        },
        newConfig(){
          this.$router.push('/configuration/add');
        }
    }
  }
</script>
