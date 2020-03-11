import {Dialog, DialogContent, DialogTitle, LinearProgress, Typography} from "@material-ui/core";
import React from "react";
import {Translate} from "react-jhipster";

export const WaitingImportIntegration = () => {
  const [open, setOpen] = React.useState(true);

  return (
    <div>
      <Dialog aria-labelledby="customized-dialog-title" open={open} style={{zIndex: 10001}}>
        <DialogTitle id="customized-dialog-title" >
          <Translate contentKey="pimnowApp.prestashopProduct.waiting.title"/>
        </DialogTitle>
        <LinearProgress />
        <DialogContent dividers>
          <Typography gutterBottom>
            <Translate contentKey="pimnowApp.prestashopProduct.waiting.content"/>
          </Typography>
        </DialogContent>
      </Dialog>
    </div>
  )
};
