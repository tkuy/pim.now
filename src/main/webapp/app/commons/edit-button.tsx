import {Translate} from "react-jhipster";
import {Button} from "@material-ui/core";
import React from "react";
import EditIcon from '@material-ui/icons/Edit';

interface Props {
  content: string
}
export const EditButton = (props:Props) => {
  return (
    <Button style={{margin: '1em'}} variant="contained" size="medium" color="secondary">
      <EditIcon/>
      &nbsp;
      <Translate contentKey={props.content}/>
    </Button>
  )
};
