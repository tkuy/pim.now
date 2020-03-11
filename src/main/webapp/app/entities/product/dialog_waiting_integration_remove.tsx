import {Dialog, DialogContent, DialogTitle, LinearProgress, Typography} from "@material-ui/core";
import React from "react";
import {Translate} from "react-jhipster";
import {makeStyles} from "@material-ui/core/styles";

export const WaitingImportIntegrationRemove = () => {
  const [open, setOpen] = React.useState(true);

  const useStyles = makeStyles(theme => ({
    colorPrimary: {
      backgroundColor: 'rgba(212,94,55,0.45)',
    },
    barColorPrimary: {
      backgroundColor: '#d45e37',
    }
  }));

  const classes = useStyles();

  return (
    <div>
      <Dialog aria-labelledby="customized-dialog-title" open={open} style={{zIndex: 10001}}>
        <DialogTitle id="customized-dialog-title">
          <Translate contentKey="pimnowApp.prestashopProduct.waitingRemove.title"/>
        </DialogTitle>
        <LinearProgress classes={{colorPrimary: classes.colorPrimary, barColorPrimary: classes.barColorPrimary}}/>
        <DialogContent dividers>
          <Typography gutterBottom>
            <Translate contentKey="pimnowApp.prestashopProduct.waitingRemove.content"/>
          </Typography>
        </DialogContent>
      </Dialog>
    </div>
  )
};
