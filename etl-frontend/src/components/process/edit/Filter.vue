<template>
  <v-container fluid grid-list-md >
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
                  <v-btn color="primary" v-on:click.native="editFilter">Edit Filter</v-btn>
                  <v-btn color="error" v-on:click.native="removeFilter">Remove Filter</v-btn>
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
           processFilter: {"criteria":"","name":""},
           idProcess: '',
           msgError: '',
           viewError: false
         }
   },
   mounted() {
            this.idProcess = this.$route.query.idProcess;
            this.idProcessFilter = this.$route.query.idProcessFilter;
            if ( this.idProcessFilter !=null){
               this.$http.get('/process/findProcessFilter', {params: {idProcess: this.idProcess, idFilter: this.idProcessFilter}}).then(response => {
                  this.processFilter=response.data;
               }, response => {
                  this.viewError=true;
                  this.msgError = "Error during call service";
               });
             }
   },
   methods: {
        removeFilter(){
            this.$http.get('/process/removeProcessFilter', {params: {idProcess: this.idProcess, idFilter: this.idProcessFilter}}).then(response => {
                 this.$router.push('/process/add/process?idProcess='+this.idProcess);
             }, response => {
                this.viewError=true;
                this.msgError = "Error during call service";
             });
        },
        editFilter(){
          this.$http.post('/process/createProcessFilter', {idProcess: this.idProcess, processFilter: this.processFilter}).then(response => {
             this.$router.push('/process/add/process?idProcess='+this.idProcess);
          }, response => {
             this.viewError=true;
             this.msgError = "Error during call service";
          });
        }
    }
  }
</script>
