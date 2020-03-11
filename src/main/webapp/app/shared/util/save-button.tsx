import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {Translate} from "react-jhipster";
import Button from "@material-ui/core/Button";
import React from "react";

interface Props {
  classes,
  disabled: boolean
}
export const SaveButton = (props: Props) => {
  return(
    <Button
      type="submit"
      variant="contained"
      color="primary"
      disabled={props.disabled}
      className={props.classes.submit}>
      <FontAwesomeIcon icon="save"/>
      &nbsp;
      <Translate contentKey="entity.action.save"/>
    </Button>
  )
}
