<template>
  <v-container fluid grid-list-md >
   <v-layout row wrap>
        <v-flex xs12 sm12 md12>
         <v-btn color="primary" v-on:click.native="generateLogstash">Generate Configuration Logstash</v-btn>
         <v-text-field name="logstash" label="Value" textarea dark v-model="fluxLogstash"></v-text-field>
        </v-flex>
     </v-layout>
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
           idConfiguration : '',
           msgError: '',
           viewError: false,
           fluxLogstash: ''
         }
   },
   mounted() {
            this.idConfiguration = this.$route.query.idConfiguration;
   },
   methods: {
        generateLogstash(){
          this.$http.get('/configuration/generate', {params: {idConfiguration: this.idConfiguration}}).then(response => {
              this.fluxLogstash=response.bodyText;
           }, response => {
               this.viewError=true;
             this.msgError = "Error during call service";
           });

      }
    }
  }
</script>
