<template>
  <v-container fluid grid-list-md >
    <v-stepper v-model="consumerWizardStep">
      <v-stepper-header>
        <v-stepper-step step="1" v-bind:complete="consumerWizardStep > 1" editable>Input</v-stepper-step>
        <v-divider></v-divider>
        <v-stepper-step step="2" v-bind:complete="consumerWizardStep > 2" :editable="consumerWizardStep > 1">Name</v-stepper-step>
        <v-divider></v-divider>
        <v-stepper-step step="3" v-bind:complete="consumerWizardStep > 3" :editable="consumerWizardStep > 2">Input</v-stepper-step>
        <v-divider></v-divider>
        <v-stepper-step step="4" v-bind:complete="consumerWizardStep > 4" :editable="consumerWizardStep > 3">Parser</v-stepper-step>
        <v-divider></v-divider>
        <v-stepper-step step="5" v-bind:complete="consumerWizardStep > 5" :editable="consumerWizardStep > 4">Transformation</v-stepper-step>
        <v-divider></v-divider>
        <v-stepper-step step="6" v-bind:complete="consumerWizardStep > 6" :editable="consumerWizardStep > 5">Validation</v-stepper-step>
        <v-divider></v-divider>
        <v-stepper-step step="7" v-bind:complete="consumerWizardStep > 7" :editable="consumerWizardStep > 9">Output</v-stepper-step>
      </v-stepper-header>

      <v-stepper-content step="1">
        <v-card class="mb-5">
           <v-text-field label="Name of your process" v-model="process.name" required :rules="[() => !!process.name || 'This field is required']""></v-text-field>
        </v-card>
        <v-btn color="primary" @click.native="nextStep()" :disabled="!process.name">Continue</v-btn>
        <v-btn flat @click.native="previousStep()">Cancel</v-btn>
      </v-stepper-content>

      <v-stepper-content step="2">
        <v-card class="mb-5" >
           <v-layout row wrap>
              <v-flex xs6 sm6 md6>
                   <p>Input Kafka</p>
                   <v-flex xs12 sm12 md12>
                      <v-layout row >
                         <v-text-field label="Host" v-model="process.processInput.host"></v-text-field>
                         <v-text-field label="Port" v-model="process.processInput.port"></v-text-field>
                      </v-layout>
                      </p></p>
                   </v-flex>
                   <v-flex xs12 sm12 md12>
                     <v-layout row >
                       <v-text-field label="Topic Input" v-model="process.processInput.topicInput"></v-text-field>
                     </v-layout>
                     </p></p>
                   </v-flex>
              </v-flex>
           </v-layout>
        </v-card>
        <v-btn color="primary" @click.native="nextStep()" :disabled="!process.processInput.topicInput">Continue</v-btn>
        <v-btn flat @click.native="previousStep()">Cancel</v-btn>
      </v-stepper-content>


      <v-stepper-content step="3">
        <v-card class="mb-5">
           <v-layout row wrap>
                  <v-flex>
                     <v-flex xs12 sm6 md6>
                        <v-layout row wrap >
                                  <v-select label="Type Transformation" v-model="process.processTransformation.typeTransformation" v-bind:items="typeTransformation"/>
                        </v-layout>
                        </p></p>
                      </v-flex>
                  </v-flex>
           </v-layout>
           <v-layout row wrap>
                        <v-layout class="pl-1" row v-show="process.processTransformation.typeTransformation == 'ADD_FIELD' || process.processTransformation.typeTransformation == 'RENAME_FIELD'">
                            <v-flex xs6 sm6 md6>
                                  <v-text-field label="Key Field" v-model="process.processTransformation.parameterTransformation.composeField.key"></v-text-field>
                                  <v-text-field label="Value Field" v-model="process.processTransformation.parameterTransformation.composeField.value"></v-text-field>
                            </v-flex>
                        </v-layout>
                        </p></p>
                         <v-layout class="pl-1" row wrap v-show="process.processTransformation.typeTransformation == 'FORMAT_DATE'">
                            <v-flex xs6 sm6 md6>
                                  <v-text-field label="Key Field" v-model="process.processTransformation.parameterTransformation.formatDateValue.keyField"></v-text-field>
                                  <v-text-field label="Source Format" v-model="process.processTransformation.parameterTransformation.formatDateValue.srcFormat"></v-text-field>
                                  <v-text-field label="Target Format" v-model="process.processTransformation.parameterTransformation.formatDateValue.targetFormat"></v-text-field>
                            </v-flex>
                         </v-layout>
                        <v-layout class="pl-1" row wrap v-show="process.processTransformation.typeTransformation == 'LOOKUP_LIST'">
                            <v-flex xs6 sm6 md6>
                                 <v-text-field label="Limit on field (Optional) " v-model="process.processTransformation.parameterTransformation.keyField"></v-text-field>
                                 <v-text-field name="add list example : key;value" label="add list example : key;value" textarea dark v-model="mapLookup"></v-text-field>
                            </v-flex>
                        </v-layout>
                        <v-layout class="pl-1" row wrap v-show="process.processTransformation.typeTransformation == 'DELETE_FIELD'
                        || process.processTransformation.typeTransformation == 'FORMAT_BOOLEAN'
                        || process.processTransformation.typeTransformation == 'FORMAT_DOUBLE'
                        || process.processTransformation.typeTransformation == 'FORMAT_LONG'
                        || process.processTransformation.typeTransformation == 'FORMAT_IP'
                        || process.processTransformation.typeTransformation == 'FORMAT_GEOPOINT'">
                            <v-flex xs6 sm6 md6>
                                  <v-text-field label="Field " v-model="process.processTransformation.parameterTransformation.keyField"></v-text-field>
                            </v-flex>
                        </v-layout>
                        </p></p>
                        <v-layout class="pl-1" row wrap v-show="process.processTransformation.typeTransformation == 'LOOKUP_EXTERNAL'">
                             <v-flex xs6 sm6 md6>
                                  <v-text-field label="Url" v-model="process.processTransformation.parameterTransformation.externalHTTPData.url"></v-text-field>
                                  <v-select label="METHOD" v-model="process.processTransformation.parameterTransformation.externalHTTPData.httpMethod" v-bind:items="methodCall"/>
                             </v-flex>
                             <v-flex xs6 sm6 md6>
                                  <v-text-field type="number" label="Refresh in seconds" v-model="process.processTransformation.parameterTransformation.externalHTTPData.refresh"></v-text-field>
                                  <v-text-field label="Limit on field (Optional) " v-model="process.processTransformation.parameterTransformation.keyField"></v-text-field>
                             </v-flex>
                             <v-flex xs6 sm6 md6>
                                  <v-text-field label="body with POST" v-model="process.processTransformation.parameterTransformation.externalHTTPData.body"></v-text-field>
                             </v-flex>
                        </v-layout>
                        </p></p>
                 </v-layout>
        </v-card>
        <v-btn color="success" @click.native="createTransformation()">Create</v-btn>
        <v-btn color="primary" @click.native="nextStep()" :disabled="!process.processTransformation.typeTransformation">Continue</v-btn>
        <v-btn flat @click.native="previousStep()">Cancel</v-btn>
      </v-stepper-content>

    </v-stepper>
 </v-container>

