import * as React from 'react';
import {useEffect} from 'react';
import {makeStyles} from '@material-ui/core/styles';
import TreeView from '@material-ui/lab/TreeView';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import ChevronRightIcon from '@material-ui/icons/ChevronRight';
import TreeItem from '@material-ui/lab/TreeItem';
import {IFamily} from "app/shared/model/family.model";
import {IAttribut} from "app/shared/model/attribut.model";
import {Box} from "@material-ui/core";
import {theme} from "app/app";

const useStyles = makeStyles({
  root: {}
});

export interface NodeTree {
  id: string,
  label: string,
  children: NodeTree[]
}

interface Props {
  node: IFamily,
  handlingFunction?: Function,
  attributes?: IAttribut[],
  selectedNodeId?: number;
  handlingFunctionFamily?: (family: IFamily) => void
  familyList?: any[],
}

function NodeTreeComponent(props: Props) {
  const {node, selectedNodeId} = props;
  const childrenComponent = node && node.successors && node.successors.filter(x => x && x.deleted !== true).map(child =>
    <NodeTreeComponent attributes={props.attributes} key={`node-tree-${child.id}`}
                       handlingFunction={props.handlingFunction} handlingFunctionFamily={props.handlingFunctionFamily}
                       node={child} selectedNodeId={selectedNodeId}/>);

  let style ;

  if (childrenComponent.length === 0) {
    style = {
      marginLeft: "26px"
    };
  } else {
    style = {
      marginLeft: "0"
    };
  }

  return (
    <TreeItem
      key={`tree-item-${node.id}`}
      nodeId={node.nom}
      label={node.id === selectedNodeId ?
        <Box style={style} fontWeight={'bold'} fontStyle={theme.palette.primary}>{`${node.nom} (${node.idF})`} ✓ </Box> :
        <Box style={style} fontWeight={'lighter'}>{`${node.nom} (${node.idF})`}</Box>}
      onClick={(e) => {
        props.handlingFunction && props.handlingFunction(node.id, node.nom);
        props.handlingFunctionFamily && props.handlingFunctionFamily(node);
      }}
    >
      {childrenComponent}
    </TreeItem>
  )
}

export function FamilyTree(props: Props) {
  const classes = useStyles();
  const {node, selectedNodeId} = props;

  const [familyList, setFamilyList] = React.useState([]);

  useEffect(() => {
    setFamilyList(props.familyList);
  }, [props.familyList]);

  return (
    <React.Fragment>
      {familyList instanceof Array ?
        (
          <React.Fragment>
            {familyList.length >= 1 ? (
              <TreeView
                defaultExpanded={familyList}
                style={{width: "100%"}}
                className={classes.root}
                defaultCollapseIcon={<ExpandMoreIcon/>}
                defaultExpandIcon={<ChevronRightIcon/>}
              >
                {node.id && <NodeTreeComponent handlingFunction={props.handlingFunction} node={node}
                                               handlingFunctionFamily={props.handlingFunctionFamily}
                                               selectedNodeId={selectedNodeId}/>}
              </TreeView>
            ) : null
            }
          </React.Fragment>
        ) :
        (

          <TreeView
            defaultExpanded={["family root"]}
            style={{width: "100%"}}
            className={classes.root}
            defaultCollapseIcon={<ExpandMoreIcon/>}
            defaultExpandIcon={<ChevronRightIcon/>}
          >
            {node.id && <NodeTreeComponent handlingFunction={props.handlingFunction} node={node}
                                           handlingFunctionFamily={props.handlingFunctionFamily}
                                           selectedNodeId={selectedNodeId}/>}
          </TreeView>
        )}
    </React.Fragment>
  )

}
