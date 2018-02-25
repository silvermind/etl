<template>
  <v-container fluid grid-list-md >
   <v-flex>
       <v-flex xs12 sm6 md6>
          <v-layout row wrap >
                    <v-select label="Type Transformation" v-model="processTransformation.typeTransformation" v-bind:items="type" v-on:change="actionView"/>
          </v-layout>
          </p></p>
       </v-flex>
   </v-flex>
   <v-flex>
       <v-flex xs12 sm6 md6>
          <v-layout row wrap v-show="viewComposeField">
                    <v-text-field label="Key Field" v-model="processTransformation.parameterTransformation.composeField.key"></v-text-field>
                    <v-text-field label="Value Field" v-model="processTransformation.parameterTransformation.composeField.value"></v-text-field>
          </v-layout>
          </p></p>
       </v-flex>
   </v-flex>
   <v-flex>
       <v-flex xs12 sm6 md6>
          <v-layout row wrap v-show="viewKeyField">
                    <v-text-field label="Field " v-model="processTransformation.parameterTransformation.keyField"></v-text-field>
          </v-layout>
          </p></p>
       </v-flex>
   </v-flex>
   <v-flex>
       <v-flex xs12 sm6 md6>
          <v-layout row wrap v-show="viewLookupExternal">
                    <v-text-field label="Url" v-model="processTransformation.parameterTransformation.externalHTTPData.url"></v-text-field>
          </v-layout>
          <v-layout row wrap v-show="viewLookupExternal">
                    <v-select label="METHOD" v-model="processTransformation.parameterTransformation.externalHTTPData.httpMethod" v-bind:items="methodCall"/>
                    <v-text-field type="number" label="Refresh in seconds" v-model="processTransformation.parameterTransformation.externalHTTPData.refresh"></v-text-field>
                    <v-text-field label="Limit on field (Optional) " v-model="processTransformation.parameterTransformation.keyField"></v-text-field>
          </v-layout>
          <v-layout row wrap v-show="viewLookupExternal">
                    <v-text-field label="body with POST" v-model="processTransformation.parameterTransformation.externalHTTPData.body"></v-text-field>
          </v-layout>
          </p></p>
        </v-flex>
   </v-flex>
   <v-flex>
       <v-flex xs12 sm6 md6>
          <v-layout row wrap v-show="viewLookupList">
              <v-text-field label="Limit on field (Optional) " v-model="processTransformation.parameterTransformation.keyField"></v-text-field>
              <v-text-field label="add list example : key;value" textarea dark v-model="mapRender"></v-text-field>
          </v-layout>
        </v-flex>
   </v-flex>
   <v-flex>
        <v-flex xs12 sm6 md6>
           <v-layout row wrap v-show="viewDateField">
                    <v-text-field label="Key Field" v-model="processTransformation.parameterTransformation.formatDateValue.keyField"></v-text-field>
                    <v-text-field label="Source Format" v-model="processTransformation.parameterTransformation.formatDateValue.srcFormat"></v-text-field>
                    <v-text-field label="Target Format" v-model="processTransformation.parameterTransformation.formatDateValue.targetFormat"></v-text-field>
           </v-layout>
           </p></p>
        </v-flex>
   </v-flex>
   <v-flex>
      <v-flex xs12 sm6 md6>
         <v-layout row wrap>
                  <v-btn color="primary" v-on:click.native="addTransformation">Edit Transformation</v-btn>
                  <v-btn color="error" v-on:click.native="removeTransformation">Remove Transformation</v-btn>
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
         processTransformation: {
                                 "parameterTransformation":{
                                   "composeField": {"key":"","value":""},
                                   "formatDateValue": {"keyField":"","srcFormat":"","targetFormat":""},
                                   "keyField": "",
                                   "externalHTTPData": {"url":"","httpMethod":"","refresh":"","body":""}
                                 }
                                 },
           idProcess: '',
           keyBlackList: '',
           valueBlackList: '',
           methodCall: ["GET","POST"],
           type : ["ADD_FIELD","DELETE_FIELD","RENAME_FIELD","FORMAT_DATE","FORMAT_BOOLEAN","FORMAT_GEOPOINT","FORMAT_DOUBLE","FORMAT_LONG","FORMAT_IP", "LOOKUP_LIST","LOOKUP_EXTERNAL"],
           msgError: '',
           viewError: false,
           viewKeyField: false,
           viewComposeField: false,
           viewDateField: false,
           viewLookupList: false,
           viewLookupExternal: false,
           mapRender: ''
         }
   },
   mounted() {
        this.idProcess = this.$route.query.idProcess;
        this.idProcessTransformation = this.$route.query.id;
        if(this.idProcessTransformation != null){
               this.$http.get('/process/findProcessTransformation', {params: {idProcess: this.idProcess, id: this.idProcessTransformation}}).then(response => {
                  this.processTransformation=response.data;
                  this.actionView(this.processTransformation.typeTransformation);
                  var result= "";
                  var rootMap= this.processTransformation.parameterTransformation.mapLookup
                  for(var key in rootMap ){
                    result=result.concat(key).concat(';').concat(rootMap[key]);
                  }
                  this.mapRender=result;
               }, response => {
                  this.viewError=true;
                  this.msgError = "Error during call service";
               });
         }
   },
   methods: {
        addTransformation(){
             var tabItem = this.mapRender.split("\n");
             var result={};
             for(var i=0; i<tabItem.length; i++){
                 var itemLookup=tabItem[i].split(';');
                 result[itemLookup[0]]=itemLookup[1];
             }
             this.processTransformation.parameterTransformation.mapLookup=result;
          this.$http.post('/process/createProcessTransformation', {idProcess: this.idProcess, processTransformation: this.processTransformation}).then(response => {
             this.$router.push('/process/add/process?idProcess='+this.idProcess);
          }, response => {
             this.viewError=true;
             this.msgError = "Error during call service";
          });
        },
        removeTransformation(){
          this.$http.get('/process/removeProcessTransformation', {params: {idProcess: this.idProcess, id: this.idProcessTransformation}}).then(response => {
             this.$router.push('/process/add/process?idProcess='+this.idProcess);
          }, response => {
             this.viewError=true;
             this.msgError = "Error during call service";
          });
        },
        disable(){
           this.viewComposeField=false;
           this.viewKeyField=false;
           this.viewDateField=false;
           this.viewLookupList=false;
           this.viewLookupExternal=false;
        },
        actionView(value){
          if(value== "FORMAT_DATE"){
            this.disable();
            this.viewDateField=true;
          }else if(value== "LOOKUP_EXTERNAL"){
            this.disable();
            this.viewLookupExternal=true;
          }else if(value== "LOOKUP_LIST"){
            this.disable();
            this.viewLookupList=true;
          }else if(value== "ADD_FIELD" || value == "RENAME_FIELD"){
            this.disable();
            this.viewComposeField=true;
          }else if(value == "DELETE_FIELD" || value == "FORMAT_BOOLEAN" || value == "FORMAT_GEOPOINT" || value == "FORMAT_DOUBLE"|| value == "FORMAT_LONG" || value == "FORMAT_IP"){
            this.disable();
            this.viewKeyField=true;
          }else{
            this.disable();
          }
        }
    }
  }
</script>
