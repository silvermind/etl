<template>
  <v-container fluid grid-list-md >
   <v-flex xs3 sm3 md3>
      <v-text-field label="Name of your process" v-model="process.name"></v-text-field>
   </v-flex>
   <v-layout row class="borderbox" >
      <v-flex xs6 sm6 md6 >
           <p>Input Kafka</p>
           <v-flex xs8 sm8 md8>
              <v-layout row >
                 <v-text-field label="Host" v-model="process.processInput.host"></v-text-field>
                 <v-text-field label="Port" v-model="process.processInput.port"></v-text-field>
              </v-layout>
           </v-flex>
           <v-flex xs8 sm8 md8>
             <v-layout row >
                 <v-text-field label="Topic Input" v-model="process.processInput.topicInput"></v-text-field>
             </v-layout>
           </v-flex>
      </v-flex>
      <v-flex xs6 sm6 md6>
           <p>Output</p>
           <v-flex xs8 sm8 md8>
              <v-layout row >
                   <v-select label="Out" v-model="process.processOutput.typeOutput" v-bind:items="typeOut" v-on:change="actionOutView"/>
              </v-layout>
           </v-flex>
           <v-flex xs8 sm8 md8>
              <v-layout row v-show="viewOut">
                   <v-text-field label="Topic Out" v-model="process.processOutput.parameterOutput.topicOut"></v-text-field>
              </v-layout>
           </v-flex>
           <v-flex xs8 sm8 md8>
             <v-layout row v-show="viewOutES">
                  <v-select label="Retention" v-model="process.processOutput.parameterOutput.elasticsearchRetentionLevel" v-bind:items="typeRetention"/>
             </v-layout>
             </p></p>
           </v-flex>
      </v-flex>
   </v-layout>
   <v-layout row class="borderbox">
       <v-flex xs6 sm6 md6>
            <p>Parser</p>
             <v-chip color="blue-grey lighten-3" small v-on:click.native="editParser(process.processParser.id)" v-if="process.processParser.typeParser">{{process.processParser.typeParser}}</v-chip></p>
             <v-btn color="primary" v-on:click.native="addParser" >add Parser</v-btn>
             </p></p></p>
       </v-flex>
       <v-flex xs6 sm6 md6>
             <p>Transformation</p>
                <v-flex xs2 sm2 md2 v-for="transfoItem in process.processTransformation">
                 <v-chip color="deep-orange lighten-3" small v-on:click.native="editTransformation(transfoItem.id)">{{formatTransformation(transfoItem)}}</v-chip>
               </v-flex>
                <v-btn color="primary" v-on:click.native="addTransformation">add transformation</v-btn>
             </p></p></p>
        </v-flex>
   </v-layout>

   <v-layout row class="borderbox">
       <v-flex xs6 sm6 md6>
            <p>Validation</p>
             <v-flex xs1 sm1 md1 v-for="validItem in process.processValidation">
                   <v-chip color="blue-grey lighten-3" small v-on:click.native="editValidation(validItem.id)">{{formatValidation(validItem)}}</v-chip>
             </v-flex>
             <v-btn color="primary" v-on:click.native="addValidation">add Validation</v-btn>
             </p></p></p>
       </v-flex>
       <v-flex xs6 sm6 md6>
             <p>Filter</p>
             <v-flex xs2 sm2 md2 v-for="filteritem in process.processFilter">
                  <v-chip color="deep-orange lighten-3" small v-on:click.native="editFilter(filteritem.idFilter)">{{filteritem.name}}</v-chip>
             </v-flex>
             <v-btn color="primary" v-on:click.native="addFilter">add Filter</v-btn>
             </p></p></p>
        </v-flex>
   </v-layout>

   </v-flex>
   <v-flex>
         <v-flex xs12 sm6 md4>
         </p>
          <v-btn color="success" v-on:click.native="createProcess">Update Config</v-btn>
          <v-btn color="error" v-on:click.native="deleteProcess">Delete Config</v-btn>
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

