import './home.scss';

import React, {useEffect} from 'react';
import {Container, Divider, Grid, Typography} from "@material-ui/core";
import {connect} from "react-redux";

import {getEntity} from 'app/shared/reducers/dashboard-reducer'
import {RouteComponentProps} from "react-router-dom";
import Skeleton from "@material-ui/lab/Skeleton";
import AlertTitle from "@material-ui/lab/AlertTitle";
import Alert from "@material-ui/lab/Alert";
import DashboardIcon from "@material-ui/icons/Dashboard"
import {Chart} from "app/modules/home/chart";
import {HomeComponent} from "app/modules/home/home-component";
import {AUTHORITIES} from "app/config/constants";
import {Translate, translate} from "react-jhipster";

export interface IAssociationUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {
}

export const Home = (props: IAssociationUpdateProps) => {

  const [state, setState] = React.useState({
    customer: null,
    nbCustomerActive: null,
    nbCustomerInactive: null,
    nbUserActive: null,
    nbUserInactive: null,
    nbProductCreated: null,
    nbProductDeleted: null,
    nbProductIntegrated: null
  });

  useEffect(() => {
    if (props.account.authorities !== undefined) {
      props.getEntity(0);
    }
  }, [props.account]);

  useEffect(() => {
    setState({
      customer: props.entity.customer,
      nbCustomerActive: props.entity.nbCustomerActive,
      nbCustomerInactive: props.entity.nbCustomerInactive,
      nbUserInactive: props.entity.nbUserInactive,
      nbUserActive: props.entity.nbUserActive,
      nbProductCreated: props.entity.nbProductCreated,
      nbProductDeleted: props.entity.nbProductDeleted,
      nbProductIntegrated: props.entity.nbProductIntegrated,
    });

  }, [props.entity]);

  const UserDash = () => (
    <>
      <Grid item xs={12} sm={6} lg={4}>
        {state.customer ?
          <HomeComponent title={"home.customer"} value={state.customer.name}/>
          :
          <HomeComponent title={"home.customer"} value={null}/>
        }
      </Grid>
      <Grid item xs={12} sm={6} lg={4}>
        {(!state.nbProductCreated && !state.nbProductDeleted) || (state.nbProductCreated === 0 && state.nbProductDeleted === 0) ? (
            <HomeComponent title={"home.chart1.title"} value={translate("home.chart1.noproduct")}/>)
          : <Chart value1={state.nbProductCreated} value2={state.nbProductDeleted}
                   legend1={translate("home.chart1.legend1")} legend2={translate("home.chart1.legend2")}
                   title={translate("home.chart1.title")}/>}
      </Grid>
      <Grid item xs={12} sm={6} lg={4}>
        {state.nbProductIntegrated !== null ?
          <HomeComponent title={"home.integratedTitle"} value={state.nbProductIntegrated}/>
          :
          <HomeComponent title={"home.integratedTitle"} value={null}/>
        }
      </Grid>
    </>
  );

  const AdminFoncDash = () => (
    <>
      {UserDash()}
      <Grid item xs={12} sm={6} lg={4}>
        <Chart value1={state.nbUserActive} value2={state.nbUserInactive} legend1={translate("home.chart2.legend1")}
               legend2={translate("home.chart2.legend2")} title={translate("home.chart2.title")}/>
      </Grid>
    </>
  );

  const AdminDash = () => (
    <>
      <Grid item xs={12} sm={6} lg={4}>
        {(!state.nbProductCreated && !state.nbProductDeleted) || (state.nbProductCreated === 0 && state.nbProductDeleted === 0) ? (
            <HomeComponent title={"home.chart1.title"} value={translate("home.chart1.noproduct")}/>)
          : <Chart value1={state.nbProductCreated} value2={state.nbProductDeleted}
                   legend1={translate("home.chart1.legend1")} legend2={translate("home.chart1.legend2")}
                   title={translate("home.chart1.title")}/>}
      </Grid>
      <Grid item xs={12} sm={6} lg={4}>
        <Chart value1={state.nbUserActive} value2={state.nbUserInactive} legend1={translate("home.chart2.legend1")}
               legend2={translate("home.chart2.legend2")} title={translate("home.chart2.title")}/>
      </Grid>
      <Grid item xs={12} sm={6} lg={4}>
        <Chart value1={state.nbCustomerActive} value2={state.nbCustomerInactive}
               legend1={translate("home.chart3.legend1")}
               legend2={translate("home.chart3.legend2")} title={translate("home.chart3.title")}/>
      </Grid>
      <Grid item xs={12} sm={6} lg={4}>
        {state.nbProductIntegrated ?
          <HomeComponent title={"home.integratedTitle"} value={state.nbProductIntegrated}/>
          :
          <HomeComponent title={"home.integratedTitle"} value={null}/>
        }
      </Grid>
    </>
  );

  const GuestDash = () => (
    <Alert severity="info">
      <AlertTitle>Info</AlertTitle>
      <Translate contentKey={"home.notLogged"}/>
    </Alert>
  );

  return (
    <React.Fragment>
      <div className={"home"} style={{textAlign:'center'}}>

        <Typography variant={"h5"}>
          <DashboardIcon fontSize="large" style={{fill:'#245173'}}/> <Translate contentKey="home.dashboard"/>
        </Typography>

        <Divider style={{marginTop: '1em', width:'100%'}}/>

        <Container style={{paddingTop: "1.3em"}} maxWidth={"xl"}>
          <Grid container alignItems={"center"} justify="center" spacing={2}>
            {props.account.authorities ? (
              <>
                {
                  props.account.authorities.includes(AUTHORITIES.ADMIN_FONC) && AdminFoncDash()
                }
                {
                  props.account.authorities.includes(AUTHORITIES.USER) && UserDash()
                }
                {
                  props.account.authorities.includes(AUTHORITIES.ADMIN) && AdminDash()
                }
              </>
            ) : <Skeleton variant="rect" height={200}/>
            }

          </Grid>
        </Container>
      </div>
    </React.Fragment>

  );
};

const mapStateToProps = storeState => ({
  account: storeState.authentication.account,
  isAuthenticated: storeState.authentication.isAuthenticated,
  entity: storeState.dashboard.entity
});

const mapDispatchToProps = {
  getEntity,

};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Home);

