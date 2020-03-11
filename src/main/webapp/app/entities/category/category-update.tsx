import React, {useEffect} from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {AvField, AvForm, AvGroup, AvInput} from 'availity-reactstrap-validation';
import {translate, Translate} from 'react-jhipster';
import {IRootState} from 'app/shared/reducers';

import {getEntities as getCategories, getEntityRoot as getCategoryRoot} from 'app/entities/category/category.reducer';
import {createEntity, getEntity, reset, updateEntity, getEntityPredecessor} from './category.reducer';
import {Button, Container, Divider, Grid, TextField} from "@material-ui/core";
import FilterList from "@material-ui/icons/FilterList";
import Typography from "@material-ui/core/Typography";
import {CategoryTree} from "app/entities/category/category-tree";
import {ICategory} from "app/shared/model/category.model";
import Alert from "@material-ui/lab/Alert";
import {ButtonsAction} from "app/shared/util/buttons-action";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";

export interface ICategoryUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {
}

export const CategoryUpdate = (props: ICategoryUpdateProps) => {
  const [isNew] = React.useState(!props.match.params || !props.match.params.id);
  const [name, setName] = React.useState('');
  const [idF, setIdF] = React.useState('');
  const [printTree, setPrintTree] = React.useState(false);
  const [idCategory, setIdCategory] = React.useState(0);
  const [errorAdd, setErrorAdd] = React.useState("");
  const [isRoot, setIsRoot] = React.useState(false);

  useEffect(() => {
    props.reset();
    if (props.updateSuccess)
      props.history.push('/entity/category');
  }, [props.updateSuccess]);

  const back = () => {
    props.reset();
    props.history.push('/entity/category');
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntityPredecessor(props.match.params.id);
      props.getEntity(props.match.params.id);
    }
    props.getCategories();
    props.getCategoryRoot(0);
    return () => props.reset();
  }, []);

  useEffect(() => {
    if (!props.categoryEntity)
      return;
    setName(props.categoryEntity.nom);
    setIdF(props.categoryEntity.idF);
    if (props.categoryEntity.predecessor) {
      setIdCategory(props.categoryEntity.predecessor.id);
    }
  }, [props.categoryEntity]);

  useEffect(() => {
    setIdCategory(props.entityPredecessor) ;
  }, [props.entityPredecessor]) ;

  useEffect(() => {
    if(props.categoryRoot.id && props.categoryEntity.id) {
      !isNew && props.categoryRoot.id === props.categoryEntity.id ? setIsRoot(true) : setIsRoot(false);
      console.warn(props.categoryRoot.id, props.categoryEntity.id) ;
    }
  },[props.categoryRoot, props.categoryEntity]) ;

  useEffect(() => {
    if (props.categoryRoot) {
      setPrintTree(true);
    }
  }, [props.categoryRoot]);

  const handleChangeName = (e) => {
    setName(e.target.value);
  };

  const handleChangeIdF = (e) => {
    setIdF(e.target.value.trim());
  };

  const handleSubmit = (e) => {

    e.preventDefault();

    const tempEntity: ICategory = {} as ICategory;
    tempEntity.nom = name;
    tempEntity.idF = idF;

    console.warn("cc" + !isRoot) ;

    if (idCategory === 0 && !isRoot) {
      setErrorAdd(translate("pimnowApp.category.noCat"));
      return ;
    }

    const predecessorEntity: ICategory = {} as ICategory;
    predecessorEntity.id = idCategory;
    tempEntity.predecessor = predecessorEntity;

    if (isNew) {
      props.createEntity(tempEntity);
    } else {
      tempEntity.id = props.categoryEntity.id;
      props.updateEntity(tempEntity);
    }

  };

  return (
    <Container maxWidth="sm">
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
            {isNew ? <Translate contentKey="pimnowApp.category.home.createLabel"/> :
              <Translate contentKey="pimnowApp.category.home.editLabel"/>}
          </Typography>
          {errorAdd.length > 0 && <Alert severity="error">{errorAdd}</Alert>}
        </Grid>
      </Grid>

      <Grid container spacing={4}>
        <Grid item xs={12}>
          <TextField value={name} onChange={handleChangeName} name={"name"} fullWidth
                     label={<Translate contentKey={"pimnowApp.category.nom"}/>} variant="outlined"/>
        </Grid>
        {isNew ?
          <Grid item xs={12}>
            <TextField value={idF} onChange={handleChangeIdF} name={"name"} fullWidth
                       label={<Translate contentKey={"pimnowApp.category.idF"}/>} variant="outlined"/>
          </Grid>
          :
          <Grid item xs={12}>
            <TextField disabled value={idF} onChange={handleChangeIdF} name={"name"} fullWidth
                       label={<Translate contentKey={"pimnowApp.category.idF"}/>} variant="outlined"/>
          </Grid>
        }
        <Grid item xs={12}>
          {isNew ? (
            <>
              <Typography variant={"h6"}><Translate contentKey={"pimnowApp.category.selectCat"}/></Typography>
              <Divider style={{marginBottom:"1em"}}/>
              <CategoryTree idCategory={idCategory} handlingFunctionSimpleClick={(e, category) => {
                setIdCategory(category.id);
              }} node={props.categoryRoot}/>
            </>
          ) : null}
        </Grid>
        <Grid item xs={12} style={{textAlign: "right"}}>
          <Button onClick={back} variant={"contained"} color="secondary">
              <span className="d-none d-md-inline">
                <FontAwesomeIcon icon="arrow-left"/>
                &nbsp;
                <Translate contentKey="entity.action.back">Back</Translate>
              </span>
          </Button>
          &nbsp;
          <Button onClick={handleSubmit} variant={"contained"} color="primary" id="save-entity" type="submit">
            <FontAwesomeIcon icon="save"/>
            &nbsp;
            <Translate contentKey="entity.action.save">Save</Translate>
          </Button>
        </Grid>
      </Grid>

    </Container>
  )

};

const mapStateToProps = (storeState: IRootState) => ({
  categories: storeState.category.entities,
  entityPredecessor: storeState.category.entityPredecessor,
  customers: storeState.customer.entities,
  products: storeState.product.entities,
  categoryEntity: storeState.category.entity,
  loading: storeState.category.loading,
  updating: storeState.category.updating,
  categoryRoot: storeState.category.entityRoot,
  updateSuccess: storeState.category.updateSuccess
});

const mapDispatchToProps = {
  getCategories,
  getEntityPredecessor,
  getEntity,
  updateEntity,
  createEntity,
  reset,
  getCategoryRoot
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(CategoryUpdate);
