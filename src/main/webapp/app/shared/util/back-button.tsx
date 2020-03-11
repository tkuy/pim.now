import {Link} from "react-router-dom";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {Translate} from "react-jhipster";
import Button from "@material-ui/core/Button";
import React from "react";

interface Props {
  classes,
  to:string
}
export const BackButton = (props: Props) => {
  return (
    <Button
      component={Link}
      to={props.to}
      color="secondary"
      variant="contained"
      className={props.classes.submit}
    >
      <FontAwesomeIcon icon="arrow-left"/>
      &nbsp;
      <Translate contentKey="entity.action.back">Back</Translate>
    </Button>
  )
}
