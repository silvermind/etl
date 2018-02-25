<template>
  <v-container fluid grid-list-md >
    <v-breadcrumbs>
      <v-icon slot="divider">forward</v-icon>
      <v-breadcrumbs-item v-for="item in bcList" :key="item.text" :disabled="item.disabled">{{ item.text }}</v-breadcrumbs-item>
    </v-breadcrumbs>
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
          <v-layout row wrap v-show="viewLookupList">
                <v-text-field label="Limit on field (Optional) " v-model="processTransformation.parameterTransformation.keyField"></v-text-field>
               <v-text-field name="add list example : key;value" label="add list example : key;value" textarea dark v-model="mapLookup"></v-text-field>
          </v-layout>
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
         <v-layout row wrap>
                  <v-btn color="primary" v-on:click.native="addTransformation">add Transformation</v-btn>
                  <v-btn color="success" v-on:click.native="nextStep">Next</v-btn>
         </v-layout>
         </p></p>
       </v-flex>
    </v-flex>
    <v-layout row wrap>
     <v-flex xs12 sm12 md12 >
       <v-flex v-for="transfoItem in listTransformation">
                <v-btn color="blue-grey lighten-3" small>{{transfoItem.typeTransformation}}</v-btn>
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
           listTransformation: [],
           processTransformation: {
                                    "parameterTransformation":{
                                      "composeField": {"key":"","value":""},
                                      "formatDateValue": {"keyField":"","srcFormat":"","targetFormat":""},
                                      "keyField": "",
                                      "mapLookup": {},
                                      "externalHTTPData": {"url":"http://url:port","refresh":"10","httpMethod":"GET","body":""}
                                    }
                                  },
           idProcess: '',
           methodCall: ["GET","POST"],
           type : ["ADD_FIELD","DELETE_FIELD","RENAME_FIELD","FORMAT_DATE","FORMAT_BOOLEAN","FORMAT_GEOPOINT","FORMAT_DOUBLE","FORMAT_LONG","FORMAT_IP", "LOOKUP_LIST","LOOKUP_EXTERNAL"],
           msgError: '',
           viewError: false,
           messageClientCreated : '',
           viewMessageClient : false,
           viewKeyField: false,
           viewComposeField: false,
           viewDateField: false,
           viewLookupList: false,
           viewLookupExternal: false,
           mapLookup: '',
           mandatory: '',
           bcList: [{text: 'Name',disabled: true},
                   {text: 'Input',disabled: true},
                   {text: 'Parser',disabled: true},
                   {text: 'Transformation',disabled: false},
                   {text: 'Validation',disabled: true},
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
             this.$router.push('/process/add/validation?idProcess='+this.idProcess);
        },
        addTransformation(){
          if(this.viewLookupList){
            //parse mapLookup
            var tabItem = this.mapLookup.split("\n");
            var result={};
            for(var i=0; i<tabItem.length; i++){
                var itemLookup=tabItem[i].split(';');
                result[itemLookup[0]]=itemLookup[1];
            }
            this.processTransformation.parameterTransformation.mapLookup=result;
          }
          this.listTransformation.push({"typeTransformation":this.processTransformation.typeTransformation});
          this.$http.post('/process/createProcessTransformation', {idProcess: this.idProcess, processTransformation: this.processTransformation}).then(response => {
            this.messageClientCreated= " Transformation added ";
            this.viewMessageClient = true;
          }, response => {
             this.viewError=true;
             this.msgError = "Error during call service";
          });
        },
        disable(){
           this.viewKeyField=false;
           this.viewComposeField=false;
           this.viewDateField=false;
           this.viewLookupList=false;
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
