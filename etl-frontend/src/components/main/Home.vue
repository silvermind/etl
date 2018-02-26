<template>
  <v-container fluid grid-list-md >
     <v-layout row style="cardSkalogs">
         <v-flex xs6 sm6 md6  class="pa-3">
           <v-card color="blue-grey darken-2" class="white--text" style="height : 450px;">
             <v-container fluid grid-list-lg>
                <v-layout row>
                 <div class="headline">Process Consumer</div>
                </v-layout>
                <v-layout row>
                    <v-flex xs1 sm1 md1>
                        <v-icon x-large dark>cached</v-icon>
                    </v-flex>
                    <v-flex xs1 sm1 md1 class="text-md-right title">
                         Active  </p>
                         Inactive </p>
                         Error  </p>
                    </v-flex>
                    <v-flex xs1 sm1 md1>
                         {{home.numberProcessActive}} </p>
                         {{home.numberProcessDeActive}} </p>
                         {{home.numberProcessError }}</p>
                    </v-flex>
                    <v-flex xs9 sm9 md9>
                      <div style="height: 100px">
                          <line-chart :chart-data="dataCharts.dataProcess" :options="optionsGlobal"></line-chart>
                      </div>
                    </v-flex>
                </v-layout>
             </v-container>
             <v-card-actions>
                 <v-btn flat color="orange" v-on:click.native="dialogProcess=true">View</v-btn>
             </v-card-actions>
           </v-card>
         </v-flex>
         <v-flex xs6 sm6 md6 class="pa-3">
           <v-card color="light-blue darken-2" class="white--text" style="height : 450px;">
             <v-container fluid grid-list-lg>
                <v-layout row>
                 <div class="headline">Worker</div>
                </v-layout>
                <v-layout row>
                    <v-flex xs1 sm1 md1>
                        <v-icon x-large dark>near_me</v-icon>
                    </v-flex>
                    <v-flex xs1 sm1 md1 class="text-md-right title">
                         Process</p>
                         Metric</p>
                         Ref.</p>
                    </v-flex>
                    <v-flex xs1 sm1 md1>
                         {{home.numberWorkerProcess}} </p>
                         {{home.numberWorkerMetric}} </p>
                         {{home.numberWorkerReferential }}</p>
                    </v-flex>
                    <v-flex xs9 sm9 md9>
                      <div style="height: 100px">
                          <line-chart :chart-data="dataCharts.dataWorker" :options="optionsGlobal"></line-chart>
                      </div>
                    </v-flex>
                </v-layout>
             </v-container>
             <v-card-actions>
                 <v-btn flat color="orange" v-on:click.native="dialogWorker=true">View</v-btn>
             </v-card-actions>
           </v-card>
         </v-flex>
     </v-layout>

     <v-layout row style="cardSkalogs">
         <v-flex xs6 sm6 md6 class="pa-3">
           <v-card color="deep-orange darken-2" class="white--text" style="height : 450px;">
             <v-container fluid grid-list-lg>
                <v-layout row>
                 <div class="headline">Process Metric</div>
                </v-layout>
                <v-layout row>
                    <v-flex xs1 sm1 md1>
                        <v-icon x-large dark>widgets</v-icon>
                    </v-flex>
                    <v-flex xs1 sm1 md1 class="text-md-right title">
                         Active  </p>
                         Inactive </p>
                         Error  </p>
                    </v-flex>
                    <v-flex xs1 sm1 md1>
                         {{home.numberMetricActive}} </p>
                         {{home.numberMetricDeActive}} </p>
                         {{home.numberMetricError }}</p>
                    </v-flex>
                    <v-flex xs9 sm9 md9>
                      <div style="height: 100px">
                          <line-chart :chart-data="dataCharts.dataMetric" :options="optionsGlobal"></line-chart>
                      </div>
                    </v-flex>
                </v-layout>
             </v-container>
             <v-card-actions>
                 <v-btn flat color="orange"  v-on:click.native="dialogMetric=true">View</v-btn>
             </v-card-actions>
           </v-card>
         </v-flex>
         <v-flex xs6 sm6 md6 class="pa-3">
           <v-card color="teal darken-2" class="white--text" style="height : 450px;">
             <v-container fluid grid-list-lg>
                <v-layout row>
                 <div class="headline">Configuration</div>
                </v-layout>
                <v-layout row>
                    <v-flex xs1 sm1 md1>
                        <v-icon x-large dark>extension</v-icon>
                    </v-flex>
                    <v-flex xs1 sm1 md1 class="text-md-right title">
                         Active  </p>
                         Inactive </p>
                         Error  </p>
                    </v-flex>
                    <v-flex xs1 sm1 md1>
                         {{home.numberConfigurationActive}} </p>
                         {{home.numberConfigurationDeActive}} </p>
                         {{home.numberConfigurationError }}</p>
                    </v-flex>
                    <v-flex xs9 sm9 md9>
                      <div style="height: 100px">
                          <line-chart :chart-data="dataCharts.dataConfiguration" :options="optionsGlobal"></line-chart>
                      </div>
                    </v-flex>
                </v-layout>
             </v-container>
             <v-card-actions>
                 <v-btn flat color="orange" v-on:click.native="dialogConfiguration=true">View</v-btn>
             </v-card-actions>
           </v-card>
         </v-flex>
     </v-layout>
    <v-dialog v-model="dialogMetric" fullscreen transition="dialog-bottom-transition" :overlay="false" >
       <v-card tile>
          <v-data-table v-bind:headers="headersMetric" :items="home.listStatMetric" hide-actions dark >
             <template slot="items" slot-scope="props">
               <td>{{props.item.name}}</td>
               <td>{{props.item.status}}</td>
               <td>{{props.item.nbMetric}}</td>
             </template>
          </v-data-table>
         <v-card-actions>
             <v-btn color="primary" flat @click.stop="dialogMetric=false">Close</v-btn>
         </v-card-actions>
       </v-card>
     </v-dialog>
     <v-dialog v-model="dialogConfiguration"  fullscreen transition="dialog-bottom-transition" :overlay="false" >
      <v-card tile>
           <v-data-table v-bind:headers="headersConfiguration" :items="home.listStatConfiguration" hide-actions dark >
              <template slot="items" slot-scope="props">
                <td>{{props.item.name}}</td>
                <td>{{props.item.status}}</td>
              </template>
           </v-data-table>
         <v-card-actions>
             <v-btn color="primary" flat @click.stop="dialogConfiguration=false">Close</v-btn>
         </v-card-actions>
      </v-card>
    </v-dialog>
    <v-dialog v-model="dialogWorker" fullscreen transition="dialog-bottom-transition" :overlay="false" >
      <v-card tile>
         <v-data-table v-bind:headers="headersWorker" :items="home.listStatWorker" hide-actions dark >
            <template slot="items" slot-scope="props">
              <td>{{props.item.name}}</td>
              <td>{{props.item.ip}}</td>
              <td>{{props.item.nbProcess}}</td>
              <td>{{props.item.type}}</td>
            </template>
         </v-data-table>
         <v-card-actions>
             <v-btn color="primary" flat @click.stop="dialogWorker=false">Close</v-btn>
         </v-card-actions>
      </v-card>
    </v-dialog>
    <v-dialog v-model="dialogProcess" fullscreen transition="dialog-bottom-transition" :overlay="false" >
      <v-card tile>
         <v-data-table v-bind:headers="headersProcess" :items="home.listStatProcess" hide-actions dark >
            <template slot="items" slot-scope="props">
              <td>{{props.item.name}}</td>
              <td>{{props.item.status}}</td>
              <td>{{props.item.nbRead}}</td>
              <td>{{props.item.nbOutput}}</td>
            </template>
         </v-data-table>
         <v-card-actions>
             <v-btn color="primary" flat @click.stop="dialogProcess=false">Close</v-btn>
         </v-card-actions>
      </v-card>
    </v-dialog>
     <v-layout row >
        <v-flex xs12 sm12 md12 >
          <v-alert v-model="viewError" xs12 sm12 md12  color="error" icon="warning" value="true" dismissible>
               {{ msgError }}
          </v-alert>
        </v-flex>
     </v-layout>

  </v-container>
