import React, {useEffect, useState} from 'react';
import {makeStyles, withStyles} from '@material-ui/core/styles';
import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import MuiDialogContent from '@material-ui/core/DialogContent';
import MuiDialogActions from '@material-ui/core/DialogActions';
import {DialogTitle, FormControl, InputLabel, Select, Typography} from "@material-ui/core";
import {Translate} from "react-jhipster";
import ArrowDownwardIcon from "@material-ui/icons/ArrowDownward";
import {IRootState} from "app/shared/reducers";
import {connect} from "react-redux";
import {getMappingsByCustomer, exportProduct} from "app/entities/product/export.reducer";
import {IExportProduct} from "app/shared/model/export.model";
import {deepUpdate} from "immupdate";
import {IProduct} from "app/shared/model/product.model";
import GetAppIcon from '@material-ui/icons/GetApp';
import BlockIcon from '@material-ui/icons/Block';
import {WaitingExport} from "app/entities/product/dialog_waiting_export";
import {toast} from "react-toastify";

export interface IExportProductProps extends StateProps, DispatchProps {data: IProduct[]}

const DialogContent = withStyles(theme => ({
  root: {
    padding: theme.spacing(2),
  },
}))(MuiDialogContent);

const DialogActions = withStyles(theme => ({
  root: {
    margin: 0,
    padding: theme.spacing(1),
  },
}))(MuiDialogActions);

const useStyles = makeStyles(theme => ({
  submit: {
    margin: theme.spacing(3, 1, 2),
  },
  formControl: {
    width: '100%',
  },
  form: {
    width: '100%', // Fix IE 11 issue.
  }
}));

export const ExportDialog = (props: IExportProductProps) => {
  const classes = useStyles();
  const [open, setOpen] = React.useState(false);
  const [exportProducts, setExportProducts] = useState<IExportProduct>({});
  const handleClickOpen = () => {
    setOpen(true);
  };
  const handleClose = () => {
    setOpen(false);
  };
  const handleSubmit = () => {
     const listIdf = props.data.map(p => p.idF);
     const newExportProducts = deepUpdate(exportProducts).at("productsIdf").set(listIdf);
     props.exportProduct(newExportProducts);
     setOpen(false);
    toast.success( <Translate contentKey="pimnowApp.product.export.success"/>);
  };

  const handleChangeExportProduct = (event, key: keyof IExportProduct) => {
    const newExport: IExportProduct = deepUpdate(exportProducts).at(key).set(event.target.value);
    setExportProducts(newExport);
  };

  const { mappings, data, exportingProduct } = props;

  useEffect(() => {
    props.getMappingsByCustomer(0);
  }, []);

  return (
    <div>
      {exportingProduct ? <WaitingExport/> : null}
      <Button variant="outlined" onClick={handleClickOpen} style={{margin:'1em', marginTop:'0em'}} size="medium" color="secondary">
        <ArrowDownwardIcon/>
        &nbsp;
        <Translate contentKey="pimnowApp.product.home.exportSelection"/>
      </Button>
      <Dialog onClose={handleClose} aria-labelledby="customized-dialog-title" open={open}>
        <DialogTitle style={{textAlign: 'center'}} id="customized-dialog-title">
          <ArrowDownwardIcon/>
          &nbsp;
          <Translate contentKey="pimnowApp.product.export.title"/>
        </DialogTitle>
        <DialogContent dividers>
          <form onSubmit={handleSubmit}>
            <Typography style={{marginBottom:'1em'}}><Translate contentKey="pimnowApp.product.export.numberProducts" interpolate={{ number: data.length }}/></Typography>
            <FormControl variant="outlined" className={classes.formControl}>
              <InputLabel id="label-authorities">
                <Translate contentKey="pimnowApp.product.export.mapping"/>
              </InputLabel>
              <Select
                labelId="label-authorities"
                onChange={e => handleChangeExportProduct(e, "idMapping")}
                native
                required
                fullWidth>
                <option value=""/>
                {mappings.map(mapping => (
                  <option value={mapping.id} key={mapping.id}>
                    ({mapping.idF}) {mapping.name}
                  </option>
                ))}
              </Select>
            </FormControl>
          </form>
        </DialogContent>
        <DialogActions>
          <Button autoFocus onClick={handleClose} color="secondary" variant="contained">
            <BlockIcon/>
            &nbsp;
            <Translate contentKey="pimnowApp.product.export.cancel"/>
          </Button>
          <Button autoFocus onClick={handleSubmit} color="primary" variant="contained">
            <GetAppIcon/>
            &nbsp;
            <Translate contentKey="pimnowApp.product.export.submit"/>
          </Button>
        </DialogActions>
      </Dialog>
    </div>
  );
};

const mapStateToProps = (storeState: IRootState) => ({
  mappings: storeState.exportProduct.mappings,
  exportingProduct: storeState.exportProduct.exportingProduct
});

const mapDispatchToProps = { getMappingsByCustomer, exportProduct };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ExportDialog);
