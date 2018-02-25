<template>
  <v-container fluid grid-list-md >

      <v-layout row>
        <v-flex xs6 sm6 md6>
            <v-select label="Grok List Available" v-model="grokSelect" v-bind:items="listGrok" item-text="keyPattern" item-value="keyPattern" max-height="800" autocomplete v-on:change="viewPattern"  :hint="`${grokSelect.valuePattern}`" persistent-hint return-object/>
            <v-text-field name="My Pattern" label="My Pattern" dark v-model="patternChoice"></v-text-field>
            <v-layout row >
                <v-btn color="primary" v-on:click.native="captureData()">Launch Simulation</v-btn>
                <v-btn color="warning" v-on:click.native="testAllPattern()">Test All Pattern</v-btn>
            </v-layout>
        </v-flex>

      </v-layout>

      <v-layout row >
            <v-text-field name="Raw Data" label="Raw Data" textarea dark v-model="textRawData"></v-text-field>
      </v-layout>
      <v-flex v-for="itemCapture in listCapture" >
               <v-layout row class="pb-3 mb-3 borderbox">
                   <v-flex xs1 sm1 md1>
                       <v-subheader>Status Treatment</v-subheader>
                       <v-subheader>Raw Data</v-subheader>
                       <v-subheader>Transformed</v-subheader>
                   </v-flex>
                   <v-flex xs11 sm11 md11>
                       <v-subheader>{{itemCapture.message}}</v-subheader>
                       <v-subheader>{{itemCapture.value}}</v-subheader>
                       <v-subheader>{{itemCapture.jsonValue}}</v-subheader>
                   </v-flex>
               </v-layout>
      </v-flex>
      <v-layout row wrap>
        <v-flex xs12 sm12 md12 >
          <v-alert v-model="viewError" xs12 sm12 md12  color="error" icon="warning" value="true" dismissible>
               {{ msgError }}
          </v-alert>
          <v-alert v-model="viewMessageCapture" xs12 sm12 md12  color="info" icon="info" value="true" dismissible>
              {{ messageCapture }}
          </v-alert>
        </v-flex>
      </v-layout>
      <v-dialog v-model="dialogAllPattern">
        <v-card>
          <v-card-title>
              <v-text-field name="One line" label="One line log" textarea dark v-model="textSingleData"></v-text-field>
          </v-card-title>
          <v-card-text>
              <v-btn color="primary" v-on:click.native="captureDataAllPattern()">Test All Pattern</v-btn>
              <v-btn color="success" v-on:click.native="resetDataAllPattern()">Reset</v-btn>
          </v-card-text>
          <v-data-table v-bind:headers="headers" :items="listCaptureAllPattern" hide-actions >
              <template slot="items" slot-scope="props">
                <td class="text-md-center">{{props.item.pattern}}</td>
                <td class="text-md-left">{{props.item.value}}</td>
              </template>
          </v-data-table>
          <v-card-actions>
              <v-btn color="primary" flat @click.stop="dialogAllPattern=false">Close</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
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
        patternChoice: "",
        listGrok: [],
        listCapture: [],
        listCaptureAllPattern: [],
        grokSelect: '',
        textRawData: '',
        textSingleData: '',
        viewError: false,
        dialogAllPattern: false,
        msgError: '',
        viewMessageCapture: false,
        messageCapture: '',
        headers: [
                   { text: 'Key',align: 'center',value: 'key', width: '15%'},
                   { text: 'Value', align: 'center',value: 'value',width: '85%' }
                 ]
      }
   },
   mounted() {
      this.$http.get('/admin/grok/find', {params: {filter: ''}}).then(response => {
         this.listGrok = response.data;
      }, response => {
         this.viewError=true;
         this.msgError = "Error during call service";
      });
   },
   methods: {
      viewPattern(item){
        if (item.keyPattern) {
          this.patternChoice = this.patternChoice + "%{" +item.keyPattern +"}";
        }
      },
      captureData(){
         this.$http.post('/admin/grok/simulate', {"grokPattern" : this.patternChoice, "valueList" : this.textRawData}).then(response => {
            this.listCapture = response.data;
         }, response => {
            this.viewError=true;
            this.msgError = "Error during call service";
         });
      },
      captureDataAllPattern(){
         this.$http.post('/admin/grok/simulateAllPattern', this.textSingleData).then(response => {
            this.listCaptureAllPattern = response.data;
         }, response => {
            this.viewError=true;
            this.msgError = "Error during call service";
         });
      },
      testAllPattern(){
        this.dialogAllPattern= true;
        this.listCaptureAllPattern= [];
      },
      resetDataAllPattern(){
        this.listCaptureAllPattern= [];
      }
    }
  }
</script>
