import React, {useState} from 'react';
import {log, Translate, translate} from 'react-jhipster';
import {connect} from 'react-redux';
import {IRootState} from 'app/shared/reducers';
import {handlePasswordResetInit, reset} from '../password-reset.reducer';
import makeStyles from "@material-ui/core/styles/makeStyles";
import {Container, Grid, TextField, Typography} from "@material-ui/core";
import Button from '@material-ui/core/Button';
import Alert from '@material-ui/lab/Alert';


export type IPasswordResetInitProps = DispatchProps;


function loadPage() {
  this.props.history.push("/login");
}

export const PasswordResetInit = (props: IPasswordResetInitProps)=>{

  const useStyles = makeStyles(theme => ({
    root: {
      '& > *': {
        margin: theme.spacing(1),
        width: '100%',
      },
    },
    grid: {
      width: '100%',
    },
    form: {
      width: '100%',
    },
    TextField: {
      width: '100%',
      align:'center',
    },

    submit: {
      textAlign: "right" as "right",
      marginRight: '0px',
      alignContent: "right",
      marginTop: theme.spacing(2),
    },
  }));

  const useStylesWarning = makeStyles(theme => ({
    root: {
      width: '100%',
      '& > * + *': {
        marginTop: theme.spacing(2),
      },
    },
  }));

  const [mail, setMail] = useState('');



  const handleSubmit = (event) => {
    event.preventDefault();
    props.handlePasswordResetInit(mail);
    loadPage();
  };

  const handleChangeMail = (event) => {
    setMail(event.target.value);
  };

  const classes = useStyles();
  const classesWarning = useStylesWarning();

  return (
    <Container component="main" maxWidth="sm">
      <Grid>
        <Typography variant="h5" align={"center"}>
          <Translate contentKey="reset.request.title">Reset your password</Translate>
        </Typography>
        <br/>
        <div className={classesWarning.root}>
          <Alert severity="info">

            <Translate contentKey="reset.request.messages.info">Enter the email address you used to
              register</Translate>
          </Alert>
          <br/>
        </div>
        <form className={classes["form"]} onSubmit={handleSubmit} align-content={"center"}>
          <Grid container
                item
                justify="flex-start"
                direction="column"
          >
            <TextField
              name={"email"}
              id="email"
              label={translate('global.form.email.label')}
              placeholder={translate('global.form.email.placeholder')}
              onChange={handleChangeMail}
              type="email"
              variant="outlined"
              size={"medium"}
              full-width
            />
          </Grid>
          <br/>
          <Grid container
                item
                justify="flex-end"
                direction="row"
          >
            <Button variant="contained" color="primary" type="submit" className={classes["submit"]}>
              <Translate contentKey="reset.finish.form.button">Reset password</Translate>
            </Button>
          </Grid>
        </form>
      </Grid>
    </Container>);

};

const mapDispatchToProps = { handlePasswordResetInit, reset,
};


const mapStateToProps = (storeState: IRootState) => ({
  email: storeState.passwordReset.loading
});

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(PasswordResetInit);
