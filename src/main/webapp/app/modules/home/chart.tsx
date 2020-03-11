import {Card, CardContent, CardHeader, Divider} from "@material-ui/core";
import {Doughnut} from "react-chartjs-2";
import React from "react";

const options = {
  maintainAspectRatio: false,
  responsive: true,
  legend: {
    display: true,
    position: "left"
  }
};

export const Chart = (props) => {
  return (
    <Card>
      <CardHeader
        style={{backgroundColor:"#F9F9F9","height":"30px"}}
        subheader={props.title}
      />
      <Divider style={{width:"100%"}} />
      <CardContent style={{height:"120px"}}>
        <Doughnut
          options={options}
          data={{
            labels: [
              props.legend1,
              props.legend2,
            ],
            datasets: [{
              data: [props.value1, props.value2],
              backgroundColor: [
                '#73d5ce',
                '#245173',
              ],
              hoverBackgroundColor: [
                '#73d5ce',
                '#245173',
              ]
            }]
          }}/>
      </CardContent>
    </Card>
  )
} ;
