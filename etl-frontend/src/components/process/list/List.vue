<template>
 <v-container fluid grid-list-md>
    <v-layout row wrap>
       <v-flex xs12 sm6 md4>
          <v-btn color="primary" v-on:click.native="newConfig">New Consumer</v-btn>
          <v-btn flat  v-on:click.native="refreshAction" icon color="blue lighten-2">
                <v-icon>refresh</v-icon>
          </v-btn>
       </v-flex>
    </v-layout>
    <v-layout row wrap>
      <v-flex xs12 sm12 md12>
          <v-data-table v-bind:headers="headers" :items="listProcess" hide-actions dark >
            <template slot="items" slot-scope="props">
                <td>
                      <v-btn color="orange lighten-2" small v-on:click.native="editProcess(props.item.id)">Edit</v-btn>
                      <v-btn color="teal lighten-2" small v-on:click.native="nextProcess(props.item.id)">action</v-btn>
                      <v-btn flat color="purple" small v-if="props.item.statusProcess == 'ENABLE'" v-on:click.native="deactivateProcess(props.item.id)">Deactivate</v-btn>
                      <v-btn flat color="teal" small v-if="props.item.statusProcess == 'DISABLE' || props.item.statusProcess == 'INIT'" v-on:click.native="activateProcess(props.item.id)">Activate</v-btn>
                      <v-btn color="red" small v-if="props.item.statusProcess == 'ERROR'" >Error</v-btn>
                </td>
                <td class="text-xs-center subheading">{{props.item.processDefinition.name}}</td>
                <td class="text-xs-center">
                    <v-flex xs12>
                       <v-chip color="purple lighten-2" small>{{props.item.processDefinition.processInput.host}} {{props.item.processDefinition.processInput.port}} {{props.item.processDefinition.processInput.topic}}</v-chip>
                    </v-flex>
                </td>
                <td class="text-xs-center">
                    <v-chip color="blue-grey lighten-3" small v-if="props.item.processDefinition.processParser.typeParser">{{props.item.processDefinition.processParser.typeParser}}</v-chip>
                </td>
                <td class="text-xs-center">
                  <v-flex  class="pa-0 ma-0" xs12 sm12 md12 v-for="transformationitem in props.item.processDefinition.processTransformation">
                    <v-flex class="pa-0 ma-0">
                       <v-chip color="blue-grey lighten-3" small>{{formatTransformation(transformationitem)}}</v-chip>
                    </v-flex>
                  </v-flex>
                </td>
                <td class="text-xs-center">
                  <v-flex  class="pa-0 ma-0" xs12 sm12 md12 v-for="validationitem in props.item.processDefinition.processValidation">
                    <v-flex class="pa-0 ma-0">
                       <v-chip color="blue-grey lighten-3" small>{{formatValidation(validationitem)}}</v-chip>
                    </v-flex>
                  </v-flex>
                </td>

                <td class="text-xs-center">
                  <v-flex xs12 sm12 md12 v-for="filteritem in props.item.processDefinition.processFilter">
                    <v-flex xs10>
                       <v-chip color="deep-orange lighten-3" small>{{filteritem.name}}</v-chip>
                    </v-flex>
                  </v-flex>
                </td>
                <td class="text-xs-center">
                    <v-flex xs10>
                       <v-chip color="deep-orange lighten-3" small>{{props.item.processDefinition.processOutput.typeOutput}}</v-chip>
                    </v-flex>
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
           idProcess: '',
           input: {},
           uiCreate: '',
           msgError: '',
           viewError: false,
           selectedToCheckBox : false,
           headers: [
             { text: 'Action',align: 'center',value: '', width: '4%'},
             { text: 'Name',align: 'center',value: 'name',width: '8%'},
             { text: 'Input', align: 'center',value: 'input',width: '8%' },
             { text: 'Parser',align: 'center', value: 'parser', width: '8%' },
             { text: 'Transformation',align: 'center', value: 'transformation', width: '16%' },
             { text: 'Validation',align: 'center', value: 'validation', width: '16%' },
             { text: 'Filter',align: 'center', value: 'filter', width: '16%' },
             { text: 'Output', align: 'center',value: 'output',width: '8%' }
           ]
         }
    },
    mounted() {
        this.$http.get('/process/findAll').then(response => {
            this.listProcess=response.data;
         }, response => {
           this.viewError=true;
           this.msgError = "Error during call service";
         });
    },
    methods: {
        deactivateProcess(idProcess){
          this.$http.get('/process/deactivate', {params: {idProcess: idProcess}}).then(response => {
             this.refreshAction();
          }, response => {
             this.viewError=true;
             this.msgError = "Error during call service";
          });
        },
        activateProcess(idProcess){
          this.$http.get('/process/activate', {params: {idProcess: idProcess}}).then(response => {
             this.refreshAction();
          }, response => {
             this.viewError=true;
             this.msgError = "Error during call service";
          });
        },
        refreshAction(){
          this.$http.get('/process/findAll').then(response => {
              this.listProcess=response.data;
          }, response => {
             this.viewError=true;
             this.msgError = "Error during call service";
          });
        },
        nextProcess(idProcessSelect){
          this.$router.push('/process/action/view?idProcess='+idProcessSelect);
        },
        editProcess(idProcessSelect){
          this.$router.push('/process/add/process?idProcess='+idProcessSelect);
        },
        newConfig(){
          this.$http.post('/process/initProcess').then(response => {
              console.log(''+response);
              this.idProcess=response.bodyText;
              this.$router.push('/process/add/name?idProcess='+this.idProcess);
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
            case "FIELD_EXIST":
              return validationitem.typeValidation + " " + validationitem.parameterValidation.fieldExist;
            case "FORMAT_DATE":
              return validationitem.typeValidation;
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
