<template>
  <v-container fluid grid-list-md >
  <v-breadcrumbs>
    <v-icon slot="divider">forward</v-icon>
    <v-breadcrumbs-item v-for="item in bcList" :key="item.text" :disabled="item.disabled">{{ item.text }}</v-breadcrumbs-item>
  </v-breadcrumbs>
   <v-flex>
       <v-flex xs12 sm6 md6>
             <v-text-field label="Name" v-model="processFilter.name"></v-text-field>
             <v-text-field label="criteria (SQL Like)" v-model="processFilter.criteria"></v-text-field>
          </p></p>
        </v-flex>
   </v-flex>
   <v-flex>
      <v-flex xs12 sm6 md6>
         <v-layout row wrap>
                  <v-btn color="primary" v-on:click.native="addFilter">add filter</v-btn>
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
                   {text: 'Parser',disabled: true},
                   {text: 'Transformation',disabled: true},
                   {text: 'Validation',disabled: true},
                   {text: 'Filter',disabled: false},
                   {text: 'Output',disabled: true},
                   {text: 'Finish',disabled: false }
                  ],
           processFilter: {"criteria":"","name":""},
           idProcess: '',
           msgError: '',
           viewError: false,
           messageClientCreated : '',
           viewMessageClient : false,
         }
   },
   mounted() {
            this.idProcess = this.$route.query.idProcess;
   },
   methods: {
        addFilter(){
          this.$http.post('/process/createProcessFilter', {idProcess: this.idProcess, processFilter: this.processFilter}).then(response => {
             this.messageClientCreated= " Transformation added ";
             this.viewMessageClient = true;
          }, response => {
             this.viewError=true;
             this.msgError = "Error during call service";
          });
        },
        nextStep(){
            this.$router.push('/process/add/output?idProcess='+this.idProcess);
        }
    }
  }
</script>
