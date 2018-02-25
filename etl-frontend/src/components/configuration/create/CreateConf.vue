<template>
  <v-container fluid grid-list-md >
    <v-stepper v-model="confWizardStep">
          <v-stepper-header>
                <v-stepper-step step="1" v-bind:complete="confWizardStep > 1" editable>Process Name</v-stepper-step>
                <v-divider></v-divider>
                <v-stepper-step step="2" v-bind:complete="confWizardStep > 2" :editable="confWizardStep > 1">Input</v-stepper-step>
                <v-divider></v-divider>
                <v-stepper-step step="3" v-bind:complete="confWizardStep > 3" :editable="confWizardStep > 2">Topic Output</v-stepper-step>
                <v-divider></v-divider>
          </v-stepper-header>

          <v-stepper-content step="1">
            <v-card class="mb-5" >
                <v-text-field label="Name" dark v-model="configurationLogstash.name"></v-text-field>
            </v-card>
            <v-btn color="primary" @click.native="nextStep()" :disabled="!configurationLogstash.name">Continue</v-btn>
            <v-btn flat @click.native="previousStep()">Cancel</v-btn>
          </v-stepper-content>

          <v-stepper-content step="2">
            <v-card class="mb-5 pa-3">
              <v-flex>
                   <v-flex xs12 sm4 md4>
                      <v-layout row wrap >
                         <v-select label="typeInput" v-model="typeInput" v-bind:items="typeDataIn" v-on:change="actionView"/>
                      </v-layout>
                      </p></p>
                    </v-flex>
                 </v-flex>
                 <v-flex>
                   <v-flex xs12 sm4 md4>
                      <v-layout row wrap v-show="viewHost">
                         <v-text-field label="Host" v-model="host"></v-text-field>
                         <v-text-field label="Port" v-model="port"></v-text-field>
                      </v-layout>
                      </p></p>
                    </v-flex>
                 </v-flex>
                 <v-flex>
                   <v-flex xs12 sm4 md4>
                      <v-layout row wrap v-show="viewType">
                         <v-text-field label="Type" v-model="typeForced"></v-text-field>
                         <v-text-field label="Codec"v-model="codec"></v-text-field>
                      </v-layout>
                      </p></p>
                    </v-flex>
                 </v-flex>
                 <v-flex>
                   <v-flex xs12 sm4 md4>
                      <v-layout row wrap v-show="viewTopic" >
                         <v-text-field label="Topic" v-model="topic"></v-text-field>
                      </v-layout>
                      </p></p>
                    </v-flex>
                 </v-flex>
                 <v-flex>
                   <v-flex xs12 sm4 md4>
                      <v-layout row wrap v-show="viewPath">
                         <v-text-field label="Path" v-model="path"></v-text-field>
                      </v-layout>
                      </p></p>
                    </v-flex>
                 </v-flex>
                 <v-flex>
                   <v-flex xs12 sm4 md4>
                      <v-layout row wrap >
                         <v-btn color="primary" v-on:click.native="addConfig">Add</v-btn>
                      </v-layout>
                      </p></p>
                    </v-flex>
                 </v-flex
              <v-flex>
              <v-layout row>
                     <v-flex v-for="(item,index) in configurationLogstash.input">
                        <v-chip color="orange" text-color="white" close @input="deleteItem(index)">{{item.typeInput}}</v-chip>
                     </v-flex>
              </v-layout row wrap>
            </v-card>

            <v-btn color="primary" @click.native="nextStep()" :disabled="configurationLogstash.input.length===0">Continue</v-btn>
            <v-btn flat @click.native="previousStep()">Cancel</v-btn>

          </v-stepper-content>

           <v-stepper-content step="3">
              <v-card class="mb-5 pa-3" >
                   <v-flex>
                     <v-flex xs6 sm6 md6>
                        <v-layout row wrap>
                           <v-text-field label="Host" v-model="configurationLogstash.output.host"></v-text-field>
                           <v-text-field label="Port" v-model="configurationLogstash.output.port"></v-text-field>
                        </v-layout>
                        </p></p>
                      </v-flex>
                   </v-flex>
                   <v-flex>
                     <v-flex xs6 sm6 md6>
                        <v-layout row wrap >
                           <v-text-field label="Topic" v-model="configurationLogstash.output.topic"></v-text-field>
                           <v-text-field label="Codec" v-model="configurationLogstash.output.codec"></v-text-field>
                        </v-layout>
                        </p></p>
                      </v-flex>
                   </v-flex>
              </v-card>
              <v-btn color="primary" @click.native="create()" :disabled="!configurationLogstash.output.topic">Creation</v-btn>
              <v-btn flat @click.native="previousStep()">Cancel</v-btn>
           </v-stepper-content>



    </v-stepper>

  </v-container>

</template>

<style scoped>
  .borderbox {
      border-style: solid;
      border-width: 1px;
  }
</style>

<script>
  export default{
   data () {
      return {
        confWizardStep: '',
        typeInput: '',
        host: '',
        port: '',
        typeForced: '',
        codec: '',
        topic: '',
        path: '',
        configurationLogstash: {"idConfiguration":"","name":"","input":[],"output":{"host":"","port":"","topic":"","codec":""}},
        viewError: false,
        msgError: '',
        typeDataIn : ["KAFKA","UDP","TCP","FILE","BEATS","User Advanced"],
        viewTopic: false,
        viewPath: false,
        viewType: false,
        viewHost: false
      }
   },
   mounted() {
      this.idConfiguration = this.$route.query.idConfiguration;
      if(this.idConfiguration && this.idConfiguration !='undefined' ){
           this.$http.get('/configuration/getConfiguration', {params: {idConfiguration:this.idConfiguration}}).then(response => {
              this.configurationLogstash=response.data;
           }, response => {
             this.viewError=true;
             this.msgError = "Error during call service";
           });
      }
   },
   methods: {
      nextStep() {
        this.confWizardStep++;
      },
      previousStep() {
        this.confWizardStep--;
      },
      deleteItem(index){
         if (index > -1) {
              this.configurationLogstash.input.splice(index, 1);
         }
      },
      addConfig(){
         this.configurationLogstash.input.push({"host":this.host,"port":this.port,"topic":this.topic,"codec":this.codec,"typeForced":this.typeForced,"path":this.path,"typeInput": this.typeInput});
         this.typeInput='';
         this.host='';
         this.port='';
         this.typeForced='';
         this.codec='';
         this.topic='';
         this.path='';
      },
      create(){
          if(this.idConfiguration){
            this.$http.post('/configuration/editConfiguration', this.configurationLogstash).then(response => {
               this.$router.push('/configuration/list');
            }, response => {
               this.viewError=true;
               this.msgError = "Error during call service";
            });
          }else{
            this.$http.post('/configuration/createConfiguration', this.configurationLogstash).then(response => {
               this.$router.push('/configuration/list');
            }, response => {
               this.viewError=true;
               this.msgError = "Error during call service";
            });
          }
      },
      actionView(value){
          if(value== "User Advanced"){
            this.viewHost=false;
            this.viewTopic=false;
            this.viewPath=false;
            this.viewType=false;
          }else if(value== "KAFKA"){
            this.viewTopic=true;
            this.viewPath=false;
            this.viewType=false;
            this.viewHost=true;
          }else if(value == "FILE"){
            this.viewTopic=false;
            this.viewPath=true;
            this.viewHost=false;
            this.viewType=true;
          }else{
            this.viewTopic=false;
            this.viewPath=false;
            this.viewType=true;
            this.viewHost=true;
          }
      }
   }
  }
</script>
