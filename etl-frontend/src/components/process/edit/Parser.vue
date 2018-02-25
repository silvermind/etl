<template>
  <v-container fluid grid-list-md >
   <v-flex xs8 sm8 md8>
      <v-layout row >
           <v-select label="Choose Parser" v-model="processParser.typeParser" v-bind:items="typeParser" v-on:change="actionGrokView"/>
      </v-layout>
           </p></p>
   </v-flex>
   <v-flex>
       <v-flex xs12 sm6 md6>
             <v-text-field label="Grok Pattern" v-show="viewGrok" v-model="processParser.grokPattern"></v-text-field>
             <v-text-field label="CSV (separated by ;)" v-show="viewCSV" v-model="processParser.schemaCSV"></v-text-field>
          </p></p>
        </v-flex>
   </v-flex>
   <v-flex>
      <v-flex xs12 sm6 md6>
         <v-layout row wrap>
                  <v-btn color="primary" v-on:click.native="editParser">Edit Parser</v-btn>
                  <v-btn color="error" v-on:click.native="removeParser">Remove Parser</v-btn>
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
           processParser: {"typeParser":"","grokPattern":""},
           idProcess: '',
           typeParser: ["CEF","NITRO","GROK","CSV"],
           viewGrok : false,
           viewCSV: false,
           msgError: '',
           viewError: false
         }
   },
   mounted() {
            this.idProcess = this.$route.query.idProcess;
            this.idProcessParser = this.$route.query.id;
            if ( this.idProcessParser !=null){
               this.$http.get('/process/findProcessParser', {params: {idProcess: this.idProcess, id: this.idProcessParser}}).then(response => {
                  this.processParser=response.data;
               }, response => {
                  this.viewError=true;
                  this.msgError = "Error during call service";
               });
             }
   },
   methods: {
        removeParser(){
            this.$http.get('/process/removeProcessParser', {params: {idProcess: this.idProcess, id: this.idProcessParser}}).then(response => {
                 this.$router.push('/process/add/process?idProcess='+this.idProcess);
             }, response => {
                this.viewError=true;
                this.msgError = "Error during call service";
             });
        },
        editParser(){
          this.$http.post('/process/createProcessParser', {idProcess: this.idProcess, processParser: this.processParser}).then(response => {
             this.$router.push('/process/add/process?idProcess='+this.idProcess);
          }, response => {
             this.viewError=true;
             this.msgError = "Error during call service";
          });
        },
        actionGrokView(value){
           if(value == "GROK"){
             this.viewGrok=true;
             this.viewCSV=false;
           }else if(value =="CSV"){
             this.viewCSV=true;
             this.viewGrok=false;
           }else{
             this.viewGrok=false;
             this.viewCSV=false;
           }
        }
    }
  }
</script>
