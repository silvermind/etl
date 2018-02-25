<template>
  <v-container fluid grid-list-md >
     <v-layout row wrap>
         <v-flex xs12 sm6 md4>
            <v-btn color="primary" v-on:click.native="addView">New Referential</v-btn>
            <v-btn flat  v-on:click.native="load" icon color="blue lighten-2">
                  <v-icon>refresh</v-icon>
            </v-btn>
         </v-flex>
     </v-layout>
     <v-data-table v-bind:headers="headers" :items="listReferential" hide-actions >
         <template slot="items" slot-scope="props">
           <td class="text-md-center">
                  <v-btn color="orange lighten-2" small  text-color="white" v-on:click.native="editReferential(props.item.id)">Edit</v-btn>
                  <v-btn color="purple lighten-2 lighten-2" small text-color="white" v-if="props.item.statusProcess == 'ACTIVE'" v-on:click.native="deactivateReferential(props.item.id)">deactive</v-btn>
                  <v-btn color="teal lighten-2 lighten-2" small text-color="white" v-if="props.item.statusProcess == 'INIT' || props.item.statusProcess == 'DISABLE'" v-on:click.native="activateReferential(props.item.id)">Active</v-btn>
                  <v-btn color="red" text-color="white" small v-on:click.native="deleteReferential(props.item.id)">Delete</v-btn>
           </td>
           <td class="text-md-center subheading">{{props.item.processDefinition.name}}</td>
           <td class="text-md-center subheading">{{props.item.processDefinition.referentialKey}}</td>
           <td class="text-md-center">
             <v-layout row>
                <v-flex v-for="item in props.item.processDefinition.listAssociatedKeys">
                   <v-chip color="blue-grey lighten-3" text-color="white">{{item}}</v-chip>
                </v-flex>
             </v-layout>
           </td>
           <td class="text-md-center">
             <v-layout row>
                <v-flex v-for="item in props.item.processDefinition.listMetadata">
                   <v-chip color="blue darken-2" text-color="white">{{item}}</v-chip>
                </v-flex>
             </v-layout>
            </td>
         </template>
     </v-data-table>
     <v-layout row wrap>
        <v-flex xs12 sm12 md12 >
          <v-alert v-model="viewError" xs12 sm12 md12  color="error" icon="warning" value="true" dismissible>
               {{ msgError }}
          </v-alert>
        </v-flex>
     </v-layout>
  </v-container>

</template>

<style scoped>
  .borderbox {
      border-style: solid;
      border-width: 1px;
  }
</style>

<script>
  export default{
   data () {
      return {
        idProcess: '',
        listReferential: [],
        newEntry: "",
        itemToEdit: {"idReferential":"","listAssociatedKeys":[],"name":"","referentialKey":"","listMetadata":[]},
        dialogReferential: false,
        viewError: false,
        msgError: '',
        headers: [
                   { text: 'Action', align: 'center',value: '',width: '10%' },
                   { text: 'Name', align: 'center',value: 'name',width: '10%' },
                   { text: 'Data Referential', align: 'center',value: 'valueKey',width: '10%' },
                   { text: 'Keys',align: 'center',value: 'listAssociatedKeys', width: '35%'},
                   { text: 'Metadata',align: 'center',value: 'listMetadata', width: '35%'}
                 ]
      }
   },
   mounted() {
      this.load();
   },
   methods: {
      deactivateReferential(idReferential){
        this.$http.get('/referential/deactivate', {params: {idReferential: idReferential}}).then(response => {
           this.load();
        }, response => {
           this.viewError=true;
           this.msgError = "Error during call service";
        });
      },
      activateReferential(idReferential){
        this.$http.get('/referential/activate', {params: {idReferential: idReferential}}).then(response => {
           this.load();
        }, response => {
           this.viewError=true;
           this.msgError = "Error during call service";
        });
      },
      editReferential(id){
        this.$router.push('/referential/action?idReferential='+id);
      },
      deleteReferential(id){
        this.$http.get('/referential/delete', {params: {idReferential: id}}).then(response => {
           this.listReferential = response.data;
        }, response => {
           this.viewError=true;
           this.msgError = "Error during call service";
        });
      },
      addView(){
         this.$router.push('/referential/action');
      },
      load(){
        this.$http.get('/referential/findAll').then(response => {
           this.listReferential = response.data;
        }, response => {
           this.viewError=true;
           this.msgError = "Error during call service";
        });
      }
   }
  }
</script>
