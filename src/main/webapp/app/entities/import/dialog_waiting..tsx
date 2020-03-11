import {Dialog, DialogContent, DialogTitle, LinearProgress, Typography} from "@material-ui/core";
import React from "react";
import {Translate} from "react-jhipster";

export const WaitingImport = () => {
  const [open, setOpen] = React.useState(true);

  return (
    <div>
      <Dialog aria-labelledby="customized-dialog-title" open={open}>
        <DialogTitle id="customized-dialog-title" >
          <Translate contentKey="import.waiting.title"/>
        </DialogTitle>
        <LinearProgress />
        <DialogContent dividers>
          <Typography gutterBottom>
            <Translate contentKey="import.waiting.content"/>
          </Typography>
        </DialogContent>
      </Dialog>
    </div>
  )
};
