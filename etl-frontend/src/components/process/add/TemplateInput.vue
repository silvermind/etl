Vue.component('templateInput', {
    template: '#input-template',
    data () {
             return {
               idProcess: '',
               process: '',
               msgError: '',
               viewError: false,
               viewOut: false,
               process: {processInput: {"name":"","host":"kafka.kafka","port":"9092","topic":"processTopic"}},
               typeOut: ["KAFKA","SYSTEM_OUT","ELASTICSEARCH"],
               bcList: [{text: 'Name',disabled: true},
                       {text: 'Input',disabled: false},
                       {text: 'Parser',disabled: true},
                       {text: 'Transformation',disabled: true},
                       {text: 'Validation',disabled: true},
                       {text: 'Filter',disabled: true},
                       {text: 'Output',disabled: true},
                       {text: 'Finish',disabled: false }
                      ],
             }
        },
    mounted() {
          this.idProcess = this.$route.query.idProcess;
          this.$http.get('/process/findProcess', {params: {idProcess: this.idProcess}}).then(response => {
             this.process=response.data;
          }, response => {
             this.viewError=true;
             this.msgError = "Error during call service";
          });
    },
    methods: {
        createProcess(){
          this.$http.post('/process/createProcess', this.process).then(response => {
             this.$router.push('/process/add/parser?idProcess='+this.idProcess);
          }, response => {
             this.viewError=true;
             this.msgError = "Error during call service";
          });
        }
    }
});


<template id="input-template">
  <v-container fluid grid-list-md >
  <v-breadcrumbs>
    <v-icon slot="divider">forward</v-icon>
    <v-breadcrumbs-item v-for="item in bcList" :key="item.text" :disabled="item.disabled">{{ item.text }}</v-breadcrumbs-item>
  </v-breadcrumbs>
   <v-layout row>
      <v-flex xs6 sm6 md6>
           <p>Input Kafka</p>
           <v-flex xs8 sm8 md8>
              <v-layout row >
                 <v-text-field label="Host" v-model="process.processInput.host"></v-text-field>
                 <v-text-field label="Port" v-model="process.processInput.port"></v-text-field>
              </v-layout>
              </p></p>
           </v-flex>
           <v-flex xs8 sm8 md8>
             <v-layout row >
               <v-text-field label="Topic Input" v-model="process.processInput.topicInput"></v-text-field>
             </v-layout>
             </p></p>
           </v-flex>
      </v-flex>
   </v-layout>
   <v-layout row>
      <v-flex>
         <v-flex xs12 sm6 md4>
         </p>
          <v-btn color="success" v-on:click.native="createProcess">Next</v-btn>
         </v-flex>
      </v-flex>
   </v-layout>
   <v-layout row >
      <v-flex xs12 sm12 md12 >
        <v-alert v-model="viewError" xs12 sm12 md12  color="error" icon="warning" value="true" dismissible>
             {{ msgError }}
        </v-alert>
       </v-flex>
     </v-layout row >

    </v-container>

</template>
