import {Link, RouteComponentProps} from "react-router-dom";
import {IRootState} from "app/shared/reducers";
import {connect} from "react-redux";
import React, {useEffect, useState} from "react";
import {
  FormControl,
  FormHelperText,
  Grid,
  Input,
  InputLabel,
  Select,
  Typography
} from "@material-ui/core";
import {Translate} from "react-jhipster";
import ImportExportIcon from '@material-ui/icons/ImportExport';
import Container from "@material-ui/core/Container";
import {makeStyles} from "@material-ui/core/styles";
import {getMappingsByCustomer, importProductAndGetErrorExcel} from "./import.reducer";
import {deepUpdate} from "immupdate";
import {IImportProduct} from "app/shared/model/import.model";
import Button from "@material-ui/core/Button";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {WaitingImport} from "app/entities/import/dialog_waiting.";
import Alert from "@material-ui/lab/Alert";

export interface IImportProductProps extends StateProps, DispatchProps, RouteComponentProps<{}> {}

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

export const ImportProduct = (props: IImportProductProps) => {

  const classes = useStyles();
  const [importProducts, setImportProducts] = useState<IImportProduct>({});
  const [errorExtension, setErrorExtension] = useState<boolean>(false);
  const {loading, mappings, loadingErrorExcel, importDone} = props;
  useEffect(() => {
    props.getMappingsByCustomer(0);
  }, []);

  const handleChangeImportProduct = (event, key: keyof IImportProduct) => {
    const newImport: IImportProduct = deepUpdate(importProducts).at(key).set(event.target.value);
    setImportProducts(newImport);
  };

  const handleFileChange = (event) => {
    if(event.target.files[0].type !== "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"){
      setErrorExtension(true);
      const newImport: IImportProduct = deepUpdate(importProducts).at("fileToImport").set(null);
      setImportProducts(newImport);
    } else {
      setErrorExtension(false);
      const newImport: IImportProduct = deepUpdate(importProducts).at("fileToImport").set(event.target.files[0]);
      setImportProducts(newImport);
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if(errorExtension !== true){
      props.importProductAndGetErrorExcel(importProducts);
    }
  };

  return(
    <Container component="main" maxWidth="sm">
      {loadingErrorExcel ? <WaitingImport/> : null}
      <Grid container alignItems="center">
        <Grid container justify="center" alignItems="center" style={{marginBottom:"2em",marginTop:"1em"}}>
          <Grid item xs={10} style={{textAlign:"center"}}>
            <Typography
              component="h1"
              variant="h5">
              <ImportExportIcon fontSize="large"/>
              &nbsp;
              <Translate contentKey="import.title" />
            </Typography>
          </Grid>
        </Grid>
      </Grid>
      {importDone ? (
        <Grid container alignItems="center">
          <Grid item xs={12} justify="center" style={{marginBottom:"2em"}}>
            <Alert variant="filled" severity="success"><Translate contentKey="import.success"/></Alert>
          </Grid>
      </Grid>)
        : null}
      <Grid container spacing={2}>
        {loading ? <Translate contentKey="import.loading"/> : (
          <form className={classes.form} onSubmit={handleSubmit}>
            <FormControl variant="outlined" className={classes.formControl}>
              <InputLabel id="label-authorities">
                <Translate contentKey="import.mapping"/>
              </InputLabel>
              <Select
                labelId="label-authorities"
                onChange={e => handleChangeImportProduct(e, "idMapping")}
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
            <Grid container justify="center" alignItems="center" spacing={2}>
              <Grid item xs={6} style={{marginBottom:"2em",marginTop:"1em"}}>
                <Typography>
                  <Translate contentKey="import.file"/>
                </Typography>
              </Grid>
              <Grid item xs={6}>
                <FormControl>
                <Input error={errorExtension === true} required onChange={handleFileChange} type="file"/>
                <FormHelperText id="my-helper-text" hidden={errorExtension === false} style={{color: 'red'}}><Translate contentKey="import.extension"/></FormHelperText>
                </FormControl>
              </Grid>
            </Grid>
            <Grid container
                  item
                  justify="flex-end"
                  direction="row">
              <Button
                component={Link}
                to="/"
                color="secondary"
                variant="contained"
                className={classes.submit}
              >
                <FontAwesomeIcon icon="arrow-left"/>
                &nbsp;
                <Translate contentKey="entity.action.back">Back</Translate>
              </Button>
              <Button
                type="submit"
                variant="contained"
                color="primary"
                className={classes.submit}>
                <FontAwesomeIcon icon="save"/>
                &nbsp;
                <Translate contentKey="entity.action.import"/>
              </Button>
            </Grid>
          </form>)
        }
      </Grid>
    </Container>
  );
};

const mapStateToProps = (storeState: IRootState) => ({
  loading: storeState.importProduct.loading,
  mappings: storeState.importProduct.mappings,
  loadingErrorExcel: storeState.importProduct.loadingErrorExcel,
  importDone: storeState.importProduct.importDone
});

const mapDispatchToProps = { getMappingsByCustomer, importProductAndGetErrorExcel };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ImportProduct);
