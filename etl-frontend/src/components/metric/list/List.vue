<template>
 <v-container fluid grid-list-md>
    <v-layout row wrap>
       <v-flex xs12 sm6 md4>
          <v-btn color="primary" v-on:click.native="newConfig">New Metric Consumer</v-btn>
          <v-btn flat  v-on:click.native="refreshAction" icon color="blue lighten-2">
                <v-icon>refresh</v-icon>
          </v-btn>
       </v-flex>
    </v-layout row wrap>
    <v-layout row wrap>
      <v-flex xs12 sm12 md12>
          <v-data-table v-bind:headers="headers" :items="listProcess" hide-actions dark >
            <template slot="items" slot-scope="props">
                <td>
                  <v-btn color="warning" small v-on:click.native="editProcess(props.item.id)">Edit</v-btn>
                  <v-btn color="teal" small v-if="props.item.statusProcess == 'ENABLE'" v-on:click.native="deactivateProcess(props.item.id)">Deactivate</v-btn>
                  <v-btn color="teal" small v-if="props.item.statusProcess == 'DISABLE'" v-on:click.native="activateProcess(props.item.id)">Activate</v-btn>
                  <v-btn color="teal" small v-if="props.item.statusProcess == 'INIT'">INITIALIZING</v-btn>
                  <v-btn color="red" small v-if="props.item.statusProcess == 'ERROR'">ERROR</v-btn>
                </td>
                <td class="text-xs-center">{{props.item.processDefinition.name}}</td>
                <td class="text-xs-center">
                  <v-chip label color="blue-grey lighten-3" small>{{props.item.processDefinition.aggFunction}}</v-chip>
                </td>
                <td class="text-xs-center">
                  <v-chip label color="purple lighten-2" small>{{props.item.processDefinition.fromTopic}}</v-chip>
                </td>
                <td class="text-xs-center">
                  <v-chip label color="purple lighten-2" small>{{windowFormat(props.item.processDefinition)}}</v-chip>
                </td>
                <td class="text-xs-center">
                  <v-chip label color="blue-grey lighten-3" small v-if="props.item.processDefinition.where">{{props.item.processDefinition.where}}</v-chip>
                </td>
                <td class="text-xs-center">
                  <v-chip label color="blue-grey lighten-3" small v-if="props.item.processDefinition.groupBy">{{props.item.processDefinition.groupBy}}</v-chip>
                </td>
                <td class="text-xs-center">
                  <v-chip label color="blue-grey lighten-3" small>{{destinationFormat(props.item.processDefinition)}}</v-chip>
                </td>
            </template>
          </v-data-table>
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
           listProcess: [],
           input: {},
           uiCreate: '',
           msgError: '',
           viewError: false,
           selectedToCheckBox : false,
           headers: [
             { text: 'Action',align: 'center',value: '', width: '4%'},
             { text: 'Name',align: 'center',value: 'name',width: '8%'},
             { text: 'Function',align: 'center', value: 'transformation', width: '16%' },
             { text: 'From', align: 'center',value: 'input',width: '8%' },
             { text: 'Window', align: 'center',value: 'input',width: '8%' },
             { text: 'Where',align: 'center', value: 'parser', width: '8%' },
             { text: 'Group By',align: 'center', value: 'transformation', width: '16%' },
             { text: 'To',align: 'center', value: 'validation', width: '16%' }
           ]
         }
    },
    mounted() {
        this.$http.get('/metric/listProcess').then(response => {
            this.listProcess=response.data;
            console.log(this.listProcess);
         }, response => {
           this.viewError=true;
           this.msgError = "Error during call service";
         });
    },
    methods: {
        refreshAction(){
          this.$http.get('/metric/listProcess').then(response => {
              this.listProcess=response.data;
          }, response => {
             this.viewError=true;
             this.msgError = "Error during call service";
          });
        },
        newConfig(){
          this.$router.push('/metric/add/name');
        },
        editProcess(idProcess){
          this.$router.push('/metric/add/name?idProcess=' + idProcess);
        },
        activateProcess(idProcess){
          this.$http.get('/metric/activate', {params: {idProcess: idProcess}}).then(response => {
             this.refreshAction();
          }, response => {
             this.viewError=true;
             this.msgError = "Error during call service";
          });
        },
        deactivateProcess(idProcess){
          this.$http.get('/metric/deactivate', {params: {idProcess: idProcess}}).then(response => {
             this.refreshAction();
          }, response => {
             this.viewError=true;
             this.msgError = "Error during call service";
          });

        },
        destinationFormat(processDefinition) {
          switch (processDefinition.typeOutput) {
            case "KAFKA":
              return processDefinition.typeOutput + "(" + processDefinition.toTopic + ")";
            case "ELASTICSEARCH":
              return processDefinition.typeOutput + "(" + processDefinition.retentionLevel + ")"
            default:
              return processDefinition.typeOutput;
          }
        },
        windowFormat(processDefinition) {
            if (processDefinition.windowType == 'HOPPING') {
              return processDefinition.windowType + "(" + processDefinition.size + " " +processDefinition.sizeUnit + ")";
            } else {
              return processDefinition.windowType + "(" + processDefinition.size + " " +processDefinition.sizeUnit + ", " +
                 processDefinition.advanceBy + " " +processDefinition.advanceByUnit +")";
            }
        }

    }
  }
</script>
