import React, {useEffect, useState} from 'react';
import {connect} from 'react-redux';
import {RouteComponentProps} from 'react-router-dom';
import {Translate, translate} from 'react-jhipster';
import {IRootState} from 'app/shared/reducers';

import {getEntitiesAttributs, getEntityRoot} from 'app/entities/family/family.reducer';
import {getEntities as getAttributs} from 'app/entities/attribut/attribut.reducer';
import {createEntity, getEntity, reset, updateEntity} from './family.reducer';
import {IFamily} from 'app/shared/model/family.model';
import {Container, FormControl, Grid, Select} from "@material-ui/core";
import Typography from "@material-ui/core/Typography";
import {makeStyles} from "@material-ui/core/styles";
import TextField from "@material-ui/core/TextField";
import {deepUpdate} from "immupdate";
import Autocomplete from "@material-ui/lab/Autocomplete";
import {ButtonsAction} from "app/shared/util/buttons-action";
import {FamilyTree} from "app/entities/family/family-tree";
import {IAttribut} from "app/shared/model/attribut.model";
import Button from "@material-ui/core/Button";
import {AttributesCreation} from "app/entities/attribut/attributes-creation";

export interface IFamilyUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {
}

const useStyles = makeStyles(theme => ({
  alertError: {
    marginTop: theme.spacing(4),
    marginBottom: theme.spacing(2)
  },
  paper: {
    display: 'flex',
    flexDirection: "column" as "column",
    alignItems: 'center',
  },
  form: {
    width: '100%', // Fix IE 11 issue.
    marginTop: theme.spacing(1),
  },
  forgot: {
    textAlign: "right" as "right",
    marginRight: '0px',
  },
  controls: {
    flexGrow: 1,
  },
  formControl: {
    width: '100%',
  },
  button: {
    margin: theme.spacing(3, 1, 2)
  }
}));