<style scoped>
.borderbox {
   border-bottom-width: 1px;
   border-bottom-color: grey;
   border-bottom-style: solid;
   padding: 10px 2px 2px 10px;
}
</style>
<script>
  export default{
   data () {
         return {
           idProcess: '',
           process: '',
           msgError: '',
           viewError: false,
           viewOut: false,
           viewOutES: false,
           process: {"name":"",processInput: {"name":"","host":"","port":"","topic":""},processOutput: {"typeOutput":"KAFKA","parameterOutput": {"topicOut":"","elasticsearchRetentionLevel":""}},validation: [], processParser :{},processFilter:[]},
           typeOut: ["KAFKA","SYSTEM_OUT","ELASTICSEARCH"],
           typeRetention: ["week","month","quarter","year"]
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
        actionOutView(value){
            if(value == "KAFKA"){
              this.viewOut=true;
              this.viewOutES=false;
            }else if(value == "ELASTICSEARCH"){
              this.viewOut=false;
              this.viewOutES=true;
            }else{
              this.viewOut=false;
              this.viewOutES=false;
            }
        },
        editFilter(id){
          this.$router.push('/process/edit/filter?idProcess='+this.idProcess+'&idProcessFilter='+id);
        },
        editTransformation(id){
          this.$router.push('/process/edit/transformation?idProcess='+this.idProcess+'&id='+id);
        },
        editParser(id){
          this.$router.push('/process/edit/parser?idProcess='+this.idProcess+'&id='+id);
        },
        editValidation(id){
          this.$router.push('/process/edit/validation?idProcess='+this.idProcess+'&idProcessValidation='+id);
        },
        addValidation(){
          this.$router.push('/process/edit/validation?idProcess='+this.idProcess);
        },
        addParser(){
          this.$router.push('/process/edit/parser?idProcess='+this.idProcess);
        },
        addTransformation(){
          this.$router.push('/process/edit/transformation?idProcess='+this.idProcess);
        },
        addFilter(){
          this.$router.push('/process/edit/filter?idProcess='+this.idProcess);
        },
        createProcess(){
          this.$http.post('/process/createProcess', this.process).then(response => {
             this.$router.push('/process/list');
          }, response => {
             this.viewError=true;
             this.msgError = "Error during call service";
          });
        },
        deleteProcess(){
          this.$http.post('/process/removeProcess', this.process).then(response => {
             this.$router.push('/idProcess/list');
          }, response => {
             this.viewError=true;
             this.msgError = "Error during call service";
          });
        },
        formatValidation(validationitem) {
          switch (validationitem.typeValidation) {
            case "MANDATORY_FIELD":
              return validationitem.typeValidation + " " + validationitem.parameterValidation.mandatory;
            case "BLACK_LIST_FIELD":
              return validationitem.typeValidation + " " + validationitem.parameterValidation.blackList.map(element => element.key).join(", ");
            case "MAX_FIELD":
              return validationitem.typeValidation + " " + validationitem.parameterValidation.maxFields;
            case "MAX_MESSAGE_SIZE":
              return validationitem.typeValidation + " " + validationitem.parameterValidation.maxMessageSize;
            case "FORMAT_DATE":
              return validationitem.typeValidation;
            case "FIELD_EXIST":
              return validationitem.typeValidation + " " + validationitem.parameterValidation.fieldExist;
          }
        },
        formatTransformation(transformationItem) {
            if(transformationItem.typeTransformation == "ADD_FIELD" || transformationItem.typeTransformation == "RENAME_FIELD" ){
              return transformationItem.typeTransformation + " " + transformationItem.parameterTransformation.composeField.key;
            }else{
               return transformationItem.typeTransformation + " " + transformationItem.parameterTransformation.keyField;
            }
        }
    }
  }
</script>
