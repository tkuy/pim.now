import React, {useEffect, useState} from 'react';
import {connect} from 'react-redux';
import {Link, Redirect, RouteComponentProps} from 'react-router-dom';
import {Translate} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {IRootState} from 'app/shared/reducers';

import {createEntity, getEntity, reset, updateEntity} from './customer.reducer';
import Grid from "@material-ui/core/Grid";
import TextField from "@material-ui/core/TextField";
import Button from "@material-ui/core/Button/Button";
import {Hidden} from "@material-ui/core";
import Typography from "@material-ui/core/Typography";
import AccountCircleOutlinedIcon from "@material-ui/icons/AccountCircleOutlined";
import Container from "@material-ui/core/Container";
import {makeStyles} from "@material-ui/core/styles";
import {BusinessCenter} from "@material-ui/icons";

export interface ICustomerUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {
}

const useStyles = makeStyles(theme => ({
  formControl: {
    width: '100%',
  },
  paper: {
    display: 'flex',
    flexDirection: "column" as "column",
    alignItems: 'center',
  },
  form: {
    width: '100%', // Fix IE 11 issue.
  },
  submit: {
    margin: theme.spacing(3, 1, 2),
  },
  forgot: {
    textAlign: "right" as "right",
    marginRight: '0px',
  },
  typography: {
    textAlign: "center" as "center",
  },
}));

export interface ICustomerUpdateState {
  isNew: boolean;
  configurationId: string;
}

const URL_LIST = "/entity/customer";
const CustomerUpdate = (props: ICustomerUpdateProps) => {
  const [isNew, ] = useState(!props.match.params || !props.match.params.id);
  const [isDetail, ] = useState(window.location.href.includes("detail"));
  const [configurationId, setConfigurationId] = useState('0');
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
    }
    return () => props.reset();
  }, []);

  useEffect(() => {
    if (!isNew) {
      setName(props.customerEntity.name);
      setDescription(props.customerEntity.description);
    }
  }, [props.customerEntity]);
  const handleClose = () => {
    props.history.push(URL_LIST);
  };
  const [doRedirect, setDoRedirect] = useState(false);

  const handleSubmit = (event) => {
    event.preventDefault();
    const idSubmit: number = !isNew ? event.target.id.value : null;
    // A retirer quand IdF sera retiré
    const idF = "IdF";
    const {customerEntity} = props;

    const entity = {
      ...customerEntity,
      name,
      description,
      idF
    };
    if (isNew) {
      props.createEntity(entity);
    } else {
      props.updateEntity(entity);
    }
    setDoRedirect(true);
  };
  const titlePage = () => {
    if(isNew) {
      return (<Translate contentKey="pimnowApp.customer.home.createLabel"/>);
    }
    else if (!isNew && isDetail) {
      return (<Translate contentKey="pimnowApp.customer.home.consultLabel"/>);
    }
    else {
      return (<Translate contentKey="pimnowApp.customer.home.editLabel"/>);
    }
  };

  const classes = useStyles();

  const {loading, updating} = props;

  return (
    <Container component="main" maxWidth="sm">
        <Grid container alignItems="center" style={{marginBottom:'1em'}}>
          <Grid item sm={12} style={{textAlign:"center"}}>
            <Typography
              component="h1"
              variant="h5">
              <BusinessCenter fontSize="large"/>
              &nbsp;
              {titlePage()}
            </Typography>
          </Grid>
        </Grid>
      {loading ? (
        <p>Loading...</p>
      ) : (
        <Grid container spacing={2} className={classes.typography}>
          <form className={classes.form} onSubmit={handleSubmit}>
            <Grid container>
              <Grid item sm={12} style={{textAlign:"center"}}>
                <TextField
                  variant="outlined"
                  margin="normal"
                  required
                  fullWidth
                  id="customer-name"
                  label={<Translate contentKey="pimnowApp.customer.name"/>}
                  name="name"
                  value={name|| ""}
                  onChange={e => setName(e.target.value)}
                  disabled={isDetail}
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  variant="outlined"
                  margin="normal"
                  required
                  fullWidth
                  id="customer-description"
                  label={<Translate contentKey="pimnowApp.customer.description"/>}
                  name="description"
                  value={description || ""}
                  onChange={e => setDescription(e.target.value)}
                  disabled={isDetail}
                />
              </Grid>
            </Grid>
            <Grid
              container
              direction="row"
              justify="flex-end"
              style={{marginTop:'1em'}}>
              <Button component={Link} id="cancel-save" to="/entity/customer" color="secondary" variant="contained">
                <FontAwesomeIcon icon="arrow-left"/>
                &nbsp;
                <span className="d-none d-md-inline">
                        <Translate contentKey="entity.action.back">Back</Translate>
                      </span>
              </Button>
              &nbsp;
              {!isDetail ? (<Button
                type="submit"
                id="save-entity"
                variant="contained"
                color="primary"
                disabled={updating}
              >
                <FontAwesomeIcon icon="save"/>
                &nbsp;
                <Translate contentKey= {isNew ? "pimnowApp.customer.confirm" : "pimnowApp.customer.edit"}/>
              </Button>) : null}
            </Grid>
            {doRedirect && <Redirect to={URL_LIST}/>}
          </form>
        </Grid>
      )}
    </Container>
  );
};

const mapStateToProps = (storeState: IRootState) => ({
  customerEntity: storeState.customer.entity,
  loading: storeState.customer.loading,
  updating: storeState.customer.updating,
  updateSuccess: storeState.customer.updateSuccess
});

const mapDispatchToProps = {
  getEntity,
  updateEntity,
  createEntity,
  reset
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(CustomerUpdate);
