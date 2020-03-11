import {Dialog, DialogContent, DialogTitle, LinearProgress, Typography} from "@material-ui/core";
import React from "react";
import {Translate} from "react-jhipster";

export const WaitingExport = () => {
  const [open, setOpen] = React.useState(true);

  return (
    <div>
      <Dialog aria-labelledby="customized-dialog-title" open={open}>
        <DialogTitle id="customized-dialog-title" >
          <Translate contentKey="pimnowApp.product.export.waiting.title"/>
        </DialogTitle>
        <LinearProgress />
        <DialogContent dividers>
          <Typography gutterBottom>
            <Translate contentKey="pimnowApp.product.export.waiting.content"/>
          </Typography>
        </DialogContent>
      </Dialog>
    </div>
  )
};
