<template>
  <v-container fluid grid-list-md >
   <v-flex>
       <v-flex xs12 sm6 md6>
          <v-layout row wrap >
                    <v-select label="typeInput" v-model="processValidation.typeValidation" v-bind:items="type" v-on:change="actionView"/>
          </v-layout>
          </p></p>
       </v-flex>
   </v-flex>
   <v-flex>
       <v-flex xs12 sm6 md6>
          <v-layout row wrap v-show="viewMandatory">
                    <v-text-field label="Mandatory (separeted by ;)" v-model="processValidation.parameterValidation.mandatory"></v-text-field>
          </v-layout>
          </p></p>
       </v-flex>
   </v-flex>
   <v-flex>
       <v-flex xs12 sm6 md6 v-show="viewBlackList">
         <v-layout row wrap >
                   <v-text-field label="Key" v-model="keyBlackList"></v-text-field>
                   <v-text-field label="Value" v-model="valueBlackList"></v-text-field>
                   <v-btn color="primary" v-on:click.native="addItemBlackList">add BlackList Item</v-btn>
         </v-layout>
         <v-layout>
            <v-btn color="primary" v-on:click.native="removeBlackList">Remove</v-btn>
            <v-flex xs2 sm2 md2 v-for="itemBlack in processValidation.parameterValidation.blackList">
                  <v-btn color="purple lighten-2" small>{{itemBlack.key}}-{{itemBlack.value}}</v-btn>
            </v-flex>
         </v-layout>
          </p></p>
       </v-flex>
   </v-flex>
   <v-flex>
       <v-flex xs12 sm6 md6>
          <v-layout row wrap v-show="viewMaxFields">
                    <v-text-field label="Maximum field " v-model="processValidation.parameterValidation.maxFields"></v-text-field>
          </v-layout>
          </p></p>
       </v-flex>
   </v-flex>
   <v-flex>
       <v-flex xs12 sm6 md6>
          <v-layout row wrap v-show="vieMaxMessageSize">
                    <v-text-field label="Maximum Size message" v-model="processValidation.parameterValidation.maxMessageSize"></v-text-field>
          </v-layout>
          </p></p>
       </v-flex>
   </v-flex>
   <v-flex>
       <v-flex xs12 sm6 md6>
         <v-layout row wrap v-show="viewFieldExist">
                   <v-text-field label="Field exist" v-model="processValidation.parameterValidation.fieldExist"></v-text-field>
         </v-layout>
         </p></p>
        </v-flex>
   </v-flex>
   <v-flex>
      <v-flex xs12 sm6 md6>
         <v-layout row wrap>
                  <v-btn color="primary" v-on:click.native="addValidation">Edit Validation</v-btn>
                  <v-btn color="error" v-on:click.native="removeValidation">Remove Validation</v-btn>
         </v-layout>
         </p></p>
      </v-flex>
   </v-flex>
   <v-layout row wrap>
     <v-flex xs12 sm12 md12 >
       <v-alert v-model="viewError" xs12 sm12 md12  color="error" icon="warning" value="true" dismissible>
            {{ msgError }}
       </v-alert>
     </v-flex>
    </v-layout row wrap>
   </v-container>

</template>


<script>
  export default{
   data () {
         return {
           processValidation: {"idProcessValidation":"",
                               "parameterValidation":{
                                             "mandatory":"",
                                             "blackList":[],
                                             "maxFields":"",
                                             "maxMessageSize":"",
                                             "fieldExist":""
                                             }
                                           },
           idProcess: '',
           keyBlackList: '',
           valueBlackList: '',
           type : ["MANDATORY_FIELD","BLACK_LIST_FIELD","MAX_FIELD","MAX_MESSAGE_SIZE","FIELD_EXIST"],
           msgError: '',
           viewError: false,
           vieMaxMessageSize: false,
           viewMaxFields: false,
           viewBlackList: false,
           viewFieldExist: false,
           viewMandatory: false
         }
   },
   mounted() {
            this.idProcess = this.$route.query.idProcess;
            this.idProcessValidation = this.$route.query.idProcessValidation;
            if(this.idProcessValidation != null){
                   this.$http.get('/process/findProcessValidation', {params: {idProcess: this.idProcess, idProcessValidation: this.idProcessValidation}}).then(response => {
                      this.processValidation=response.data;
                      this.actionView(this.processValidation.typeValidation);
                   }, response => {
                      this.viewError=true;
                      this.msgError = "Error during call service";
                   });
             }
   },
   methods: {
        addValidation(){
          this.$http.post('/process/createProcessValidation', {idProcess: this.idProcess, processValidation: this.processValidation}).then(response => {
             this.$router.push('/process/add/process?idProcess='+this.idProcess);
          }, response => {
             this.viewError=true;
             this.msgError = "Error during call service";
          });
        },
        removeValidation(){
          this.$http.get('/process/removeProcessValidation', {params: {idProcess: this.idProcess, idProcessValidation: this.idProcessValidation}}).then(response => {

             this.$router.push('/process/add/process?idProcess='+this.idProcess);
          }, response => {
             this.viewError=true;
             this.msgError = "Error during call service";
          });
        },
        addItemBlackList(){
           this.processValidation.parameterValidation.blackList.push({key: this.keyBlackList ,value: this.valueBlackList});
           this.keyBlackList = '';
           this.valueBlackList = '';
        },
        removeBlackList(){
           this.processValidation.parameterValidation.blackList=[];
           this.keyBlackList = '';
           this.valueBlackList = '';
        },
        disable(){
           this.vieMaxMessageSize=false;
           this.viewMaxFields=false;
           this.viewFormatDate=false;
           this.viewBlackList=false;
           this.viewMandatory=false;
           this.viewFieldExist=false;
           this.processValidation.formatJson=false;
        },
        actionView(value){
          //Miam miam miammmmmmm
          if(value== "FORMAT_JSON"){
            this.disable();
            this.processValidation.formatJson=true;
          }else if(value== "MANDATORY_FIELD"){
            this.disable();
            this.viewMandatory=true;
          }else if(value == "BLACK_LIST_FIELD"){
            this.disable();
            this.viewBlackList=true;
          }else if(value == "FORMAT_DATE"){
            this.disable();
            this.viewFormatDate=true;
          }else if(value == "MAX_FIELD"){
            this.disable();
            this.viewMaxFields=true;
          }else if(value == "MAX_MESSAGE_SIZE"){
            this.disable();
            this.vieMaxMessageSize=true;
          }else if(value == "FIELD_EXIST"){
            this.disable();
            this.viewFieldExist=true;
          }else{
            this.disable();
          }
        }
    }
  }
</script>
