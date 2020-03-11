import React, {useEffect, useState} from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Translate} from 'react-jhipster';

import {languages, locales} from 'app/config/translation';
import {
  createUser,
  createUserWithUserExtra,
  getRoles,
  getUser,
  reset,
  updateUser,
  updateUserWithUserExtra
} from './user-management.reducer';
import {IRootState} from 'app/shared/reducers';
import {makeStyles} from "@material-ui/core/styles";
import Container from "@material-ui/core/Container";
import Typography from "@material-ui/core/Typography";
import TextField from "@material-ui/core/TextField";
import {FormControl, Grid, InputLabel, Select} from "@material-ui/core";
import {IUser} from "app/shared/model/user.model";
import {deepUpdate} from 'immupdate';
import AccountCircleOutlinedIcon from '@material-ui/icons/AccountCircleOutlined';
import {getEntities as getCustomers} from "app/entities/customer/customer.reducer";
import Button from "@material-ui/core/Button";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {IUserExtra} from "app/shared/model/user-extra.model";
import {createEntity, getEntity, updateEntity} from "app/entities/user-extra/user-extra.reducer";
import {IUserWithUserExtra} from "app/shared/model/user.with.user.extra.model";
import {ICustomer} from "app/shared/model/customer.model";
import {AUTHORITIES} from "app/config/constants";

