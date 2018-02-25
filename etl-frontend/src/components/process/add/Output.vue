<template>
  <v-container fluid grid-list-md >
  <v-breadcrumbs>
    <v-icon slot="divider">forward</v-icon>
    <v-breadcrumbs-item v-for="item in bcList" :key="item.text" :disabled="item.disabled">{{ item.text }}</v-breadcrumbs-item>
   </v-breadcrumbs>
   <v-flex xs6 sm6 md6>
           <p>Output</p>
           <v-flex xs8 sm8 md8>
              <v-layout row >
                   <v-select label="Out" v-model="process.processOutput.typeOutput" v-bind:items="typeOut" v-on:change="actionOutView"/>
              </v-layout>
                   </p></p>
           </v-flex>
           <v-flex xs8 sm8 md8>
              <v-layout row v-show="viewOut">
                   <v-text-field label="Topic Out" v-model="process.processOutput.parameterOutput.topicOut"></v-text-field>
              </v-layout>
              </p></p>
           </v-flex>
           <v-flex xs8 sm8 md8>
              <v-layout row v-show="viewES">
                   <v-select label="Retention" v-model="process.processOutput.parameterOutput.elasticsearchRetentionLevel" v-bind:items="typeRetention"/>
              </v-layout>
              </p></p>
           </v-flex>
      </v-flex>
   </v-layout>
   <v-flex>
         <v-flex xs12 sm6 md4>
         </p>
          <v-btn color="primary" v-on:click.native="addOutput">Add Output</v-btn>
          <v-btn color="success" v-on:click.native="nextAction" :disabled="viewaddOutput">Next</v-btn>
         </v-flex>
    </v-flex>
    <v-layout row >
      <v-flex xs12 sm12 md12 >
        <v-alert v-model="viewError" xs12 sm12 md12  color="error" icon="warning" value="true" dismissible>
             {{ msgError }}
        </v-alert>
       </v-flex>
     </v-layout row >
    </v-container>

</template>


<script>
  export default{
   data () {
         return {
           activeNext: true,
           viewaddOutput: true,
           idProcess: '',
           process: '',
           msgError: '',
           viewError: false,
           viewOut: false,
           viewES: false,
           process: {processOutput: {"typeOutput":"KAFKA","parameterOutput": {"topicOut":"","elasticsearchRetentionLevel":"week"}}},
           typeOut: ["KAFKA","SYSTEM_OUT","ELASTICSEARCH"],
           typeRetention: ["week","month","quarter","year"],
           bcList: [{text: 'Name',disabled: true},
                   {text: 'Input',disabled: true},
                   {text: 'Parser',disabled: true},
                   {text: 'Transformation',disabled: true},
                   {text: 'Validation',disabled: true},
                   {text: 'Filter',disabled: true},
                   {text: 'Output',disabled: false},
                   {text: 'Finish',disabled: false }
                  ]
         }
    },
    mounted() {
          this.idProcess = this.$route.query.idProcess;
          this.process.idProcess=this.idProcess;
          this.viewOut=true;
    },
    methods: {
       actionOutView(value){
            if(value == "KAFKA"){
              this.viewOut=true;
              this.viewES=false;
            }else if (value == "ELASTICSEARCH"){
              this.viewOut=false;
              this.viewES=true;
            }else{
              this.viewOut=false;
              this.viewES=false;
            }
        },
       nextAction(){
         this.$http.get('/process/finishProcess', {params: {idProcess: this.idProcess}}).then(response => {
            this.$router.push('/process/list');
         }, response => {
            this.viewError=true;
            this.msgError = "Error during call service";
         });
       },
       addOutput(){
          this.$http.post('/process/addOut', this.process).then(response => {
             this.viewaddOutput=false;
          }, response => {
             this.viewError=true;
             this.msgError = "Error during call service";
          });
       }
    }
  }
</script>
