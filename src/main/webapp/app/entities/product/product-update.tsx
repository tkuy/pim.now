import React, {useEffect, useState} from 'react';
import {connect} from 'react-redux';
import {RouteComponentProps} from 'react-router-dom';
import {IRootState} from 'app/shared/reducers';

import {getEntityRoot as getFamilyRoot} from 'app/entities/family/family.reducer';
import {getEntityRoot as getCategoryRoot} from 'app/entities/category/category.reducer';
import {createEntity, getEntity, reset, updateEntity} from './product.reducer';
import {Button, Chip, Container, FormControl, FormHelperText, Grid, Input, Link} from "@material-ui/core";
import {makeStyles} from "@material-ui/core/styles";
import Typography from "@material-ui/core/Typography";
import {translate, Translate} from "react-jhipster";
import TextField from "@material-ui/core/TextField";
import {deepUpdate} from "immupdate";
import {IProduct} from "app/shared/model/product.model";
import {loadAttributs} from "../family/family-service";
import {ButtonsAction} from "app/shared/util/buttons-action";
import {CategoryTree} from "../category/category-tree";
import {ICategory} from "app/shared/model/category.model";
import {IAttributValue} from "app/shared/model/attribut-value.model";
import {IFamily} from "app/shared/model/family.model";
import Collapse from "@material-ui/core/Collapse";
import {AttributType} from "app/shared/model/enumerations/attribut-type.model";
import {STANDARD_SEPARATOR} from "app/config/constants";
import {FamilyTree} from "../family/family-tree";
import {IAttribut} from "app/shared/model/attribut.model";
import {filesLocation} from "../../../config/vps-config";
import {Simulate} from "react-dom/test-utils";
import select = Simulate.select;

export interface IProductUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

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
  root: {
    '& .MuiTextField-root': {
      margin: theme.spacing(1),
      width: 200,
    },
  },
  button: {
    margin: theme.spacing(3, 1, 2)
  }
}));