export interface IUserManagementUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ login: string, id: string }> {
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

export const UserManagementUpdate = (props: IUserManagementUpdateProps) => {
  const [isNew, setIsNew] = useState(!props.match.params || !props.match.params.login || !props.match.params.id);
  const [userState, setUser] = useState<IUser>({});
  const [userUserExtraState, setUserUserExtra] = useState<IUserWithUserExtra>({});
  const [userExtraState, setUserExtra] = useState<IUserExtra>({});
  const [showCustomer, setShowCustomer] = useState(true);
  const [rolesUpdated, setRoles] = useState([]);
  const [isAdminFonc, setIsAdminFonc] = useState(props.account.authorities[0] === AUTHORITIES.ADMIN_FONC);
  const inputLabel = React.useRef<HTMLLabelElement>(null);
  const [labelWidth, setLabelWidth] = React.useState(0);
  const [isDetail, ] = useState(window.location.href.includes("detail"));
  React.useEffect(() => {
    setLabelWidth(inputLabel.current && inputLabel.current.offsetWidth);
  }, []);
  const {user, loading, updating, roles, customers, account} = props;
  useEffect(() => {
    if (isNew && !isDetail) {
      props.reset();
    } else {
      props.getUser(props.match.params.login);
      props.getEntity(props.match.params.id);
    }
    if(isAdminFonc){
      setShowCustomer(false);
    }
    props.getCustomers();
    props.getRoles();
    return () => props.reset();
  }, []);
  useEffect(() => {
    if (!isNew) {
      if (userState) {
        setUser(props.user);
      }
      if(props.user && props.user.authorities[0] === AUTHORITIES.ADMIN){
        setShowCustomer(false);
      }
    }
  }, [props.user]);
  useEffect(() => {
    if (!isNew) {
      if (userExtraState) {
        setUserExtra(props.userExtra);
      }
    }
  }, [props.userExtra]);
  useEffect(() => {
    if(props.account.authorities[0] !== AUTHORITIES.ADMIN){
      setRoles(roles.slice(1, 3));
    } else {
      setRoles(roles);
    }
  }, [props.roles]);
  useEffect(() => {
    if(props.updateSuccess){
      props.history.push("/admin/user-management")
    }
  }, [props.updateSuccess]);

  const handleChangeAuthorities = (event) => {
    if(props.account.authorities[0] !== AUTHORITIES.ADMIN_FONC){
      if (event.target.value === AUTHORITIES.ADMIN) {
        setShowCustomer(false);
      } else {
        setShowCustomer(true);
      }
    }
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    userUserExtraState.user = userState;
    userUserExtraState.phone = userExtraState.phone;
    if(userState.authorities[0] !== AUTHORITIES.ADMIN){
      userUserExtraState.customer = userExtraState.customer;
    }
    if (isNew) {
      props.createUserWithUserExtra(userUserExtraState);
    } else {
      props.updateUserWithUserExtra(userUserExtraState);
    }
  };

  const classes = useStyles();

  const handleChange = (event, key: keyof IUser) => {
    const value = key === 'login' ? event.target.value.trim() : event.target.value;
    const newUser: IUser = deepUpdate(userState).at(key).set(value);
    setUser(newUser);
  };

  const handleChangeUserAuthority = (event) => {
    const authority: [string] = [event.target.value];
    const newUser: IUser = deepUpdate(userState).at("authorities").set(authority);
    setUser(newUser);
  };

  const handleChangeUserExtra = (event, key: keyof IUserExtra) => {
    const newUserExtra: IUserExtra = deepUpdate(userExtraState).at(key).set(event.target.value);
    setUserExtra(newUserExtra);
  };

  const changeLabelRoles = (role) => {
    switch (role) {
      case AUTHORITIES.ADMIN :
        return "Administrateur technique";
      case AUTHORITIES.ADMIN_FONC:
        return "Administrateur fonctionnel";
      case AUTHORITIES.USER:
        return "Utilisateur";
      default:
        return "";
    }
  };

  const handleChangeUserExtraCustomer = (event) => {
    const customer = customers.find(c => c.id === parseInt(event.target.value, 10));
    const customerObject: ICustomer = {
      id: customer.id,
      idF: customer.idF,
      name: customer.name,
      description: customer.description,
      familyRoot: customer.familyRoot,
      categoryRoot: customer.categoryRoot,
      configuration: customer.configuration
    };
    const newUserExtra: IUserExtra = deepUpdate(userExtraState).at("customer").set(customerObject);
    setUserExtra(newUserExtra);
  };

  return (
    <Container component="main" maxWidth="sm">
        <Grid container alignItems="center">
          <Grid container justify="center" alignItems="center" style={{marginBottom:"2em",marginTop:"1em"}}>
            <Grid item xs={10} style={{textAlign:"center"}}>
              <Typography
                component="h1"
                variant="h5">
                <AccountCircleOutlinedIcon fontSize="large"/>
                &nbsp;
                {isNew ?
                  (<Translate contentKey="userManagement.home.createLabel"/>)
                  : <Translate contentKey="userManagement.home.editLabel" />}
              </Typography>
            </Grid>
          </Grid>
        </Grid>
      <Grid container spacing={2} className={classes.typography}>
        {loading ? (<p>Loading...</p>) : (
          <form className={classes.form} onSubmit={handleSubmit}>
            <TextField
              variant="outlined"
              margin="normal"
              required
              fullWidth
              type="text"
              label={<Translate contentKey={"userManagement.login"}/>}
              value={userState.login  || ''}
              onChange={e => handleChange(e, "login")}
              disabled={isDetail}
            />
            <Grid container spacing={2}>
              <Grid item xs={6}>
                <TextField
                  variant="outlined"
                  margin="normal"
                  required
                  fullWidth
                  type="text"
                  label={<Translate contentKey={"userManagement.firstName"}/>}
                  value={userState.firstName  || ''}
                  onChange={e => handleChange(e, "firstName")}
                  disabled={isDetail}
                />
              </Grid>
              <Grid item xs={6}>
                <TextField
                  variant="outlined"
                  margin="normal"
                  required
                  fullWidth
                  type="text"
                  label={<Translate contentKey={"userManagement.lastName"}/>}
                  value={userState.lastName  || ''}
                  onChange={e => handleChange(e, "lastName")}
                  disabled={isDetail}
                />
              </Grid>
              <Grid item xs={6}>
                <TextField
                  variant="outlined"
                  margin="normal"
                  required
                  fullWidth
                  type="email"
                  label={<Translate contentKey={"global.form.email.label"}/>}
                  value={userState.email  || ''}
                  onChange={e => handleChange(e, "email")}
                  disabled={isDetail}
                />
              </Grid>
              <Grid item xs={6}>
                <TextField
                  variant="outlined"
                  margin="normal"
                  fullWidth
                  type="tel"
                  label={<Translate contentKey={"pimnowApp.userExtra.phone"}/>}
                  value={userExtraState.phone  || ''}
                  onChange={e => handleChangeUserExtra(e, "phone")}
                  disabled={isDetail}
                />
              </Grid>
            </Grid>
            <Grid container spacing={2}>
              <Grid item xs={12}>
                <FormControl variant="outlined" className={classes.formControl}>
                  <InputLabel ref={inputLabel}>
                    <Translate contentKey="userManagement.langKey"/>
                  </InputLabel>
                  <Select
                    onChange={e => handleChange(e, "langKey")}
                    value={userState.langKey  || ''}
                    native
                    required
                    fullWidth
                    labelWidth={labelWidth}
                    disabled={isDetail}>
                    <option value=""/>
                    {locales.map(locale => (
                      <option value={locale} key={locale}>
                        {languages[locale].name}
                      </option>
                    ))}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12}>
                <FormControl variant="outlined" className={classes.formControl}>
                  <InputLabel id="label-authorities" ref={inputLabel} shrink={userState.authorities && userState.authorities[0] !== undefined}>
                    <Translate contentKey="userManagement.profiles"/>
                  </InputLabel>
                  <Select
                    labelId="label-authorities"
                    onChange={e => {
                      handleChangeUserAuthority(e);
                      handleChangeAuthorities(e);
                    }}
                    value={userState.authorities || ''}
                    native
                    required
                    fullWidth
                    labelWidth={labelWidth}
                    disabled={isDetail}>
                    <option value=""/>
                    {rolesUpdated.map(role => (
                      <option value={role} key={role}>
                        {changeLabelRoles(role)}
                      </option>
                    ))}
                  </Select>
                </FormControl>
              </Grid>
              {
                showCustomer ? (
                  <Grid item xs={12}>
                    <FormControl variant="outlined" className={classes.formControl}>
                      <InputLabel ref={inputLabel} shrink={userExtraState && userExtraState.customer && userExtraState.customer.name !== undefined}>
                        <Translate contentKey="pimnowApp.userExtra.customer"/>
                      </InputLabel>
                      <Select
                        native
                        fullWidth
                        onChange={e => handleChangeUserExtraCustomer(e)}
                        labelWidth={labelWidth}
                        disabled={isDetail}>
                        <option value=""/>
                        {customers.map((customer, i) => (
                          <option selected={userExtraState.customer && customer.name === userExtraState.customer.name} value={customer.id} key={i}>
                            {customer.name}
                          </option>
                        ))}
                      </Select>
                    </FormControl>
                  </Grid>) : null
              }
            </Grid>
            <Grid container
                  item
                  justify="flex-end"
                  direction="row">
              <Button
                component={Link}
                to="/admin/user-management"
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
                <Translate contentKey="entity.action.save"/>
              </Button>
            </Grid>
          </form>
        )}
      </Grid>
    </Container>
  );
};

const mapStateToProps = (storeState: IRootState) => ({
  customers: storeState.customer.entities,
  user: storeState.userManagement.user,
  userExtra: storeState.userExtra.entity,
  roles: storeState.userManagement.authorities,
  loading: storeState.userManagement.loading,
  updating: storeState.userManagement.updating,
  updateSuccess: storeState.userManagement.updateSuccess,
  account: storeState.authentication.account
});

const mapDispatchToProps = {
  getUser,
  getEntity,
  getCustomers,
  getRoles,
  updateUser,
  createUser,
  reset,
  createEntity,
  updateEntity,
  createUserWithUserExtra,
  updateUserWithUserExtra
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(UserManagementUpdate);
