import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import Button from '@material-ui/core/Button';
import { Translate, translate, getUrlParameter } from 'react-jhipster';
import { RouteComponentProps } from 'react-router-dom';
import { handlePasswordResetFinish, reset } from '../password-reset.reducer';
import PasswordStrengthBar from 'app/shared/layout/password/password-strength-bar';
import makeStyles from "@material-ui/core/styles/makeStyles";
import { ValidatorForm, TextValidator } from "react-material-ui-form-validator";
import {toast} from "react-toastify";
import {Container, Grid, Typography} from "@material-ui/core";
export interface IPasswordResetFinishProps extends DispatchProps, RouteComponentProps<{ key: string }> {}

export const PasswordResetFinishPage = (props: IPasswordResetFinishProps) => {
  const [password, setPassword] = useState('');
  const [confirmPassword,setConfirmPassword] = useState('');
  const [key] = useState(getUrlParameter('key', props.location.search));

  useEffect(() => () => props.reset(), []);

  const handleValidSubmit = (event) => {
    if(password.length>3 && password.length<50){
      if(password===confirmPassword){
        props.handlePasswordResetFinish(key, password) ;
        props.history.push('/login');
      }else{
        toast.error( <Translate contentKey="reset.finish.messages.notSamePswd">Not Same Password</Translate>)
      }

    }else{
      toast.error(<Translate contentKey="reset.finish.messages.notGoodPswd">Bad Password</Translate>);
    }
    event.preventDefault();
  }
  const updatePassword = (event) => {
    setPassword(event.target.value);
  }
  const updateConfirmPassword = (event) => {
    setConfirmPassword(event.target.value);
  }



  const useStyles = makeStyles(theme => ({
    root: {
      '& > *': {
        margin: theme.spacing(1),
        width: "100%",
      },
    },
    grid: {
      width: '100%',
    },
    ValidatorForm: {
      width: '100%',
      alignItems:'center',
    },
    TextValidator: {
      width: '100%',
    },

    submit: {
      textAlign: "right" as "right",
      marginRight: '0px',
      alignContent: "right",
      marginTop: theme.spacing(2),
    },
  }));

  const getResetForm = () => {
    const classes = useStyles();

    return (
      <ValidatorForm className={classes["form"]} onSubmit={handleValidSubmit} align-content={"center"}>
        <Grid container
              item
              justify="flex-start"
              direction="column"
        >
          <TextValidator

            name="password"
            label={translate('global.form.newpassword.label')}
            placeholder={translate('global.form.newpassword.placeholder')}
            type="password"
            onChange={updatePassword}
            value={password}
            variant="outlined"
            size={"medium"}
            full-width
          />
          <Grid container
                item
                justify="flex-start"
                direction="column"
                alignContent={"center"}
          >

            <PasswordStrengthBar password={password} />

          </Grid>
          <TextValidator
            name="confirmPassword"
            required
            label={translate('global.form.confirmpassword.label')}
            placeholder={translate('global.form.confirmpassword.placeholder')}
            type="password"
            value={confirmPassword}
            onChange={updateConfirmPassword}
            variant="outlined"
            size={"medium"}
            full-width

          />
        </Grid>
        <br/><br/>
        <Grid container
              item
              justify="flex-end"
              direction="row"
        >
          <Button variant="contained" color="primary" type="submit" className={classes["submit"]}>
            <Translate contentKey="reset.finish.form.button">Reset password</Translate>
          </Button>
        </Grid>

      </ValidatorForm>

    );
  };

  return (
    <Container component="main" maxWidth="sm" >
      <Grid>
        <Typography variant="h5" align={"center"}>
          <Translate contentKey="reset.finish.title">Reset password</Translate>
        </Typography>
        <br/>
        <div>{key ? getResetForm() : null}</div>
      </Grid>
    </Container>
  );
};

const mapDispatchToProps = { handlePasswordResetFinish, reset };

type DispatchProps = typeof mapDispatchToProps;

export default connect(
  null,
  mapDispatchToProps
)(PasswordResetFinishPage);
