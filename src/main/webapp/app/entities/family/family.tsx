import React, {useEffect} from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {IRootState} from 'app/shared/reducers';

import {getEntityRoot} from './family.reducer';
import {FamilyTree} from "app/entities/family/family-tree";
import {
  Button,
  Divider,
  Grid,
  List,
  Paper, Table, TableCell,
  TableContainer, TableHead, TableRow,
  Typography
} from "@material-ui/core";
import {FormatListBulleted} from '@material-ui/icons';
import axios from "axios";
import {IAttribut} from "app/shared/model/attribut.model";
import {Translate} from "react-jhipster";
import {makeStyles} from "@material-ui/core/styles";
import {AttributType} from "app/shared/model/enumerations/attribut-type.model";
import {IFamily} from "app/shared/model/family.model";
import {AddButton} from "app/commons/add-button";
import {EditButton} from "app/commons/edit-button";
import {DeleteButton} from "app/commons/delete-button";
import {AUTHORITIES} from "app/config/constants";

export interface IFamilyState extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {
}

const Family = (props: IFamilyState) => {

  const [attributesSelect, setAttributesSelect] = React.useState([]);
  const [loading, setLoading] = React.useState(false);
  const [familyList, setFamilyList] = React.useState([]);
  const [printTree, setPrintTree] = React.useState(false);
  const [idFamily, setIdFamily] = React.useState(0);
  const [isUser, setIsUser] = React.useState(false);

  const useStyles = makeStyles({
    table: {
      minWidth: 650,
    },
  });

  const loadAttributs = (familyId: number) => {
    const apiUrl = 'api/families';
    axios.get<IAttribut[]>(`${apiUrl}/familyAttributs/${familyId}`)
      .then(response => {
        setAttributesSelect(response.data);
        setLoading(false);
      })
  };

  useEffect(() => {
    props.getEntityRoot(0);
    if(props.account.authorities[0] === AUTHORITIES.USER){
      setIsUser(true);
    }
  }, []);

  const checkPredecessor = (family, table) => {
    table.push(`${family.nom} (${family.idF})`);
    if (family.successors) {
      family.successors.filter(f => !f.deleted).forEach((familySuccessor) => {
        checkPredecessor(familySuccessor, table);
      });
    }
  };

  useEffect(() => {
    const tempFamilyList: IFamily[] = [];
    checkPredecessor(props.familyEntityRoot, tempFamilyList);
    setFamilyList(tempFamilyList.filter(f => !f.deleted));
    if (tempFamilyList.length > 0) {
      setPrintTree(true);
    }
  }, [props.familyEntityRoot]);

  const handleClick = (id, familyName) => {
    setLoading(true);
    loadAttributs(id);
    setIdFamily(id);
  };

  const printTypeLabel = (type) => {
    switch(type){
      case AttributType.NUMBER :
        return <Translate contentKey="global.attributTypes.number"/>;
      case AttributType.MULTIPLE_VALUE :
        return <Translate contentKey="global.attributTypes.multiple"/>;
      case AttributType.RESSOURCE :
        return <Translate contentKey="global.attributTypes.ressource"/>;
      case AttributType.TEXT :
        return <Translate contentKey="global.attributTypes.text"/>;
      default:
        return "";
    }
  };

  const classes = useStyles();

  return (
    <React.Fragment>
      <Grid xs={12} style={{textAlign:"center"}}>
          <Typography variant="h4">
            <FormatListBulleted />&nbsp;<Translate contentKey={"pimnowApp.family.home.tree"}>Arbre des familles</Translate>
          </Typography>
      </Grid>
      {isUser === false ? (      <Link to={`${props.match.url}/new`} className="float-right">
        <AddButton content={"pimnowApp.family.home.createLabel"}/>
      </Link>) : null}
      <Grid
        container
        spacing={0}
        direction="row"
        justify="center"
        style={{marginBottom: "2em", padding: "2em"}}
      >
        <Grid xs={12} sm={6} style={{borderRight: "1px solid #e0e0e0"}}>
          <Grid style={{textAlign: "center"}} xs={12}>
            <Typography variant="h5"><Translate contentKey={"pimnowApp.family.home.tree"}>Arbre des familles</Translate></Typography>
            <Divider style={{marginBottom: "2em"}}/>
          </Grid>
          {printTree ?
            <FamilyTree familyList={familyList} handlingFunction={handleClick} node={props.familyEntityRoot}/>
            :
            null
          }
        </Grid>
        <Grid xs={12} sm={6}>
          <Grid style={{textAlign: "center"}} xs={12}>
            <Typography variant="h5"><Translate contentKey={"pimnowApp.family.home.attributes"}>Attributs de la
              famille</Translate></Typography>
            <Divider style={{marginBottom: "2em"}}/>
          </Grid>
          <List>
            {!isUser && idFamily !== 0 && (
              <Grid style={{textAlign: "center"}} xs={12}>
                <Link to={`${props.match.url}/${idFamily}/edit`}>
                  <EditButton content={"pimnowApp.family.home.editLabel"}/>
                </Link>
                {idFamily !== props.familyEntityRoot.id && (
                  <Link to={`${props.match.url}/${idFamily}/delete`}>
                    <DeleteButton content="pimnowApp.family.home.deleteLabel"/>
                  </Link> )}
              </Grid>
            )}

            {attributesSelect.length > 0 ? (
              <TableContainer component={Paper} style={{marginLeft:'1.6em'}}>
              <Table size="small" className={classes.table} aria-label="attribute-table" style={{borderRadius:'Opx'}}>
                <TableHead>
                  <TableRow style={{backgroundColor:'#245173'}}>
                    <TableCell style={{color:'white'}} align="center"><b><Translate contentKey="pimnowApp.family.attributesTable.idf"/></b></TableCell>
                    <TableCell style={{color:'white'}} align="center"><b><Translate contentKey="pimnowApp.family.attributesTable.name"/></b></TableCell>
                    <TableCell style={{color:'white'}} align="center"><b><Translate contentKey="pimnowApp.family.attributesTable.type"/></b></TableCell>
                  </TableRow>
                  {attributesSelect.map((value, key) => (
                    <TableRow key={value.idf}>
                      <TableCell align="center">{value.idF}</TableCell>
                      <TableCell align="center">{value.nom}</TableCell>
                      <TableCell align="center">{printTypeLabel(value.type)}</TableCell>
                    </TableRow>
                  ))}
                </TableHead>
              </Table>
            </TableContainer>) : null}
          </List>
        </Grid>
      </Grid>
    </React.Fragment>
  );
};

const mapStateToProps = (storeState: IRootState) => ({
  familyList: storeState.family.entities,
  familyEntityRoot: storeState.family.entityRoot,
  account: storeState.authentication.account
});

const mapDispatchToProps = {
  getEntityRoot
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Family);
