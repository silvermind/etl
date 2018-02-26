<template>
  <v-container fluid grid-list-md >
    <v-stepper v-model="metricWizardStep">
      <v-stepper-header>
        <v-stepper-step step="1" v-bind:complete="metricWizardStep > 1" editable>Process Name</v-stepper-step>
        <v-divider></v-divider>
        <v-stepper-step step="2" v-bind:complete="metricWizardStep > 2" :editable="metricWizardStep > 1">Source</v-stepper-step>
        <v-divider></v-divider>
        <v-stepper-step step="3" v-bind:complete="metricWizardStep > 3" :editable="metricWizardStep > 2">Window</v-stepper-step>
        <v-divider></v-divider>
        <v-stepper-step step="4" v-bind:complete="metricWizardStep > 4" :editable="metricWizardStep > 3">Function</v-stepper-step>
        <v-divider></v-divider>
        <v-stepper-step step="5" v-bind:complete="metricWizardStep > 5" :editable="metricWizardStep > 4">
          Where
          <small>Optional</small>
        </v-stepper-step>
        <v-divider></v-divider>
        <v-stepper-step step="6" v-bind:complete="metricWizardStep > 6" :editable="metricWizardStep > 4">
          Group By
          <small>Optional</small>
        </v-stepper-step>
        <v-divider></v-divider>
        <v-stepper-step step="7" v-bind:complete="metricWizardStep > 7" :editable="metricWizardStep > 4">Destination</v-stepper-step>
        <v-divider></v-divider>
        <v-stepper-step step="8" v-bind:complete="metricWizardStep > 8">Summary</v-stepper-step>
      </v-stepper-header>

      <v-stepper-content step="1">
        <v-card class="mb-5">
          <v-text-field label="Name of your process" v-model="metricProcess.name" required :rules="[() => !!metricProcess.name || 'This field is required']"></v-text-field>
        </v-card>
        <v-btn color="primary" @click.native="nextStep()" :disabled="!metricProcess.name">Continue</v-btn>
        <v-btn flat @click.native="previousStep()">Cancel</v-btn>
      </v-stepper-content>

      <v-stepper-content step="2">
        <v-card class="mb-5" >
          <v-text-field label="Topic name" v-model="metricProcess.fromTopic" required :rules="[() => !!metricProcess.fromTopic || 'This field is required']"></v-text-field>
        </v-card>
        <v-btn color="primary" @click.native="nextStep()" :disabled="!metricProcess.fromTopic">Continue</v-btn>
        <v-btn flat @click.native="previousStep()">Cancel</v-btn>
      </v-stepper-content>

      <v-stepper-content step="3">
        <v-card class="mb-5" >
          <v-select v-bind:items="windowTypes" v-model="metricProcess.windowType" label="Select Window Type" item-value="text" required :rules="[() => !!metricProcess.windowType || 'This field is required']"></v-select>
          <v-text-field label="Size" v-model="metricProcess.size" required :rules="[() => !!metricProcess.size || 'This field is required']"></v-text-field>
          <v-select v-bind:items="timeunits" v-model="metricProcess.sizeUnit" label="Select Window TimeUnit" item-value="text" required :rules="[() => !!metricProcess.sizeUnit || 'This field is required']"></v-select>
          <v-text-field label="Advance by Size" v-model="metricProcess.advanceBy" v-if="metricProcess.windowType == 'HOPPING'" required :rules="[() => metricProcess.windowType == 'HOPPING' && !!metricProcess.advanceBy || 'This field is required']"></v-text-field>
          <v-select v-bind:items="timeunits" v-model="metricProcess.advanceByUnit" v-if="metricProcess.windowType == 'HOPPING'" label="Select advance by timeUnit" item-value="text" required :rules="[() => metricProcess.windowType == 'HOPPING' && !!metricProcess.advanceByUnit || 'This field is required']"></v-select>
        </v-card>
        <v-btn color="primary" @click.native="nextStep()" :disabled="!metricProcess.fromTopic">Continue</v-btn>
        <v-btn flat @click.native="previousStep()">Cancel</v-btn>
      </v-stepper-content>

      <v-stepper-content step="4">
        <v-card class="mb-5">
         <v-select v-bind:items="functions" v-model="metricProcess.functionName" label="Select function" item-value="text" required :rules="[() => !!metricProcess.functionName || 'This field is required']"></v-select>
         <v-text-field label="Field" v-model="metricProcess.functionField" required :rules="[() => !!metricProcess.functionField || 'This field is required']"></v-text-field>
        </v-card>
        <v-btn color="primary" @click.native="nextStep()" :disabled="!metricProcess.functionName || !metricProcess.functionField">Continue</v-btn>
        <v-btn flat @click.native="previousStep()">Cancel</v-btn>
      </v-stepper-content>


      <v-stepper-content step="5">
        <v-card class="mb-5">
          <v-text-field label="Where condition" v-model="metricProcess.where"></v-text-field>
        </v-card>
        <v-btn color="primary" @click.native="nextStep()">Continue</v-btn>
        <v-btn flat @click.native="previousStep()">Cancel</v-btn>
      </v-stepper-content>


      <v-stepper-content step="6">
        <v-card class="mb-5">
          <v-text-field label="Group by Field" v-model="metricProcess.groupBy"></v-text-field>
        </v-card>
        <v-btn color="primary" @click.native="nextStep()">Continue</v-btn>
        <v-btn flat @click.native="previousStep()">Cancel</v-btn>
      </v-stepper-content>

      <v-stepper-content step="7">
        <v-select v-bind:items="outputTypes" v-model="metricProcess.typeOutput" label="Select output type" item-value="text" required :rules="[() => !!metricProcess.typeOutput || 'This field is required']"></v-select>
        <v-text-field label="Destination Topic" v-model="metricProcess.toTopic" required v-if="metricProcess.typeOutput == 'KAFKA'" :rules="[() => metricProcess.typeOutput == 'KAFKA'  && !!metricProcess.toTopic || 'This field is required']"></v-text-field>
        <v-select v-bind:items="retentionLevels" v-model="metricProcess.retentionLevel" label="Select retention level" item-value="text" required v-if="metricProcess.typeOutput == 'ELASTICSEARCH'"  :rules="[() => metricProcess.typeOutput == 'ELASTICSEARCH' && !!metricProcess.retentionLevel || 'This field is required']"></v-select>
        <v-btn color="primary" @click.native="nextStep()" :disabled="!metricProcess.toTopic">Continue</v-btn>
        <v-btn flat @click.native="previousStep()">Cancel</v-btn>
      </v-stepper-content>

      <v-stepper-content step="8">
        <v-card class="mb-5">
          <v-layout row wrap>
            <v-flex xs6>
              <v-subheader>Process Name : </v-subheader>
              {{metricProcess.name}}
            </v-flex>

            <v-flex xs6>
              <v-subheader>Function</v-subheader>
              {{metricProcess.functionName + '(' + metricProcess.functionField + ')'}}
            </v-flex>
            <v-flex xs6>
              <v-subheader>From</v-subheader>
              {{metricProcess.fromTopic}}
            </v-flex>

            <v-flex xs6>
              <v-subheader>Window</v-subheader>
              {{windowFormat(metricProcess)}}
            </v-flex>

            <v-flex xs6>
              <v-subheader>Where</v-subheader>
              {{metricProcess.where}}
            </v-flex>
            <v-flex xs6>
              <v-subheader>Group By</v-subheader>
              {{metricProcess.groupBy}}
            </v-flex>

            <v-flex xs6>
              <v-subheader>Destination</v-subheader>
              {{destinationFormat(metricProcess)}}
            </v-flex>

           </v-layout>
           <v-btn color="primary" @click.native="launch()">Launch</v-btn>
           <v-btn flat @click.native="previousStep()">Cancel</v-btn>
        </v-card>
      </v-stepper-content>
    </v-stepper>

  </v-container>

