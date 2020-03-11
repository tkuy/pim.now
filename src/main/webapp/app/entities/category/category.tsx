import React, {useEffect} from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {IRootState} from 'app/shared/reducers';

import {getEntities as getCategories, getEntityRoot as getCategoryRoot} from 'app/entities/category/category.reducer';
import {Container, Divider, Grid, Typography} from "@material-ui/core";
import {FormatListBulleted} from '@material-ui/icons';
import {Translate} from "react-jhipster";
import {CategoryTree} from "app/entities/category/category-tree";
import {AddButton} from "app/commons/add-button";
import {EditButton} from "app/commons/edit-button";
import {DeleteButton} from "app/commons/delete-button";
import {AUTHORITIES} from "app/config/constants";

export interface IFamilyState extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {
}

const Family = (props: IFamilyState) => {

  const [categoryList, setCategoryList] = React.useState([]);
  const [printTree, setPrintTree] = React.useState(false);
  const [idCategory, setIdCategory] = React.useState(0);
  const [isUser, setIsUser] = React.useState(false);

  useEffect(() => {
    props.getCategoryRoot(0);
  }, []);

  const checkPredecessor = (category, table) => {
    table.push(category.nom);
    if (category.successors) {
      category.successors.forEach((categorySuccessor) => {
        checkPredecessor(categorySuccessor, table);
      });
    }
  };

  useEffect(() => {

    const tempCategoryList = [];

    checkPredecessor(props.categoryRoot, tempCategoryList);
    setCategoryList(tempCategoryList);

    if (tempCategoryList.length > 0) {
      setPrintTree(true);
    }

  }, [props.categoryRoot]);
  useEffect(() => {
    if(props.account.authorities[0] === AUTHORITIES.USER){
      setIsUser(true);
    }
  }, [props.account]);

  return (
    <React.Fragment>
      <Grid xs={12} style={{textAlign: "center"}}>
        <Typography variant="h4">
          <FormatListBulleted/>&nbsp;<Translate contentKey={"pimnowApp.category.home.title"}/>
        </Typography>
      </Grid>
      {
        !isUser &&
          <React.Fragment>
            <Link to={`${props.match.url}/new`} style={{marginTop: '1em', marginLeft: '0.2em'}} className="float-right">
              <AddButton content={"pimnowApp.category.home.createLabel"}/>
            </Link>
            {idCategory !== 0 && (
              <Grid style={{textAlign: "center", marginTop: '1em'}} xs={12}>
              <Link to={`${props.match.url}/${idCategory}/edit`} className="float-right" style={{marginLeft: '0.2em'}}>
              <EditButton content={"pimnowApp.category.home.editLabel"}/>
              </Link>
              {idCategory !== props.categoryRoot.id && (
                <Link to={`${props.match.url}/${idCategory}/delete`} className="float-right" style={{marginLeft: '0.2em'}}>
                  <DeleteButton content="pimnowApp.category.home.deleteLabel"/>
                </Link>)}
              </Grid>
              )}
          </React.Fragment>
      }
      <Grid
        container
        alignItems="center"
        justify="center"
        style={{marginBottom: "2em", padding: "2em"}}
      >
        <Grid item xs={12}>
          <Typography style={{textAlign:'center'}} variant="h5"><Translate contentKey={"pimnowApp.category.categoriesTree"}/></Typography>
          <Divider style={{marginBottom: "2em"}}/>

          {printTree ?

            <CategoryTree handlingFunctionSimpleClick={(e, category) => {
              setIdCategory(category.id);
            }} node={props.categoryRoot}/>
            :
            null
          }
        </Grid>
      </Grid>
    </React.Fragment>
  );
};

const mapStateToProps = (storeState: IRootState) => ({
  categoryRoot: storeState.category.entityRoot,
  account: storeState.authentication.account
});

const mapDispatchToProps = {
  getCategories,
  getCategoryRoot
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Family);
