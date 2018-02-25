<template>
  <v-container fluid grid-list-md >
  <v-breadcrumbs>
    <v-icon slot="divider">forward</v-icon>
    <v-breadcrumbs-item v-for="item in bcList" :key="item.text" :disabled="item.disabled">{{ item.text }}</v-breadcrumbs-item>
  </v-breadcrumbs>
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
                  <v-btn color="primary" v-on:click.native="addParser">add parser</v-btn>
                  <v-btn color="success" v-on:click.native="nextStep">Next</v-btn>
         </v-layout>
         </p></p>
       </v-flex>
   </v-flex>
   <v-layout row wrap>
     <v-flex xs12 sm12 md12 >
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
          bcList: [{text: 'Name',disabled: true},
                   {text: 'Input',disabled: true},
                   {text: 'Parser',disabled: false},
                   {text: 'Transformation',disabled: true},
                   {text: 'Validation',disabled: true},
                   {text: 'Filter',disabled: true},
                   {text: 'Output',disabled: true},
                   {text: 'Finish',disabled: false }
                  ],
           processParser: {"typeParser":"","grokPattern":""},
           typeParser: ["CEF","NITRO","GROK","CSV"],
           viewGrok : false,
           viewCSV: false,
           idProcess: '',
           msgError: '',
           viewError: false,
           viewMessageClient: false,
           messageClientCreated : '',
         }
   },
   mounted() {
            this.idProcess = this.$route.query.idProcess;
   },
   methods: {
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
        },
        addParser(){
          this.$http.post('/process/createProcessParser', {idProcess: this.idProcess, processParser: this.processParser}).then(response => {
            this.messageClientCreated= " Parser added ";
            this.viewMessageClient = true;
          }, response => {
             this.viewError=true;
             this.msgError = "Error during call service";
          });
        },
        nextStep(){
            this.$router.push('/process/add/transformation?idProcess='+this.idProcess);
        }
    }
  }
</script>