</template>


<script>

  //import templateInput from './TemplateInput.vue';

  export default{
  // components: {
  //    templateInput
  // },
   data () {
         return {
            process: {
              name: "",
              processInput: {"host":"kafka.kafka","port":"9092","topicInput":"processTopic"},
              processTransformation: {"typeTransformation":"","parameterTransformation": {"composeField":{"key":"","value":""},
                                                                                          "keyField":"",
                                                                                          "formatDateValue":{"keyField":"","srcFormat":"","targetFormat":""},
                                                                                          "externalHTTPData":{"url":"","httpMethod":"","refresh":"10","body":""},
                                                                                          mapLookup:''
                                                                                         }},
            },
            consumerWizardStep: 1,
            message:"",
            typeTransformation: ["ADD_FIELD","DELETE_FIELD","RENAME_FIELD","FORMAT_DATE","FORMAT_BOOLEAN","FORMAT_GEOPOINT","FORMAT_DOUBLE","FORMAT_LONG","FORMAT_IP", "LOOKUP_LIST","LOOKUP_EXTERNAL"],
            methodCall: ["GET","POST"],
            mapLookup: ''
         }
    },
    mounted() {

    },
    methods: {
      createTransformation(){
        var tabItem = this.mapLookup.split("\n");
        var result={};
        for(var i=0; i<tabItem.length; i++){
            var itemLookup=tabItem[i].split(';');
            result[itemLookup[0]]=itemLookup[1];
        }
        this.processTransformation.parameterTransformation.mapLookup=result;

      },
      nextStep() {
        this.consumerWizardStep++;
      },
      previousStep() {
        this.consumerWizardStep--;
      },
      launch() {
        this.metricProcess.aggFunction=this.metricProcess.functionName + '(' + this.metricProcess.functionField + ')';
        this.$http.post('/metric/update', this.metricProcess).then(response => {
           console.log(response);
            this.$router.push('/metric/list');
        }, response => {
           this.viewError=true;
           this.msgError = "Error during call service";
        });
      }
    }
  }
</script>
