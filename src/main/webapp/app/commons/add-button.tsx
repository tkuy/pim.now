import {Translate} from "react-jhipster";
import {Button} from "@material-ui/core";
import React from "react";
import AddIcon from '@material-ui/icons/Add';

interface Props {
  content: string
}
export const AddButton = (props:Props) => {
  return (
    <Button style={{margin: '1em'}} variant="contained" size="medium" color="primary">
      <AddIcon/>
      &nbsp;
      <Translate contentKey={props.content}/>
    </Button>
  )
};
