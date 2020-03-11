import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Redirect, RouteComponentProps } from 'react-router-dom';

import { IRootState } from 'app/shared/reducers';
import { login } from 'app/shared/reducers/authentication';
import LoginModal from './login-modal';

import Avatar from '@material-ui/core/Avatar';
import Button from '@material-ui/core/Button';
import CssBaseline from '@material-ui/core/CssBaseline';
import TextField from '@material-ui/core/TextField';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import Checkbox from '@material-ui/core/Checkbox';
import Link from '@material-ui/core/Link';
import Grid from '@material-ui/core/Grid';
import Typography from '@material-ui/core/Typography';
import { makeStyles } from '@material-ui/core/styles';
import Container from '@material-ui/core/Container';
import {Translate} from "react-jhipster";
import Alert from '@material-ui/lab/Alert';

export interface ILoginProps extends StateProps, DispatchProps, RouteComponentProps<{}> {}

const useStyles = makeStyles(theme => ({
  alertError:{
    marginTop: theme.spacing(4),
    marginBottom: theme.spacing(2)
  },
  paper: {
    display: 'flex',
    flexDirection: "column" as "column",
    alignItems: 'center',
  },
  avatar: {
    margin: theme.spacing(1),
    backgroundColor: theme.palette.secondary.main,
  },
  form: {
    width: '100%', // Fix IE 11 issue.
    marginTop: theme.spacing(1),
  },
  submit: {
    margin: theme.spacing(3, 0, 2),
  },
  forgot: {
    textAlign: "right" as "right",
    marginRight: '0px',
  },
  controls: {
    flexGrow: 1,
  },
}));

export const Login = (props: ILoginProps) => {

  const handleLogin = (username, password, rememberMe = false) => props.login(username, password, rememberMe);

  const handleSubmit = (event) => {
    event.preventDefault() ;
    handleLogin(event.target.username.value, event.target.password.value, event.target.rememberMe.checked) ;
  };

  const { location, isAuthenticated } = props;
  const { from } = location.state as any || { from: { pathname: '/', search: location.search } };

  if (isAuthenticated) {
    return <Redirect to={from} />;
  }

  const classes = useStyles();

  return (
    <Container component="main" maxWidth="xs">
      <CssBaseline />
      <div className={classes.paper}>
        <Typography component="h1" variant="h5">
          <Translate contentKey={"login.title"}></Translate>
        </Typography>
        <Grid container>
          <Grid item xs>
            {props.loginError ? (
              <Alert color="error">
                <Translate contentKey={"login.messages.error.authentication"}></Translate>
              </Alert>
            ) : null}
          </Grid>
        </Grid>
        <form className={classes.form} onSubmit={handleSubmit}>
          <TextField
            variant="outlined"
            margin="normal"
            required
            fullWidth
            id="email"
            label={<Translate contentKey={"login.form.username"}></Translate>}
            name="username"
            autoComplete="email"
            autoFocus
          />
          <TextField
            variant="outlined"
            margin="normal"
            required
            fullWidth
            name="password"
            label={<Translate contentKey={"login.form.password"}></Translate>}
            type="password"
            id="password"
            autoComplete="current-password"
          />
          <FormControlLabel
            control={<Checkbox id="rememberMe" name="rememberMe" value="remember" color="primary" />}
            label={<Translate contentKey={"login.form.rememberme"}></Translate>}
          />
          <Button
            type="submit"
            fullWidth
            variant="contained"
            color="primary"
            className={classes.submit}
          >
            <Translate contentKey={"login.form.button"}></Translate>
          </Button>
          <div className={classes.controls}>
            <Grid
              container
              direction="row"
              justify="flex-end"
              alignItems="center"
            >
              <Grid item xs={6}>
                <Link className={classes.forgot} href="/account/reset/request" variant="body2">
                  <Translate contentKey={"login.password.forgot"}></Translate>
                </Link>
              </Grid>
            </Grid>
          </div>
        </form>
      </div>
    </Container>
  )
};

const mapStateToProps = ({ authentication }: IRootState) => ({
  isAuthenticated: authentication.isAuthenticated,
  loginError: authentication.loginError,
});

const mapDispatchToProps = { login };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Login);