export const FamilyUpdate = (props: IFamilyUpdateProps) => {
  const [isNew, ] = useState(!props.match.params || !props.match.params.id);
  const [newAttributes, setNewAttributes] = useState<IAttribut[]>([{},{}]);
  const [newExistingAttributes, setNewExistingAttributes] = useState<IAttribut[]>([{}, {}]);
  const [familysAttributsState, setFamilysAttributsState] = useState([])
  const [familyState, setFamilyState] = useState<IFamily>({} as IFamily);
  const [validatedFamily, setValidatedFamily] = useState();
  const inputLabel = React.useRef<HTMLLabelElement>(null);
  const [labelWidth, setLabelWidth] = React.useState(0);
  React.useEffect(() => {
    setLabelWidth(inputLabel.current && inputLabel.current.offsetWidth);
  }, []);
  const classes = useStyles();

  const attributesArray = [] ;

  const uselessAttributs = ["Nom", "Id Fonctionnel", "Description", "Famille", "Categorie"];
  const filterAttributs = (values: IAttribut[]) => {
    return values.filter(value => !uselessAttributs.includes(value.nom));
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
      props.getEntitiesAttributs(props.match.params.id);
    }
    props.getAttributs();
    props.getEntityRoot(0);
    return () => props.reset();
  }, []);

  useEffect(() => {
    if(!props.familyEntity) {
      return;
    }
    setNewAttributes([{}]);
    setNewExistingAttributes([{}]);
    setFamilyState(props.familyEntity);
  }, [props.familyEntity]);
  useEffect(() => {
    setFamilysAttributsState(filterAttributs(props.familysAttributs.slice()));
  }, [props.familysAttributs])
  const handleClose = () => {
    props.history.push('/entity/family');
  };

  useEffect(() => {
    if(props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);
  const handleChange = (event, key: keyof IFamily) => {
    const value = key==="idF" ? event.target.value.trim() : event.target.value;
    setFamilyState(deepUpdate(familyState).at(key).set(value));

  };
  const saveEntity = (event) => {
    event.preventDefault() ;
    if (isNew) {
      const entity = {
        ...familyState,
        newAttributes,
        newExistingAttributes
      };
      props.createEntity(entity);
    } else {
      const entity = {
        ...familyState,
        idF: props.familyEntity.idF,
        newAttributes,
        newExistingAttributes
      };
      props.updateEntity(entity);

    }
  };
  const toggleFamily = (e) => {
    setValidatedFamily(!validatedFamily);
  };
  const familysAttributs = filterAttributs(props.familysAttributs.slice());

  return (
    <Container component="main" maxWidth="md" style={{paddingRight:'10em', paddingLeft:'10em'}}>
      <div className={classes.paper}>
        <Grid item xs={12}>
          <Typography
            component="h1"
            variant="h5">
            &nbsp;
            {isNew ? <Translate contentKey="pimnowApp.family.home.createLabel"/> : <Translate contentKey="pimnowApp.family.home.editLabel"/>}
          </Typography>
        </Grid>
      </div>
      <form className={classes.form} onSubmit={(e) => saveEntity(e)}>
        <FormControl variant="outlined" className={classes.formControl}>
          <Grid container spacing={3}>
            <Grid item xs={12}>
              <TextField
                required
                name={"name"}
                variant="outlined"
                fullWidth
                onChange={e => handleChange(e, 'nom')}
                value={familyState.nom || ''}
                id="name"
                label={<Translate contentKey="pimnowApp.family.nom"/>}
              />
            </Grid>

            <Grid item xs={12}>
              <TextField
                required
                name="idF"
                fullWidth
                onChange={e => {
                  if(isNew) {
                    handleChange(e, 'idF');
                  }
                }}
                variant="outlined"
                value={familyState.idF || ''}
                id="idF"
                disabled={!isNew}
                label={<Translate contentKey="pimnowApp.family.idF"/>}
              />
            </Grid>


            <Grid item xs={12}>
              <div>

                <Typography component="h2" variant="h5">
                  {isNew ? <Translate contentKey="pimnowApp.family.home.selectionParent"/> : <Translate contentKey="pimnowApp.family.home.treeFamily"/>}
                </Typography>
              </div>
            </Grid>
            <Grid item xs={12}>
              {props.familyEntityRoot && props.familyEntityRoot.id &&
              <FamilyTree handlingFunction={(id) => {
                if(isNew && !validatedFamily) {
                  familyState.idPredecessor = id;
                  const newFamily = {
                    ...familyState,
                    idPredecessor: id
                  };
                  setFamilyState(newFamily);
                }
              }}
                node={props.familyEntityRoot} selectedNodeId={isNew ? familyState.idPredecessor : familyState.id}
              />}
            </Grid>
            {isNew &&
              <Grid container item justify="flex-end" direction="row">
                <Button variant="contained" color="secondary" disabled={!validatedFamily} onClick={(e)=>{
                  toggleFamily(e);
                }}
                  className={classes.button}>
                  <Translate contentKey={"pimnowApp.family.familyAnotherChoice"}/>
                </Button>
                <Button variant="contained" color="primary" disabled={validatedFamily} onClick={(e) => {
                  toggleFamily(e);
                  props.getEntitiesAttributs(familyState.idPredecessor);
                }}
                        className={classes.button}>
                  <Translate contentKey={"pimnowApp.family.familyChoiceValidation"}/>
                </Button>
              </Grid>
            }

            {(validatedFamily || (!isNew)) &&
            <React.Fragment>
              <Grid item xs={12}>
                <div>
                  <Typography component="h2" variant="h5">
                    <Translate contentKey="pimnowApp.family.home.currentAttributs"/>
                  </Typography>
                </div>
              </Grid>
              <AttributesCreation attributes={familysAttributsState} setAttributes={(values) => setFamilysAttributsState(values)} disabled={true}/>
              </React.Fragment>
            }
            <Grid item xs={12}>
              <div>
                <Typography component="h2" variant="h5">
                  <Translate contentKey="pimnowApp.family.home.addExistingAttributs"/>
                </Typography>
              </div>
            </Grid>
            {newExistingAttributes && newExistingAttributes.map((attribut, index) =>
              <Grid item xs={12} key={`attribut${index}`}>
                <Autocomplete
                  id={"name-new-attribut" + index}
                  options={props.attributs.slice()}
                  getOptionLabel={option => `(${option.idF}) ${option.nom} (${translate(`pimnowApp.AttributType.${option.type}`)})`}
                  renderInput={params => (
                    <TextField {...params} label={<Translate contentKey={"pimnowApp.family.attributechoice"}/>} variant="outlined" fullWidth />
                  )}
                  onBlur={e => {
                    props.attributs.forEach(elt => {
                      attributesArray[`(${elt.idF}) ${elt.nom} (${translate(`pimnowApp.AttributType.${elt.type}`)})`] = elt ;
                    });
                    const attributesTmp = newExistingAttributes.slice();
                    attributesTmp[index] = attributesArray[e.target['value']];
                    setNewExistingAttributes(attributesTmp);
                  }}
                />
              </Grid>
            )}
            <Grid item xs={12} container justify="flex-end" direction="row">
              <Button variant="contained" color="secondary" onClick={() => {
                setNewExistingAttributes(newExistingAttributes.concat({}));
              }} className={classes.button}>
                <Translate contentKey={"pimnowApp.family.addAttributs"}/>
              </Button>
            </Grid>


            <Grid item xs={12}>
              <div>
                <Typography component="h2" variant="h5">
                  <Translate contentKey="pimnowApp.family.home.addNewAttributs"/>
                </Typography>
              </div>
            </Grid>
                <AttributesCreation attributes={newAttributes} setAttributes={setNewAttributes} disabled={false}/>
            <Grid item xs={12} container justify="flex-end" direction="row">
              <Button variant="contained" color="secondary" onClick={() => {
                setNewAttributes(newAttributes.concat({}));
              }} className={classes.button}>
                <Translate contentKey={"pimnowApp.family.addAttributs"}/>
              </Button>
            </Grid>
          </Grid>
        </FormControl>
        <ButtonsAction backUrl={"/entity/family"} saveButtonDisabled={!(validatedFamily || !isNew)}/>
      </form>
    </Container>
  )
};



const mapStateToProps = (storeState: IRootState) => ({
  attributs: storeState.attribut.entities,
  familyEntityRoot: storeState.family.entityRoot,
  familyEntity: storeState.family.entity,
  loading: storeState.family.loading,
  updating: storeState.family.updating,
  updateSuccess: storeState.family.updateSuccess,
  familysAttributs: storeState.family.entitiesAttributs
});

const mapDispatchToProps = {
  getEntityRoot,
  getAttributs,
  getEntity,
  updateEntity,
  createEntity,
  reset,
  getEntitiesAttributs,

};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(FamilyUpdate);
