import {IMappingUpdateState} from "app/entities/mapping/mapping-update";
import React, {useEffect, useState} from "react";
import {IRootState} from "app/shared/reducers";
import {getEntities as getCustomers} from "app/entities/customer/customer.reducer";
import {
  createEntity,
  getEntity, reset,
  updateEntity
} from "app/entities/configuration-customer/configuration-customer.reducer";
import {connect} from "react-redux";
import {RouteComponentProps} from "react-router-dom";
import {Avatar, Button, Container, Grid, TextField} from "@material-ui/core";
import {IConfigurationCustomer} from "app/shared/model/configuration-customer.model";
import {Translate} from "react-jhipster";
import Typography from "@material-ui/core/Typography";
import Build from "@material-ui/icons/Build";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import Divider from '@material-ui/core/Divider';

export interface IConfigurationCustomerUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}


const ConfigurationCustomer = (props: IConfigurationCustomerUpdateProps) => {

  const [apiKeyPrestashop, setApiKeyPrestashop] = useState() ;
  const [urlPrestashop, setUrlPrestashop] = useState() ;
  const [configurationCustomer, setConfigurationCustomer] = useState<IConfigurationCustomer>() ;

  useEffect(() => {
    props.getEntity("");
  }, []);

  useEffect(() => {
    if(props.configurationCustomerEntity){
      setConfigurationCustomer(props.configurationCustomerEntity) ;
      setUrlPrestashop(props.configurationCustomerEntity.urlPrestashop) ;
      setApiKeyPrestashop(props.configurationCustomerEntity.apiKeyPrestashop) ;
    }
  }, [props.configurationCustomerEntity]);


  const handleSubmit = () => {
    configurationCustomer.apiKeyPrestashop = apiKeyPrestashop ;
    configurationCustomer.urlPrestashop = urlPrestashop ;
    props.updateEntity(configurationCustomer) ;
  };

  return (
    <Container maxWidth={"xs"}>
      <Grid
        container
        spacing={2}
        direction="row"
        alignItems="center"
        justify="center"
        style={{marginBottom:"2em"}}
      >
        <Grid item style={{textAlign:"center", marginBottom:"2em"}} xs={12}>
          <Typography
            component="h1"
            variant="h5">
            <Build style={{fill: "#245173"}} fontSize="large"/>
            &nbsp;
            <Translate contentKey="pimnowApp.configurationCustomer.home.createLabel"/>
          </Typography>
        </Grid>
        <Divider style={{width:"100%"}} />
        <Avatar alt="Prestashop" src="../../content/images/prestashop.png" style={{marginTop:'-1em'}} />
        <Grid item xs={12}>
          <Typography style={{textAlign:"center", marginBottom:'0.5em'}} component="h3" variant="h5">Prestashop</Typography>
          <TextField value={apiKeyPrestashop || ''} onChange={e => setApiKeyPrestashop(e.target.value)} name={"apiKeyPrestashop"} fullWidth label={<Translate contentKey={"pimnowApp.configurationCustomer.apiKeyPrestashop"} />} variant="outlined" />
        </Grid>
        <Grid item xs={12}>
          <TextField placeholder="" value={urlPrestashop || ''} onChange={e => setUrlPrestashop(e.target.value)} name={"urlPrestashop"} fullWidth label={<Translate contentKey={"pimnowApp.configurationCustomer.urlPrestashop"} />} variant="outlined" />
        </Grid>
        <Grid
          container
          spacing={0}
          direction="row"
          alignItems="center"
          justify="flex-end"
          style={{marginBottom:"2em"}}
        >
          <Grid item xs={2}>
            <Button onClick={handleSubmit} variant="contained" color="primary" style={{marginTop:'1em'}}>
              <FontAwesomeIcon icon="save"/>
              &nbsp;
              <Translate contentKey="entity.action.save"/>
            </Button>
          </Grid>
        </Grid>
      </Grid>
    </Container>
  )
}

const mapStateToProps = (storeState: IRootState) => ({
  customers: storeState.customer.entities,
  configurationCustomerEntity: storeState.configurationCustomer.entity,
  loading: storeState.configurationCustomer.loading,
  updating: storeState.configurationCustomer.updating,
  updateSuccess: storeState.configurationCustomer.updateSuccess
});

const mapDispatchToProps = {
  getEntity,
  updateEntity,
  reset
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ConfigurationCustomer);
