import React, {useEffect, useState} from 'react';
import axios from 'axios';
import {connect} from 'react-redux';
import {RouteComponentProps} from 'react-router-dom';
import {Translate, translate} from 'react-jhipster';
import {IRootState} from 'app/shared/reducers';
import {getEntity as getEntityFamily, getEntityRoot} from '../family/family.reducer';
import {getEntities as getCustomers} from 'app/entities/customer/customer.reducer';
import {createEntity, getEntity, reset, updateEntity} from './mapping.reducer';
import {
  AttributeDuplicated,
  AttributeFromExcel,
  AttributeWithField,
  FormFields,
  HeaderAttributesFields,
  Modal
} from './mapping-components';
import {IAttribut} from "app/shared/model/attribut.model";
import readXlsxFile from 'read-excel-file'
import {Button, Container, Grid, Select, TextField} from '@material-ui/core';
import {IMapping} from "app/shared/model/mapping.model";
import {IAssociation} from "app/shared/model/association.model";
import association from "app/entities/association/association";
import Alert from "@material-ui/lab/Alert";
import {ArrowRightAlt} from "@material-ui/icons";
import Skeleton from "@material-ui/lab/Skeleton";
import {ButtonsAction} from "app/shared/util/buttons-action";
import Typography from "@material-ui/core/Typography";
import FilterList from "@material-ui/icons/FilterList";

export interface IMappingUpdateState extends StateProps, DispatchProps, RouteComponentProps<{ id: string, type: string }> {
  isNew: boolean;
  customerId: string;
}

