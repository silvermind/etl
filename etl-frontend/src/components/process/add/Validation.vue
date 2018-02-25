<template>
  <v-container fluid grid-list-md >
    <v-breadcrumbs>
      <v-icon slot="divider">forward</v-icon>
      <v-breadcrumbs-item v-for="item in bcList" :key="item.text" :disabled="item.disabled">{{ item.text }}</v-breadcrumbs-item>
    </v-breadcrumbs>
   <v-flex>
       <v-flex xs12 sm6 md6>
          <v-layout row wrap >
                    <v-select label="Type Validation" v-model="processValidation.typeValidation" v-bind:items="type" v-on:change="actionView"/>
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
          <v-layout row wrap v-show="viewGrok">
                    <v-text-field label="Grok field " v-model="processValidation.parameterValidation.grok.key"></v-text-field>
                    <v-text-field label="Grok Pattern" v-model="processValidation.parameterValidation.grok.value"></v-text-field>
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
                  <v-btn color="primary" v-on:click.native="addValidation">add Validation</v-btn>
                  <v-btn color="success" v-on:click.native="nextStep">Next</v-btn>
         </v-layout>
         </p></p>
       </v-flex>
   </v-flex>

   <v-layout row wrap>
     <v-flex xs12 sm12 md12 >
       <v-flex v-for="validItem in listValidation">
          <v-btn color="blue-grey lighten-3" small>{{validItem.typeValidation}}</v-btn>
       </v-flex>
       <v-alert v-model="viewError" xs12 sm12 md12  color="error" icon="warning" value="true" dismissible>
            {{ msgError }}
       </v-alert>
       <v-alert v-model="viewMessageClient" xs12 sm12 md12  color="info" icon="info" value="true" dismissible>
            {{ messageClientCreated }}
       </v-alert>
      </v-flex>
   </v-layout row wrap>
    </v-container>

</template>


<script>
  export default{
   data () {
         return {
           messageClientCreated:'',
           viewMessageClient: false,
           listValidation: [],
           processValidation: {"parameterValidation":{
                                  "mandatory":'',
                                  "blackList":[],
                                  "addField": {"key":"","value":""},
                                  "renameField": {"key":"","value":""},
                                  "deleteField": {"key":"","value":""},
                                  "formatDate": {"key":"","value":""},
                                  "grok": {"key":"","value":""},
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
           viewGrok: false,
           viewBlackList: false,
           viewMandatory: false,
           viewFieldExist: false,
           mandatory: '',
           bcList: [{text: 'Name',disabled: true},
                   {text: 'Input',disabled: true},
                   {text: 'Parser',disabled: true},
                   {text: 'Transformation',disabled: true},
                   {text: 'Validation',disabled: false},
                   {text: 'Filter',disabled: true},
                   {text: 'Output',disabled: true},
                   {text: 'Finish',disabled: true }
                  ],
         }
   },
   mounted() {
            this.idProcess = this.$route.query.idProcess;
   },
   methods: {
        nextStep(){
             this.$router.push('/process/add/filter?idProcess='+this.idProcess);
        },
        addValidation(){
          this.listValidation.push({"typeValidation":this.processValidation.typeValidation});
          this.$http.post('/process/createProcessValidation', {idProcess: this.idProcess, processValidation: this.processValidation}).then(response => {
             this.messageClientCreated="Validation created";
             this.viewMessageClient=true;
          }, response => {
             this.viewError=true;
             this.msgError = "Error during call service";
          });
        },
        disable(){
           this.vieMaxMessageSize=false;
           this.viewMaxFields=false;
           this.viewGrok=false;
           this.viewBlackList=false;
           this.viewMandatory=false;
           this.viewFieldExist=false;
           this.processValidation.parameterValidation.formatJson=false;
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
        actionView(value){
          //Miam miam miammmmmmm
          if(value== "FORMAT_JSON"){
            this.disable();
            this.processValidation.parameterValidation.formatJson=true;
          }else if(value== "MANDATORY_FIELD"){
            this.disable();
            this.viewMandatory=true;
          }else if(value == "BLACK_LIST_FIELD"){
            this.disable();
            this.viewBlackList=true;
          }else if(value == "GROK"){
            this.disable();
            this.viewGrok=true;
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