</template>
<style>
  .cardSkalogs {
    height : 450px; padding-top:10px;padding-bottom:10px;padding-right:10px;padding-left:10px;
  }
</style>
<script>
  import LineChart from './LineChart.js'
  export default{
    components: {
      LineChart
    },
    data () {
         return {
           dialogProcess : false,
           dialogWorker : false,
           dialogConfiguration : false,
           dialogMetric : false,
           viewError : false,
           msgError : '',
           home : '',
           headersProcess : [
             { text : 'Name',align : 'center',value : 'name'},
             { text : 'Status',align : 'center',value : 'status'},
             { text : 'Nb Read',align : 'center',value : 'nbRead'},
             { text : 'Nb Treat',align : 'center',value : 'nbOutput'}
           ],
           headersWorker : [
             { text : 'Name',align : 'center',value : 'name'},
             { text : 'Ip',align : 'center',value : 'ip'},
             { text : 'Nb Process',align : 'center',value : 'nbProcess'},
             { text : 'Type',align : 'center',value : 'type'}
           ],
           headersMetric : [
             { text : 'Name',align : 'center',value : 'name'},
             { text : 'Status',align : 'center',value : 'status'},
             { text : 'Nb Metric',align : 'center',value : 'nbMetric'}
           ],
           headersConfiguration : [
             { text : 'Name',align : 'center',value : 'name'},
             { text : 'Status',align : 'center',value : 'status'}
           ],
           optionsGlobal: {responsive: true,maintainAspectRatio: false,
                                   legend: {
                                       position: 'bottom',
                                       labels: {fontColor: "white",
                                                fontSize: 12
                                               }
                                   },
                                   hover: {
                                       mode: 'label'
                                   },
                                   scales: {
                                       xAxes: [{
                                               display: true,
                                               type: 'linear',
                                               scaleLabel: {
                                                   display: true,
                                                   fontStyle: 'bold'
                                               },ticks: {fontColor: "#CCC"}
                                           }],
                                       yAxes: [{
                                               display: true,
                                               ticks: {
                                                   beginAtZero: true,
                                                   steps: 10,
                                                   stepValue: 5,
                                                   fontColor: "#CCC"
                                               }
                                           }]
                                   }
                                },
           dataCharts: {"dataProcess": '',"dataMetric": '',"dataWorker": '',"dataConfiguration" :''}
         }
    },
    mounted() {
         this.$http.get('/home/fetch').then(response => {
           this.home=response.data;
         }, response => {
           this.viewError=true;
           this.msgError = "Error during call service";
         });
         this.$http.get('/home/dataCapture').then(response => {
           this.dataCharts=response.data;
         }, response => {
           this.viewError=true;
           this.msgError = "Error during call service";
         });
    },
    methods : {

    }
  }
</script>
