<template>
  <v-container fluid grid-list-md >



     <v-layout row style="height : 250px">
         <v-flex xs6 sm6 md6 class="pa-3">
           <v-card color="blue-grey darken-2" class="white--text">
             <v-container fluid grid-list-lg>
                <v-layout row>
                 <div class="headline">Process Consumer</div>
                </v-layout>
                <v-layout row>
                    <v-flex>
                        <v-icon x-large dark>cached</v-icon>
                    </v-flex>
                    <v-flex>
                         Active : {{home.numberProcessActive}} </p>
                         Inactive : {{home.numberProcessDeActive}} </p>
                         Error : {{home.numberProcessError }}</p>
                    </v-flex>
                </v-layout>
             </v-container>
             <v-card-actions>
                 <v-btn flat color="orange" v-on:click.native="dialogProcess=true">View</v-btn>
             </v-card-actions>
           </v-card>
         </v-flex>
         <v-flex xs6 sm6 md6 class="pa-3">
           <v-card color="light-blue darken-2" class="white--text">
             <v-container fluid grid-list-lg>
                <v-layout row>
                 <div class="headline">Worker</div>
                </v-layout>
                <v-layout row>
                    <v-flex>
                        <v-icon x-large dark>near_me</v-icon>
                    </v-flex>
                    <v-flex>
                         Process : {{home.numberWorkerProcess}} </p>
                         Metric : {{home.numberWorkerMetric}} </p>
                         Referential : {{home.numberWorkerReferential }} </p>
                    </v-flex>
                </v-layout>
             </v-container>
             <v-card-actions>
                 <v-btn flat color="orange" v-on:click.native="dialogWorker=true">View</v-btn>
             </v-card-actions>
           </v-card>
         </v-flex>
     </v-layout>

     <v-layout row style="height : 250px">
         <v-flex xs6 sm6 md6 class="pa-3">
           <v-card color="deep-orange darken-2" class="white--text">
             <v-container fluid grid-list-lg>
                <v-layout row>
                 <div class="headline">Process Metric</div>
                </v-layout>
                <v-layout row>
                    <v-flex>
                        <v-icon x-large dark>widgets</v-icon>
                    </v-flex>
                    <v-flex>
                         Active : {{home.numberMetricActive}} </p>
                         Inactive : {{home.numberMetricDeActive}} </p>
                         Error : {{home.numberMetricError }} </p>
                    </v-flex>
                </v-layout>
             </v-container>
             <v-card-actions>
                 <v-btn flat color="orange"  v-on:click.native="dialogMetric=true">View</v-btn>
             </v-card-actions>
           </v-card>
         </v-flex>
         <v-flex xs6 sm6 md6 class="pa-3">
           <v-card color="teal darken-2" class="white--text">
             <v-container fluid grid-list-lg>
                <v-layout row>
                 <div class="headline">Configuration</div>
                </v-layout>
                <v-layout row>
                    <v-flex>
                        <v-icon x-large dark>extension</v-icon>
                    </v-flex>
                    <v-flex>
                         Active : {{home.numberConfigurationActive}}</p>
                         Inactive : {{home.numberConfigurationDeActive}}</p>
                         Error : {{home.numberConfigurationError }}</p>
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



<script>
  export default{
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
         }
    },
    mounted() {
        this.$http.get('/home/fetch').then(response => {
            this.home=response.data;
         }, response => {
           this.viewError=true;
           this.msgError = "Error during call service";
         });
    },
    methods : {

    }
  }
</script>
