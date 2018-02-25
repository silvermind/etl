<template>
  <v-container fluid grid-list-md >
    <v-stepper v-model="referentialWizardStep">
          <v-stepper-header>
                <v-stepper-step step="1" v-bind:complete="referentialWizardStep > 1" editable>Process Name</v-stepper-step>
                <v-divider></v-divider>
                <v-stepper-step step="2" v-bind:complete="referentialWizardStep > 2" :editable="referentialWizardStep > 1">Key Referential</v-stepper-step>
                <v-divider></v-divider>
                <v-stepper-step step="3" v-bind:complete="referentialWizardStep > 3" :editable="referentialWizardStep > 2">List Process Consumer</v-stepper-step>
                <v-divider></v-divider>
                <v-stepper-step step="4" v-bind:complete="referentialWizardStep > 4" :editable="referentialWizardStep > 3">Add Entry</v-stepper-step>
                <v-divider></v-divider>
                <v-stepper-step step="5" v-bind:complete="referentialWizardStep > 5" :editable="referentialWizardStep > 4">Extract Meta-data</v-stepper-step>
          </v-stepper-header>

          <v-stepper-content step="1">
            <v-card class="mb-5" >
                <v-text-field label="Name" dark v-model="itemToEdit.name"></v-text-field>
            </v-card>
            <v-btn color="primary" @click.native="nextStep()" :disabled="!itemToEdit.name">Continue</v-btn>
            <v-btn flat @click.native="previousStep()">Cancel</v-btn>
          </v-stepper-content>

          <v-stepper-content step="2">
            <v-card class="mb-5">
              <v-text-field label="Name" dark v-model="itemToEdit.referentialKey"></v-text-field>
            </v-card>
            <v-btn color="primary" @click.native="nextStep()" :disabled="!itemToEdit.referentialKey">Continue</v-btn>
            <v-btn flat @click.native="previousStep()">Cancel</v-btn>
          </v-stepper-content>

          <v-stepper-content step="3">
            <v-card class="mb-5">
               <v-layout row>
                   <v-subheader>Apply on Process Consumer </v-subheader>
               </v-layout>
               <v-layout row class="mb-3 pb-3">
                   <v-flex v-for="item in listProcess">
                      <v-switch :label="item.processDefinition.name" v-model="listSelected" :value="item.id"></v-switch>
                   </v-flex>
               </v-layout>
            </v-card>
            <v-btn color="primary" v-on:click.native="nextStep()" :disabled="listSelected.length==0">Continue</v-btn>
            <v-btn flat @click.native="previousStep()">Cancel</v-btn>
          </v-stepper-content>


          <v-stepper-content step="4">
            <v-card class="mb-5">
              <v-layout row>
                 <v-flex xs2 sm2 md2>
                    <v-text-field label="New Entry" dark v-model="newEntry"></v-text-field>
                    <v-btn color="primary" v-on:click.native="addKeys()">Add</v-btn>
                 </v-flex>
              </v-layout>
              <v-layout row>
                   <v-flex xs2 sm2 md2>
                      <v-flex v-for="item in itemToEdit.listAssociatedKeys">
                         <v-chip color="orange" text-color="white" close @input="deleteItem(item)">{{item}}</v-chip>
                      </v-flex>
                   </v-flex>
                   <v-flex xs1 sm1 md1>
                         <v-icon large color="blue" v-show="itemToEdit.name!=''">arrow_forward</v-icon>
                   </v-flex>
                   <v-flex xs8 sm8 md8>
                         <v-btn flat color="blue">{{itemToEdit.referentialKey}}</v-btn>
                   </v-flex>
              </v-layout>
            </v-card>
            <v-btn color="primary" @click.native="nextStep()" :disabled="itemToEdit.listAssociatedKeys.length==0">Continue</v-btn>
            <v-btn flat @click.native="previousStep()">Cancel</v-btn>
          </v-stepper-content>

          <v-stepper-content step="5">
            <v-card class="mb-5">
              <v-layout row>
                 <v-flex xs6 sm6 md6>
                    <v-text-field label="New Metadata" dark v-model="newMetadata"></v-text-field>
                    <v-btn color="primary" v-on:click.native="addMetadata()">Add</v-btn>
                 </v-flex>
              </v-layout>
              <v-layout row>
                   <v-flex xs2 sm2 md2>
                   <v-layout row>
                      <v-flex v-for="item in itemToEdit.listMetadata">
                         <v-chip color="orange" text-color="white" close @input="deleteMetadata(item)">{{item}}</v-chip>
                      </v-flex>
                   </v-layout>
                   </v-flex>
              </v-layout>
            </v-card>
            <v-btn color="primary" @click.native="updateReferential()">Create</v-btn>
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
        referentialWizardStep: '',
        idReferential: '',
        newMetadata: '',
        newEntry: '',
        itemToEdit: {"idReferential":"","listAssociatedKeys":[],"name":"","referentialKey":"","listIdProcessConsumer":[],"listMetadata":[]},
        viewError: false,
        msgError: '',
        listProcess: [],
        listSelected: [],
        headers: [
                   { text: 'Action', align: 'center',value: '',width: '10%' },
                   { text: 'Name', align: 'center',value: 'name',width: '10%' },
                   { text: 'Data Referential', align: 'center',value: 'valueKey',width: '10%' },
                   { text: 'Keys',align: 'center',value: 'listKeys', width: '80%'}
                 ]
      }
   },
   mounted() {
      this.$http.get('/process/findAll').then(response => {
         this.listProcess=response.data;
      }, response => {
         this.viewError=true;
         this.msgError = "Error during call service";
      });
      this.idReferential = this.$route.query.idReferential;
      if(this.idReferential){
          this.editReferential(this.idReferential);
      }
   },
   methods: {
      nextStep() {
        this.referentialWizardStep++;
      },
      previousStep() {
        this.referentialWizardStep--;
      },
      editReferential(id){
        this.$http.get('/referential/find', {params: {idReferential: id}}).then(response => {
           this.itemToEdit = response.data;
        }, response => {
           this.viewError=true;
           this.msgError = "Error during call service";
        });
      },
      deleteReferential(id){
        this.$http.get('/referential/delete', {params: {idReferential: id}}).then(response => {
           this.back();
        }, response => {
           this.viewError=true;
           this.msgError = "Error during call service";
        });
      },
      updateReferential(){
        if(this.itemToEdit.idReferential != ""){
            this.callUpdate();
        }else{
            this.addReferential();
        }
      },
      callUpdate(){
        this.$http.post('/referential/update',this.itemToEdit ).then(response => {
           this.back();
        }, response => {
           this.viewError=true;
           this.msgError = "Error during call service";
        });
      },
      addReferential(){
        this.$http.post('/  referential/add', this.itemToEdit ).then(response => {
           this.back();
        }, response => {
           this.viewError=true;
           this.msgError = "Error during call service";
        });
      },
      deleteItem(item){
         this.itemToEdit.listAssociatedKeys=this.itemToEdit.listAssociatedKeys.filter(e => e !== item);
      },
      deleteMetadata(item){
         this.itemToEdit.listMetadata=this.itemToEdit.listMetadata.filter(e => e !== item);
      },
      addKeys(){
         this.itemToEdit.listAssociatedKeys.push(this.newEntry);
      },
      addMetadata(){
         this.itemToEdit.listMetadata.push(this.newMetadata);
      },
      back(){
        this.$router.push('/referential');
      }
   }
  }
</script>