const MappingUpdate = (props: IMappingUpdateState) => {

  const [isNew, setIsNew] = useState(!props.match.params || !props.match.params.id) ;
  const [isInfo, ] = useState(props.match.params.type === "info") ;
  const [open, setOpen] = React.useState(false) ;
  const [attributes, setAttributes] = React.useState([]);
  const [attributesSelect, setAttributesSelect] = React.useState([]);
  const [loading, setLoading] = React.useState(!props.match.params || !props.match.params.id);
  const [loadingAttributes, setLoadingAttributes] = React.useState(props.match.params.id !== undefined);
  const [errorAdd, setErrorAdd] = React.useState("");
  const [familyNameSelected, setFamilyNameSelected] = React.useState("");
  const [rightAttributes, setRightAttributes] = React.useState(new Map());
  const [freeField, setFreeField] = React.useState(0) ;
  const [loadedAttributes, setLoadedAttributes] = React.useState(new Map()) ;
  const [loadedAttributesRight, setLoadedAttributesRight] = React.useState(new Map()) ;
  const [separator, setSeparator] = React.useState("") ;
  const [description, setDescription] = React.useState("") ;
  const [idf, setIdf] = React.useState("") ;
  const [nameField, setName] = React.useState("") ;
  const [isEdit] = React.useState(props.match.params.type === "edit") ;

  const toAdd = {} as IMapping ;

  const addFreeField = () => setFreeField(freeField + 1) ;
  const removeFreeField = (key) => {
    document.getElementById("free_attribute_" + key).remove() ;
  } ;

  const handleClickOpen = () => {
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      setLoadingAttributes(true) ;
      props.getEntity(props.match.params.id);
    }
    props.getEntityRoot(0);

    return () => {
      props.reset() ;
    }
  }, []);

  useEffect(() => {
    const newRightAttributes = new Map() ;
    const newRightAttributesRight = new Map() ;

    props.entityDup.associations && props.entityDup.associations.forEach(a => {
      newRightAttributes.set(a.column, a.idAttribut) ;
      newRightAttributesRight.set(a.idAttribut, a.nameAttribut) ;
      setLoadingAttributes(false) ;
    }) ;

    if(props.entityDup.mapping){
      setSeparator(props.entityDup.mapping.separator) ;
      if(props.match.params.type === "edit" || props.match.params.type === "info") {
        setIdf(props.entityDup.mapping.idF) ;
        setName(props.entityDup.mapping.name) ;
        setDescription(props.entityDup.mapping.description) ;
      }
    }

    setLoadedAttributes(newRightAttributes) ;
    setLoadedAttributesRight(newRightAttributesRight) ;

    newRightAttributesRight.forEach( (v,k) => {
      rightAttributes.set(v,k) ;
    }) ;
  }, [props.entityDup]) ;

  useEffect(() => {
    if(props.updateSuccess) {
      props.history.push('/entity/mapping') ;
      props.reset() ;
    }
  },[props.updateSuccess]) ;

  const handleChangeFile = (evt) => {
    const header = readXlsxFile(evt.target.files[0]).then((rows) => {
      return rows[0] ;
    }) ;
    header.then(rows => {

      const temp = [] ;

      rightAttributes.forEach((v,k) => {
        temp.push(v) ;
      }) ;

      let errorImport = false ;
      let attributesFields = null ;
      attributesFields = [] ;
      rows.forEach( row => {
        if(attributesFields.find(r => r === row)) {
          errorImport = true ;
        } else  {
          if(!temp.includes(row)){
            attributesFields.push(row) ;
          }
        }
      }) ;
      if(errorImport) {
        setErrorAdd(translate("pimnowApp.mapping.error.importDouble")) ;
      } else
        setAttributes(attributesFields) ;
    }) ;
  } ;

  const loadAttributs = (familyId: number) => {
    const apiUrl = 'api/families';
    axios.get<IAttribut[]>(`${apiUrl}/familyAttributs/${familyId}`)
      .then(response => {
        setAttributesSelect(response.data) ;
        setLoading(false) ;
      })
  } ;

  const handleCheckboxChange =  (event, id, name) => {
    const newRightAttributes = rightAttributes ;

    if(event.target.checked){
      if(name === "Id Fonctionnel" || name === "Nom"  || name === "Description"  || name === "Famille"  || name === "Categorie"  || name === "Prix" || name === "Stock" )  {
        newRightAttributes.set(name, id);
      } else {
        newRightAttributes.set("[" + familyNameSelected + "] " + name, id);
      }
    } else  {
      newRightAttributes.delete("[" + familyNameSelected + "] " + name) ;
      newRightAttributes.delete(name) ;
    }

    loadedAttributesRight.forEach((k,v) => {
      newRightAttributes.set(k, v) ;
    }) ;

    setRightAttributes(newRightAttributes) ;
  } ;

  const commonCheck = (right, left, checkMapLeft, checkMapRight, finalAttributes, e) => {
    right.style.backgroundColor = "";
    if(!checkMapLeft.has(left.value)) {
      left.style.backgroundColor = "";

      if (!checkMapRight.has(right[right.selectedIndex].text)) {
        finalAttributes.set(left.value, right.value);
        checkMapRight.set(right[right.selectedIndex].text, 1);
        checkMapLeft.set(left.value, right.value) ;
        right.style.backgroundColor = "";
        return false ;
      } else {
        right.style.backgroundColor = "#ffa18c";
        return true ;
      }
    } else  {
      left.style.backgroundColor = "#ffa18c";
      return true ;
    }
  };

  const checkHeaderFields = (right, left, checkMapLeft, checkMapRight, finalAttributes, e) => {
    if(left.value !== "") {
      if(right.value !== "") {
        return commonCheck(right, left, checkMapLeft, checkMapRight, finalAttributes, e) ;
      } else  {
        right.style.backgroundColor = "#ffa18c";
        return true ;
      }
    }
  };

  const checkFreeFields = (right, left, checkMapLeft, checkMapRight, finalAttributes, e) => {
    if(left.value !== "" && right.value !== "") {
      left.style.backgroundColor = "";
      return commonCheck(right, left, checkMapLeft, checkMapRight, finalAttributes, e) ;
    } else  {
      left.style.backgroundColor = "#ffa18c";
      right.style.backgroundColor = "#ffa18c";
      return true ;
    }
  };

  const loadingBlock = () => (
    [1,2,3,4,5].map((k) => (<Grid key={k} container spacing={2} alignItems={"center"} style={{marginBottom: "2em"}}>
      <Grid item xs={5} style={{textAlign: "center"}}>
        <Skeleton animation="wave" variant="rect" style={{width:"100%",height:"55px", borderRadius:"4px"}} />
      </Grid>
      <Grid item xs={2} style={{textAlign: "center"}}><ArrowRightAlt/></Grid>
      <Grid item xs={5} style={{textAlign: "center"}}>
        <Skeleton animation="wave" variant="rect" style={{width:"100%",height:"55px", borderRadius:"4px"}} />
      </Grid>
    </Grid>
    )));

  const handleSubmit = (e) => {
    e.preventDefault() ;

    let error = false ;

    toAdd.name = e.target['name'].value ;
    toAdd.description = e.target['description'].value ;
    toAdd.separator = e.target['separator'].value ;
    toAdd.associations = [] as IAssociation[] ;
    toAdd.idF = e.target['idF'].value ;

    if(toAdd.name.length === 0 || toAdd.description.length === 0 || toAdd.separator.length === 0 || toAdd.idF.length === 0){
      setErrorAdd(translate("pimnowApp.mapping.error.fields")) ;
      return ;
    }

    setErrorAdd("") ;

    const finalAttributes = new Map() ;
    const checkMapRight = new Map() ;
    const checkMapLeft = new Map() ;

    if(e.target['headerFieldLeft'] !== undefined) {
      if (e.target['headerFieldLeft'].length === undefined) {
        const left = e.target['headerFieldLeft'] ;
        const right = e.target['headerFieldRight'] ;
        error = checkHeaderFields(right, left, checkMapLeft, checkMapRight, finalAttributes, e) ;
      } else if(!error) {
        for (let i = 0; i < e.target['headerFieldLeft'].length && !error; i++) {
          const left = e.target['headerFieldLeft'][i] ;
          const right = e.target['headerFieldRight'][i] ;
          error = checkHeaderFields(right, left, checkMapLeft, checkMapRight, finalAttributes, e) ;
        }
      }
    }

    if(e.target['freeFieldLeft'] !== undefined && !error) {
      if (e.target['freeFieldLeft'].length === undefined) {
        const left = e.target['freeFieldLeft'] ;
        const right = e.target['freeFieldRight'] ;
        error = checkFreeFields(right, left, checkMapLeft, checkMapRight, finalAttributes, e)
      } else if(!error){
        for (let i = 0; i < e.target['freeFieldLeft'].length && !error ; i++) {
          const left = e.target['freeFieldLeft'][i] ;
          const right = e.target['freeFieldRight'][i] ;
          error = checkFreeFields(right, left, checkMapLeft, checkMapRight, finalAttributes, e)
        }
      }
    }

    if(error) {
      setErrorAdd(translate("pimnowApp.mapping.error.error")) ;
    } else if(finalAttributes.size === 0) {
      setErrorAdd(translate("pimnowApp.mapping.error.empty")) ;
    } else  {
      setErrorAdd("") ;
      finalAttributes.forEach((value, key) => {
        const iAssociation = {column:key, idFAttribut:value} ;
        toAdd.associations.push(iAssociation);
      });

      if(props.match.params.type){
        if(props.match.params.type === "edit"){
          toAdd.id = parseInt(props.match.params.id, 10) ;
          props.updateEntity(toAdd) ;
          props.history.push('/entity/mapping');
          return ;
        }
      }
      props.createEntity(toAdd) ;
    }
  } ;

  return (
    <React.Fragment>
      <Grid
        container
        spacing={0}
        direction="column"
        alignItems="center"
        justify="center"
        style={{marginBottom: "2em"}}
      >
        <Grid item xs={12}>
          <Typography
            component="h1"
            variant="h5">
            <FilterList fontSize="large"/>
            &nbsp;
            {isNew ? <Translate contentKey="pimnowApp.mapping.home.createLabel"/> : props.match.params.type === "edit" ? <Translate contentKey="pimnowApp.mapping.home.edit"/> : props.match.params.type === "duplicate" ?  <Translate contentKey="pimnowApp.mapping.home.createOrEditLabel"/> : <Translate contentKey="pimnowApp.mapping.home.info"/>}
          </Typography>
        </Grid>
      </Grid>
      <form onSubmit={handleSubmit}>
        {FormFields(errorAdd,handleChangeFile, isNew,
          separator, setSeparator,
          nameField, setName,
          idf, setIdf,
          description, setDescription, isInfo, isEdit)}
        <Container maxWidth={"md"}>
          <Grid
            container
            spacing={0}
            direction="row"
            alignItems="center"
            justify="center"
            style={{marginBottom:"2em"}}
          >
            {errorAdd.length > 0 && <Alert style={{marginTop:"2em"}} severity="error">{errorAdd}</Alert> }
            {HeaderAttributesFields(handleClickOpen,addFreeField, isInfo)}
            {AttributeFromExcel(attributes,rightAttributes, isInfo)}
            {AttributeWithField(freeField,removeFreeField,rightAttributes, isInfo)}
            {AttributeDuplicated(freeField,removeFreeField,rightAttributes,loadedAttributes, loadedAttributesRight, isInfo)}
            {Modal(handleClose,open,props,setFamilyNameSelected,setAttributesSelect,setLoading,loadAttributs,attributesSelect,rightAttributes,handleCheckboxChange,familyNameSelected,loading, isInfo)}
            {loadingAttributes ? loadingBlock() : null}
          </Grid>
        </Container>
        <Grid
          container
          spacing={0}
          direction="row"
          alignItems="center"
          justify="center"
          style={{marginBottom:"2em"}}
        >
          <Grid
            container
            spacing={0}
            direction="row"
            alignItems="center"
            justify="flex-end"
          >
            <ButtonsAction saveButtonDisabled={isInfo} backUrl={"/entity/mapping"}/>
          </Grid>
        </Grid>
      </form>
    </React.Fragment>
  ) ;
} ;

const mapStateToProps = (storeState: IRootState) => ({
  customers: storeState.customer.entities,
  mappingEntity: storeState.mapping.entity,
  entityDup: storeState.mapping.entityDup,
  loading: storeState.mapping.loading,
  updating: storeState.mapping.updating,
  updateSuccess: storeState.mapping.updateSuccess,
  familyEntityRoot: storeState.family.entityRoot,
});

const mapDispatchToProps = {
  getCustomers,
  getEntity,
  updateEntity,
  createEntity,
  reset,
  getEntityFamily,
  getEntityRoot,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(MappingUpdate);
