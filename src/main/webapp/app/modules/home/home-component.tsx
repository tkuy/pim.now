import {Card, CardContent, CardHeader, Divider, Typography} from "@material-ui/core";
import {Translate} from "react-jhipster";
import React from "react";
import Skeleton from "@material-ui/lab/Skeleton";

export const HomeComponent = (props) => {
  return (
    <Card>
      <CardHeader
        style={{backgroundColor:"#F9F9F9","height":"30px"}}
        subheader={<Translate contentKey={props.title}/>}
      />
      <Divider style={{width:"100%"}} />
      <CardContent style={{textAlign:"center",height:"120px"}}>
        {props.value !== null ?
          <Typography style={{marginTop:"60px", transform:"translateY(-100%)"}} variant={"h4"}>{props.value}</Typography>
          :
          <Skeleton variant={"rect"} height={40}/>
        }
      </CardContent>
    </Card>
  )
} ;