</template>


<script>
  export default{
   data () {
         return {
            metricProcess: {
              idProcess: "",
              processName: "",
              functionName: "SUM",
              functionField: "",
              fromTopic:"",
              windowType: "TUMBLING",
              size: 5,
              sizeUnit: "MINUTES",
              advanceBy: 1,
              advanceByUnit: "MINUTES",
              where: "",
              groupBy: "",
              typeOutput: "KAFKA",
              toTopic:"",
              retentionLevel: "week"
            },
            functions: ["SUM","AVG","MIN","MAX"],
            windowTypes: ["TUMBLING", "HOPPING", "SESSION"],
            timeunits: ["SECONDS","MINUTES","HOURS","DAYS"],
            outputTypes: [ "KAFKA", "SYSTEM_OUT", "ELASTICSEARCH" ],
            retentionLevels: [ "day", "week", "month", "quarter", "year"],
            metricWizardStep: 1,
            message:""
         }
    },
    mounted() {
       this.metricProcess.idProcess=this.$route.query.idProcess;

       if (this.metricProcess.idProcess) {
          this.$http.get('/metric/findById', {params: {idProcess: this.metricProcess.idProcess}}).then(response => {
             this.metricProcess=response.data;
             let result = this.metricProcess.aggFunction.match(/(.*)\((.*)\)/);
             this.metricProcess.functionName=result[1];
             this.metricProcess.functionField=result[2];
             console.log(response);

          }, response => {
             this.viewError=true;
             this.msgError = "Error during call service";
          });
       }
    },
    methods: {
      nextStep() {
        this.metricWizardStep++;
      },
      previousStep() {
        this.metricWizardStep--;
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
      },
      destinationFormat(processDefinition) {
        switch (processDefinition.typeOutput) {
          case "KAFKA":
            return processDefinition.typeOutput + "(" + processDefinition.toTopic + ")";
          case "ELASTICSEARCH":
            return processDefinition.typeOutput + "(" + processDefinition.retentionLevel + ")"
          default:
            return processDefinition.typeOutput;
        }
      },
      windowFormat(processDefinition) {
          if (processDefinition.windowType == 'HOPPING') {
            return processDefinition.windowType + "(" + processDefinition.size + " " +processDefinition.sizeUnit + ")";
          } else {
            return processDefinition.windowType + "(" + processDefinition.size + " " +processDefinition.sizeUnit + ", " +
               processDefinition.advanceBy + " " +processDefinition.advanceByUnit +")";
          }
      }
    }
  }
</script>