const ProductUpdate = (props: IProductUpdateProps) => {
  const [isNew,] =  useState(!props.match.params || !props.match.params.id);
  const [productState, setProductState] = useState({} as IProduct);
  const classes = useStyles();
  const [selectedFamily, setSelectedFamily] = useState<IFamily>({});
  const [selectedCategories, setSelectedCategories] = useState<ICategory[]>([]);
  const [attributValues, setAttributValues] = useState<IAttributValue[]>([]);
  const [validatedFamily, setValidatedFamily] = useState<boolean>(!(!props.match.params || !props.match.params.id));
  const [selectedCategory, setSelectedCategory] = useState<ICategory>();
  const uselessAttributs = ["Nom", "Id Fonctionnel", "Description", "Famille", "Categorie"];
  const filterAttributs = (values: IAttribut[]) => {
    return values.filter(value => !uselessAttributs.includes(value.nom));
  };
  const filterAttributValues = (values: IAttributValue[]) => {
    return values.filter(value => !uselessAttributs.includes(value.attribut.nom));
  };

  const splitMultipleValues = (value: string) => {
    return value.split(STANDARD_SEPARATOR);
  };
  const concatMultipleValues = (values: string[]) => {
    return values.join(STANDARD_SEPARATOR);
  };
  useEffect(() => {
    if(!isNew && props.productEntity && props.productEntity.id) {
      setProductState(props.productEntity);
      setSelectedCategories(props.productEntity.categories);

      // Prepare the attributs values
      const attributValuesEdit: IAttributValue[] = props.productEntity && props.productEntity.attributValues && props.productEntity.attributValues.map(attVal => {
        if(attVal.attribut.type === AttributType.MULTIPLE_VALUE) {
          const splits = splitMultipleValues(attVal.value);
          const attributValuesTmp = splits.length===1 && splits[0]==="" ? [] : splits;
          return {...attVal, values: attributValuesTmp};
        }
        return attVal;
      });
      loadAttributs(props.productEntity.family.id, (entities => {
        const attributInformationIds = attributValuesEdit.map(attributValue => attributValue.attribut.id);
        const missingAttributs: IAttribut[] = filterAttributs(entities.filter(att => !attributInformationIds.includes(att.id)));
        const missingAttributsValues = missingAttributs.map(att => {
          return {attribut: att, values: []} as IAttributValue;
        });
        setAttributValues(filterAttributValues(attributValuesEdit.concat(missingAttributsValues)));
      }));
      setSelectedFamily(props.productEntity.family);
    }
  }, [props.productEntity]);

  const categoriesChip = selectedCategories.map((category) =>
    <Chip key={`chip-category-${category.id}`}
      label={`${category.nom} (${category.idF})`}
      onDelete={() => setSelectedCategories(selectedCategories.filter(c => c.id !== category.id))}
    />
  );
  const [files, setFiles] = useState<{[key: number]: File}>({});
  const [errorExtension, setErrorExtension] = useState<boolean>(false);
  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
    }
    props.getFamilyRoot(0);
    props.getCategoryRoot(0);
    return () => props.reset();
  }, []);



  const handleClose = () => {
    props.history.push('/entity/product');
  };
  useEffect(() => {
    if(props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);


  const saveEntity = (event) => {
    event.preventDefault();
    const newAttributValues = attributValues.map(attributValue => {
      return {
        ...attributValue,
        value: attributValue.attribut.type === AttributType.MULTIPLE_VALUE ?
          concatMultipleValues(attributValue.values) : attributValue.value
      }
    });
    const f: File[] = Object.values(files);
    if (isNew) {
      const entity = {
        ...productState,
        categories: selectedCategories,
        family: selectedFamily,
        attributValues: newAttributValues,
        files:f
      };
      props.createEntity(entity);
    } else {
      const entity = {
        ...props.productEntity,
        categories: selectedCategories,
        attributValues: newAttributValues,
        files:f
      };
      props.updateEntity(entity);
      handleClose();
    }
  };

  const handleChange = (event, key: keyof IProduct) => {
    const value = key==="idF" ? event.target.value.trim() : event.target.value;
    setProductState(deepUpdate(productState).at(key).set(value));
  };
  const handleClickFamily = (family: IFamily) => {
    if(!validatedFamily) {
      loadAttributs(family.id, attributs => {
        setSelectedFamily(family);

        const attributValuesTmp: IAttributValue[] = filterAttributs(attributs).map(att => {
          return {
            attribut: att,
            'value':'',
            currentValue:'',
            'values' : []
          } as IAttributValue;
        });
        setAttributValues(attributValuesTmp);
      });
    }
  };
  const toggleFamily = (e) => {
    setValidatedFamily(!validatedFamily);
  };
  const invalidNumberFormat = (value: any) => {
    return isNaN(value);
  };
  const renameFile = (file: File, newName: string) => {
    return new File([file as Blob], newName, {
      type:file.type, lastModified:file.lastModified
    });
  };
  const fields = attributValues.map((attVal, index) => {
    const update = (key: keyof IAttributValue, value) => {
      const newAttribut = deepUpdate(attVal).at(key).set(value);
      const attributValuesTmp = attributValues.slice();
      attributValuesTmp[index] = newAttribut;
      setAttributValues(attributValuesTmp);
    };
      switch(attVal.attribut.type) {
        case AttributType.NUMBER:
          return (
            <Grid container item spacing={3}>
              <Grid item xs={12}>
                <FormControl fullWidth>
                  <TextField
                    name={"name"}
                    variant="outlined"
                    fullWidth
                    key={`attribut-value-field${index}`}
                    onChange={e => {
                      const value = e.target.value;
                      update('value', value);
                    }}
                    value={attVal.value || ''}
                    id={`attribut-value-field${attVal.attribut.id}`}
                    label={`${attVal.attribut.nom} (${translate("pimnowApp.AttributType." + attVal.attribut.type)})`}
                  />
                  <FormHelperText id="wrong-number-format" hidden={!attVal.value || !invalidNumberFormat(attVal.value)} style={{color: 'red'}}><Translate contentKey="pimnowApp.product.invalidNumberFormat"/></FormHelperText>
                </FormControl>
              </Grid>
            </Grid>
          );
        case AttributType.TEXT :
          return (
            <Grid container item spacing={3}>
              <Grid item xs={12}>
                <TextField
                  name={"name"}
                  variant="outlined"
                  fullWidth
                  key={`attribut-value-field${index}`}
                  onChange={e => {
                    const value = e.target.value;
                    update('value', value);
                  }}
                  value={attVal.value || ''}
                  id="name"
                  label={`${attVal.attribut.nom} (${translate("pimnowApp.AttributType." + attVal.attribut.type)})`}
                />
              </Grid>
            </Grid>
          );
        case AttributType.MULTIPLE_VALUE:
          return (
            <Grid container item spacing={3} alignItems={'center'} alignContent={'center'}>
                <Grid item xs={8}>
                  <TextField
                    name={"name"}
                    variant="outlined"
                    fullWidth
                    key={`attribut-value-field${index}`}
                    onChange={e => {
                      const value = e.target.value;
                      update('currentValue', value);
                    }}
                    value={attVal.currentValue}
                    id={`attribut-value-field${attVal.attribut.id}`}
                    label={`${attVal.attribut.nom} (${translate("pimnowApp.AttributType." + attVal.attribut.type)})`}
                  />
                </Grid>

                <Grid item xs={4}>
                  <Button variant="contained" color="primary" disabled={!attVal.currentValue} onClick={(e) =>{
                    const values = attVal.values.slice();
                    values.push(attVal.currentValue);
                    const newAttribut = {...attVal, values, currentValue:''};
                    const attributValuesTmp = attributValues.slice();
                    attributValuesTmp[index] = newAttribut;
                    setAttributValues(attributValuesTmp);
                  }} className={classes.button}>
                    <Translate contentKey={"pimnowApp.product.add"}/>
                  </Button>
              </Grid>
              <Grid item xs={12} spacing={3}>
                {
                  attVal.values && attVal.values.map((value, i) =>
                    <Chip key={`chip-multiple-value-${attVal.attribut.id}-${i}`}
                      label={value}
                      onDelete={() => {
                        update('values', attVal.values.filter(v => v!==value));
                      }}
                  />)
                }
              </Grid>
            </Grid>
          );
        case AttributType.RESSOURCE:
          return (
            <Grid container item spacing={3}>
              <Grid item xs={6}>
                {`${attVal.attribut.nom} (${translate("pimnowApp.AttributType.RESSOURCE")})`}
              </Grid>
              <Grid item xs={6}>

                {isNew || (!isNew && !attVal.id)?
                  <FormControl>
                    <Input error={errorExtension === true} type="file" onChange={(event) => {
                      const target = event.target as HTMLInputElement;
                      const file = target.files[0];
                      const fileName = String(file.name);
                      if(fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".jpeg") || fileName.endsWith(".xlsx") || fileName.endsWith(".pdf") || fileName.endsWith(".txt")){
                        setErrorExtension(false);
                        const newName = `${attVal.attribut.id}${fileName}`;
                        files[attVal.attribut.id] = renameFile(file, newName);
                        update('value', newName);
                        setFiles(files);
                      } else {
                        update('value', null);
                        setErrorExtension(true);
                        files[attVal.attribut.id] = file;
                        setFiles(files);
                      }
                    }}/>
                    <FormHelperText id="my-helper-text" hidden={errorExtension === false} style={{color: 'red'}}><Translate contentKey="pimnowApp.product.importHelper"/></FormHelperText>
                  </FormControl> :
                  <Link href={`${filesLocation}/${attVal.value}`}>
                    {attVal.value}
                  </Link>
                }
              </Grid>
            </Grid>
          );
        case AttributType.VALUES_LIST:
          break;
        default:
          return <div>{attVal.attribut.type}</div>
      }
  });


  const handleClickAddCategory = (e) => {
    if(selectedCategory && !selectedCategories.includes(selectedCategory)) {
      setSelectedCategories(selectedCategories.concat(selectedCategory));
      setSelectedCategory(undefined);
    }
  };
  return (
    <Container component="main" maxWidth="sm">
      <div className={classes.paper}>
        <form className={classes.form} onSubmit={e => saveEntity(e)}>
          <Grid item xs={12}>
            <Typography
              component="h1"
              variant="h5">
              &nbsp;
              {isNew ? <Translate contentKey="pimnowApp.product.home.createLabel"/> : <Translate contentKey="pimnowApp.product.home.editLabel"/>}
            </Typography>
          </Grid>
          <FormControl className={classes.form}>
            <Grid container spacing={3}>
              <Grid item xs={12}>
                <TextField
                  required
                  name={"name"}
                  variant="outlined"
                  fullWidth
                  onChange={e => handleChange(e, 'nom')}
                  value={productState.nom || ''}
                  id="name"
                  label={<Translate contentKey="pimnowApp.product.nom"/>}
                />
              </Grid>

              <Grid item xs={12}>
                <TextField
                  required
                  name="idF"
                  fullWidth
                  onChange={e => handleChange(e, 'idF')}
                  variant="outlined"
                  value={productState.idF || ''}
                  id="idF"
                  label={<Translate contentKey="pimnowApp.product.idF"/>}
                  disabled={!isNew}
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  InputLabelProps={{ shrink: !!productState.description }}
                  id="outlined-multiline-static"
                  label={<Translate contentKey="pimnowApp.product.description"/>}
                  multiline
                  rows="4"
                  value={productState.description}
                  onChange={e => handleChange(e, 'description')}
                  fullWidth
                  variant="outlined"
                />
              </Grid>

              <Grid item xs={12}>
                <Typography
                  component="h1"
                  variant="h5">
                  &nbsp;
                  <Translate contentKey="pimnowApp.product.categories"/>
                </Typography>
              </Grid>
              <Grid item xs={12}>
                <CategoryTree node={props.categoryRoot} handlingFunction={(category) => {
                  if(!selectedCategories.includes(category)) {
                    setSelectedCategory(category);
                  }
                }}/>
              </Grid>
              <Grid container item justify="flex-end" direction="row">
                <Button variant="contained" color="secondary" className={classes.button} disabled={!selectedCategory} onClick={handleClickAddCategory}>
                  <Translate contentKey={"pimnowApp.product.categoryChoice"} />
                </Button>
              </Grid>
              <Grid item xs={12}>
                {categoriesChip}
              </Grid>
              <Grid item xs={12}>
                <Typography
                  component="h1"
                  variant="h5">
                  &nbsp;
                  <Translate contentKey="pimnowApp.product.families"/>
                </Typography>
              </Grid>
              <Grid item xs={12}>
                <FamilyTree node={props.familyRoot} handlingFunctionFamily={handleClickFamily} selectedNodeId={selectedFamily && selectedFamily.id}/>
                {isNew && (
                  <Grid container item justify="flex-end" direction="row">
                    <Button variant="contained" color="secondary" disabled={!validatedFamily} onClick={toggleFamily} className={classes.button}>
                      <Translate contentKey={"pimnowApp.product.familyAnotherChoice"} />
                    </Button>
                    <Button variant="contained" color="primary" disabled={validatedFamily} onClick={toggleFamily} className={classes.button}>
                      <Translate contentKey={"pimnowApp.product.familyChoiceValidation"}/>
                    </Button>
                  </Grid>
                )}
              </Grid>
              <Collapse style={{width:'100%', spacing:3}} in={validatedFamily} timeout="auto" unmountOnExit>
                <Grid container spacing={3}>
                  <Grid item xs={12}>
                    <Typography
                      component="h1"
                      variant="h5">
                      &nbsp;
                      <Translate contentKey="pimnowApp.product.informationsFamily"/> ({`${selectedFamily.nom} ${selectedFamily.idF}`})
                    </Typography>
                  </Grid>
                  <Grid item container spacing={3}>
                    {fields}
                  </Grid>
                </Grid>
              </Collapse>
            </Grid>
            <ButtonsAction backUrl={"/entity/product"} saveButtonDisabled={!(validatedFamily || !isNew)}/>
          </FormControl>
        </form>
      </div>
    </Container>
  );
};

const mapStateToProps = (storeState: IRootState) => ({
  productEntity: storeState.product.entity,
  loading: storeState.product.loading,
  updating: storeState.product.updating,
  updateSuccess: storeState.product.updateSuccess,
  categoryRoot: storeState.category.entityRoot,
  familyRoot: storeState.family.entityRoot,
});

const mapDispatchToProps = {
  getEntity,
  updateEntity,
  createEntity,
  getFamilyRoot,
  getCategoryRoot,
  reset
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ProductUpdate);
