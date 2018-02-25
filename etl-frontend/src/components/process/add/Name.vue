<template>
  <v-container fluid grid-list-md >
  <v-breadcrumbs>
    <v-icon slot="divider">forward</v-icon>
    <v-breadcrumbs-item v-for="item in bcList" :key="item.text" :disabled="item.disabled">{{ item.text }}</v-breadcrumbs-item>
  </v-breadcrumbs>
   <v-flex xs3 sm3 md3>
      <v-text-field label="Name of your process" v-model="process.name"></v-text-field>
   </v-flex>
   <v-flex>
         <v-flex xs12 sm6 md4>
         </p>
          <v-btn color="success" v-on:click.native="createProcess">Next</v-btn>
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


<script>
  export default{
   data () {
         return {
           idProcess: '',
           process: '',
           msgError: '',
           viewError: false,
           viewOut: false,
           process: {"name":""},
           typeOut: ["KAFKA","SYSTEM_OUT","ELASTICSEARCH"],
           bcList: [{text: 'Name',disabled: false},
                   {text: 'Input',disabled: true},
                   {text: 'Parser',disabled: true},
                   {text: 'Transformation',disabled: true},
                   {text: 'Validation',disabled: true},
                   {text: 'Filter',disabled: true},
                   {text: 'Output',disabled: true},
                   {text: 'Finish',disabled: false }
                  ],
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
        createProcess(){
          this.$http.get('/process/addName', {params: {name: this.process.name,idProcess: this.idProcess}} ).then(response => {

             this.$router.push('/process/add/input?idProcess='+this.idProcess);
          }, response => {
             this.viewError=true;
             this.msgError = "Error during call service";
          });
        }
    }
  }
</script>
