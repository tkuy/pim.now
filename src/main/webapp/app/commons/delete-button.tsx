import {Translate} from "react-jhipster";
import {Button} from "@material-ui/core";
import React from "react";
import DeleteIcon from '@material-ui/icons/Delete';
interface Props {
  content: string
}
export const DeleteButton = (props:Props) => {
  return (
    <Button style={{margin: '1em'}} variant="contained" size="medium" color="secondary">
      <DeleteIcon/>
      &nbsp;
      <Translate contentKey={props.content}/>
    </Button>
  )
};
