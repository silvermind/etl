<template>
 <v-container fluid grid-list-md>
    <v-layout row wrap>
      <v-flex xs12 sm12 md12>
           <v-text-field label="Search on key" prepend-icon="search" hide-details single-line v-on:keyup.enter="searchKey" v-model="textSearch"></v-text-field>
          <v-flex v-for="grokItem in listGrok">
             <v-flex>
                <v-subheader>Key</v-subheader> {{grokItem.keyPattern}}
             </v-flex>
             <v-flex>
               <v-text-field name="Preview" label="Value" textarea dark v-model="grokItem.valuePattern"></v-text-field>
             </v-flex>
         </v-flex>
      </v-flex>
    </v-layout row wrap>
    <v-layout row wrap>
      <v-flex xs12 sm12 md12 >
        <v-alert v-model="viewError" xs12 sm12 md12  color="error" icon="warning" value="true" dismissible>
             {{ msgError }}
        </v-alert>
       </v-flex>
     </v-layout row wrap>
 </v-container fluid grid-list-md>
</template>


<script>
  export default{
    data () {
         return {
           textSearch: '',
           listGrok: [],
           msgError: '',
           viewError: false,
           headers: [
             { text: 'Key',align:'center',value: 'keyPattern'},
             { text: 'Value',align:'center',value: 'valuePattern'}
           ]
         }
    },
    mounted() {
       this.$http.get('/admin/grok/find').then(response => {
            this.listGrok=response.data;
         }, response => {
           this.viewError=true;
           this.msgError = "Error during call service";
         });
    },
    methods: {
      searchKey(value){
        this.$http.get('/admin/grok/find',{params : {filter : this.textSearch}}).then(response => {
            this.listGrok=response.data;
         }, response => {
           this.viewError=true;
           this.msgError = "Error during call service";
         });
      }
    }
  }
</script>
