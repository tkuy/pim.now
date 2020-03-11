import {SaveButton} from "app/shared/util/save-button";
import {Grid} from "@material-ui/core";
import React from "react";
import {BackButton} from "app/shared/util/back-button";
import {makeStyles} from "@material-ui/core/styles";

interface Props {
  backUrl:string,
  saveButtonDisabled?: boolean
}
const useStyles = makeStyles(theme => ({
  submit: {
    margin: theme.spacing(3, 1, 2),
  }
}));
export const ButtonsAction = (props:Props) => {
  const classes = useStyles();
  return(
    <Grid container
               item
               justify="flex-end"
               direction="row">
      <BackButton classes={classes} to={props.backUrl}/>
      <SaveButton classes={classes} disabled={props.saveButtonDisabled}/>
  </Grid>
  )
}
